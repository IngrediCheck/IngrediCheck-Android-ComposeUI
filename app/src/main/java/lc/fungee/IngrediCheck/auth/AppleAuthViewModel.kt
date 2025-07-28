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
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationException

sealed class AppleLoginState {
    object Idle : AppleLoginState()
    object Loading : AppleLoginState()
    data class Success(val session: SupabaseSession) : AppleLoginState()
    data class Error(val message: String) : AppleLoginState()
}

class AppleAuthViewModel(
    private val repository: AppleAuthRepository
) : ViewModel() {

//already exposing loginState like this so your Composables can observe it.
    private val _loginState = MutableStateFlow<AppleLoginState>(AppleLoginState.Idle)
    val loginState: StateFlow<AppleLoginState> = _loginState

    fun startAppleLogin(activity: Activity) {
        _loginState.value = AppleLoginState.Loading
        val request = repository.getAppleAuthRequest()
        repository.performAuthRequest(activity, request)
    }

    fun handleAuthResponse(response: AuthorizationResponse?, ex: AuthorizationException?) {
        if (ex != null) {
            _loginState.value = AppleLoginState.Error(ex.localizedMessage ?: "Unknown error")
            return
        }
        if (response != null) {
            val idToken = response.idToken

            if (idToken != null) {
                viewModelScope.launch {
                    val result = repository.exchangeIdTokenWithSupabase(idToken)
                    _loginState.value = result.fold(
                        onSuccess = { AppleLoginState.Success(it) },
                        onFailure = { AppleLoginState.Error(it.localizedMessage ?: "Supabase error") }
                    )
                }
            } else {
                _loginState.value = AppleLoginState.Error("No id_token received from Apple")
            }
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            val result = repository.exchangeGoogleIdTokenWithSupabase(idToken)
            _loginState.value = result.fold(
                onSuccess = { AppleLoginState.Success(it) },
                onFailure = { AppleLoginState.Error(it.localizedMessage ?: "Supabase error") }
            )
        }
    }

    fun resetState() {
        // Reset login state to Idle so WelcomeScreen doesn't auto-navigate after logout
        _loginState.value = AppleLoginState.Idle
    }
} 