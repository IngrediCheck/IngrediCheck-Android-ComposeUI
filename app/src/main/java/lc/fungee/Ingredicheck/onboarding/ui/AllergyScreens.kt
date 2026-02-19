package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import lc.fungee.Ingredicheck.onboarding.data.RegionDefinition
import lc.fungee.Ingredicheck.onboarding.data.AvoidOptionDefinition
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.ui.components.StackedCardsComponent
import lc.fungee.Ingredicheck.ui.components.buttons.primaryButtonEffect
import lc.fungee.Ingredicheck.ui.components.buttons.primaryChipEffect
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale70
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Primary700

@Composable
internal fun AddAllergiesSheet(
    members: List<OnboardingViewModel.FamilyOverviewMember>,
    selectedMemberId: String,
    selectedAllergies: Set<String>,
    onMemberSelected: (String) -> Unit,
    onToggleAllergy: (String) -> Unit,
    onNext: () -> Unit,
    onSkipPreferences: () -> Unit = {},
    showFineTuneDecision: Boolean = false,
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

    // Special fine‑tune decision screen between Life Style and Nutrition.
    if (showFineTuneDecision) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Want to fine‑tune your experience?",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Greyscale150,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Add extra preferences to tailor your experience.\nJump in or skip!",
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "All Set!" – secondary (outlined) button
                SecondaryButton(
                    title = "All Set!",
                    modifier = Modifier.weight(1f),
                    onClick = { onSkipPreferences() },
                    takeFullWidth = true
                )

                // "Add Preferences" – primary (filled) button
                PrimaryButton(
                    title = "Add Preferences",
                    modifier = Modifier.weight(1f),
                    onClick = { onNext() },
                    takeFullWidth = true
                )
            }
        }
        return
    }

    AnimatedContent(
        targetState = questionStepIndex.coerceIn(0, 9),
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

    Spacer(modifier = Modifier.height(20.dp))

    if (questionStepIndex == 4) {
        // Region-style grouped UI – mirrors iOS DynamicRegionsQuestionView.
        // The list of regions scrolls, but the green arrow button stays fixed
        // at the bottom-right of the sheet (does not scroll).
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
        ) {
            // Scrollable content: region capsules + their inner chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 0.dp)



            ) {
                RegionSelectionSection(
                    selectedAllergies = selectedAllergies,
                    onToggleAllergy = onToggleAllergy
                )

                // Extra bottom spacer so last row isn't hidden behind the arrow button
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Fixed-position primary arrow button (does NOT scroll)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp)
                    .size(50.dp)
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
    } else if (questionStepIndex == 5) {
        // Avoid step: show stacked cards with forward arrow button always visible
        val avoidCards = OnboardingChipData.avoidCards
        val totalCards = avoidCards.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            StackedCardsComponent(
                modifier = Modifier,
                cardCount = avoidCards.size,
                cardContent = { index, isTop, positionInStack ->
                    val card = avoidCards[index]

                    val bgColor = try {
                        Color(android.graphics.Color.parseColor(card.colorHex))
                    } catch (_: IllegalArgumentException) {
                        Color(0xFFFFF6B3)
                    }

                    // Smooth fade-in of card content when this card becomes top (0 -> 1 opacity)
                    val contentAlpha by animateFloatAsState(
                        targetValue = if (isTop) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 750,
                            easing = FastOutSlowInEasing
                        ),
                        label = "cardContentAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor)
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        if (isTop) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(contentAlpha),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = card.title,
                                        fontFamily = Nunito,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Greyscale150
                                    )
                                    Text(
                                        text = "${positionInStack}/$totalCards",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Greyscale140
                                    )
                                }

                                Text(
                                    text = card.description,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Greyscale140
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                SimpleFlowRow(
                                    horizontalSpacing = 8.dp,
                                    verticalSpacing = 8.dp
                                ) {
                                    card.options.forEach { opt ->
                                        val isSelected = selectedAllergies.contains(opt.id)
                                        AvoidOptionChip(
                                            option = opt,
                                            isSelected = isSelected,
                                            onClick = { onToggleAllergy(opt.id) }
                                        )
                                    }
                                }
                            }
                        }

                        if (isTop) {
                            // Adjust leaf icon position (positive X = right, positive Y = down)
                            val leafIconOffsetX = 5.dp
                            val leafIconOffsetY = 34.dp
                            Image(
                                painter = painterResource(id = R.drawable.leaf_arrow_circlepath),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = leafIconOffsetX, y = leafIconOffsetY)
                                    .height(100.dp)
                                    .alpha(contentAlpha),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            )
            
            // Fixed-position forward arrow button (always visible)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp)
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
    } else if (questionStepIndex == 6) {
        // LifeStyle step: same stacked cards as Avoid, 3 cards (Plant & Balance, Quality & Source, Sustainable Living)
        val lifestyleCards = OnboardingChipData.lifestyleCards
        val totalCards = lifestyleCards.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            StackedCardsComponent(
                modifier = Modifier,
                cardCount = lifestyleCards.size,
                cardContent = { index, isTop, positionInStack ->
                    val card = lifestyleCards[index]

                    val bgColor = try {
                        Color(android.graphics.Color.parseColor(card.colorHex))
                    } catch (_: IllegalArgumentException) {
                        Color(0xFFFFF6B3)
                    }

                    val contentAlpha by animateFloatAsState(
                        targetValue = if (isTop) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 750,
                            easing = FastOutSlowInEasing
                        ),
                        label = "cardContentAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor)
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        if (isTop) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(contentAlpha),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = card.title,
                                        fontFamily = Nunito,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Greyscale150
                                    )
                                    Text(
                                        text = "${positionInStack}/$totalCards",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Greyscale140
                                    )
                                }

                                Text(
                                    text = card.description,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Greyscale140
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                SimpleFlowRow(
                                    horizontalSpacing = 8.dp,
                                    verticalSpacing = 8.dp
                                ) {
                                    card.options.forEach { opt ->
                                        val isSelected = selectedAllergies.contains(opt.id)
                                        AvoidOptionChip(
                                            option = opt,
                                            isSelected = isSelected,
                                            onClick = { onToggleAllergy(opt.id) }
                                        )
                                    }
                                }
                            }
                        }

                        if (isTop) {
                            val leafIconOffsetX = 5.dp
                            val leafIconOffsetY = 34.dp
                            Image(
                                painter = painterResource(id = R.drawable.leaf_arrow_circlepath),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = leafIconOffsetX, y = leafIconOffsetY)
                                    .height(100.dp)
                                    .alpha(contentAlpha),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            )
            
            // Fixed-position forward arrow button (always visible)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp)
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
    } else if (questionStepIndex == 7) {
        // Nutrition step: 3 cards (Macronutrient Goals, Sugar & Fiber, Diet Frameworks & Patterns)
        val nutritionCards = OnboardingChipData.nutritionCards
        val totalCards = nutritionCards.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            StackedCardsComponent(
                modifier = Modifier,
                cardCount = nutritionCards.size,
                cardContent = { index, isTop, positionInStack ->
                    val card = nutritionCards[index]

                    val bgColor = try {
                        Color(android.graphics.Color.parseColor(card.colorHex))
                    } catch (_: IllegalArgumentException) {
                        Color(0xFFFFF6B3)
                    }

                    val contentAlpha by animateFloatAsState(
                        targetValue = if (isTop) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 750,
                            easing = FastOutSlowInEasing
                        ),
                        label = "cardContentAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(bgColor)
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        if (isTop) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(contentAlpha),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = card.title,
                                        fontFamily = Nunito,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Greyscale150
                                    )
                                    Text(
                                        text = "${positionInStack}/$totalCards",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Greyscale140
                                    )
                                }

                                Text(
                                    text = card.description,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Greyscale140
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                SimpleFlowRow(
                                    horizontalSpacing = 8.dp,
                                    verticalSpacing = 8.dp
                                ) {
                                    card.options.forEach { opt ->
                                        val isSelected = selectedAllergies.contains(opt.id)
                                        AvoidOptionChip(
                                            option = opt,
                                            isSelected = isSelected,
                                            onClick = { onToggleAllergy(opt.id) }
                                        )
                                    }
                                }
                            }
                        }

                        if (isTop) {
                            val leafIconOffsetX = 5.dp
                            val leafIconOffsetY = 34.dp
                            Image(
                                painter = painterResource(id = R.drawable.leaf_arrow_circlepath),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = leafIconOffsetX, y = leafIconOffsetY)
                                    .height(100.dp)
                                    .alpha(contentAlpha),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            )
            
            // Fixed-position forward arrow button (always visible)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 16.dp)
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
    } else {
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
            fontSize = 14.sp,
            color = if (selected) Greyscale10 else Greyscale150,
            maxLines = 1
        )
    }
}

