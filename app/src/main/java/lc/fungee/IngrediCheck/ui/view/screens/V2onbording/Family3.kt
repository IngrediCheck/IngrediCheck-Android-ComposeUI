//package lc.fungee.IngrediCheck.ui.view.screens.V2onbording
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.animateColor
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.animation.core.animateDp
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.core.updateTransition
//import androidx.compose.animation.expandHorizontally
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.shrinkHorizontally
//import androidx.compose.animation.togetherWith
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Devices.PIXEL_9
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import lc.fungee.IngrediCheck.R
//import lc.fungee.IngrediCheck.ui.theme.Malerope
//import lc.fungee.IngrediCheck.ui.theme.Nunitosemibold
//import java.nio.file.WatchEvent
//
//@Preview(showBackground = true, name = "a", device = PIXEL_9)
//@Composable
//fun Famaliy3() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(20.dp)
//    )
//    {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 17.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 15.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            )
//            {
//
//                Text(
//                    text = "Generate  Avatar",
//                    fontSize = 18.sp,
//
//                    textAlign = TextAlign.Center,
//                    color = Color(color = 0xFF303030),
//                    fontFamily = Malerope,
//                    fontWeight = FontWeight.Bold,
//
//                    )
//                Text(
//                    "0/2", fontSize = 18.sp,
//
//                    textAlign = TextAlign.Center,
//                    color = Color(color = 0xFFBDBDBD),
//                    fontFamily = Malerope,
//                    fontWeight = FontWeight.Normal,
//
//                    )
//            }
//
//            SelectableCapsuleRow()
//            Text(
//                text = "Tell us how you’re related to them so we can create the perfect avatar!",
//                fontSize = 16.sp,
//
//                textAlign = TextAlign.Start,
//                color = Color(color = 0xFF949494),
//                fontFamily = Malerope,
//                fontWeight = FontWeight.Medium,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//
//        }
//
//
//        FamaliySelectionCard()
////        GestureSectionCard()
////        HairStyleSectionCard()
////        SkinToneSectionCard()
////        AccessoriesSectionCard()
////        ColorSectionCard()
////        ColorSectionCard1()
//
//
////        ColorThemSectionCard()
//
//    }
//
//}
//
//@Composable
//fun SelectableCapsuleRow() {
//    // List of demo items
//    val items = listOf(
//        "Family Member" to R.drawable.heroicons_user_group_solid,
//        "Gesture" to R.drawable.solar_hand_shake_bold,
//        "Hair Style" to R.drawable.mingcute_hair_2_fill,
//        "Skin Tone" to R.drawable.group_1171276302,
//        "Accessories" to R.drawable.bi_sunglasses,
//        "Color Theme" to R.drawable.mdi_palette
//    )
//    var selectedIndex by remember { mutableStateOf(0) } // first one selected initially
//
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
////        contentPadding = PaddingValues(horizontal = 16.dp)
//    ) {
//        itemsIndexed(items) { index, item ->
//            val (text, iconRes) = item
//            CapsuleItem3(
//                text = text,
//                icon = iconRes,
//                isSelected = selectedIndex == index,
//                onClick = { selectedIndex = index }
//            )
//        }
//    }
//}
//
//@Composable
//fun CapsuleItem3(
//
//    text: String,
//    icon: Int,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    val progress by animateFloatAsState(
//        targetValue = if (isSelected) 1f else 0f,
//        animationSpec = tween(durationMillis = 350),
//        label = ""
//    )
//
//    val borderColor by animateColorAsState(
//        targetValue = if (isSelected) Color(0xFF91B640) else Color(0xFFE3E3E3),
//        animationSpec = tween(350),
//        label = ""
//    )
//
//    val backgroundColor by animateColorAsState(
//        targetValue = if (isSelected) Color(0xFFF6FCED) else Color(0xFFFFFFFF),
//        animationSpec = tween(350),
//        label = ""
//    )
//    val collapsedWidth = 45.dp // same as height
//    val expandedWidth = 160.dp // your choice when selected
//    val width1 = collapsedWidth + (expandedWidth - collapsedWidth) * progress
//
//    val cornerRadius by animateDpAsState(
//        targetValue = if (isSelected) 50.dp else collapsedWidth / 2,
//        animationSpec = tween(350),
//        label = ""
//    )
//
//
////    val cornerRadius by animateDpAsState(
////        targetValue = if (isSelected) 50.dp else 35.dp,
////        animationSpec = tween(350),
////        label = ""
////    )
//
//    val width = 70.dp + (90.dp * progress)
//
//    Box(
//        modifier = Modifier
//            .width(width1)
//            .height(45.dp)
//            .clip(RoundedCornerShape(cornerRadius))
//            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
//            .background(backgroundColor)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Icon(
//                painter = painterResource(id = icon),
//                contentDescription = null,
//                tint = if (isSelected) Color(0xFF91B640) else Color.Unspecified,
//                modifier = Modifier
//                    .size(25.dp)
//                    .graphicsLayer {
//                        scaleX = 0.9f + 0.1f * progress
//                        scaleY = 0.9f + 0.1f * progress
//                    }
//            )
//
//            // Text with animated width
//            AnimatedVisibility(
//                visible = isSelected,
//                enter = fadeIn(animationSpec = tween(350)) + expandHorizontally(
//                    animationSpec = tween(
//                        350
//                    )
//                ),
//                exit = fadeOut(animationSpec = tween(350)) + shrinkHorizontally(
//                    animationSpec = tween(
//                        350
//                    )
//                )
//            ) {
//                Text(
//                    text = text,
//                    color = Color(0xFF91B640),
//                    fontSize = 15.sp,
//                    fontFamily = Nunitosemibold,
//                    maxLines = 1,
//                    modifier = Modifier.padding(start = 6.dp)
//                )
//            }
//        }
//    }
//}
//
//
package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Malerope
import lc.fungee.IngrediCheck.ui.theme.Nunitosemibold

