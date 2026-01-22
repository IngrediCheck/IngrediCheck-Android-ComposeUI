@file:Suppress("PackageName")

package lc.fungee.Ingredicheck.onboarding.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.*

/**
 * Data class to hold icon resources for both unselected (grey) and selected (primary) states.
 */
data class CategoryIcon(
    val unselectedRes: Int,
    val selectedRes: Int
)

/**
 * Data class for family member options
 */
data class FamilyMember(
    val id: String,
    val iconRes: Int,
    val label: String,
    val contentScale: ContentScale = ContentScale.Crop
)

private fun avatarOptionsForCategory(categoryIndex: Int): List<FamilyMember> {
    return when (categoryIndex) {
        0 -> listOf(
            FamilyMember("baby_boy", R.drawable.baby_boy, "Age (0-4)", ContentScale.Crop),
            FamilyMember("baby_girl", R.drawable.baby_girl, "Age (0-4)", ContentScale.Crop),
            FamilyMember("young_son", R.drawable.young_son, "Age (4-25)", ContentScale.Crop),
            FamilyMember("young_daughter", R.drawable.young_daughter, "Age (4-25)", ContentScale.Crop),
            FamilyMember("mom", R.drawable.mom, "Age (25-55)", ContentScale.Crop),
            FamilyMember("father", R.drawable.father, "Age (25-55)", ContentScale.Crop),
            FamilyMember("grandfather", R.drawable.grand_father, "Age (55+)", ContentScale.Crop),
            FamilyMember("grandmother", R.drawable.grand_mother, "Age (55+)", ContentScale.Crop),
        )

        1 -> listOf(
            FamilyMember("hand_thumbsup", R.drawable.hand_thumbsup_sigin, "Thumbs up", ContentScale.Fit),
            FamilyMember("hand_victory", R.drawable.hand_victory_sigin, "Victory", ContentScale.Fit),
            FamilyMember("hand_wave", R.drawable.wave_sigin, "Wave", ContentScale.Fit),
            FamilyMember("hand_pointing", R.drawable.pointed_sigin, "Pointing", ContentScale.Fit),
            FamilyMember("heart_hand", R.drawable.heart_hand_sigin, "Heart hand", ContentScale.Fit),
            FamilyMember("phone_sign", R.drawable.phone_sign, "Phone", ContentScale.Fit)
        )

        2 -> listOf(
            FamilyMember("hair_short", R.drawable.short_hair, "Short Hair", ContentScale.Fit),
            FamilyMember("hair_short_spiky", R.drawable.short_spiky_hair, "Short Spiky", ContentScale.Fit),
            FamilyMember("hair_curly", R.drawable.curly_hair, "Curly Hair", ContentScale.Fit),
            FamilyMember("hair_long", R.drawable.long_hair, "Long Hair", ContentScale.Fit),
            FamilyMember("hair_bun", R.drawable.bun_hair, "Bun", ContentScale.Fit),
            FamilyMember("hair_ponytail", R.drawable.ponytail_hair, "Ponytail", ContentScale.Fit),
            FamilyMember("hair_braided", R.drawable.braided_hair, "Braided", ContentScale.Fit),
            FamilyMember("hair_medium_curls", R.drawable.medium_curls_hair, "Medium Curls", ContentScale.Fit),
            FamilyMember("hair_bald", R.drawable.blad_hair, "Bald", ContentScale.Fit),
        )

        3 -> listOf(
            FamilyMember("skin_very_light", R.drawable.very_light, "Very Light", ContentScale.Fit),
            FamilyMember("skin_light", R.drawable.light, "Light", ContentScale.Fit),
            FamilyMember("skin_medium_light", R.drawable.medium_light, "Medium Light", ContentScale.Fit),
            FamilyMember("skin_medium", R.drawable.medium, "Medium", ContentScale.Fit),
            FamilyMember("skin_medium_dark", R.drawable.medium_dark, "Medium Dark", ContentScale.Fit),
            FamilyMember("skin_dark", R.drawable.dark, "Dark", ContentScale.Fit),
            FamilyMember("skin_very_dark", R.drawable.very_dark, "Very Dark", ContentScale.Fit),
        )

        4 -> listOf(
            FamilyMember("acc_specs", R.drawable.specs, "Specs", ContentScale.Fit),
            FamilyMember("acc_sunglasses", R.drawable.sunglasses, "Sunglasses", ContentScale.Fit),
            FamilyMember("acc_earrings", R.drawable.earrings, "Earrings", ContentScale.Fit),
            FamilyMember("acc_cap", R.drawable.cap, "Cap", ContentScale.Fit),
            FamilyMember("acc_hat", R.drawable.hat, "Hat", ContentScale.Fit),
        )

        5 -> listOf(
            FamilyMember("color_pastel_blue", R.drawable.pestle_blue, "Pastel Blue", ContentScale.Fit),
            FamilyMember("color_warm_pink", R.drawable.warm_pink, "Warm pink", ContentScale.Fit),
            FamilyMember("color_soft_green", R.drawable.soft_green, "Soft green", ContentScale.Fit),
            FamilyMember("color_lavender", R.drawable.lavender, "Lavender", ContentScale.Fit),
            FamilyMember("color_orange", R.drawable.orange, "Orange", ContentScale.Fit),
            FamilyMember("color_yellow", R.drawable.yellow, "Yellow", ContentScale.Fit),
            FamilyMember("color_transparent", R.drawable.transprint, "Transparent", ContentScale.Fit),
        )

        else -> emptyList()
    }
}

