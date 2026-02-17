package lc.fungee.Ingredicheck.onboarding.model

/**
 * Single data class for all onboarding flow UI state exposed by [OnboardingViewModel].
 * Keeps the View layer decoupled from individual state vars and aligns with MVVM.
 */
data class OnboardingUiState(
    val currentStep: OnboardingStep,
    val stepHistory: List<String>,
    val inviteCode: String,
    val inviteCodeError: Boolean,
    val addFamilyName: String,
    val addFamilyAvatarId: String,
    val addFamilyAvatarSelections: Map<Int, String>,
    val addFamilyGeneratedAvatarUrl: String,
    val memojiGenerationCompleted: Boolean,
    val familyOverviewMembers: List<FamilyOverviewMember>,
    val editingMemberId: String?,
    val addFamilyDraftId: String,
    val isRestored: Boolean
) {
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
}
