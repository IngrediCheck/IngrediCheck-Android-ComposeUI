package lc.fungee.Ingredicheck.auth

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.memoji.MemojiRepository
import lc.fungee.Ingredicheck.memoji.MemojiRequest

enum class AuthProvider {
    Google,
    Apple,
    Guest
}

sealed class MemojiGenState {
    data object Idle : MemojiGenState()
    data object Loading : MemojiGenState()
    data class Success(val imageUrl: String) : MemojiGenState()
    data class Error(val message: String) : MemojiGenState()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val session: UserSession, val provider: AuthProvider) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AuthRepository(app.applicationContext)
    private val memojiRepository = MemojiRepository()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    private val _debugLog = MutableStateFlow<List<String>>(emptyList())
    val debugLog: StateFlow<List<String>> = _debugLog.asStateFlow()

    private val _memojiState = MutableStateFlow<MemojiGenState>(MemojiGenState.Idle)
    val memojiState: StateFlow<MemojiGenState> = _memojiState.asStateFlow()

    private fun pushDebug(message: String) {
        val line = "${System.currentTimeMillis()} | $message"
        _debugLog.value = (_debugLog.value + line).takeLast(60)
        Log.d("AuthDebug", message)
    }

    fun setError(message: String) {
        _state.value = AuthState.Error(message)
        pushDebug("ERROR: $message")
    }

    private fun mapColorThemeToApi(id: String?): String? {
        if (id.isNullOrBlank()) return null
        val raw = id.removePrefix("color_")
        return raw.replace('_', '-').lowercase()
    }

    private fun mapHairToApi(id: String): String {
        val raw = id.removePrefix("hair_")
        return when (raw) {
            "short" -> "short"
            "long" -> "long"
            "curly" -> "curly"
            "medium_curls" -> "curly"
            "short_spiky" -> "short spiky"
            "braided" -> "braided"
            "ponytail" -> "ponytail"
            "bun" -> "bun"
            "bald" -> "bald"
            else -> raw.replace('_', ' ').lowercase()
        }
    }

    private fun mapGestureToApi(id: String): String {
        // Backend currently expects whatever iOS sends; keep stable by removing known prefix.
        return id.removePrefix("hand_")
    }

    fun generateAddFamilyMemoji(selections: Map<Int, String>) {
        _memojiState.value = MemojiGenState.Loading
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                _memojiState.value = MemojiGenState.Error("Not signed in")
                return@launch
            }

            val familyType = selections[0].orEmpty()
            val gesture = selections[1].orEmpty()
            val hair = selections[2].orEmpty()
            val skinTone = selections[3].orEmpty()
            val accessory = selections[4]
            val colorTheme = selections[5]

            val request = MemojiRequest(
                familyType = familyType,
                gesture = mapGestureToApi(gesture),
                hair = mapHairToApi(hair),
                skinTone = skinTone,
                accessories = accessory?.let { listOf(it) } ?: emptyList(),
                background = "transparent",
                size = "1024x1024",
                model = "gpt-image-1",
                subscriptionTier = "monthly_basic",
                colorTheme = mapColorThemeToApi(colorTheme),
                mood = null
            )

            memojiRepository.generateMemoji(accessToken = accessToken, request = request)
                .fold(
                    onSuccess = { response ->
                        val url = response.resolvedImageUrl
                        if (url.isNullOrBlank()) {
                            _memojiState.value = MemojiGenState.Error("No image URL returned")
                        } else {
                            _memojiState.value = MemojiGenState.Success(url)
                        }
                    },
                    onFailure = { e ->
                        _memojiState.value = MemojiGenState.Error(e.localizedMessage ?: "Memoji generation failed")
                    }
                )
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        Log.d("AuthViewModel", "Google sign-in started")
        pushDebug("Google sign-in started")
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signInWithGoogleIdToken(idToken)
            result.fold(
                onSuccess = { session ->
                    Log.d("AuthViewModel", "Google login success userId=${session.user?.id}, email=${session.user?.email}")
                    pushDebug("Google login success userId=${session.user?.id}")
                    _state.value = AuthState.Success(session, AuthProvider.Google)
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "Google login failed", e)
                    pushDebug("Google login failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    _state.value = AuthState.Error(e.localizedMessage ?: "Google login failed")
                }
            )
        }
    }

    fun signInWithAppleIdToken(idToken: String) {
        Log.d("AuthViewModel", "Apple ID token sign-in started")
        pushDebug("Apple ID token sign-in started")
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signInWithAppleIdToken(idToken)
            result.fold(
                onSuccess = { session ->
                    Log.d(
                        "AuthViewModel",
                        "Apple login success userId=${session.user?.id}, email=${session.user?.email}"
                    )
                    pushDebug("Apple login success userId=${session.user?.id}")
                    _state.value = AuthState.Success(session, AuthProvider.Apple)
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "Apple login failed", e)
                    pushDebug("Apple login failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    _state.value = AuthState.Error(e.localizedMessage ?: "Apple login failed")
                }
            )
        }
    }

    fun signInWithAppleCode(code: String) {
        Log.d("AuthViewModel", "Apple code exchange started")
        pushDebug("Apple code exchange started")
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.exchangeAppleCodeForSession(code)
            result.fold(
                onSuccess = { session ->
                    Log.d("AuthViewModel", "Apple login success userId=${session.user?.id}, email=${session.user?.email}")
                    pushDebug("Apple login success (code) userId=${session.user?.id}")
                    _state.value = AuthState.Success(session, AuthProvider.Apple)
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "Apple login failed", e)
                    pushDebug("Apple login failed (code): ${e.localizedMessage ?: e.javaClass.simpleName}")
                    _state.value = AuthState.Error(e.localizedMessage ?: "Apple login failed")
                }
            )
        }
    }

    fun handleDeepLink(uri: Uri?) {
        if (uri == null) return
        if (uri.scheme != AuthEnv.OAUTH_REDIRECT_SCHEME) return

        Log.d("AuthViewModel", "Deep link received: $uri")
        pushDebug("Deep link received")

        val error = uri.getQueryParameter("error")
        val errorDescription = uri.getQueryParameter("error_description")
        if (!error.isNullOrBlank()) {
            Log.e("AuthViewModel", "Apple redirect error=$error, description=$errorDescription")
            pushDebug("Apple redirect error: ${errorDescription ?: error}")
            _state.value = AuthState.Error(errorDescription ?: error)
            return
        }

        val code = uri.getQueryParameter("code")
        if (!code.isNullOrBlank()) {
            Log.d("AuthViewModel", "Apple redirect contained code; exchanging for session")
            pushDebug("Apple redirect contained code; exchanging")
            signInWithAppleCode(code)
        } else {
            Log.e("AuthViewModel", "Deep link did not contain code or error query parameter")
            pushDebug("Deep link missing code/error")
        }
    }

    fun reset() {
        _state.value = AuthState.Idle
    }

    fun publishCurrentSessionAsLoggedIn(provider: AuthProvider) {
        viewModelScope.launch {
            val session = runCatching { repository.currentSessionOrNull() }.getOrNull()
            if (session == null) {
                Log.e("AuthViewModel", "publishCurrentSessionAsLoggedIn failed: no session")
                pushDebug("Publish session failed: no session")
                setError("Not authenticated")
                return@launch
            }
            Log.d(
                "AuthViewModel",
                "Publishing existing session as logged-in. provider=$provider userId=${session.user?.id}"
            )
            pushDebug("Publishing existing session as logged-in provider=$provider userId=${session.user?.id}")
            _state.value = AuthState.Success(session, provider)
        }
    }

    fun signInAsGuest() {
        Log.d("AuthViewModel", "Guest sign-in started")
        pushDebug("Guest sign-in started")
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val existing = runCatching { repository.currentSessionOrNull() }.getOrNull()
            if (existing != null) {
                Log.d(
                    "AuthViewModel",
                    "Guest sign-in reused existing session userId=${existing.user?.id}, email=${existing.user?.email}"
                )
                pushDebug("Guest sign-in reused existing session userId=${existing.user?.id}")
                _state.value = AuthState.Success(existing, AuthProvider.Guest)
                return@launch
            }

            val result = repository.signInAnonymously()
            result.fold(
                onSuccess = { session ->
                    Log.d(
                        "AuthViewModel",
                        "Guest sign-in success userId=${session.user?.id}, email=${session.user?.email}"
                    )
                    pushDebug("Guest sign-in success userId=${session.user?.id}")
                    _state.value = AuthState.Success(session, AuthProvider.Guest)
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "Guest sign-in failed", e)
                    pushDebug("Guest sign-in failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    _state.value = AuthState.Error(e.localizedMessage ?: "Guest login failed")
                }
            )
        }
    }

    fun ensureAnonymousSession() {
        viewModelScope.launch {
            val existing = runCatching { repository.currentSessionOrNull() }.getOrNull()
            if (existing != null) {
                Log.d("AuthViewModel", "Anonymous session ensure skipped: session already exists userId=${existing.user?.id}")
                pushDebug("ensureAnonymousSession skipped: already has session userId=${existing.user?.id}")
                return@launch
            }

            Log.d("AuthViewModel", "Ensuring anonymous session (guest)")
            pushDebug("Ensuring anonymous session (guest)")
            val result = repository.signInAnonymously()
            result.fold(
                onSuccess = { session ->
                    Log.d(
                        "AuthViewModel",
                        "Anonymous session created userId=${session.user?.id}, email=${session.user?.email}"
                    )
                    pushDebug("Anonymous session created userId=${session.user?.id}")
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "Anonymous session creation failed", e)
                    pushDebug("Anonymous session creation failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    setError(e.localizedMessage ?: "Guest login failed")
                }
            )
        }
    }

    fun syncOnboardingMetadata(step: OnboardingStep) {
        val stage = when (step) {
            OnboardingStep.GET_STARTED,
            OnboardingStep.SIGN_IN_INITIAL,
            OnboardingStep.SIGN_IN_SOCIAL_LOGIN,
            OnboardingStep.SIGN_IN_INVITE_CODE,
            OnboardingStep.SIGN_IN_ENTER_INVITE_CODE,
            OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR,
            OnboardingStep.ADD_FAMILY_WELCOME,
            OnboardingStep.ADD_FAMILY_NAME,
            OnboardingStep.ADD_FAMILY_AVATAR_PICKER,
            OnboardingStep.ADD_FAMILY_AVATAR_GENERATING -> "pre_onboarding"
        }

        viewModelScope.launch {
            val result = repository.syncOnboardingMetadata(step = step.name, stage = stage)
            result.exceptionOrNull()?.let { e ->
                Log.w("AuthViewModel", "syncOnboardingMetadata failed", e)
            }
        }
    }

    fun debugLogCurrentSession(reason: String) {
        viewModelScope.launch {
            val session = runCatching { repository.currentSessionOrNull() }.getOrNull()
            if (session == null) {
                Log.d("AuthViewModel", "[SessionCheck] $reason -> no session")
                pushDebug("[SessionCheck] $reason -> no session")
            } else {
                val provider = session.user?.appMetadata?.get("provider")
                Log.d(
                    "AuthViewModel",
                    "[SessionCheck] $reason -> userId=${session.user?.id}, email=${session.user?.email}, provider=${provider ?: "-"}"
                )
                pushDebug("[SessionCheck] $reason -> userId=${session.user?.id}, provider=${provider ?: "-"}")
            }
        }
    }
}
