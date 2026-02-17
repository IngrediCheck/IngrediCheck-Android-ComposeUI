package lc.fungee.Ingredicheck.memoji

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.model.CategoryIcon
import lc.fungee.Ingredicheck.onboarding.model.FamilyMember
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.AvatarScreenCategory
import lc.fungee.Ingredicheck.ui.theme.Greyscale130
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.PaletteAccent
import lc.fungee.Ingredicheck.ui.theme.PaletteBackgroundDark
import lc.fungee.Ingredicheck.ui.theme.avatarOptionLabelTextStyle
import lc.fungee.Ingredicheck.ui.theme.rememberAvatarScreenCategory
import lc.fungee.Ingredicheck.ui.theme.selectedSummaryIconSize
import lc.fungee.Ingredicheck.ui.theme.selectedSummaryIconSpacing
import lc.fungee.Ingredicheck.ui.theme.selectedSummaryLabelFontSize

@Composable
internal fun SelectedSummaryBar(
    selected: List<FamilyMember>,
    modifier: Modifier = Modifier.Companion,
    isGenerateEnabled: Boolean,
    onGenerateClick: () -> Unit,
    backgroundColor: Color
) {
    val selectedLabelFontSize = selectedSummaryLabelFontSize()
    val selectedIconSize = selectedSummaryIconSize()
    val selectedIconSpacing = selectedSummaryIconSpacing()
    val generateButtonWidth = when (rememberAvatarScreenCategory()) {
        AvatarScreenCategory.Large -> 190.dp
        AvatarScreenCategory.Small,
        AvatarScreenCategory.Normal -> 170.dp
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = "Selected",
                color = Greyscale130,
                fontFamily = Manrope,
                fontWeight = FontWeight.Companion.Medium,
                fontSize = selectedLabelFontSize
            )
//            Text(
//                text = "font=${selectedLabelFontSize.value}sp  icon=${selectedIconSize.value}dp  gap=${selectedIconSpacing.value}dp",
//                color = Greyscale110,
//                fontFamily = Manrope,
//                fontWeight = FontWeight.Medium,
//                fontSize = 10.sp
//            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Row(
                modifier = Modifier.Companion.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(selectedIconSpacing)
            ) {
                selected.forEach { member ->
                    Box(
                        modifier = Modifier.Companion
                            .size(selectedIconSize)
                            .clip(CircleShape)

                    ) {
                        Image(
                            painter = painterResource(id = member.iconRes),
                            contentDescription = member.label,
                            modifier = Modifier.Companion
                                .fillMaxSize(),
                            contentScale = ContentScale.Companion.Crop
                        )
                    }
                }
            }
        }

        PrimaryButton(
            title = "Generate",
            icon = R.drawable.lucide_stars_,
            iconWidth = 20.dp,
            iconHeight = 20.dp,
            takeFullWidth = false,
            width = generateButtonWidth,
            isDisabled = !isGenerateEnabled,
            onClick = if (isGenerateEnabled) onGenerateClick else null
        )
    }
}

/**
 * A tab bar component for Avatar Creation, showing categories with icons
 * and an animated indicator.
 */
