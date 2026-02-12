@file:Suppress("PackageName")

package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.onboarding.data.avatarOptionsForCategory
import lc.fungee.Ingredicheck.onboarding.data.selectedMembersInOrder
import lc.fungee.Ingredicheck.onboarding.model.CategoryIcon
import lc.fungee.Ingredicheck.onboarding.model.FamilyMember
import lc.fungee.Ingredicheck.ui.theme.*

@Composable
internal fun SelectedSummaryBar(
    selected: List<FamilyMember>,
    modifier: Modifier = Modifier,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selected",
                color = Greyscale130,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = selectedLabelFontSize
            )
//            Text(
//                text = "font=${selectedLabelFontSize.value}sp  icon=${selectedIconSize.value}dp  gap=${selectedIconSpacing.value}dp",
//                color = Greyscale110,
//                fontFamily = Manrope,
//                fontWeight = FontWeight.Medium,
//                fontSize = 10.sp
//            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(selectedIconSpacing)
            ) {
                selected.forEach { member ->
                    Box(
                        modifier = Modifier
                            .size(selectedIconSize)
                            .clip(CircleShape)

                    ) {
                        Image(
                            painter = painterResource(id = member.iconRes),
                            contentDescription = member.label,
                            modifier = Modifier
                                .fillMaxSize()
                              ,
                            contentScale = ContentScale.Crop
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
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryIcon(R.drawable.heroicons_user_group_solid, R.drawable.heroicons_user_group_solid_primary),
        CategoryIcon(R.drawable.solar_hand_shak_bold_grey, R.drawable.solar_hand_shake_bold_primary),
        CategoryIcon(R.drawable.mingcute_hair_2_fill_grey, R.drawable.mingcute_hair_2_fill__primary),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEachIndexed { index, categoryIcon ->
                    val isSelected = index == selectedCategoryIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onCategorySelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Crossfade(
                            targetState = isSelected,
                            animationSpec = tween(durationMillis = 300),
                            label = "iconTransition"
                        ) { selected ->
                            Icon(
                                painter = painterResource(id = if (selected) categoryIcon.selectedRes else categoryIcon.unselectedRes),
                                contentDescription = null,
                                tint = Color.Unspecified, // Don't use tint
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(indicatorHeight)
            ) {
                // Bottom full-width thin line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Greyscale60)
                        .align(Alignment.BottomCenter)
                )

                // Animated selection indicator
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(indicatorWidth)
                        .height(indicatorHeight)
                        .background(
                            color = PaletteAccent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.BottomStart)
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
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(12.dp)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(68.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onMemberSelected(member.id) }
            ) {
                // Outer container (Rectangle 64x64, radius 12, border color depends on selection)
                Box(
                    modifier = Modifier
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
                    contentAlignment = Alignment.Center
                ) {
                    // Avatar Image (44x44, circular, Greyscale40 border)


                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .then(
                                    if (showInnerRing) {
                                        Modifier.border(
                                            (4.5).dp,
                                            shape = CircleShape,
                                            color = Color(color = 0xFFF9F9F9)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            Image(
                                painter = painterResource(id = member.iconRes),
                                contentDescription = member.label,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        if (member.id == "young_daughter") 0.dp
                                        else if (member.contentScale == ContentScale.Fit) 6.dp
                                        else 0.dp
                                    ),
                                contentScale = member.contentScale
                            )

                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Label (Greyscale110, Manrope Medium 10sp)
                Text(
                    text = member.label,
                    style = avatarOptionLabelTextStyle(),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
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
    modifier: Modifier = Modifier,
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
        
        Spacer(modifier = Modifier.height(22.dp))
        
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
                modifier = Modifier,
                isGenerateEnabled = isGenerateEnabled,
                onGenerateClick = { },
                backgroundColor = backgroundColor
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AvatarCategoryTabsPreview() {
//    var selectedIndex by remember { mutableIntStateOf(0) }
//    IngrediCheckTheme(darkTheme = true) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(PaletteBackgroundDark)
//                .padding(vertical = 20.dp)
//        ) {
//            AvatarCategoryTabs(
//                selectedCategoryIndex = selectedIndex,
//                onCategorySelected = { selectedIndex = it }
//            )
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun FamilyMemberSelectorPreview() {
//    var selectedId by remember { mutableStateOf("baby_boy") }
//    IngrediCheckTheme(darkTheme = true) {
//        Box(modifier = Modifier.background(PaletteBackgroundDark).padding(vertical = 20.dp)) {
//            FamilyMemberSelector(
//                selectedId = selectedId,
//                onMemberSelected = { selectedId = it }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun FamilyMemberRenderPreview() {
//    FamilyMemberSelectorPreview()
//}

@Preview(showBackground = true )
@Composable
fun AvatarCreationScreenPreview() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AvatarCreationScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                backgroundColor = Color.White,
                centerContent = false
            )
        }
    }

