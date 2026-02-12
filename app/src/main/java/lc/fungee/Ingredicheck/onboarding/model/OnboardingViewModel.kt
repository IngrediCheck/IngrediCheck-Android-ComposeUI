package lc.fungee.Ingredicheck.onboarding.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val persistence: OnboardingPersistence
) : ViewModel() {

    private companion object {
        private const val TAG = "OnboardingViewModel"
        private const val KEY_CURRENT_STEP = "onboarding_current_step"
        private const val KEY_STEP_HISTORY = "onboarding_step_history"
        private const val KEY_INVITE_CODE = "onboarding_invite_code"
        private const val KEY_ADD_FAMILY_NAME = "onboarding_add_family_name"
        private const val KEY_ADD_FAMILY_AVATAR = "onboarding_add_family_avatar"
        private const val KEY_ADD_FAMILY_AVATAR_SELECTIONS = "onboarding_add_family_avatar_selections"
        private const val KEY_ADD_FAMILY_GENERATED_AVATAR_URL = "onboarding_add_family_generated_avatar_url"
        private const val KEY_MEMOJI_GENERATION_COMPLETED = "onboarding_memoji_generation_completed"
        private const val KEY_ADD_FAMILY_DRAFT_ID = "onboarding_add_family_draft_id"
    }

    data class FamilyOverviewMember(
        val id: String,
        val name: String,
        val avatarId: String,
        val generatedAvatarUrl: String,
        val joined: Boolean,
        val backgroundColorId: String,
        val colorHex: String,
        val invitePending: Boolean = false
    )

    private fun pastelColorHex(seed: String): String {
        val palette = listOf(
            "#FFB3BA",
            "#FFDFBA",
            "#FFFFBA",
            "#BAFFC9",
            "#BAE1FF",
            "#E0BBE4",
            "#FFCCCB",
            "#B4E4FF",
            "#C7CEEA",
            "#FFE5B4"
        )
        val idx = kotlin.math.abs(seed.hashCode()) % palette.size
        return palette[idx]
    }

    private var currentStepState: OnboardingStep by mutableStateOf(
        savedStateHandle.get<String>(KEY_CURRENT_STEP)?.let(OnboardingStep::valueOf)
            ?: OnboardingStep.GET_STARTED
    )

    private var hasRestoredState: Boolean by mutableStateOf(false)

    val isRestored: Boolean
        get() = hasRestoredState

    // When non-null, indicates we are editing an existing member in the
    // family overview instead of adding a new one.
    var editingMemberId: String? by mutableStateOf(null)

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
        viewModelScope.launch {
            val persisted = persistence.getAddFamilyState()
            addFamilyNameState = persisted.name
            savedStateHandle[KEY_ADD_FAMILY_NAME] = persisted.name
            addFamilyAvatarState = persisted.avatarId
            savedStateHandle[KEY_ADD_FAMILY_AVATAR] = persisted.avatarId
            addFamilyAvatarSelectionsState = HashMap(persisted.avatarSelections)
            savedStateHandle[KEY_ADD_FAMILY_AVATAR_SELECTIONS] = addFamilyAvatarSelectionsState
            addFamilyGeneratedAvatarUrlState = persisted.generatedAvatarUrl
            savedStateHandle[KEY_ADD_FAMILY_GENERATED_AVATAR_URL] = persisted.generatedAvatarUrl
            memojiGenerationCompletedState = persisted.memojiGenerationCompleted
            savedStateHandle[KEY_MEMOJI_GENERATION_COMPLETED] = persisted.memojiGenerationCompleted
            familyOverviewMembers.clear()
            familyOverviewMembers.addAll(
                persisted.familyOverviewMembers.map { m ->
                    FamilyOverviewMember(
                        id = m.id.ifBlank { UUID.randomUUID().toString() },
                        name = m.name,
                        avatarId = m.avatarId,
                        generatedAvatarUrl = m.generatedAvatarUrl,
                        joined = m.joined,
                        backgroundColorId = m.backgroundColorId,
                        colorHex = m.colorHex.ifBlank {
                            pastelColorHex(m.id.ifBlank { m.name })
                        },
                        invitePending = false
                    )
                }
            )
        }
    }

    private fun persistAddFamilyState() {
        viewModelScope.launch {
            persistence.setAddFamilyState(
                name = addFamilyNameState,
                avatarId = addFamilyAvatarState,
                avatarSelections = addFamilyAvatarSelectionsState,
                generatedAvatarUrl = addFamilyGeneratedAvatarUrlState,
                memojiGenerationCompleted = memojiGenerationCompletedState,
                familyOverviewMembers = familyOverviewMembers.map { m ->
                    OnboardingPersistence.AddFamilyOverviewMember(
                        id = m.id,
                        name = m.name,
                        avatarId = m.avatarId,
                        generatedAvatarUrl = m.generatedAvatarUrl,
                        joined = m.joined,
                        backgroundColorId = m.backgroundColorId,
                        colorHex = m.colorHex
                    )
                }
            )
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
        savedStateHandle[KEY_ADD_FAMILY_NAME] = ""
        savedStateHandle[KEY_ADD_FAMILY_AVATAR] = ""
        savedStateHandle[KEY_ADD_FAMILY_AVATAR_SELECTIONS] = hashMapOf<Int, String>()
        savedStateHandle[KEY_ADD_FAMILY_GENERATED_AVATAR_URL] = ""
        savedStateHandle[KEY_MEMOJI_GENERATION_COMPLETED] = false
        currentStepState = OnboardingStep.GET_STARTED
        inviteCodeState = ""
        addFamilyNameState = ""
        addFamilyAvatarState = ""
        addFamilyAvatarSelectionsState = hashMapOf()
        addFamilyGeneratedAvatarUrlState = ""
        memojiGenerationCompletedState = false
        familyOverviewMembers.clear()

        viewModelScope.launch {
            persistence.setSavedState(currentStep = OnboardingStep.GET_STARTED, history = emptyList())
            persistence.setAddFamilyState(
                name = "",
                avatarId = "",
                avatarSelections = emptyMap(),
                generatedAvatarUrl = "",
                memojiGenerationCompleted = false,
                familyOverviewMembers = emptyList()
            )
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
            persistAddFamilyState()
        }

    private var addFamilyAvatarState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_AVATAR) ?: ""
    )

    var addFamilyAvatarId: String
        get() = addFamilyAvatarState
        set(value) {
            addFamilyAvatarState = value
            savedStateHandle[KEY_ADD_FAMILY_AVATAR] = value
            persistAddFamilyState()
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
            persistAddFamilyState()
        }

    private var addFamilyGeneratedAvatarUrlState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_GENERATED_AVATAR_URL) ?: ""
    )

    var addFamilyGeneratedAvatarUrl: String
        get() = addFamilyGeneratedAvatarUrlState
        set(value) {
            addFamilyGeneratedAvatarUrlState = value
            savedStateHandle[KEY_ADD_FAMILY_GENERATED_AVATAR_URL] = value
            persistAddFamilyState()
        }

    private var memojiGenerationCompletedState: Boolean by mutableStateOf(
        savedStateHandle.get<Boolean>(KEY_MEMOJI_GENERATION_COMPLETED) ?: false
    )

    private var addFamilyDraftIdState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_ADD_FAMILY_DRAFT_ID) ?: UUID.randomUUID().toString()
    )

    var memojiGenerationCompleted: Boolean
        get() = memojiGenerationCompletedState
        set(value) {
            memojiGenerationCompletedState = value
            savedStateHandle[KEY_MEMOJI_GENERATION_COMPLETED] = value
            persistAddFamilyState()
        }

    val familyOverviewMembers = mutableStateListOf<FamilyOverviewMember>()

    fun currentDraftFamilyMemberOrNull(): FamilyOverviewMember? {
        val trimmedName = addFamilyName.trim()
        if (trimmedName.isBlank()) return null

        val avatarId = addFamilyAvatarId.trim()
        val generatedUrl = addFamilyGeneratedAvatarUrl.trim()
        val colorId = addFamilyAvatarSelections[5].orEmpty()

        val id = addFamilyDraftIdState

        return FamilyOverviewMember(
            id = id,
            name = trimmedName,
            avatarId = avatarId,
            generatedAvatarUrl = generatedUrl,
            joined = familyOverviewMembers.isEmpty(),
            backgroundColorId = colorId,
            colorHex = pastelColorHex(id),
            invitePending = false
        )
    }

    fun commitDraftFamilyMember() {
        val draft = currentDraftFamilyMemberOrNull() ?: return
        Log.d(
            TAG,
            "UI commitDraftFamilyMember (local only): id=${draft.id}, name=${draft.name}, avatarId=${draft.avatarId}, generatedUrl=${draft.generatedAvatarUrl.isNotBlank()}, joined=${draft.joined}"
        )
        familyOverviewMembers.add(draft)
        addFamilyDraftIdState = UUID.randomUUID().toString()
        savedStateHandle[KEY_ADD_FAMILY_DRAFT_ID] = addFamilyDraftIdState
        persistAddFamilyState()
    }

    fun clearAddFamilyDraft() {
        Log.d(TAG, "UI clearAddFamilyDraft")
        addFamilyName = ""
        addFamilyAvatarId = ""
        addFamilyGeneratedAvatarUrl = ""
        addFamilyAvatarSelections = emptyMap()

        addFamilyDraftIdState = UUID.randomUUID().toString()
        savedStateHandle[KEY_ADD_FAMILY_DRAFT_ID] = addFamilyDraftIdState
    }

    fun regenerateFamilyOverviewMemberIds(): Map<String, String> {
        if (familyOverviewMembers.isEmpty()) return emptyMap()

        Log.d(TAG, "UI regenerateFamilyOverviewMemberIds (local only)")

        val idMap = LinkedHashMap<String, String>()
        val updated = familyOverviewMembers.map { m ->
            val newId = UUID.randomUUID().toString()
            idMap[m.id] = newId
            m.copy(
                id = newId,
                colorHex = pastelColorHex(newId)
            )
        }

        familyOverviewMembers.clear()
        familyOverviewMembers.addAll(updated)

        addFamilyDraftIdState = UUID.randomUUID().toString()
        savedStateHandle[KEY_ADD_FAMILY_DRAFT_ID] = addFamilyDraftIdState

        persistAddFamilyState()

        return idMap
    }

    fun setInvitePending(id: String, pending: Boolean) {
        val idx = familyOverviewMembers.indexOfFirst { it.id == id }
        if (idx == -1) return
        val current = familyOverviewMembers[idx]
        familyOverviewMembers[idx] = current.copy(invitePending = pending)
        persistAddFamilyState()
    }

    fun updateFamilyOverviewMember(updated: FamilyOverviewMember) {
        val idx = familyOverviewMembers.indexOfFirst { it.id == updated.id }
        if (idx == -1) return
        familyOverviewMembers[idx] = updated
        persistAddFamilyState()
    }
}
