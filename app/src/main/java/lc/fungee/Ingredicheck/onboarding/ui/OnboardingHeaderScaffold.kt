package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.EVERYONE_MEMBER_ID
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.onboarding.ui.components.PreferenceCapsuleCard
import lc.fungee.Ingredicheck.onboarding.ui.components.familyPlaceholderColor
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.Secondary200
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.delay

@Composable
internal fun CapsuleEveryoneAvatarSmall() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.everyone_seleted_home_icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
internal fun CapsuleMemberAvatarSmall(member: OnboardingViewModel.FamilyOverviewMember) {
    val avatarRes = OnboardingChipData.avatarResOrNull(member.avatarId)
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when {
            member.generatedAvatarUrl.trim().isNotBlank() -> {
                SubcomposeAsyncImage(
                    model = member.generatedAvatarUrl.trim(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            avatarRes != null -> {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Secondary200)
                )
            }
        }
    }
}

@Composable
internal fun CapsuleChipMemberAvatars(
    memberIds: Set<String>,
    members: List<OnboardingViewModel.FamilyOverviewMember>
) {
    if (memberIds.isEmpty()) return

    val everyoneId = EVERYONE_MEMBER_ID
    val hasEveryone = memberIds.contains(everyoneId)
    val concreteMemberIds = memberIds.filter { it != everyoneId }.toSet()
    val concreteMembers = members.filter { concreteMemberIds.contains(it.id) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(-8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        concreteMembers.forEach { m ->
            CapsuleMemberAvatarSmall(member = m)
        }
        if (hasEveryone) {
            CapsuleEveryoneAvatarSmall()
        }
    }
}

@Composable
internal fun OnboardingAllergyBackground(
    dynamicStepsLoaded: Boolean,
    allergySteps: List<CapsuleStep>,
    showPreferenceSummary: Boolean,
    allergyStepIndex: Int,
    onAllergyStepIndexChange: (Int) -> Unit,
    selectedAllergies: List<String>,
    selectedAllergyMemberId: String,
    showSummaryScreen: Boolean,
    showFineTuneDecision: Boolean,
    onShowFineTuneDecisionChange: (Boolean) -> Unit,
    selectedAllergiesByMember: Map<String, Set<String>>,
    familyOverviewMembers: List<OnboardingViewModel.FamilyOverviewMember>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        if (dynamicStepsLoaded && allergySteps.isNotEmpty() && !showPreferenceSummary) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                val rawProgress =
                    if (allergySteps.size <= 1) 1f
                    else allergyStepIndex.toFloat() / (allergySteps.size - 1).coerceAtLeast(1)
                val animatedProgress by animateFloatAsState(
                    targetValue = rawProgress.coerceIn(0f, 1f),
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "allergyProgress"
                )

                val maxSelectedStepIndex = (0..allergySteps.lastIndex).lastOrNull { stepIndex ->
                    val stepChipIds = OnboardingChipData
                        .chipsForStep(stepIndex)
                        .map { it.id }
                        .toSet()
                    selectedAllergies.any { it in stepChipIds }
                } ?: 0
                val maxReachedAllergyStepIndex = maxOf(allergyStepIndex, maxSelectedStepIndex)

                AnimatedProgressLine(
                    progress = animatedProgress,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (!showSummaryScreen) {
                    CapsuleStepperRow(
                        steps = allergySteps,
                        activeIndex = allergyStepIndex,
                        maxReachedIndex = maxReachedAllergyStepIndex,
                        onStepClick = { clickedIndex ->
                            val clamped = clickedIndex.coerceIn(0, allergySteps.lastIndex)
                            if (clamped <= maxReachedAllergyStepIndex) {
                                onAllergyStepIndexChange(clamped)
                                if (showFineTuneDecision) {
                                    onShowFineTuneDecisionChange(false)
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val hasAnySelections = selectedAllergies.isNotEmpty()
                val maxStepIndex = allergySteps.lastIndex

                val cardSteps: List<Int> = if (hasAnySelections) {
                    (0..maxStepIndex).filter { stepIndex ->
                        val stepChipIds = OnboardingChipData
                            .chipsForStep(stepIndex)
                            .map { it.id }
                            .toSet()
                        selectedAllergies.any { it in stepChipIds }
                    }
                } else {
                    val upper = minOf(maxStepIndex, 3)
                    (0..upper).toList()
                }

                val cardsListState = rememberLazyListState()

                val activeMemberId = selectedAllergyMemberId
                val everyoneIdCaps = EVERYONE_MEMBER_ID
                val activeMember = familyOverviewMembers
                    .firstOrNull { it.id == activeMemberId }

                val trailingAvatarContent: (@Composable () -> Unit)? =
                    when {
                        activeMemberId == everyoneIdCaps -> {
                            { CapsuleEveryoneAvatarSmall() }
                        }

                        activeMember != null -> {
                            { CapsuleMemberAvatarSmall(activeMember) }
                        }

                        else -> null
                    }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    state = cardsListState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    userScrollEnabled = hasAnySelections
                ) {
                    items(cardSteps) { stepIndex ->
                        val stepChipIds = OnboardingChipData
                            .chipsForStep(stepIndex)
                            .map { it.id }
                            .toSet()
                        val selectedChipsForStep = selectedAllergies
                            .filter { it in stepChipIds }
                            .toSet()

                        PreferenceCapsuleCard(
                            modifier = Modifier.fillMaxWidth(),
                            selectedChipIds = if (hasAnySelections) {
                                selectedChipsForStep
                            } else {
                                emptySet()
                            },
                            sectionTitle = allergySteps
                                .getOrNull(stepIndex)
                                ?.title ?: "Step ${stepIndex + 1}",
                            sectionIconRes = allergySteps
                                .getOrNull(stepIndex)
                                ?.iconRes ?: R.drawable.ic_step_allergies,
                            trailingAvatarsForChip = { chipId ->
                                val memberIds = selectedAllergiesByMember
                                    .mapNotNull { (memberKey, chips) ->
                                        if (chips.contains(chipId)) memberKey else null
                                    }
                                    .toSet()
                                if (memberIds.isEmpty()) {
                                    null
                                } else {
                                    {
                                        CapsuleChipMemberAvatars(
                                            memberIds = memberIds,
                                            members = familyOverviewMembers
                                        )
                                    }
                                }
                            }
                        )
                    }

                    if (hasAnySelections) {
                        item {
                            Spacer(modifier = Modifier.height(140.dp))
                        }
                    }
                }

                LaunchedEffect(allergyStepIndex, cardSteps, hasAnySelections) {
                    if (!hasAnySelections || cardSteps.isEmpty()) return@LaunchedEffect

                    val layoutInfo = cardsListState.layoutInfo
                    if (layoutInfo.totalItemsCount <= layoutInfo.visibleItemsInfo.size) {
                        return@LaunchedEffect
                    }

                    val healthConditionsIndex = allergySteps.indexOfFirst { it.id == "healthConditions" }
                        .takeIf { it >= 0 } ?: 2

                    val hasLaterCards = cardSteps.any { it >= healthConditionsIndex }
                    if (allergyStepIndex < healthConditionsIndex && !hasLaterCards) {
                        return@LaunchedEffect
                    }

                    val clampedStep = allergyStepIndex.coerceIn(0, allergySteps.lastIndex)

                    val currentStepChipIds = OnboardingChipData
                        .chipsForStep(clampedStep)
                        .map { it.id }
                        .toSet()
                    val stepHasSelection = selectedAllergies.any { it in currentStepChipIds }
                    if (!stepHasSelection) return@LaunchedEffect

                    val targetIndex = cardSteps.indexOf(clampedStep)
                    if (targetIndex >= 0) {
                        cardsListState.animateScrollToItem(targetIndex)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

