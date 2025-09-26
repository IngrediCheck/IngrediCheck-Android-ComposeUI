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
import com.google.gson.Gson
import lc.fungee.IngrediCheck.data.model.SupabaseSession

sealed class AppleLoginState {
    object Idle : AppleLoginState()
    object Loading : AppleLoginState()
    data class Success(val session: SupabaseSession) : AppleLoginState()
    data class Error(val message: String) : AppleLoginState()
    object NavigateToDisclaimer : AppleLoginState()
}

class AppleAuthViewModel(
    private val repository: AppleAuthRepository
) : ViewModel() {
    // Flag to indicate if session storage failed
    private val _sessionStoreFailed = MutableStateFlow(false)
    val sessionStoreFailed: StateFlow<Boolean> = _sessionStoreFailed
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    //already exposing loginState like this so your Composables can observe it.
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
                        android.util.Log.d("AppleAuthViewModel", "Apple login successful, storing session")
                        try {
                            storeSession(session, context)
                            // Persist login provider for Settings UI
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("login_provider", "apple")
                                .apply()
                            _sessionStoreFailed.value = false
                        } catch (e: Exception) {
                            android.util.Log.e("AppleAuthViewModel", "Error storing session", e)
                            _sessionStoreFailed.value = true
                        }
                        userEmail = session.user.email
                        userId = session.user.id
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
                        android.util.Log.d("AppleAuthViewModel", "Apple login successful, storing session")
                        try {
                            storeSession(session, context)
                            // Persist login provider for Settings UI
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("login_provider", "apple")
                                .apply()
                            _sessionStoreFailed.value = false
                        } catch (e: Exception) {
                            android.util.Log.e("AppleAuthViewModel", "Error storing session", e)
                            _sessionStoreFailed.value = true
                        }
                        userEmail = session.user.email
                        userId = session.user.id
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
                val result = repository.exchangeGoogleIdTokenWithSupabase(idToken)
                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Google login successful, storing session")
                        try {
                            storeSession(session, context)
                            // Persist login provider for Settings UI
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("login_provider", "google")
                                .apply()
                            _sessionStoreFailed.value = false
                        } catch (e: Exception) {
                            android.util.Log.e("AppleAuthViewModel", "Error storing session", e)
                            _sessionStoreFailed.value = true
                        }
                        userEmail = session.user.email
                        userId = session.user.id
                        AppleLoginState.Success(session)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Google login failed", exception)
                        AppleLoginState.Error(exception.localizedMessage ?: "Supabase error")
                    }
                )
                _loginState.value = newState
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during Google login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun signInWithAppleAccessToken(accessToken: String, context: Context) {
        android.util.Log.d(
            "AppleAuthViewModel",
            "signInWithAppleAccessToken called with token: ${accessToken.take(10)}..."
        )
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                android.util.Log.d(
                    "AppleAuthViewModel",
                    "Calling repository.exchangeAppleAccessTokenWithSupabase"
                )
                val result = repository.exchangeAppleAccessTokenWithSupabase(accessToken)
                android.util.Log.d(
                    "AppleAuthViewModel",
                    "Apple access token exchange result: $result"
                )

                _loginState.value = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d(
                            "AppleAuthViewModel",
                            "Apple login successful, storing session"
                        )
                        try {
                            // Store session in SharedPreferences so repo can read it
                            storeSession(session, context)
                            // Persist login provider for Settings UI
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("login_provider", "apple")
                                .apply()
                            _sessionStoreFailed.value = false
                            // Extract user data
                            userEmail = session.user.email
                            userId = session.user.id
                            android.util.Log.d(
                                "AppleAuthViewModel",
                                "User data extracted - Email: $userEmail, ID: $userId"
                            )
                            AppleLoginState.Success(session)
                        } catch (e: Exception) {
                            android.util.Log.e("AppleAuthViewModel", "Error storing session", e)
                            _sessionStoreFailed.value = true
                            AppleLoginState.Error("Failed to store session: ${e.message}")
                        }
                    },
                    onFailure = { exception ->
                        android.util.Log.e("AppleAuthViewModel", "Apple login failed", exception)
                        val errorMessage = when {
                            exception.message?.contains("401") == true -> "Unauthorized - Check Supabase configuration"
                            exception.message?.contains("400") == true -> "Bad request - Invalid access token"
                            exception.message?.contains("500") == true -> "Server error - Try again later"
                            exception.message?.contains("404") == true -> "Apple Sign-In not configured in Supabase"
                            else -> exception.localizedMessage
                                ?: "Supabase error: ${exception.message}"
                        }
                        AppleLoginState.Error(errorMessage)
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("AppleAuthViewModel", "Exception during Apple login", e)
                _loginState.value = AppleLoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    // ✅ Anonymous Sign-In Method - Moved to class level
    fun signInAnonymously(context: Context) {
        android.util.Log.d("AppleAuthViewModel", "Starting anonymous sign-in")
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            try {
                val result = repository.signInAnonymously()
                val newState = result.fold(
                    onSuccess = { session ->
                        android.util.Log.d("AppleAuthViewModel", "Anonymous sign-in successful, storing session")
                        try {
                            storeSession(session, context)
                            // Persist login provider for Settings UI
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                                .putString("login_provider", "anonymous")
                                .apply()
                            _sessionStoreFailed.value = false
                        } catch (e: Exception) {
                            android.util.Log.e("AppleAuthViewModel", "Error storing anonymous session", e)
                            _sessionStoreFailed.value = true
                        }
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

    // Make storeSession public so MainActivity can call it
    fun storeSession(session: SupabaseSession, context: Context) {
        try {
            val sessionJson = Gson().toJson(session)
            android.util.Log.d("AppleAuthViewModel", "Session JSON: $sessionJson")
            android.util.Log.d("AppleAuthViewModel", "Session JSON length: ${sessionJson.length}")
            
            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .edit()
                .putString("session", sessionJson)
                .apply()
            
            // Debug logging
            android.util.Log.d("AppleAuthViewModel", "Session stored successfully")
            
            // Add null safety for user object
            if (session.user != null) {
                android.util.Log.d("AppleAuthViewModel", "User email: ${session.user.email}")
                android.util.Log.d("AppleAuthViewModel", "User ID: ${session.user.id}")
            } else {
                android.util.Log.w("AppleAuthViewModel", "User object is null in session")
            }
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthViewModel", "Error storing session to SharedPreferences", e)
            throw e
        }
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

    fun clearSession(context: Context) {
        try {
            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            android.util.Log.d("AppleAuthViewModel", "Session cleared successfully")
        } catch (e: Exception) {
            android.util.Log.e("AppleAuthViewModel", "Error clearing session", e)
        }
    }
}