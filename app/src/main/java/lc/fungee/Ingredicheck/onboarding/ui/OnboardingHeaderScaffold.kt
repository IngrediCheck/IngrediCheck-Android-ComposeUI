package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.EVERYONE_MEMBER_ID
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.onboarding.ui.components.AiSummaryPreferenceCard
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.onboarding.ui.components.PreferenceCapsuleCard
import lc.fungee.Ingredicheck.onboarding.ui.components.familyPlaceholderColor
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.Secondary200
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.delay
import lc.fungee.Ingredicheck.onboarding.data.memberAvatarBackgroundColor
import lc.fungee.Ingredicheck.ui.theme.Greyscale130

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
    // Resolve the same background color used everywhere else for this member:
    // - backgroundColorId (explicit memoji color), or
    // - colorHex (random pastel assigned at creation).
    val memberBgColor = memberAvatarBackgroundColor(member.backgroundColorId, member.colorHex)
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
                        .background(memberBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = member.name.trim().firstOrNull()?.uppercase() ?: "?"
                    Text(
                        text = initial,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = Greyscale130
                    )
                }
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
    familyOverviewMembers: List<OnboardingViewModel.FamilyOverviewMember>,
    summarySelectedMemberId: String? = null,
    onSummaryMemberSelected: (String?) -> Unit = {},
    onEditSection: (String) -> Unit = {},
    aiSummaryText: String? = null,
    bottomInset: Dp = 0.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (showPreferenceSummary) Greyscale10
                else Color(0xFFF2F2F7)
            )
    ) {
        if (dynamicStepsLoaded && allergySteps.isNotEmpty()) {
            if (showPreferenceSummary) {
                // Summary mode: same card list with Edit action; single source of truth for stable UI.
                val summaryChipIds = remember(
                    selectedAllergiesByMember,
                    summarySelectedMemberId
                ) {
                    if (summarySelectedMemberId.isNullOrBlank()) {
                        selectedAllergiesByMember.values.flatten().toSet()
                    } else {
                        selectedAllergiesByMember[summarySelectedMemberId] ?: emptySet()
                    }
                }
                val isFamilyFlow = familyOverviewMembers.isNotEmpty()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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

                    if (isFamilyFlow && familyOverviewMembers.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(familyOverviewMembers) { member ->
                                val isSelected = summarySelectedMemberId == member.id
                                Row(
                                    modifier = Modifier
                                        .background(
                                            color = if (isSelected) Color(0xFF91B640) else Color(0xFFF8F8F8),
                                            shape = RoundedCornerShape(66.dp)
                                        )
                                        .clickable {
                                            val newId = if (summarySelectedMemberId == member.id) null else member.id
                                            onSummaryMemberSelected(newId)
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

                    val (nonEmptySummarySteps, emptySummarySteps) = remember(summaryChipIds, allergySteps) {
                        val allIndices = 0..allergySteps.lastIndex
                        val nonEmpty = allIndices.filter { stepIndex ->
                            val stepChipIds = OnboardingChipData
                                .chipsForStep(stepIndex)
                                .map { it.id }
                                .toSet()
                            summaryChipIds.any { it in stepChipIds }
                        }
                        val empty = allIndices.filter { stepIndex ->
                            val stepChipIds = OnboardingChipData
                                .chipsForStep(stepIndex)
                                .map { it.id }
                                .toSet()
                            summaryChipIds.none { it in stepChipIds }
                        }
                        nonEmpty to empty
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = bottomInset + 24.dp)
                    ) {
                        // Match iOS: show AI summary only when no member filter is applied.
                        // To avoid the user waiting on backend latency, show a placeholder text
                        // immediately and update it when the real summary arrives.
                        if (summarySelectedMemberId == null) {
                            item {
                                val text = aiSummaryText
                                    ?: "Summarizing your food notes with AI…"
                                AiSummaryPreferenceCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    summaryText = text
                                )
                            }
                        }
                        // First show all sections that have at least one selection (for this filter),
                        // then show the remaining sections in empty state, matching iOS.
                        items(nonEmptySummarySteps) { stepIndex ->
                            val stepChipIds = OnboardingChipData
                                .chipsForStep(stepIndex)
                                .map { it.id }
                                .toSet()
                            val selectedForStep = summaryChipIds.filter { it in stepChipIds }.toSet()
                            if (selectedForStep.isNotEmpty()) {
                                val stepId = allergySteps.getOrNull(stepIndex)?.id ?: return@items
                                PreferenceCapsuleCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    selectedChipIds = selectedForStep,
                                    sectionTitle = allergySteps.getOrNull(stepIndex)?.title ?: "Step ${stepIndex + 1}",
                                    sectionIconRes = allergySteps.getOrNull(stepIndex)?.iconRes
                                        ?: R.drawable.ic_step_allergies,
                                    trailingAvatarsForChip = { chipId ->
                                        val rawMemberIds = selectedAllergiesByMember
                                            .mapNotNull { (memberKey, chips) ->
                                                if (chips.contains(chipId)) memberKey else null
                                            }
                                            .toSet()
                                        // When a specific member is selected in the summary filter, only
                                        // show that member's avatar on the chips, matching iOS behavior.
                                        val memberIds = if (summarySelectedMemberId.isNullOrBlank()) {
                                            rawMemberIds
                                        } else {
                                            rawMemberIds.filter { it == summarySelectedMemberId }.toSet()
                                        }
                                        if (memberIds.isEmpty()) null
                                        else {
                                            {
                                                CapsuleChipMemberAvatars(
                                                    memberIds = memberIds,
                                                    members = familyOverviewMembers
                                                )
                                            }
                                        }
                                    },
                                    showEditAction = true,
                                    onEditClick = { onEditSection(stepId) }
                                )
                            }
                        }

                        items(emptySummarySteps) { stepIndex ->
                            PreferenceCapsuleCard(
                                modifier = Modifier.fillMaxWidth(),
                                selectedChipIds = emptySet(),
                                sectionTitle = allergySteps.getOrNull(stepIndex)?.title
                                    ?: "Step ${stepIndex + 1}",
                                sectionIconRes = allergySteps.getOrNull(stepIndex)?.iconRes
                                    ?: R.drawable.ic_step_allergies,
                                showEditAction = true,
                                onEditClick = {
                                    val stepId = allergySteps.getOrNull(stepIndex)?.id
                                    if (stepId != null) {
                                        onEditSection(stepId)
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
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
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 0.dp,
                        bottom = bottomInset + 16.dp
                    ),
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

                    // Extra bottom padding via bottomInset ensures the last card can scroll
                    // fully above the bottom sheet, so nothing is hidden behind it.
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