@Composable
fun AvatarCategoryTabs(
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    backgroundColor: Color = PaletteBackgroundDark,
    modifier: Modifier = Modifier.Companion
) {
    val categories = listOf(
        CategoryIcon(
            R.drawable.heroicons_user_group_solid,
            R.drawable.heroicons_user_group_solid_primary
        ),
        CategoryIcon(
            R.drawable.solar_hand_shak_bold_grey,
            R.drawable.solar_hand_shake_bold_primary
        ),
        CategoryIcon(
            R.drawable.mingcute_hair_2_fill_grey,
            R.drawable.mingcute_hair_2_fill__primary
        ),
        CategoryIcon(R.drawable.colors_circle_grey, R.drawable.colors_circle_primary),
        CategoryIcon(R.drawable.sunglasses_grey, R.drawable.sunglasses_primary),
        CategoryIcon(R.drawable.color_palete_grey, R.drawable.color_palate_primary)
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        val totalWidth = maxWidth
        val tabWidth = totalWidth / categories.size

        // The indicator is a rounded pill centered under the selected icon
        val indicatorWidth = 40.dp
        val indicatorHeight = 3.dp

        val indicatorOffset by animateDpAsState(
            targetValue = (tabWidth * selectedCategoryIndex) + (tabWidth - indicatorWidth) / 2,
            label = "indicatorOffset"
        )

        Column {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                categories.forEachIndexed { index, categoryIcon ->
                    val isSelected = index == selectedCategoryIndex
                    Box(
                        modifier = Modifier.Companion
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onCategorySelected(index) },
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        Crossfade(
                            targetState = isSelected,
                            animationSpec = tween(durationMillis = 300),
                            label = "iconTransition"
                        ) { selected ->
                            Icon(
                                painter = painterResource(id = if (selected) categoryIcon.selectedRes else categoryIcon.unselectedRes),
                                contentDescription = null,
                                tint = Color.Companion.Unspecified, // Don't use tint
                                modifier = Modifier.Companion.size(28.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(indicatorHeight)
            ) {
                // Bottom full-width thin line
                Box(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Greyscale60)
                        .align(Alignment.Companion.BottomCenter)
                )

                // Animated selection indicator
                Box(
                    modifier = Modifier.Companion
                        .offset(x = indicatorOffset)
                        .width(indicatorWidth)
                        .height(indicatorHeight)
                        .background(
                            color = PaletteAccent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.Companion.BottomStart)
                )
            }
        }
    }
}

/**
 * A horizontal scrollable selector for family members (age groups).
 */
@Composable
fun FamilyMemberSelector(
    selectedId: String,
    onMemberSelected: (String) -> Unit,
    items: List<FamilyMember>,
    showInnerRing: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier.Companion
) {
    val cardShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val selectedShadowColor = Color(0x805B5B5B)
    val selectedShadowElevationPx = with(LocalDensity.current) { 9.dp.toPx() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { member ->
            val isSelected = member.id == selectedId

            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                modifier = Modifier.Companion
                    .width(68.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onMemberSelected(member.id) }
            ) {
                // Outer container (Rectangle 64x64, radius 12, border color depends on selection)
                Box(
                    modifier = Modifier.Companion
                        .size(64.dp)
                        .graphicsLayer {
                            if (isSelected) {
                                shadowElevation = selectedShadowElevationPx
                                shape = cardShape
                                clip = false
                                ambientShadowColor = selectedShadowColor
                                spotShadowColor = selectedShadowColor
                            } else {
                                shadowElevation = 0f
                            }
                        }
                        .background(color = backgroundColor, shape = cardShape)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) PaletteAccent else Greyscale40,
                            shape = cardShape
                        ),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    // Avatar Image (44x44, circular, Greyscale40 border)


                    Box(
                        modifier = Modifier.Companion
                            .size(52.dp)
                            .clip(CircleShape)
                            .then(
                                if (showInnerRing) {
                                    Modifier.Companion.border(
                                        (4.5).dp,
                                        shape = CircleShape,
                                        color = Color(color = 0xFFF9F9F9)
                                    )
                                } else {
                                    Modifier.Companion
                                }
                            )
                    ) {
                        Image(
                            painter = painterResource(id = member.iconRes),
                            contentDescription = member.label,
                            modifier = Modifier.Companion
                                .fillMaxSize()
                                .padding(
                                    if (member.id == "young_daughter") 0.dp
                                    else if (member.contentScale == ContentScale.Companion.Fit) 6.dp
                                    else 0.dp
                                ),
                            contentScale = member.contentScale
                        )

                    }
                }

                Spacer(modifier = Modifier.Companion.height(8.dp))

                // Label (Greyscale110, Manrope Medium 10sp)
                Text(
                    text = member.label,
                    style = avatarOptionLabelTextStyle(),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Companion.Ellipsis
                )
            }
        }
    }
}

/**
 * Demonstration screen for Avatar Creation UI
 */
@Composable
fun AvatarCreationScreen(
    modifier: Modifier = Modifier.Companion,
    initialSelections: Map<Int, String> = emptyMap(),
    backgroundColor: Color = PaletteBackgroundDark,
    centerContent: Boolean = false
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val selections = remember { mutableStateMapOf<Int, String>() }

    LaunchedEffect(initialSelections) {
        if (selections.isEmpty() && initialSelections.isNotEmpty()) {
            selections.putAll(initialSelections)
        }
    }

    val currentItems = remember(selectedCategoryIndex) {
        avatarOptionsForCategory(selectedCategoryIndex)
    }

    val selectedForCategory by remember {
        derivedStateOf { selections[selectedCategoryIndex] ?: "" }
    }
    val selectedSummary by remember {
        derivedStateOf { selectedMembersInOrder(selections) }
    }
    val isGenerateEnabled = selections[0] != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center
    ) {
        AvatarCategoryTabs(
            selectedCategoryIndex = selectedCategoryIndex,
            onCategorySelected = { selectedCategoryIndex = it },
            backgroundColor = backgroundColor
        )

        Spacer(modifier = Modifier.Companion.height(22.dp))

        // Show the same selector UI for categories that have selectable options.
        if (currentItems.isNotEmpty()) {
            key(selectedCategoryIndex) {
                FamilyMemberSelector(
                    selectedId = selectedForCategory,
                    onMemberSelected = { selections[selectedCategoryIndex] = it },
                    items = currentItems,
                    showInnerRing = selectedCategoryIndex == 0,
                    backgroundColor = backgroundColor
                )
            }
        }
//        Spacer(modifier = Modifier.height(22.dp))
        if (!centerContent) {

            SelectedSummaryBar(
                selected = selectedSummary,
                modifier = Modifier.Companion,
                isGenerateEnabled = isGenerateEnabled,
                onGenerateClick = { },
                backgroundColor = backgroundColor
            )
        }
    }
}

@Preview(showBackground = true )
@Composable
fun AvatarCreationScreenPreview() {
    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color.Companion.White),
        contentAlignment = Alignment.Companion.Center
    ) {
        AvatarCreationScreen(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .background(Color.Companion.White),
            backgroundColor = Color.Companion.White,
            centerContent = false
        )
    }
    }