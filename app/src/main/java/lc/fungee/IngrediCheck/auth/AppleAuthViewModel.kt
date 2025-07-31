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

sealed class AppleLoginState {
    object Idle : AppleLoginState()
    object Loading : AppleLoginState()
    data class Success(val session: SupabaseSession) : AppleLoginState()
    data class Error(val message: String) : AppleLoginState()
}

class AppleAuthViewModel(
    private val repository: AppleAuthRepository
) : ViewModel() {
    var userEmail by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

//already exposing loginState like this so your Composables can observe it.
    private val _loginState = MutableStateFlow<AppleLoginState>(AppleLoginState.Idle)
    val loginState: StateFlow<AppleLoginState> = _loginState

    fun launchAppleWebViewLogin(activity: Activity) {
        _loginState.value = AppleLoginState.Loading
        repository.launchAppleLoginWebView(activity)
    }

    fun signInWithAppleIdToken(idToken: String) {
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            val result = repository.exchangeAppleIdTokenWithSupabase(idToken)
            _loginState.value = result.fold(
                onSuccess = { AppleLoginState.Success(it) },
                onFailure = { AppleLoginState.Error(it.localizedMessage ?: "Supabase error") }
            )
        }
    }

    fun signInWithAppleCode(code: String) {
        _loginState.value = AppleLoginState.Loading
        viewModelScope.launch {
            val result = repository.exchangeAppleCodeWithSupabase(code)
            _loginState.value = result.fold(
                onSuccess = { AppleLoginState.Success(it) },
                onFailure = { AppleLoginState.Error(it.localizedMessage ?: "Supabase error") }
            )
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

    fun setError(message: String) {
        _loginState.value = AppleLoginState.Error(message)
    }

    fun resetState() {
        _loginState.value = AppleLoginState.Idle
    }
} 
