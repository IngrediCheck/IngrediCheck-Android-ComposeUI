package llc.fungee.IngrediCheck

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.gotrue

import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.gotrue.providers.Google
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.IDTokenProvider
//import io.github.jan.supabase.gotrue.provider.Google

class GoogleAuthViewModel : ViewModel() {

    // Sealed class to represent authentication states
    sealed class AuthState {
        object Loading : AuthState()
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    // Current authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Current user information
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val session = SupabaseManager.client.gotrue.currentSessionOrNull()
                if (session != null) {
                    _currentUser.value = session.user
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to check authentication: ${e.message}")
            }
        }
    }
    fun authenticateWithSupabase(idToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val session = SupabaseManager.client.gotrue.signUpWith(
                    provider = Google,
                    redirectUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co/auth/v1/callback"
                )
                println("Logged in successfully: ")//${session.user?.email}")
            } catch (e: Exception) {
                println("Error: ${e.localizedMessage}")
            }
        }
    }


    fun signInWithGoogle() {
        viewModelScope.launch {
            Log.d("Auth", "Sign-In triggered") // 👈 Add this
            _authState.value = AuthState.Loading
            try {
                SupabaseManager.client.gotrue.loginWith(Google) {
                    // Optional: Add additional scopes if needed
                    // scope = "email profile"
                }
                // Refresh the auth state after successful login
                checkAuthState()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Google Sign-In failed: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseManager.client.gotrue.logout()
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign out failed: ${e.message}")
            }
        }
    }
}