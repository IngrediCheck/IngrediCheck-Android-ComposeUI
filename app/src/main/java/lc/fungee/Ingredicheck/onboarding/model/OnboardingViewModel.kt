package lc.fungee.Ingredicheck.onboarding.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val persistence: OnboardingPersistence
) : ViewModel() {

    private companion object {
        private const val KEY_CURRENT_STEP = "onboarding_current_step"
        private const val KEY_STEP_HISTORY = "onboarding_step_history"
        private const val KEY_INVITE_CODE = "onboarding_invite_code"
        private const val KEY_ADD_FAMILY_NAME = "onboarding_add_family_name"
        private const val KEY_ADD_FAMILY_AVATAR = "onboarding_add_family_avatar"
        private const val KEY_ADD_FAMILY_AVATAR_SELECTIONS = "onboarding_add_family_avatar_selections"
        private const val KEY_ADD_FAMILY_GENERATED_AVATAR_URL = "onboarding_add_family_generated_avatar_url"
    }

    private var currentStepState: OnboardingStep by mutableStateOf(
        savedStateHandle.get<String>(KEY_CURRENT_STEP)?.let(OnboardingStep::valueOf)
            ?: OnboardingStep.GET_STARTED
    )

    private var hasRestoredState: Boolean by mutableStateOf(false)

    val isRestored: Boolean
        get() = hasRestoredState

    init {
        viewModelScope.launch {
            persistence.savedStateFlow.collect { state ->
                val history = ArrayList(state.history)
                savedStateHandle[KEY_STEP_HISTORY] = history

                if (state.currentStep != currentStepState) {
                    savedStateHandle[KEY_CURRENT_STEP] = state.currentStep.name
                    currentStepState = state.currentStep
                }

                if (!hasRestoredState) {
                    hasRestoredState = true
                }
            }
        }
    }

    val currentStep: OnboardingStep
        get() = currentStepState

    fun canGoBack(): Boolean = history().isNotEmpty()

    fun navigateTo(step: OnboardingStep) {
        if (step == currentStep) return

        val stack = history()
        stack.add(currentStep.name)
        savedStateHandle[KEY_STEP_HISTORY] = stack
        savedStateHandle[KEY_CURRENT_STEP] = step.name
        currentStepState = step

        viewModelScope.launch {
            persistence.setSavedState(currentStep = step, history = stack)
        }
    }

    fun back() {
        val stack = history()
        if (stack.isEmpty()) return

        val previous = stack.removeAt(stack.lastIndex)
        savedStateHandle[KEY_STEP_HISTORY] = stack
        savedStateHandle[KEY_CURRENT_STEP] = previous
        currentStepState = OnboardingStep.valueOf(previous)

        viewModelScope.launch {
            persistence.setSavedState(currentStep = currentStepState, history = stack)
        }
    }

    fun reset() {
        savedStateHandle[KEY_STEP_HISTORY] = arrayListOf<String>()
        savedStateHandle[KEY_CURRENT_STEP] = OnboardingStep.GET_STARTED.name
        savedStateHandle[KEY_INVITE_CODE] = ""
        currentStepState = OnboardingStep.GET_STARTED
        inviteCodeState = ""

        viewModelScope.launch {
            persistence.setSavedState(currentStep = OnboardingStep.GET_STARTED, history = emptyList())
        }
    }

    private fun history(): ArrayList<String> {
        return savedStateHandle.get<ArrayList<String>>(KEY_STEP_HISTORY) ?: arrayListOf()
    }

    private var inviteCodeState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_INVITE_CODE) ?: ""
    )

    var inviteCode: String
        get() = inviteCodeState
        set(value) {
            inviteCodeState = value
            savedStateHandle[KEY_INVITE_CODE] = value
            inviteCodeError = false
        }

    var inviteCodeError by mutableStateOf(false)

    private var addFamilyNameState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_NAME) ?: ""
    )

    var addFamilyName: String
        get() = addFamilyNameState
        set(value) {
            addFamilyNameState = value
            savedStateHandle[KEY_ADD_FAMILY_NAME] = value
        }

    private var addFamilyAvatarState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_AVATAR) ?: ""
    )

    var addFamilyAvatarId: String
        get() = addFamilyAvatarState
        set(value) {
            addFamilyAvatarState = value
            savedStateHandle[KEY_ADD_FAMILY_AVATAR] = value
        }

    private var addFamilyAvatarSelectionsState: HashMap<Int, String> by mutableStateOf(
        savedStateHandle.get<HashMap<Int, String>>(KEY_ADD_FAMILY_AVATAR_SELECTIONS)
            ?: hashMapOf<Int, String>().apply {
                val legacy = savedStateHandle.get<String>(KEY_ADD_FAMILY_AVATAR).orEmpty()
                if (legacy.isNotBlank()) put(0, legacy)
            }
    )

    var addFamilyAvatarSelections: Map<Int, String>
        get() = addFamilyAvatarSelectionsState
        set(value) {
            val normalized = value
                .asSequence()
                .map { (k, v) -> k to v.trim() }
                .filter { (k, v) -> k >= 0 && v.isNotBlank() }
                .distinctBy { it.first }
                .toMap()

            addFamilyAvatarSelectionsState = HashMap(normalized)
            savedStateHandle[KEY_ADD_FAMILY_AVATAR_SELECTIONS] = addFamilyAvatarSelectionsState

            val familyMember = normalized[0].orEmpty()
            addFamilyAvatarState = familyMember
            savedStateHandle[KEY_ADD_FAMILY_AVATAR] = familyMember
        }

    private var addFamilyGeneratedAvatarUrlState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_GENERATED_AVATAR_URL) ?: ""
    )

    var addFamilyGeneratedAvatarUrl: String
        get() = addFamilyGeneratedAvatarUrlState
        set(value) {
            addFamilyGeneratedAvatarUrlState = value
            savedStateHandle[KEY_ADD_FAMILY_GENERATED_AVATAR_URL] = value
        }
}