private fun selectedMembersInOrder(selections: Map<Int, String>): List<FamilyMember> {
    return (0..5).mapNotNull { index ->
        val id = selections[index] ?: return@mapNotNull null
        avatarOptionsForCategory(index).firstOrNull { it.id == id }
    }
}

@Composable
private fun SelectedSummaryBar(
    selected: List<FamilyMember>,
    modifier: Modifier = Modifier,
    isGenerateEnabled: Boolean,
    onGenerateClick: () -> Unit,
    backgroundColor: Color
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selected",
                color = Greyscale110,
                fontFamily = Manrope,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                selected.forEach { member ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = member.iconRes),
                            contentDescription = member.label,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (member.contentScale == ContentScale.Fit) 3.dp else 0.dp),
                            contentScale = member.contentScale
                        )
                    }
                }
            }
        }

        PrimaryButton(
            title = "Generate",
            icon = R.drawable.lucide_stars_,
            takeFullWidth = false,
            width = 180.dp,
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
                        ) {
                            Image(
                                painter = painterResource(id = member.iconRes),
                                contentDescription = member.label,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (member.contentScale == ContentScale.Fit) 6.dp else 0.dp),
                                contentScale = member.contentScale
                            )

                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Label (Greyscale110, Manrope Medium 10sp)
                Text(
                    text = member.label,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = Greyscale110
                    )
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

    LaunchedEffect(selectedCategoryIndex) {
        if (selectedCategoryIndex == 0) return@LaunchedEffect

        val currentSelectedId = selections[selectedCategoryIndex]
        if (currentItems.isNotEmpty() && (currentSelectedId == null || currentItems.none { it.id == currentSelectedId })) {
            selections[selectedCategoryIndex] = currentItems.first().id
        }
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Show the same selector UI for categories that have selectable options.
        if (currentItems.isNotEmpty()) {
            key(selectedCategoryIndex) {
                FamilyMemberSelector(
                    selectedId = selectedForCategory,
                    onMemberSelected = { selections[selectedCategoryIndex] = it },
                    items = currentItems,
                    backgroundColor = backgroundColor
                )
            }
        }

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
                initialSelections = mapOf(
                    0 to "young_daughter",
                    1 to "hand_victory",
                    2 to "hair_long",
                    3 to "skin_medium",
                    4 to "acc_cap",
                    5 to "color_lavender"
                ),
                backgroundColor = Color.White,
                centerContent = false
            )
        }
    }

