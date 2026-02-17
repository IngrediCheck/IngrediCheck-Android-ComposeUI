package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.ui.components.buttons.primaryButtonEffect
import lc.fungee.Ingredicheck.ui.components.buttons.primaryChipEffect
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale70
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary700

@Composable
internal fun AddAllergiesSheet(
    members: List<OnboardingViewModel.FamilyOverviewMember>,
    selectedMemberId: String,
    selectedAllergies: Set<String>,
    onMemberSelected: (String) -> Unit,
    onToggleAllergy: (String) -> Unit,
    onNext: () -> Unit,
    questionStepIndex: Int = 0
) {
    val everyoneId = "ALL"
    val fallbackMembers = remember(members) {
        if (members.isNotEmpty()) members else emptyList()
    }
    val resolvedSelectedId = remember(selectedMemberId, fallbackMembers) {
        when {
            selectedMemberId == everyoneId -> everyoneId
            fallbackMembers.any { it.id == selectedMemberId } -> selectedMemberId
            fallbackMembers.isNotEmpty() -> everyoneId
            else -> ""
        }
    }

    AnimatedContent(
        targetState = questionStepIndex.coerceIn(0, 5),
        label = "allergyQuestion",
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 250)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 250))
        }
    ) { idx ->
        val (question, subtitle) = OnboardingChipData.questionForStep(idx)
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Greyscale70)) { append("Q. ") }
                    withStyle(style = SpanStyle(color = Greyscale150)) { append(question) }
                },
                fontFamily = Manrope,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (fallbackMembers.isNotEmpty()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                val isSelected = resolvedSelectedId == everyoneId
                val avatarSize by animateDpAsState(
                    targetValue = if (isSelected) 42.dp else 36.dp,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "everyoneAvatarSize"
                )
                val borderWidth by animateDpAsState(
                    targetValue = if (isSelected) 2.dp else 1.dp,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "everyoneBorderWidth"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onMemberSelected(everyoneId) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(
                                width = borderWidth,
                                color = if (isSelected) Primary700 else Color.Unspecified,
                                shape = CircleShape
                            )
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.everyone_seleted_home_icon),
                            contentDescription = null,
                            modifier = Modifier.size(avatarSize),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Everyone",
                        fontFamily = Manrope,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) Primary700 else Greyscale120,
                        maxLines = 1
                    )
                }
            }
            items(fallbackMembers) { m ->
                val isSelected = m.id == resolvedSelectedId
                val avatarRes = OnboardingChipData.avatarResOrNull(m.avatarId)
                val avatarSize by animateDpAsState(
                    targetValue = if (isSelected) 42.dp else 36.dp,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "memberAvatarSize"
                )
                val borderWidth by animateDpAsState(
                    targetValue = if (isSelected) 2.dp else 1.dp,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    label = "memberBorderWidth"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onMemberSelected(m.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(
                                width = borderWidth,
                                color = if (isSelected) Primary700 else Color.Unspecified,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            m.generatedAvatarUrl.trim().isNotBlank() -> {
                                AsyncImage(
                                    model = m.generatedAvatarUrl.trim(),
                                    contentDescription = null,
                                    modifier = Modifier.size(avatarSize).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            avatarRes != null -> {
                                Image(
                                    painter = painterResource(id = avatarRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(avatarSize).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Greyscale40)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = m.name.trim().split(" ").firstOrNull() ?: m.name,
                        fontFamily = Manrope,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) Primary700 else Greyscale120,
                        maxLines = 1
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xE8FFE140),
                                        Color(0xE8FFE140).copy(alpha = 0.4f),
                                        Color(0xE8FFE140).copy(alpha = 0f)
                                    ),
                                    center = center,
                                    radius = size.minDimension / 2f
                                )
                            )
                        }
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bulb_on_light_icon),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Text(
                text = "Select members one by one to personalize their choices.",
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Greyscale100
            )
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    val allergies = remember(questionStepIndex) {
        OnboardingChipData.chipsForStep(questionStepIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Column {
            FlowRowWithRightAlignedButton(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) {
                allergies.forEach { def ->
                    val isSelected = selectedAllergies.contains(def.id)
                    AllergyChip(
                        label = def.iconPrefix + def.label,
                        selected = isSelected,
                        onClick = { onToggleAllergy(def.id) }
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .primaryButtonEffect(
                            isDisabled = false,
                            shape = RoundedCornerShape(percent = 50),
                            disabledBackgroundColor = Greyscale40
                        )
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Greyscale10,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllergyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)
    val baseModifier = if (selected) {
        Modifier.primaryChipEffect(shape)
    } else {
        Modifier.background(Color.White).border(1.dp, lc.fungee.Ingredicheck.ui.theme.Greyscale60, shape)
    }
    Box(
        modifier = Modifier
            .then(baseModifier)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(start = 12.dp, end = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = if (selected) Greyscale10 else Greyscale150,
            maxLines = 1
        )
    }
}

@Composable
private fun FlowRowWithRightAlignedButton(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val spacingX = horizontalSpacing.roundToPx()
        val spacingY = verticalSpacing.roundToPx()
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }
        val chips = measurables.dropLast(1)
        val button = measurables.last()
        val chipPlaceables = chips.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val buttonPlaceable = button.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val maxWidth = constraints.maxWidth
        var x = 0
        var y = 0
        var rowHeight = 0
        val chipPositions = ArrayList<IntArray>(chipPlaceables.size)
        chipPlaceables.forEach { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + spacingY
                rowHeight = 0
            }
            chipPositions.add(intArrayOf(x, y))
            x += p.width + spacingX
            rowHeight = maxOf(rowHeight, p.height)
        }
        val lastRowStartY = if (chipPositions.isNotEmpty()) chipPositions.last()[1] else 0
        val lastRowRightmostX = if (chipPositions.isNotEmpty()) {
            chipPlaceables.mapIndexedNotNull { index, chip ->
                if (chipPositions[index][1] == lastRowStartY) chipPositions[index][0] + chip.width else null
            }.maxOrNull() ?: 0
        } else 0
        val buttonFitsOnLastRow = lastRowRightmostX + spacingX + buttonPlaceable.width <= maxWidth
        val buttonX = maxWidth - buttonPlaceable.width
        val buttonY: Int
        if (buttonFitsOnLastRow && chipPlaceables.isNotEmpty()) {
            buttonY = lastRowStartY
            rowHeight = maxOf(rowHeight, buttonPlaceable.height)
        } else {
            buttonY = y + rowHeight + spacingY
            rowHeight = buttonPlaceable.height
        }
        val totalHeight = (buttonY + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width = maxWidth, height = totalHeight) {
            chipPlaceables.forEachIndexed { i, p ->
                val pos = chipPositions[i]
                p.placeRelative(pos[0], pos[1])
            }
            buttonPlaceable.placeRelative(buttonX, buttonY)
        }
    }
}
