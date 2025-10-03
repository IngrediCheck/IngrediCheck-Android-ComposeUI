package lc.fungee.IngrediCheck.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lc.fungee.IngrediCheck.model.repository.LoginAuthRepository
import lc.fungee.IngrediCheck.model.repository.auth.AuthProvider
import lc.fungee.IngrediCheck.model.utils.AppConstants

// Unified UI state for all auth providers
data class AuthUiState(
    val provider: AuthProvider = AuthProvider.NONE,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: LoginAuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(provider = repository.authProvider(), isLoading = true, error = null))
    val state: StateFlow<AuthUiState> = _state

    init {
        // keep state in sync with Supabase session status
        viewModelScope.launch {
            try {
                repository.supabaseClient.auth.sessionStatus.collect {
                    val current = runCatching { repository.currentSessionOrNull() }.getOrNull()
                    if (current != null) {
                        _state.value = AuthUiState(provider = repository.authProvider(), isLoading = false, error = null)
                    } else {
                        _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = null)
                    }
                }
            } catch (e: Exception) {
                // On error, just mark not loading
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun currentSessionOrNull(): UserSession? = repository.currentSessionOrNull()

    fun authProvider(): AuthProvider = repository.authProvider()

    suspend fun signInGoogle(idToken: String, context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = withContext(Dispatchers.IO) { repository.signInWithGoogleIdToken(idToken) }
        result.fold(
            onSuccess = { session ->
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                    .edit().putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.GOOGLE).apply()
                _state.value = AuthUiState(provider = AuthProvider.GOOGLE, isLoading = false, error = null)
            },
            onFailure = { e ->
                Log.e("AuthViewModel", "Google sign-in failed", e)
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage ?: "Google sign-in failed")
            }
        )
    }

    suspend fun signInAppleWithIdToken(idToken: String, context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = withContext(Dispatchers.IO) { repository.signInWithAppleIdToken(idToken) }
        result.fold(
            onSuccess = { session ->
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                    .edit().putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.APPLE).apply()
                _state.value = AuthUiState(provider = AuthProvider.APPLE, isLoading = false, error = null)
            },
            onFailure = { e ->
                Log.e("AuthViewModel", "Apple sign-in failed", e)
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage ?: "Apple sign-in failed")
            }
        )
    }

    suspend fun signInAppleWithCode(code: String, context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = withContext(Dispatchers.IO) { repository.exchangeAppleCodeWithSupabase(code) }
        result.fold(
            onSuccess = { session ->
                context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .edit().putString("login_provider", "apple").apply()
                _state.value = AuthUiState(provider = AuthProvider.APPLE, isLoading = false, error = null)
            },
            onFailure = { e ->
                Log.e("AuthViewModel", "Apple code sign-in failed", e)
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage ?: "Apple code sign-in failed")
            }
        )
    }

    suspend fun continueAsGuest(context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = withContext(Dispatchers.IO) { repository.signInAnonymously() }
        result.fold(
            onSuccess = { session ->
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                    .edit().putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.ANONYMOUS).apply()
                _state.value = AuthUiState(provider = AuthProvider.ANONYMOUS, isLoading = false, error = null)
            },
            onFailure = { e ->
                Log.e("AuthViewModel", "Anonymous sign-in failed", e)
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage ?: "Anonymous sign-in failed")
            }
        )
    }

    suspend fun signOutUnified(context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = withContext(Dispatchers.IO) { repository.signOut() }
        result.fold(
            onSuccess = {
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = null)
            },
            onFailure = { e ->
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage)
            }
        )
    }

    suspend fun deleteAccountAndData(context: Context) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result: Result<Unit> = withContext(Dispatchers.IO) { repository.deleteAccountAndData() }
        result.fold(
            onSuccess = {
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = null)
            },
            onFailure = { e ->
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                context.getSharedPreferences(AppConstants.Prefs.SUPABASE_SESSION, Context.MODE_PRIVATE).edit().clear().apply()
                _state.value = AuthUiState(provider = AuthProvider.NONE, isLoading = false, error = e.localizedMessage)
            }
        )
    }
}