data class Gesture(val emoji: String, val name: String)
data class HairStyle(val iconRes: Int, val name: String)
data class SkinTone(val iconRes: Int, val name: String)
data class Accessory(val iconRes: Int, val name: String)
data class ColorTheme(val color: Color, val name: String)

@Preview(showBackground = true, name = "a", device = PIXEL_9)
@Composable
fun Famaliy3() {
    // Shared state for selected family member
    var selectedFamilyMember by remember { mutableStateOf<FamilyMember?>(null) }
    var selectedGesture by remember { mutableStateOf<Gesture?>(null) }
    var selectedHairStyle by remember { mutableStateOf<HairStyle?>(null) }
    var selectedSkinTone by remember { mutableStateOf<SkinTone?>(null) }
    var selectedAccessory by remember { mutableStateOf<Accessory?>(null) }
    var selectedColorTheme by remember { mutableStateOf<ColorTheme?>(null) }
    // Track current section (0=Family, 1=Gesture, 2=Hair, 3=Skin, 4=Accessories, 5=Color)
    val currentSection = when {
        selectedFamilyMember == null -> 0
        selectedGesture == null -> 1
        selectedHairStyle == null -> 2
        selectedSkinTone == null -> 3
        selectedAccessory == null -> 4
        selectedColorTheme == null -> 5
        else -> 6 // All done
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 17.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Generate  Avatar",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF303030),
                    fontFamily = Malerope,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "0/2",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFBDBDBD),
                    fontFamily = Malerope,
                    fontWeight = FontWeight.Normal,
                )
            }

            // Pass the selected family member icon to SelectableCapsuleRow
            SelectableCapsuleRow(
                selectedFamilyIcon = selectedFamilyMember,
                selectedGesture = selectedGesture
            )


        }
        when (currentSection) {
            0 -> FamaliySelectionCard(
                selectedMember = selectedFamilyMember,
                onMemberSelected = { selectedFamilyMember = it }
            )

            1 -> GestureSectionCard(
                selectedGesture = selectedGesture,
                onGestureSelected = { selectedGesture = it }
            )

            else -> {
                // All selections complete
                Text(
                    "All selections complete! 🎉",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF91B640),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top =40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CapsuleButton1("Random")
            CapsuleButton("Generate")
        }

    }
//        if (selectedFamilyMember == null) {
//
//            // Pass callback to update selected family member
//            FamaliySelectionCard(
//                selectedMember = selectedFamilyMember,
//                onMemberSelected = { selectedFamilyMember = it }
//            )
//        } else{
//            GestureSectionCard()
//
//        }


}


