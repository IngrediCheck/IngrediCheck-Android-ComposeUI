package lc.fungee.IngrediCheck.viewmodel
import lc.fungee.IngrediCheck.model.utils.AppConstants

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// import com.posthog.android.PostHog
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.BuildConfig
import lc.fungee.IngrediCheck.model.repository.LoginAuthRepository

sealed class AppleLoginState {
    object Idle : AppleLoginState()
    object Loading : AppleLoginState()
    data class Success(val session: UserSession) : AppleLoginState()
    data class Error(val message: String) : AppleLoginState()
    object NavigateToDisclaimer : AppleLoginState()
}

class AppleAuthViewModel(
    private val repository: LoginAuthRepository
) : ViewModel() {
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    private val _loginState = MutableStateFlow<AppleLoginState>(AppleLoginState.Loading)
    val loginState: StateFlow<AppleLoginState> = _loginState

    // App-level gate to block rendering until auth/session check finishes
    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked

    // Track whether a spinner should be shown for Apple/Google auth flows.
    // Guest (anonymous) should not trigger a spinner in the Apple section UI.
    var isAppleLoading by mutableStateOf(false)
        private set

    // Internal mode state
    private val _isInternalUser = MutableStateFlow(false)
    val isInternalUser: StateFlow<Boolean> = _isInternalUser

    @Volatile
    private var restoring = true

    init {
        // Initialize internal mode from local prefs
        _isInternalUser.value = repository.getInternalModeFromPrefs()

        // Auto-enable internal mode on emulator/debug builds (like iOS simulator)
        val isEmulator = Build.FINGERPRINT.contains("generic") ||
                         Build.PRODUCT.contains("sdk") ||
                         Build.MODEL.contains("Emulator")
        if (isEmulator && BuildConfig.DEBUG && !_isInternalUser.value) {
            viewModelScope.launch {
                Log.d("AppleAuthViewModel", "Auto-enabling internal mode on emulator")
                repository.setInternalModeInPrefs(true)
                _isInternalUser.value = true
            }
        }

        // Restore session on app start and keep loginState in sync with Supabase Auth
        viewModelScope.launch {
            // If there is a stored session blob, wait briefly for SDK to restore it
            if (repository.hasStoredSession()) {
                // Poll for a short duration until session becomes available
                repeat(20) { // ~4s total
                    val s = runCatching { repository.getCurrentSession() }.getOrNull()
                    if (s != null) {
                        _loginState.value = AppleLoginState.Success(s)
                        userEmail = s.user?.email
                        userId = s.user?.id
                        restoring = false
                        _isAuthChecked.value = true
                        // Refresh internal mode from server
                        refreshInternalModeFromServer()
                        return@launch
                    }
                    delay(200)
                }
            }
            restoring = false
            // No stored session or not restored in time -> fall back to Idle
            if (repository.getCurrentSession() == null) {
                _loginState.value = AppleLoginState.Idle
                isAppleLoading = false
            }
            _isAuthChecked.value = true
        }

        // Observe status changes; ignore while restoring to avoid flicker
        viewModelScope.launch {
            try {
                repository.supabaseClient.auth.sessionStatus.collect {
                    if (restoring) return@collect
                    val current = runCatching { repository.getCurrentSession() }.getOrNull()
                    if (current != null) {
                        _loginState.value = AppleLoginState.Success(current)
                        userEmail = current.user?.email
                        userId = current.user?.id
                        isAppleLoading = false
                    } else {
                        _loginState.value = AppleLoginState.Idle
                        userEmail = null
                        userId = null
                        isAppleLoading = false
                    }
                }
            } catch (_: Exception) {
                if (repository.getCurrentSession() == null) {
                    _loginState.value = AppleLoginState.Idle
                    isAppleLoading = false
                }
            }
        }
    }

    fun launchAppleWebViewLogin(activity: Activity) {
        Log.d("AppleAuthViewModel", "Launching Apple WebView login")
        _loginState.value = AppleLoginState.Loading
        isAppleLoading = true
        repository.launchAppleLoginWebView(activity)
    }

    fun completeWithSupabaseTokens(
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Long,
        tokenType: String,
        context: Context
    ) {
        Log.d("AppleAuthViewModel", "Completing login with implicit tokens")
        _loginState.value = AppleLoginState.Loading
        isAppleLoading = true
        viewModelScope.launch {
            try {
                val result = repository.importSessionFromTokens(accessToken, refreshToken, expiresInSeconds, tokenType)
                val newState = result.fold(
                    onSuccess = { session ->
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.APPLE)
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Import session failed", exception)
                        AppleLoginState.Error(exception.localizedMessage ?: "Import session failed")
                    }
                )
                _loginState.value = newState
                isAppleLoading = false
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Exception during importing tokens", e)
                _loginState.value = AppleLoginState.Error("Import tokens failed: ${e.message}")
                isAppleLoading = false
            }
        }
    }

    fun signInWithAppleIdToken(idToken: String, context: Context) {
        Log.d("AppleAuthViewModel", "signInWithAppleIdToken called with token: ${idToken.take(10)}...")
        _loginState.value = AppleLoginState.Loading
        isAppleLoading = true
        viewModelScope.launch {
            try {
                Log.d("AppleAuthViewModel", "Calling repository.exchangeAppleIdTokenWithSupabase")
                val result = repository.exchangeAppleIdTokenWithSupabase(idToken)
                Log.d("AppleAuthViewModel", "Apple ID token exchange result: $result")

                val newState = result.fold(
                    onSuccess = { session ->
                        Log.d("AppleAuthViewModel", "Apple login successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.APPLE)
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        Log.d("AppleAuthViewModel", "User data extracted - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Apple login failed", exception)
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
                isAppleLoading = false
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Exception during Apple login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
                isAppleLoading = false
            }
        }
    }

    fun signInWithAppleCode(code: String, context: Context) {
        Log.d("AppleAuthViewModel", "signInWithAppleCode called with code: ${code.take(10)}...")
        _loginState.value = AppleLoginState.Loading
        isAppleLoading = true
        viewModelScope.launch {
            try {
                Log.d("AppleAuthViewModel", "Calling repository.exchangeAppleCodeWithSupabase")
                val result = repository.exchangeAppleCodeWithSupabase(code)
                Log.d("AppleAuthViewModel", "Apple code exchange result: $result")

                val newState = result.fold(
                    onSuccess = { session ->
                        Log.d("AppleAuthViewModel", "Apple login successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.APPLE)
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        Log.d("AppleAuthViewModel", "User data extracted - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Apple login failed", exception)
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
                Log.e("AppleAuthViewModel", "Exception during Apple login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun signInWithGoogleIdToken(idToken: String, context: Context) {
        Log.d("AppleAuthViewModel", "signInWithGoogleIdToken called")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogleIdTokenSdk(idToken)
                val newState = result.fold(
                    onSuccess = { session ->
                        Log.d("AppleAuthViewModel", "Google login successful")
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.GOOGLE)
                            .apply()
                        userEmail = session.user?.email
                        userId = session.user?.id
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Google login failed", exception)
                        AppleLoginState.Error(exception.localizedMessage ?: "Google sign-in failed")
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Exception during Google login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    // ✅ Anonymous Sign-In Method - Using Supabase SDK
    fun signInAnonymously(context: Context) {
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                val result = repository.signInAnonymously()
                val newState = result.fold(
                    onSuccess = { session ->
                        Log.d("AppleAuthViewModel", "Anonymous sign-in successful")
                        // Session is automatically managed by Supabase SDK
                        // Persist login provider for Settings UI
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .putString(AppConstants.Prefs.KEY_LOGIN_PROVIDER, AppConstants.Providers.ANONYMOUS)
                            .apply()
                        userEmail = session.user?.email ?: "anonymous@example.com"
                        userId = session.user?.id ?: "anonymous_${System.currentTimeMillis()}"
                        Log.d("AppleAuthViewModel", "Anonymous user data - Email: $userEmail, ID: $userId")
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Anonymous sign-in failed", exception)
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
                Log.e("AppleAuthViewModel", "Exception during anonymous sign-in", e)
                _loginState.value = AppleLoginState.Error("Anonymous sign-in failed: ${e.message}")
            }
        }
    }

    // Add this method to handle navigation
    fun navigateToDisclaimer() {
        Log.d("AppleAuthViewModel", "Triggering navigation to Disclaimer")
        _loginState.value = AppleLoginState.NavigateToDisclaimer
        isAppleLoading = false
    }

    fun setError(message: String) {
        Log.e("AppleAuthViewModel", "Setting error: $message")
        _loginState.value = AppleLoginState.Error(message)
        isAppleLoading = false
    }

    fun resetState() {
        Log.d("AppleAuthViewModel", "Resetting state")
        _loginState.value = AppleLoginState.Idle
        userEmail = null
        userId = null
        isAppleLoading = false
    }

    // Clear any locally stored Supabase session blobs (SharedPreferencesSessionManager)
    fun clearSupabaseLocalSession() {
        viewModelScope.launch {
            runCatching { repository.clearLocalData() }
        }
    }

    // ✅ Sign Out Method - Using Supabase SDK
    fun signOut(context: Context) {
        Log.d("AppleAuthViewModel", "Starting sign out")
        viewModelScope.launch {
            try {
                val result = repository.signOut()
                result.fold(
                    onSuccess = {
                        Log.d("AppleAuthViewModel", "Sign out successful")
                        // Clear local state
                        userEmail = null
                        userId = null
                        _loginState.value = AppleLoginState.Idle
                        // Clear login provider
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply()
                    },
                    onFailure = { exception ->
                        Log.e("AppleAuthViewModel", "Sign out failed", exception)
                        // Still clear local state even if sign out fails
                        userEmail = null
                        userId = null
                        _loginState.value = AppleLoginState.Idle
                        context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply()
                    }
                )
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Exception during sign out", e)
                // Still clear local state even if sign out fails
                userEmail = null
                userId = null
                _loginState.value = AppleLoginState.Idle
                context.getSharedPreferences(AppConstants.Prefs.USER_SESSION, Context.MODE_PRIVATE)
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

    // Internal mode methods
    fun enableInternalMode(context: Context) {
        Log.d("AppleAuthViewModel", "Enabling internal mode")
        viewModelScope.launch {
            try {
                // Update local state first
                _isInternalUser.value = true
                repository.setInternalModeInPrefs(true)

                // Sync to Supabase if user is authenticated
                val session = repository.getCurrentSession()
                if (session != null) {
                    repository.syncInternalModeToSupabase(true)
                }

                // Update analytics
                updateAnalyticsIdentity(context)

                Log.d("AppleAuthViewModel", "Internal mode enabled successfully")
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Failed to enable internal mode", e)
            }
        }
    }

    fun disableInternalMode(context: Context) {
        Log.d("AppleAuthViewModel", "Disabling internal mode")
        viewModelScope.launch {
            try {
                // Update local state first
                _isInternalUser.value = false
                repository.setInternalModeInPrefs(false)

                // Sync to Supabase if user is authenticated
                val session = repository.getCurrentSession()
                if (session != null) {
                    repository.syncInternalModeToSupabase(false)
                }

                // Update analytics
                updateAnalyticsIdentity(context)

                Log.d("AppleAuthViewModel", "Internal mode disabled successfully")
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Failed to disable internal mode", e)
            }
        }
    }

    fun refreshInternalModeFromServer() {
        viewModelScope.launch {
            try {
                val serverValue = repository.refreshInternalModeFromServer()
                if (serverValue != null) {
                    _isInternalUser.value = serverValue
                    repository.setInternalModeInPrefs(serverValue)
                    Log.d("AppleAuthViewModel", "Internal mode refreshed from server: $serverValue")
                }
            } catch (e: Exception) {
                Log.e("AppleAuthViewModel", "Failed to refresh internal mode from server", e)
            }
        }
    }

    private fun updateAnalyticsIdentity(context: Context) {
        try {
            // val posthog = PostHog.with(context)
            val currentUserId = userId ?: "anonymous"

            // Identify user with internal status
            /* posthog.identify(
                currentUserId,
                mapOf("is_internal" to _isInternalUser.value),
                null
            ) */

            Log.d("AppleAuthViewModel", "PostHog identity updated: userId=$currentUserId, isInternal=${_isInternalUser.value}")
        } catch (e: Exception) {
            Log.e("AppleAuthViewModel", "Failed to update PostHog identity", e)
        }
    }

    // Google Web OAuth removed. Use native GoogleSignInClient to obtain an ID token,
    // then call signInWithGoogleIdToken(idToken, context)
}
