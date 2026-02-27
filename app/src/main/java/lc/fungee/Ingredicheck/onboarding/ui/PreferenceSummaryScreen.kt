package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.ui.components.PreferenceCapsuleCard
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

@Composable
fun PreferenceSummaryScreen(
    modifier: Modifier = Modifier.Companion,
    isFamilyFlow: Boolean,
    selectedChipIds: Set<String>,
    onEditSection: (String) -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val steps = OnboardingChipData.foodNotesStepIds

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Greyscale10),
        contentAlignment = Alignment.Companion.TopCenter
    ) {
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.Companion.height(30.dp))
            Text(
                text = if (isFamilyFlow) "Your IngrediFam Food Notes" else "Your Food Notes",
                fontFamily = Nunito,
                fontWeight = FontWeight.Companion.Bold,
                fontSize = 20.sp,
                color = Greyscale150,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier.Companion
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.Companion.height(16.dp))

            LazyColumn(
                modifier = Modifier.Companion

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

        NonDraggableBottomSheet(
            onDismissRequest = {},
            horizontalPaddingEnabled = true
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isFamilyFlow) "All set to join your family!" else "Preferences added successfully!",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Companion.Bold,
                    fontSize = 20.sp,
                    color = Greyscale150,
                    textAlign = TextAlign.Companion.Center,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Text(
                    text = if (isFamilyFlow) {
                        "Your family’s food preferences are already added.You can review them anytime, or edit a specific preference section by tapping Edit."
                    } else {
                        "Your food preferences are saved. You can review them anytime, or edit a specific preference section by tapping Edit."
                    },
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Companion.Normal,
                    fontSize = 14.sp,
                    color = Greyscale120,
                    textAlign = TextAlign.Companion.Center,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                PrimaryButton(
                    title = "Continue",
//                    modifier = Modifier.fillMaxWidth(),
                    takeFullWidth = true,
                    onClick = onContinue
                )
            }
        }
    }
}