@Composable
fun SelectableCapsuleRow(selectedFamilyIcon: FamilyMember?, selectedGesture: Gesture?) {
    // List of demo items - first item icon will be replaced with selected family member
    val defultfamalyIcon = R.drawable.heroicons_user_group_solid

    val items: List<Pair<String, Int>> = listOf(
        (if (selectedFamilyIcon != null) selectedFamilyIcon.name to selectedFamilyIcon.iconRes else "Family Member" to defultfamalyIcon),
        (if (selectedGesture != null) selectedGesture.name to R.drawable.dropupicon else "Gresture" to R.drawable.solar_hand_shake_bold),
        "Hair Style" to R.drawable.mingcute_hair_2_fill,
        "Skin Tone" to R.drawable.group_1171276302,
        "Accessories" to R.drawable.bi_sunglasses,
        "Color Theme" to R.drawable.mdi_palette
    )
    var selectedIndex by remember { mutableStateOf(0) }
    // Reset selection when family member is selected
    LaunchedEffect(selectedFamilyIcon) {
        if (selectedFamilyIcon != null) {
            selectedIndex = +1 // No capsule selected
        }
        if (selectedGesture != null) {
            selectedIndex = +1
        }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(items) { index, item ->
            val (text, iconRes) = item
            CapsuleItem3(
                text = text,
                icon = iconRes,
                isSelected = selectedIndex == index,
                onClick = { selectedIndex = index }
            )
        }
    }
}

@Composable
fun CapsuleItem3(
    text: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = ""
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF91B640) else Color(0xFFE3E3E3),
        animationSpec = tween(350),
        label = ""
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF6FCED) else Color(0xFFFFFFFF),
        animationSpec = tween(350),
        label = ""
    )

    val collapsedWidth = 45.dp
    val expandedWidth = 160.dp
    val width1 = collapsedWidth + (expandedWidth - collapsedWidth) * progress

    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 50.dp else collapsedWidth / 2,
        animationSpec = tween(350),
        label = ""
    )

    Box(
        modifier = Modifier
            .width(width1)
            .height(45.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Use Image for family member icon (first item), Icon for others
            if (text == "Family Member") {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .graphicsLayer {
                            scaleX = 0.9f + 0.1f * progress
                            scaleY = 0.9f + 0.1f * progress
                        }
                )
            } else {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(25.dp)
                        .graphicsLayer {
                            scaleX = 0.9f + 0.1f * progress
                            scaleY = 0.9f + 0.1f * progress
                        }
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(350)) + expandHorizontally(
                    animationSpec = tween(350)
                ),
                exit = fadeOut(animationSpec = tween(350)) + shrinkHorizontally(
                    animationSpec = tween(350)
                )
            ) {
                Text(
                    text = text,
                    color = Color(0xFF91B640),
                    fontSize = 15.sp,
                    fontFamily = Nunitosemibold,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

@Composable
fun FamaliySelectionCard(
    selectedMember: FamilyMember?,
    onMemberSelected: (FamilyMember) -> Unit
) {
    var expandCard by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
//        .height(113.dp)
    ) {

        Text(
            text = "Tell us how you're related to them so we can create the perfect avatar!",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            color = Color(0xFF949494),
            fontFamily = Malerope,
            fontWeight = FontWeight.Medium,
//            modifier = Modifier.padding(top = 14.dp)
        )
        Spacer(Modifier.height(17.dp))
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val menuWidth = maxWidth

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(
                        elevation = 0.5.dp,
                        shape = RoundedCornerShape(24.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { expandCard = true }
                    .border(
                        width = 0.5.dp,
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFEEEEEE)
                    )
                    .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedMember != null) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFF9F9F9), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {

                                Image(
                                    painter = painterResource(id = selectedMember.iconRes),
                                    contentDescription = selectedMember.name,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .padding(6.dp)
                                )
                            }
                        } else {
                            // Show placeholder icon when nothing selected
//                            Icon(
//                                painter = painterResource(id = R.drawable.heroicons_user_group_solid),
//                                contentDescription = "Select family member",
//                                tint = Color(0xFFBDBDBD),
//                                modifier = Modifier.size(24.dp)
//                            )
                        }

                        Text(
                            text = selectedMember?.name ?: "Select Family Member",
                            modifier = Modifier.padding(start = 8.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight(500),
                                color = if (selectedMember == null) Color(0xFF7F7F7F) else Color(
                                    0xFF303030
                                ),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = if (expandCard) R.drawable.dropupicon else R.drawable.dropdownicon),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expandCard,
                onDismissRequest = { expandCard = false },
                modifier = Modifier
                    .width(menuWidth)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(24.dp)
                    ),
                offset = DpOffset(0.dp, -8.dp),
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp
            ) {
                familyMembers.forEach { member ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color(0xFFF9F9F9), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = member.iconRes),
                                        contentDescription = member.name,
                                        modifier = Modifier
                                            .matchParentSize()
                                            .padding(6.dp)
                                    )
                                }
                                Text(
                                    text = member.name,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 21.sp,
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFF303030),
                                        textAlign = TextAlign.Center,
                                    )
                                )
                            }
                        },
                        onClick = {
                            onMemberSelected(member)
                            expandCard = false
                        }
                    )
                }
            }
        }
    }
}
