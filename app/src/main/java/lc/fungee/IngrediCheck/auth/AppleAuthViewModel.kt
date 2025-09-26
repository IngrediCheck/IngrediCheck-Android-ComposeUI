package lc.fungee.IngrediCheck.auth

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.user.UserSession

sealed class AppleLoginState {
    object Idle : AppleLoginState()
    object Loading : AppleLoginState()
    data class Success(val session: UserSession) : AppleLoginState()
    data class Error(val message: String) : AppleLoginState()
    object NavigateToDisclaimer : AppleLoginState()
}

class AppleAuthViewModel(
    private val repository: AppleAuthRepository
) : ViewModel() {
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    private val _loginState = MutableStateFlow<AppleLoginState>(AppleLoginState.Idle)
    val loginState: StateFlow<AppleLoginState> = _loginState

    fun launchAppleWebViewLogin(activity: Activity) {
        android.util.Log.d("AppleAuthViewModel", "Launching Apple WebView login")
        _loginState.value = AppleLoginState.Loading
        repository.launchAppleLoginWebView(activity)
    }

    fun signInWithAppleIdToken(idToken: String, context: Context) {
        android.util.Log.d("AppleAuthViewModel", "signInWithAppleIdToken called with token: ${idToken.take(10)}...")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                android.util.Log.d("AppleAuthViewModel", "Calling repository.exchangeAppleIdTokenWithSupabase")
                val result = repository.exchangeAppleIdTokenWithSupabase(idToken)
                android.util.Log.d("AppleAuthViewModel", "Apple ID token exchange result: $result")

                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Apple login successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .putString("login_provider", "apple")
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        android.util.Log.d("AppleAuthViewModel", "User data extracted - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Apple login failed", exception)
                        val errorMessage = when {
                            exception.message?.contains("401") == true -> "Unauthorized - Check Supabase configuration"
                            exception.message?.contains("400") == true -> "Bad request - Invalid token"
                            exception.message?.contains("500") == true -> "Server error - Try again later"
                            exception.message?.contains("404") == true -> "Apple Sign-In not configured in Supabase"
                            else -> exception.localizedMessage ?: "Supabase error: ${exception.message}"
                        }
                        AppleLoginState.Error(errorMessage)
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during Apple login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun signInWithAppleCode(code: String, context: Context) {
        android.util.Log.d("AppleAuthViewModel", "signInWithAppleCode called with code: ${code.take(10)}...")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                android.util.Log.d("AppleAuthViewModel", "Calling repository.exchangeAppleCodeWithSupabase")
                val result = repository.exchangeAppleCodeWithSupabase(code)
                android.util.Log.d("AppleAuthViewModel", "Apple code exchange result: $result")

                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Apple login successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .putString("login_provider", "apple")
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        android.util.Log.d("AppleAuthViewModel", "User data extracted - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Apple login failed", exception)
                        val errorMessage = when {
                            exception.message?.contains("401") == true -> "Unauthorized - Check Supabase configuration"
                            exception.message?.contains("400") == true -> "Bad request - Invalid code"
                            exception.message?.contains("500") == true -> "Server error - Try again later"
                            exception.message?.contains("404") == true -> "Apple Sign-In not configured in Supabase"
                            else -> exception.localizedMessage ?: "Supabase error: ${exception.message}"
                        }
                        AppleLoginState.Error(errorMessage)
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during Apple login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun signInWithGoogleIdToken(idToken: String, context: Context) {
        android.util.Log.d("AppleAuthViewModel", "signInWithGoogleIdToken called")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogleIdTokenSdk(idToken)
                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Google login successful")
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .putString("login_provider", "google")
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Google login failed", exception)
                        // Guide UI to trigger web-based OAuth flow
                        AppleLoginState.Error("Use Google web OAuth flow. ${exception.localizedMessage ?: ""}")
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during Google login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    // ✅ Anonymous Sign-In Method - Using Supabase SDK
    fun signInAnonymously(context: Context) {
        android.util.Log.d("AppleAuthViewModel", "Starting anonymous sign-in")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                val result = repository.signInAnonymously()
                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Anonymous sign-in successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .putString("login_provider", "anonymous")
                            .apply()
                        userEmail = session.user?.email ?: "anonymous@example.com"
                        userId = session.user?.id ?: "anonymous_${System.currentTimeMillis()}"
                        android.util.Log.d("AppleAuthViewModel", "Anonymous user data - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Anonymous sign-in failed", exception)
                        val errorMessage = when {
                            exception.message?.contains("401") == true -> "Unauthorized - Check Supabase configuration"
                            exception.message?.contains("400") == true -> "Bad request - Invalid credentials"
                            exception.message?.contains("500") == true -> "Server error - Try again later"
                            exception.message?.contains("404") == true -> "Anonymous sign-in not configured"
                            else -> exception.localizedMessage ?: "Anonymous sign-in error: ${exception.message}"
                        }
                        AppleLoginState.Error(errorMessage)
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during anonymous sign-in", e)
                _loginState.value = AppleLoginState.Error("Anonymous sign-in failed: ${e.message}")
            }
        }
    }

    // Add this method to handle navigation
    fun navigateToDisclaimer() {
        android.util.Log.d("AppleAuthViewModel", "Triggering navigation to Disclaimer")
        _loginState.value = AppleLoginState.NavigateToDisclaimer
    }

    fun setError(message: String) {
        android.util.Log.e("AppleAuthViewModel", "Setting error: $message")
        _loginState.value = AppleLoginState.Error(message)
    }

    fun resetState() {
        android.util.Log.d("AppleAuthViewModel", "Resetting state")
        _loginState.value = AppleLoginState.Idle
        userEmail = null
        userId = null
    }

    // ✅ Sign Out Method - Using Supabase SDK
    fun signOut(context: Context) {
        android.util.Log.d("AppleAuthViewModel", "Starting sign out")
        viewModelScope.launch {
            try {
                val result = repository.signOut()
                result.fold(
                    onSuccess = {
                        android.util.Log.d("AppleAuthViewModel", "Sign out successful")
                        // Clear local state
                        userEmail = null
                        userId = null
                        _loginState.value = AppleLoginState.Idle
                        // Clear login provider
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply()
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Sign out failed", exception)
                        // Still clear local state even if sign out fails
                        userEmail = null
                        userId = null
                        _loginState.value = AppleLoginState.Idle
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply()
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during sign out", e)
                // Still clear local state even if sign out fails
                userEmail = null
                userId = null
                _loginState.value = AppleLoginState.Idle
                context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
            }
        }
    }

    // ✅ Get Current Session - Using Supabase SDK
    fun getCurrentSession(): UserSession? {
        return repository.getCurrentSession()
    }

    // ✅ Launch Google OAuth via WebView (SDK-supported flow)
    fun launchGoogleOAuth(context: Context) {
        (context as? Activity)?.let { activity ->
            android.util.Log.d("AppleAuthViewModel", "Launching Google WebView login")
            _loginState.value = AppleLoginState.Loading
            repository.launchGoogleLoginWebView(activity)
        } ?: run {
            android.util.Log.e("AppleAuthViewModel", "launchGoogleOAuth requires an Activity context")
            _loginState.value = AppleLoginState.Error("Unable to launch Google login")
        }
    }
}