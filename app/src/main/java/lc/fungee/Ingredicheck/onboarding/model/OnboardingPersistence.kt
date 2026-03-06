package lc.fungee.Ingredicheck.onboarding.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding")

class OnboardingPersistence(
    private val context: Context
) {
    private companion object {
        private val KEY_CURRENT_STEP = stringPreferencesKey("onboarding_current_step")
        private val KEY_STEP_HISTORY = stringPreferencesKey("onboarding_step_history")
        private val KEY_ADD_FAMILY_NAME = stringPreferencesKey("onboarding_add_family_name")
        private val KEY_ADD_FAMILY_AVATAR_ID = stringPreferencesKey("onboarding_add_family_avatar_id")
        private val KEY_ADD_FAMILY_AVATAR_SELECTIONS = stringPreferencesKey("onboarding_add_family_avatar_selections")
        private val KEY_ADD_FAMILY_GENERATED_AVATAR_URL = stringPreferencesKey("onboarding_add_family_generated_avatar_url")
        private val KEY_MEMOJI_GENERATION_COMPLETED = booleanPreferencesKey("onboarding_memoji_generation_completed")
        private val KEY_FAMILY_OVERVIEW_MEMBERS = stringPreferencesKey("onboarding_family_overview_members")
        private val KEY_SELECTED_ALLERGIES_BY_MEMBER = stringPreferencesKey("onboarding_selected_allergies_by_member")
        private val KEY_SELECTED_ALLERGY_MEMBER_ID = stringPreferencesKey("onboarding_selected_allergy_member_id")
        private val KEY_ALLERGY_STEP_INDEX = stringPreferencesKey("onboarding_allergy_step_index")
        private val KEY_ALLERGY_PHASE = stringPreferencesKey("onboarding_allergy_phase")
        private val KEY_CACHED_FAMILY = stringPreferencesKey("onboarding_cached_family")

        /** Sub-phase within ADD_FAMILY_ALLERGIES for restore-after-kill. */
        const val ALLERGY_PHASE_CHIPS = "chips"
        const val ALLERGY_PHASE_SUMMARY_ROBOT = "summary_robot"
        const val ALLERGY_PHASE_CHAT_INTRO = "chat_intro"
        const val ALLERGY_PHASE_CHAT_CONVERSATION = "chat_conversation"
        const val ALLERGY_PHASE_PREFERENCE_SUMMARY = "preference_summary"
    }

    data class RestoredAllergyState(
        val selectedAllergiesByMember: Map<String, Set<String>>,
        val selectedAllergyMemberId: String,
        val allergyStepIndex: Int,
        val allergyPhase: String
    )

    data class SavedState(
        val currentStep: OnboardingStep,
        val history: List<String>
    )

    data class AddFamilyState(
        val name: String,
        val avatarId: String,
        val avatarSelections: Map<Int, String>,
        val generatedAvatarUrl: String,
        val memojiGenerationCompleted: Boolean,
        val familyOverviewMembers: List<AddFamilyOverviewMember>
    )

    @Serializable
    data class AddFamilyOverviewMember(
        val id: String = "",
        val name: String,
        val avatarId: String,
        val generatedAvatarUrl: String,
        val joined: Boolean,
        val backgroundColorId: String,
        val colorHex: String = ""
    )

    val savedStateFlow: Flow<SavedState> = context.onboardingDataStore.data
        .map { prefs ->
            val step = prefs[KEY_CURRENT_STEP]?.let { runCatching { OnboardingStep.valueOf(it) }.getOrNull() }
                ?: OnboardingStep.GET_STARTED
            val history = prefs[KEY_STEP_HISTORY]
                ?.takeIf { it.isNotBlank() }
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
            SavedState(currentStep = step, history = history)
        }
        .distinctUntilChanged()

    suspend fun setSavedState(currentStep: OnboardingStep, history: List<String>) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_CURRENT_STEP] = currentStep.name
            prefs[KEY_STEP_HISTORY] = history.joinToString("|")
        }
    }

    suspend fun getAddFamilyState(): AddFamilyState {
        val prefs = context.onboardingDataStore.data.first()
        val name = prefs[KEY_ADD_FAMILY_NAME].orEmpty()
        val avatarId = prefs[KEY_ADD_FAMILY_AVATAR_ID].orEmpty()
        val avatarSelectionsJson = prefs[KEY_ADD_FAMILY_AVATAR_SELECTIONS].orEmpty()
        val avatarSelections = if (avatarSelectionsJson.isNotBlank()) {
            runCatching {
                Json.decodeFromString<Map<String, String>>(avatarSelectionsJson)
                    .mapKeys { it.key.toIntOrNull() ?: -1 }
                    .filter { it.key >= 0 }
            }.getOrElse { emptyMap() }
        } else emptyMap()
        val generatedAvatarUrl = prefs[KEY_ADD_FAMILY_GENERATED_AVATAR_URL].orEmpty()
        val memojiGenerationCompleted = prefs[KEY_MEMOJI_GENERATION_COMPLETED] ?: false
        val membersJson = prefs[KEY_FAMILY_OVERVIEW_MEMBERS].orEmpty()
        val familyOverviewMembers = if (membersJson.isNotBlank()) {
            runCatching {
                Json.decodeFromString<List<AddFamilyOverviewMember>>(membersJson)
            }.getOrElse { emptyList() }
        } else emptyList()
        return AddFamilyState(
            name = name,
            avatarId = avatarId,
            avatarSelections = avatarSelections,
            generatedAvatarUrl = generatedAvatarUrl,
            memojiGenerationCompleted = memojiGenerationCompleted,
            familyOverviewMembers = familyOverviewMembers
        )
    }

    suspend fun setAddFamilyState(
        name: String,
        avatarId: String,
        avatarSelections: Map<Int, String>,
        generatedAvatarUrl: String,
        memojiGenerationCompleted: Boolean,
        familyOverviewMembers: List<AddFamilyOverviewMember>
    ) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_ADD_FAMILY_NAME] = name
            prefs[KEY_ADD_FAMILY_AVATAR_ID] = avatarId
            prefs[KEY_ADD_FAMILY_AVATAR_SELECTIONS] = Json.encodeToString(
                avatarSelections.mapKeys { it.key.toString() }
            )
            prefs[KEY_ADD_FAMILY_GENERATED_AVATAR_URL] = generatedAvatarUrl
            prefs[KEY_MEMOJI_GENERATION_COMPLETED] = memojiGenerationCompleted
            prefs[KEY_FAMILY_OVERVIEW_MEMBERS] = Json.encodeToString(familyOverviewMembers)
        }
    }

    /**
     * Save allergy selections state (selected chips/cards per member and current step index).
     */
    suspend fun setAllergySelectionsState(
        selectedAllergiesByMember: Map<String, Set<String>>,
        selectedAllergyMemberId: String,
        allergyStepIndex: Int
    ) {
        Log.d(
            "OnboardingAllergies",
            "[PERSIST] setAllergySelectionsState selections=$selectedAllergiesByMember " +
                "selectedMember=$selectedAllergyMemberId stepIndex=$allergyStepIndex"
        )
        context.onboardingDataStore.edit { prefs ->
            // Convert Map<String, Set<String>> to JSON
            val selectionsJson = Json.encodeToString(
                selectedAllergiesByMember.mapValues { it.value.toList() }
            )
            prefs[KEY_SELECTED_ALLERGIES_BY_MEMBER] = selectionsJson
            prefs[KEY_SELECTED_ALLERGY_MEMBER_ID] = selectedAllergyMemberId
            prefs[KEY_ALLERGY_STEP_INDEX] = allergyStepIndex.toString()
        }
    }

    /**
     * Get allergy selections state (selected chips/cards per member, step index, and allergy sub-phase).
     */
    suspend fun getAllergySelectionsState(): RestoredAllergyState {
        val prefs = context.onboardingDataStore.data.first()
        val selectionsJson = prefs[KEY_SELECTED_ALLERGIES_BY_MEMBER].orEmpty()
        val selectedAllergiesByMember = if (selectionsJson.isNotBlank()) {
            runCatching {
                Json.decodeFromString<Map<String, List<String>>>(selectionsJson)
                    .mapValues { it.value.toSet() }
            }.getOrElse { emptyMap() }
        } else emptyMap()
        val selectedAllergyMemberId = prefs[KEY_SELECTED_ALLERGY_MEMBER_ID].orEmpty()
        val allergyStepIndex = prefs[KEY_ALLERGY_STEP_INDEX]?.toIntOrNull() ?: 0
        val allergyPhase = prefs[KEY_ALLERGY_PHASE].orEmpty().ifBlank { ALLERGY_PHASE_CHIPS }
        Log.d(
            "OnboardingAllergies",
            "[RESTORE] getAllergySelectionsState selections=$selectedAllergiesByMember " +
                "selectedMember=$selectedAllergyMemberId stepIndex=$allergyStepIndex phase=$allergyPhase " +
                "jsonLength=${selectionsJson.length}"
        )
        return RestoredAllergyState(
            selectedAllergiesByMember = selectedAllergiesByMember,
            selectedAllergyMemberId = selectedAllergyMemberId,
            allergyStepIndex = allergyStepIndex,
            allergyPhase = allergyPhase
        )
    }

    /**
     * Save the current allergy sub-phase (chips / summary_robot / chat_intro / chat_conversation / preference_summary)
     * so the user lands on the correct screen after process kill.
     */
    suspend fun setAllergyPhase(phase: String) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_ALLERGY_PHASE] = phase
        }
    }

    suspend fun getCachedFamily(): String? {
        val prefs = context.onboardingDataStore.data.first()
        return prefs[KEY_CACHED_FAMILY]
    }

    suspend fun setCachedFamily(familyJson: String) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_CACHED_FAMILY] = familyJson
        }
    }
}
