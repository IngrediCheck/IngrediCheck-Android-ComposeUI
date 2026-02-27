package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.ui.components.PreferenceCapsuleCard
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.ui.CapsuleMemberAvatarSmall
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

/**
 * Background summary canvas: shows the list of preference sections and selected chips.
 * This mirrors iOS's `summaryJustMe` / `summaryAddFamily` canvas route.
 */
@Composable
fun PreferenceSummaryBackground(
    modifier: Modifier = Modifier,
    isFamilyFlow: Boolean,
    selectedChipIds: Set<String>,
    members: List<OnboardingViewModel.FamilyOverviewMember> = emptyList(),
    selectedMemberId: String? = null,
    onMemberSelected: (String?) -> Unit = {},
    onEditSection: (String) -> Unit = {}
) {
    val steps = OnboardingChipData.foodNotesStepIds

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Greyscale10)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = if (isFamilyFlow) "Your IngrediFam Food Notes" else "Your Food Notes",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Greyscale150,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Family member capsules row (Just Me + family members) – only for family flow.
        if (isFamilyFlow && members.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(members) { member ->
                    val isSelected = selectedMemberId == member.id

                    Row(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFF91B640) else Color(0xFFF8F8F8),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(66.dp)
                            )
                            .clickable {
                                // Toggle selection: tap again to clear filter and show all.
                                val newId = if (selectedMemberId == member.id) null else member.id
                                onMemberSelected(newId)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CapsuleMemberAvatarSmall(member = member)
                        Text(
                            text = member.name,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Greyscale150,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(steps) { stepId ->
                val stepIndex = steps.indexOf(stepId)
                if (stepIndex >= 0) {
                    val stepChips = OnboardingChipData.chipsForStep(stepIndex)
                    val chipIdsForStep = stepChips.map { it.id }.toSet()
                    val selectedForStep =
                        selectedChipIds.filter { it in chipIdsForStep }.toSet()
                    if (selectedForStep.isNotEmpty()) {
                        PreferenceCapsuleCard(
                            selectedChipIds = selectedForStep,
                            sectionTitle = stepId.replaceFirstChar { it.uppercase() },
                            sectionIconRes = OnboardingChipData.iconResForStepId(stepId),
                            showEditAction = true,
                            onEditClick = { onEditSection(stepId) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom‑sheet content for the preferences‑added success state.
 * This mirrors iOS's `PreferencesAddedSuccessSheet`.
 *
 * NOTE: This composable renders only the inner content; it should be hosted inside
 * `NonDraggableBottomSheet` or `OnboardingShell` depending on context.
 */
@Composable
fun PreferenceSummarySheetContent(
    isFamilyFlow: Boolean,
    onContinue: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isFamilyFlow) "All set to join your family!" else "Preferences added successfully!",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Greyscale150,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isFamilyFlow) {
                    "Your family’s food preferences are already added."
                } else {
                    "Your food preferences are saved. You can review them anytime,"
                },
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "or edit a specific preference section by tapping Edit.",
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        PrimaryButton(
            title = "Continue",
            takeFullWidth = false,
            onClick = onContinue
        )
    }
}

/**
 * Convenience wrapper that combines the background canvas and bottom sheet
 * for use in previews or standalone usage.
 */
@Composable
fun PreferenceSummaryScreen(
    modifier: Modifier = Modifier,
    isFamilyFlow: Boolean,
    selectedChipIds: Set<String>,
    onEditSection: (String) -> Unit = {},
    onContinue: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Greyscale10),
        contentAlignment = Alignment.TopCenter
    ) {
        PreferenceSummaryBackground(
            modifier = Modifier.fillMaxSize(),
            isFamilyFlow = isFamilyFlow,
            selectedChipIds = selectedChipIds,
            onEditSection = onEditSection
        )

        NonDraggableBottomSheet(
            onDismissRequest = {},
            horizontalPaddingEnabled = true
        ) {
            PreferenceSummarySheetContent(
                isFamilyFlow = isFamilyFlow,
                onContinue = onContinue
            )
        }
    }
}
