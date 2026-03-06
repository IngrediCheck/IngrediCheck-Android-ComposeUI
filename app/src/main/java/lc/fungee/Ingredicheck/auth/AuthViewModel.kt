package lc.fungee.Ingredicheck.auth

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.BuildConfig
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.family.FamilyDto
import lc.fungee.Ingredicheck.family.FamilyRepository
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.memoji.MemojiRepository
import lc.fungee.Ingredicheck.memoji.MemojiRequestMapper
import lc.fungee.Ingredicheck.family.FamilyMemberDto
import lc.fungee.Ingredicheck.dietary.DietaryPreferenceRepository
import lc.fungee.Ingredicheck.family.UpdateMemberRequest
import lc.fungee.Ingredicheck.foodnotes.FoodNotesRepository
import lc.fungee.Ingredicheck.foodnotes.FoodNotesUseCase


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
    private val familyRepository = FamilyRepository()
    private val dietaryPreferenceRepository = DietaryPreferenceRepository()
    private val foodNotesUseCase = FoodNotesUseCase(
        scope = viewModelScope,
        authRepository = repository,
        foodNotesRepository = FoodNotesRepository(),
        currentFamilyProvider = { _currentFamily.value },
        debugLogger = { message -> pushDebug(message) }
    )

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state


    private val _debugLog = MutableStateFlow<List<String>>(emptyList())
    val debugLog: StateFlow<List<String>> = _debugLog.asStateFlow()

    private val _currentFamily = MutableStateFlow<FamilyDto?>(null)
    val currentFamily: StateFlow<FamilyDto?> = _currentFamily.asStateFlow()

    private val _isFamilyLoading = MutableStateFlow<Boolean>(true)
    val isFamilyLoading: StateFlow<Boolean> = _isFamilyLoading.asStateFlow()

    private val _memojiState = MutableStateFlow<MemojiGenState>(MemojiGenState.Idle)
    val memojiState: StateFlow<MemojiGenState> = _memojiState.asStateFlow()

    init {
        // Fetch current family on startup if a session exists
        viewModelScope.launch {
            repository.sessionFlow.collect { session ->
                if (session != null) {
                    Log.d("AuthViewModel", "Session update: Found active session for user=${session.user?.id}")
                    pushDebug("Session update: Active session found, fetching family")
                    loadCurrentFamily()
                } else {
                    Log.d("AuthViewModel", "Session update: No active session")
                    _isFamilyLoading.value = false
                }
            }
        }
    }

    // AI-generated summary of food notes (text), exposed from FoodNotesUseCase.
    val foodNotesSummary: StateFlow<String?> = foodNotesUseCase.foodNotesSummary

    private fun pushDebug(message: String) {
        val line = "${System.currentTimeMillis()} | $message"
        _debugLog.value = (_debugLog.value + line).takeLast(60)
        if (BuildConfig.DEBUG) {
            Log.d("AuthDebug", message)
        }
    }

    /**
     * Sync onboarding fine-tune selections to the backend as one dietary preference (same as iOS).
     * Called when user taps "All Set!" or completes the last preference step.
     * Logs success/failure for debugging.
     */
    fun syncDietaryPreferencesFromOnboarding(preferenceText: String) {
        if (preferenceText.isBlank()) {
            if (BuildConfig.DEBUG) {
                Log.d("AuthDebug", "DietaryPreference: skip sync (empty text)")
            }
            return
        }
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                Log.w("AuthDebug", "DietaryPreference: skip sync (no access token)")
                return@launch
            }
            pushDebug("DietaryPreference: syncing length=${preferenceText.length}")
            val clientActivityId = java.util.UUID.randomUUID().toString()
            val result = dietaryPreferenceRepository.addOrEditDietaryPreference(
                accessToken = accessToken,
                clientActivityId = clientActivityId,
                preferenceText = preferenceText,
                id = null
            )
            result.fold(
                onSuccess = { validationResult ->
                    when (validationResult) {
                        is lc.fungee.Ingredicheck.dietary.PreferenceValidationResult.Success -> {
                            pushDebug("DietaryPreference: sync success id=${validationResult.preference.id}")
                            if (BuildConfig.DEBUG) {
                                Log.d("AuthDebug", "DietaryPreference: sync success id=${validationResult.preference.id}")
                            }
                        }
                        is lc.fungee.Ingredicheck.dietary.PreferenceValidationResult.Failure -> {
                            pushDebug("DietaryPreference: sync failure ${validationResult.explanation}")
                            Log.w("AuthDebug", "DietaryPreference: sync failure ${validationResult.explanation}")
                        }
                    }
                },
                onFailure = { e ->
                    pushDebug("DietaryPreference: sync error ${e.message}")
                    Log.e("AuthDebug", "DietaryPreference: sync error", e)
                }
            )
        }
    }

    /**
     * Delegate food-notes sync and AI summary loading to FoodNotesUseCase so this ViewModel
     * doesn't directly own that domain logic.
     */
    fun syncFoodNotesFromOnboarding(selectedAllergiesByMember: Map<String, Set<String>>) {
        foodNotesUseCase.syncFoodNotesFromOnboarding(selectedAllergiesByMember)
    }

    fun loadFoodNotesSummary(force: Boolean = false) {
        foodNotesUseCase.loadFoodNotesSummary(force)
    }

    /**
     * Fetch the current family from the backend and update the local state.
     */
    fun loadCurrentFamily() {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                Log.d("AuthViewModel", "loadCurrentFamily: Skipping, no access token")
                pushDebug("loadCurrentFamily: No access token, skipping")
                return@launch
            }
            _isFamilyLoading.value = true
            Log.d("AuthViewModel", "loadCurrentFamily: Fetching current family from backend...")
            pushDebug("loadCurrentFamily: Fetching from backend")
            val result = familyRepository.getFamily(accessToken)
            result.fold(
                onSuccess = { family ->
                    _currentFamily.value = family
                    _isFamilyLoading.value = false
                    Log.d("AuthViewModel", "loadCurrentFamily: Success! familyName=${family.name}, selfName=${family.selfMember.name}, avatar=${family.selfMember.imageFileHash}")
                    pushDebug("loadCurrentFamily: Success name=${family.name}")
                },
                onFailure = { e ->
                    _isFamilyLoading.value = false
                    Log.e("AuthViewModel", "loadCurrentFamily: Failed", e)
                    pushDebug("loadCurrentFamily: Failed ${e.localizedMessage ?: e.javaClass.simpleName}")
                    Log.w("AuthDebug", "loadCurrentFamily failed", e)
                }
            )
        }
    }

    /**
     * Update the current family's self member name (used in Just Me \"Meet your profile\" flow).
     * Mirrors iOS MeetYourProfileView.commitPrimaryName behavior for the self member only.
     */
    fun updateSelfMemberName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotBlank()) {
            viewModelScope.launch {
                val accessToken = repository.accessTokenOrNull()
                val snapshot = _currentFamily.value
                if (accessToken.isNullOrBlank() || snapshot == null) return@launch

                val self = snapshot.selfMember
                if (self.name == trimmed) return@launch

                val request = UpdateMemberRequest(
                    name = trimmed,
                    color = self.color,
                    imageFileHash = self.imageFileHash
                )
                Log.d("AuthViewModel", "updateSelfMemberName: Starting editMember for id=${self.id}, newName=$trimmed")
                pushDebug("Family editMember (self) started id=${self.id}, name=$trimmed")
                val result = familyRepository.editMember(
                    accessToken = accessToken,
                    memberId = self.id,
                    request = request
                )
                result.fold(
                    onSuccess = { family ->
                        _currentFamily.value = family
                        Log.d("AuthViewModel", "updateSelfMemberName: Success! Backend now has name=${family.selfMember.name}")
                        pushDebug("Family editMember (self) success name=${family.selfMember.name}")
                    },
                    onFailure = { e ->
                        Log.e("AuthViewModel", "updateSelfMemberName: Failed", e)
                        pushDebug("Family editMember (self) failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    }
                )
            }
        }
    }

    fun updateFamilyName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotBlank()) {
            viewModelScope.launch {
                val accessToken = repository.accessTokenOrNull()
                if (accessToken.isNullOrBlank()) return@launch

                Log.d("AuthViewModel", "updateFamilyName: Starting updateFamily for newName=$trimmed")
                pushDebug("Family updateFamily started name=$trimmed")
                val result = familyRepository.updateFamily(accessToken = accessToken, name = trimmed)
                result.fold(
                    onSuccess = { family ->
                        _currentFamily.value = family
                        Log.d("AuthViewModel", "updateFamilyName: Success! Backend now has family name=${family.name}")
                        pushDebug("Family updateFamily success name=${family.name}")
                    },
                    onFailure = { e ->
                        Log.e("AuthViewModel", "updateFamilyName: Failed", e)
                        pushDebug("Family updateFamily failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                    }
                )
            }
        }
    }

    /**
     * Update the current family's self member avatar (imageFileHash).
     * Used when the user saves a generated or selected avatar in the Just Me "Meet your profile" flow.
     */
    fun updateSelfMemberAvatar(imageFileHash: String?) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            val snapshot = _currentFamily.value
            if (accessToken.isNullOrBlank() || snapshot == null) return@launch

            val self = snapshot.selfMember
            val cleanedHash = imageFileHash?.takeIf { it.isNotBlank() }
            if (self.imageFileHash == cleanedHash) return@launch

            val request = UpdateMemberRequest(
                name = self.name,
                color = self.color,
                imageFileHash = cleanedHash
            )
            Log.d("AuthViewModel", "updateSelfMemberAvatar: Starting editMember for id=${self.id}, newAvatar=$cleanedHash")
            pushDebug("Family editMember (self avatar) started id=${self.id}")
            val result = familyRepository.editMember(
                accessToken = accessToken,
                memberId = self.id,
                request = request
            )
            result.fold(
                onSuccess = { family ->
                    _currentFamily.value = family
                    Log.d("AuthViewModel", "updateSelfMemberAvatar: Success! Backend now has avatar=${family.selfMember.imageFileHash}")
                    pushDebug("Family editMember (self avatar) success")
                },
                onFailure = { e ->
                    Log.e("AuthViewModel", "updateSelfMemberAvatar: Failed", e)
                    pushDebug("Family editMember (self avatar) failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
        }
    }

    fun addFamilyMember(member: FamilyMemberDto, onResult: (Result<FamilyDto>) -> Unit) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                onResult(Result.failure(IllegalStateException("Not signed in")))
                return@launch
            }
            pushDebug("Family addMember started id=${member.id}, name=${member.name}")
            val result = familyRepository.addMember(accessToken = accessToken, member = member)
            result.fold(
                onSuccess = { family ->
                    _currentFamily.value = family
                    val ids = buildList {
                        add(family.selfMember.id)
                        addAll(family.otherMembers.map { it.id })
                    }
                    pushDebug("Family addMember success members=${ids.joinToString(",")}")
                },
                onFailure = { e ->
                    pushDebug("Family addMember failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
            onResult(result)
        }
    }

    fun createFamily(request: CreateFamilyRequest, onResult: (Result<FamilyDto>) -> Unit) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                onResult(Result.failure(IllegalStateException("Not signed in")))
                return@launch
            }
            pushDebug("Family createFamily started name=${request.name} others=${request.otherMembers?.size ?: 0}")
            val result = familyRepository.createFamily(accessToken = accessToken, request = request)
            result.fold(
                onSuccess = { family ->
                    _currentFamily.value = family
                    val ids = buildList {
                        add(family.selfMember.id)
                        addAll(family.otherMembers.map { it.id })
                    }
                    pushDebug("Family createFamily success members=${ids.joinToString(",")}")
                },
                onFailure = { e ->
                    pushDebug("Family createFamily failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
            onResult(result)
        }
    }

    fun joinFamily(inviteCode: String, onResult: (Result<FamilyDto>) -> Unit) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                onResult(Result.failure(IllegalStateException("Not signed in")))
                return@launch
            }
            pushDebug("Family joinFamily started code=$inviteCode")
            val result = familyRepository.joinFamily(accessToken = accessToken, inviteCode = inviteCode)
            result.fold(
                onSuccess = { family ->
                    _currentFamily.value = family
                    pushDebug("Family joinFamily success name=${family.name} self=${family.selfMember.id}")
                },
                onFailure = { e ->
                    pushDebug("Family joinFamily failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
            onResult(result)
        }
    }

    fun leaveFamily(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                onResult(Result.failure(IllegalStateException("Not signed in")))
                return@launch
            }
            pushDebug("Family leaveFamily started")
            val result = familyRepository.leaveFamily(accessToken = accessToken)
            result.fold(
                onSuccess = {
                    _currentFamily.value = null
                    pushDebug("Family leaveFamily success")
                },
                onFailure = { e ->
                    pushDebug("Family leaveFamily failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
            onResult(result)
        }
    }

    fun inviteFamilyMember(memberId: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                onResult(Result.failure(IllegalStateException("Not signed in")))
                return@launch
            }
            val familySnapshot = _currentFamily.value
            if (familySnapshot == null) {
                pushDebug("Family invite started memberId=$memberId but currentFamily is null (no members known client-side)")
            } else {
                val allMembers = buildList {
                    add("self=${familySnapshot.selfMember.id},joined=${familySnapshot.selfMember.joined}")
                    addAll(
                        familySnapshot.otherMembers.map {
                            "other=${it.id},joined=${it.joined ?: false}"
                        }
                    )
                }
                val contains = (familySnapshot.selfMember.id == memberId) ||
                    familySnapshot.otherMembers.any { it.id == memberId }
                pushDebug(
                    "Family invite started memberId=$memberId; inFamily=$contains; members=[${allMembers.joinToString("; ")}]"
                )
            }
            val result = familyRepository.invite(accessToken = accessToken, memberId = memberId)
            result.fold(
                onSuccess = { code ->
                    pushDebug("Family invite success code=$code")
                },
                onFailure = { e ->
                    pushDebug("Family invite failed: ${e.localizedMessage ?: e.javaClass.simpleName}")
                }
            )
            onResult(result)
        }
    }

    fun setError(message: String) {
        _state.value = AuthState.Error(message)
        pushDebug("ERROR: $message")
    }

    /**
     * Used to restore a previously generated memoji URL after process death.
     */
    fun restoreMemojiSuccess(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            _memojiState.value = MemojiGenState.Success(imageUrl)
        }
    }

    fun generateAddFamilyMemoji(selections: Map<Int, String>) {
        _memojiState.value = MemojiGenState.Loading
        viewModelScope.launch {
            val accessToken = repository.accessTokenOrNull()
            if (accessToken.isNullOrBlank()) {
                _memojiState.value = MemojiGenState.Error("Not signed in")
                return@launch
            }

            val request = MemojiRequestMapper.fromSelections(selections)

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
                    // Fetch family after creating anonymous session
                    loadCurrentFamily()
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
            OnboardingStep.ADD_FAMILY_AVATAR_GENERATING,
            OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE,
            OnboardingStep.ADD_FAMILY_EDIT_MEMBER,
            OnboardingStep.FALLING_CAPSULES,
            OnboardingStep.ADD_FAMILY_ALLERGIES -> "pre_onboarding"


            else -> {
                ""
            }
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