@Composable
fun AvoidOptionChip(
    option: AvoidOptionDefinition,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(999.dp)

    val baseModifier =
        if (isSelected) {
            Modifier.primaryChipEffect(shape)
        } else {
            Modifier
                .background(Color.White, shape)
                .border(1.dp, Greyscale40, shape)
        }

    Box(
        modifier = Modifier
            .then(baseModifier)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = option.iconPrefix,
                fontSize = 16.sp
            )
            Text(
                text = option.label,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = if (isSelected) Greyscale10 else Color(0xFF303030),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RegionSelectionSection(
    selectedAllergies: Set<String>,
    onToggleAllergy: (String) -> Unit
) {
    val regions = remember { OnboardingChipData.regions }
    // Track which regions are expanded (by name). Mirrors the iOS expandedSectionIds set.
    var expandedRegionNames by remember { mutableStateOf<Set<String>>(emptySet()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        regions.forEach { region ->
            // If a region only has a single sub-region, skip the expandable
            // header and surface the chip directly – same as iOS.
            if (region.subRegions.size == 1) {
                SimpleFlowRow(
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    val def = region.subRegions.first()
                    val isSelected = selectedAllergies.contains(def.id)
                    AllergyChip(
                        label = def.iconPrefix + def.label,
                        selected = isSelected,
                        onClick = { onToggleAllergy(def.id) }
                    )
                }
            } else {
                val hasAnySelection = region.subRegions.any { selectedAllergies.contains(it.id) }
                val isExpanded = expandedRegionNames.contains(region.name)
                RegionSectionRow(
                    region = region,
                    isSectionSelected = hasAnySelection,
                    isExpanded = isExpanded,
                    selectedAllergies = selectedAllergies,
                    onToggleExpanded = {
                        expandedRegionNames =
                            if (isExpanded) expandedRegionNames - region.name
                            else expandedRegionNames + region.name
                    },
                    onToggleAllergy = onToggleAllergy
                )
            }
        }
    }
}

@Composable
private fun RegionSectionRow(
    region: RegionDefinition,
    isSectionSelected: Boolean,
    isExpanded: Boolean,
    selectedAllergies: Set<String>,
    onToggleExpanded: () -> Unit,
    onToggleAllergy: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val shape = RoundedCornerShape(999.dp)
        val headerModifier =
            if (isSectionSelected || isExpanded) {
                Modifier.primaryChipEffect(shape)
            } else {
                Modifier
                    .background(Color.White)
                    .border(1.dp, lc.fungee.Ingredicheck.ui.theme.Greyscale60, shape)
            }

        Box(
            modifier = Modifier
                .then(headerModifier)
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggleExpanded() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = region.name,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isSectionSelected || isExpanded) Greyscale10 else Greyscale150,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isSectionSelected || isExpanded) Greyscale10 else Greyscale120,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            SimpleFlowRow(
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) {
                region.subRegions.forEach { def ->
                    val isSelected = selectedAllergies.contains(def.id)
                    AllergyChip(
                        label = def.iconPrefix + def.label,
                        selected = isSelected,
                        onClick = { onToggleAllergy(def.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleFlowRow(
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

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val maxWidth = constraints.maxWidth
        var x = 0
        var y = 0
        var rowHeight = 0
        val positions = ArrayList<IntArray>(placeables.size)

        placeables.forEach { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + spacingY
                rowHeight = 0
            }
            positions.add(intArrayOf(x, y))
            x += p.width + spacingX
            rowHeight = maxOf(rowHeight, p.height)
        }

        val totalHeight = (y + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width = maxWidth, height = totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val pos = positions[index]
                placeable.placeRelative(pos[0], pos[1])
            }
        }
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
