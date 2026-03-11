package lc.fungee.Ingredicheck.onboarding.ui

// hello

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.animation.core.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.auth.MemojiGenState
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.onboarding.data.DynamicStepsLoader
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.data.avatarBackgroundColorForId
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.memoji.AvatarCategoryTabs
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.memoji.SelectedSummaryBar
import lc.fungee.Ingredicheck.memoji.FamilyMemberSelector

import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Fail100
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale80
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.sheetTitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetSubtitleTextStyle
import lc.fungee.Ingredicheck.memoji.avatarOptionsForCategory
import kotlinx.coroutines.delay

@Composable
internal fun AddFamilyAvatarPickerSheet(
    displayName: String,
    selections: Map<Int, String>,
    onBackClick: () -> Unit,
    onAvatarSelected: (Map<Int, String>) -> Unit,
    onGenerateClick: () -> Unit
) {
//    SheetHeader(
//        onBackClick = onBackClick
//    )

//    Spacer(modifier = Modifier.height(responsiveSpacerHeight(16.dp, 18.dp, 20.dp)))

    val handle = displayName.trim().ifBlank { "" }
    // iOS UI shows a 0/2 counter (generated avatars). Selections here are per-category.
    val generatedCount = 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.icon_chevron_back),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = Greyscale150
            )
        }

        Text(
            text = "Avatar is for @${handle}",
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Greyscale150
            ),
            maxLines = 1,
            color = Greyscale150,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),

            textAlign = TextAlign.Start
        )

        Text(
            text = "${generatedCount}/2",
            style = sheetSubtitleTextStyle(),
            color = Greyscale110
        )
    }

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(12.dp, 14.dp, 16.dp)))

    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var currentSelections by remember(selections) { mutableStateOf(selections) }

    AvatarCategoryTabs(
        selectedCategoryIndex = selectedCategoryIndex,
        onCategorySelected = { selectedCategoryIndex = it },
        backgroundColor = Color.White
    )

    Spacer(modifier = Modifier.height(22.dp))

    val currentItems = remember(selectedCategoryIndex) {
        avatarOptionsForCategory(selectedCategoryIndex)
    }

    if (currentItems.isNotEmpty()) {
        FamilyMemberSelector(
            selectedId = currentSelections[selectedCategoryIndex].orEmpty(),
            onMemberSelected = {
                val updated = currentSelections.toMutableMap().apply {
                    this[selectedCategoryIndex] = it
                }
                currentSelections = updated
                onAvatarSelected(updated)
            },
            items = currentItems,
            showInnerRing = selectedCategoryIndex == 0,
            backgroundColor = Color.White
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    val selectedMembers = remember(currentSelections) {
        (0..5).mapNotNull { index ->
            val id = currentSelections[index] ?: return@mapNotNull null
            avatarOptionsForCategory(index).firstOrNull { it.id == id }
        }
    }

    SelectedSummaryBar(
        selected = selectedMembers,
        modifier = Modifier.fillMaxWidth(),
        isGenerateEnabled = !currentSelections[0].isNullOrBlank(),
        onGenerateClick = onGenerateClick,
        backgroundColor = Color.White
    )
}

@Composable
internal fun AddFamilyAvatarGeneratingSheet(
    state: MemojiGenState,
    selections: Map<Int, String>,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onRegenerate: () -> Unit,
    onAssign: (String) -> Unit
) {
    // Matches iOS IngrediBotWithText.swift animation timings
    // - background opacity: easeInOut 2.0s repeatForever autoreverse
    // - shimmer: linear 3.6s repeatForever
    // - bot float X: easeInOut 3.0s repeatForever autoreverse
    // - bot float Y: easeInOut 2.5s repeatForever autoreverse with ~0.5s delay

    val infinite = rememberInfiniteTransition(label = "avatarGenerating")
    val bgAlpha by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgAlpha"
    )

    // Drives the shimmer sweep from left to right across the whole illustration
    val shimmerProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )

    // Use common floating robot animation
    val (botX, botY) = OnboardingAnimations.rememberFloatingRobotOffsets(label = "avatarGenerating")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


//        // Back is optional on this state; keep behavior consistent with other sheets
//        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//            IconButton(onClick = onBackClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ion_chevron_back),
//                    contentDescription = "Back",
//                    modifier = Modifier.size(24.dp),
//                    tint = Greyscale150
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(18.dp))

        when (state) {
            is MemojiGenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(199.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background frame with subtle breathing opacity
                    Image(
                        painter = painterResource(id = R.drawable.ingredi_robo1_frame),
                        contentDescription = null,
                        modifier = Modifier
                            .width(335.dp)
                            .height(199.dp)
                            .alpha(bgAlpha),
                        contentScale = ContentScale.Fit
                    )

                    // Floating robot
                    Image(
                        painter = painterResource(id = R.drawable.ingredi_robo1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(147.dp)
                            .offset(x = botX.dp, y = botY.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Narrow shimmer band that moves horizontally across both images
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .drawWithContent {
                                // Width of the shimmer band as a fraction of total width
                                val bandWidth = size.width * 0.22f
                                // Height of the band (centered vertically) so it feels like a light sweep,
                                // not a full-height border line
                                val bandHeight = size.height * 0.7f
                                val topY = (size.height - bandHeight) / 2f
                                // Travel from fully offscreen left to fully offscreen right
                                val travel = size.width + bandWidth * 2f
                                val startX = -bandWidth + travel * shimmerProgress

                                // Horizontal gradient: faded -> bright -> faded across the band
                                val shimmerBrush = Brush.linearGradient(
                                    0f to Color.Transparent,
                                    0.25f to Color.White.copy(alpha = 0.15f),
                                    0.5f to Color.White.copy(alpha = 0.7f),
                                    0.75f to Color.White.copy(alpha = 0.15f),
                                    1f to Color.Transparent,
                                    start = androidx.compose.ui.geometry.Offset(startX, topY),
                                    end = androidx.compose.ui.geometry.Offset(
                                        x = startX + bandWidth,
                                        y = topY
                                    )
                                )

                                drawRect(
                                    brush = shimmerBrush,
                                    topLeft = androidx.compose.ui.geometry.Offset(startX, topY),
                                    size = androidx.compose.ui.geometry.Size(
                                        bandWidth,
                                        bandHeight
                                    ),
                                    blendMode = BlendMode.SrcOver
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Bringing your avatar to life... it’s\ngoing to be awesome!",
                    style = sheetTitleTextStyle(),
                    color = Greyscale150,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-30).dp)
                )
            }

            is MemojiGenState.Success -> {
                // Determine the background color from the user's selected color category (index 5)
                val selectedColorId = selections[5]
                val avatarBackgroundColor = remember(selectedColorId) {
                    avatarBackgroundColorForId(selectedColorId)
                }

                // Confetti overlay: match iOS behavior (start after 0.4s, stop after ~5.4s),
                // without changing the layout or height of the sheet.
                var showConfetti by remember { mutableStateOf(false) }
                val confettiComposition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.confetti)
                )

                LaunchedEffect(Unit) {
                    delay(400L)
                    showConfetti = true
                    delay(5000L)
                    showConfetti = false
                }

                val selectedMembers = remember(selections) {
                    (0..5).mapNotNull { index ->
                        val id = selections[index] ?: return@mapNotNull null
                        avatarOptionsForCategory(index).firstOrNull { it.id == id }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = state.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(170.dp)
                                    .clip(CircleShape)
                                    .background(avatarBackgroundColor),
                                contentScale = ContentScale.Crop
                            ) {
                                when (painter.state) {
                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(avatarBackgroundColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(160.dp),
                                                strokeWidth = 3.dp,
                                                color = Primary800
                                            )
                                        }
                                    }
                                    else -> {
                                        SubcomposeAsyncImageContent()
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Selected",
                            style = sheetSubtitleTextStyle(),
                            color = Greyscale110,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Greyscale30)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedMembers.forEach { member ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = member.iconRes),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Meet your new avatar,\nlooking good!",
                            style = sheetTitleTextStyle(),
                            color = Greyscale150,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SecondaryButton(
                                title = "Regenerate",
                                textColor = Primary800,
                                icon = R.drawable.two_star_image,
                                takeFullWidth = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = onRegenerate
                            )

                            PrimaryButton(
                                title = "Assign",
                                takeFullWidth = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                onClick = { onAssign(state.imageUrl) }
                            )
                        }
                    }

                    if (showConfetti && confettiComposition != null) {
                        LottieAnimation(
                            composition = confettiComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .matchParentSize()
                        )
                    }
                }
            }

            is MemojiGenState.Idle,
            is MemojiGenState.Error -> {
                Spacer(modifier = Modifier.height(24.dp))

                val message = when (state) {
                    is MemojiGenState.Error -> state.message
                    else -> "Avatar generation was interrupted. Please try again."
                }

                Text(
                    text = message,
                    style = sheetSubtitleTextStyle(),
                    color = Fail100,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    title = "Retry",
                    takeFullWidth = false,
                    width = 180.dp,
                    onClick = onRetry
                )
            }
        }
    }
}

@Composable
internal fun AddFamilyWelcomeSheet(
    onBackClick: () -> Unit,
    onContinue: () -> Unit
) {




    Column(
        modifier = Modifier.fillMaxWidth()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
           ) {
            SheetHeader(
                title = "",
                subtitle = null,
                onBackClick = onBackClick

            )

            Image(
                painter = painterResource(id = R.drawable.add_family_welcome_img),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.8f)
                    .height(147.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Let's meet your IngrediFam!",
            style = sheetTitleTextStyle(),
            color = Greyscale150,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add everyone in here! In the future we can tailor tips and scans just for them.",
            maxLines = 2,
            style = sheetSubtitleTextStyle(),
            color = Greyscale120.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            title = "Continue",
            onClick = onContinue,
            takeFullWidth = false,
            width = 180.dp
        )
    }
}

@Composable
internal fun AddFamilyAllSetOrMoreSheet(
    onAllSet: () -> Unit,
    onAddMore: () -> Unit
) {
    SheetHeader(
        title = "Add more members?",
        subtitle = "Start by adding their name and a fun avatar—it'll help us personalize food tips just for them.",
        onBackClick = null
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            title = "All Set!",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onAllSet,
            textColor = Primary800,
            borderColor = Primary800,
            disabledBackgroundColor = Greyscale40
        )

        PrimaryButton(
            title = "Add More",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onAddMore
        )
    }
}

enum class ProductGuidanceSubStep {
    INITIAL,
    QUICK_LOOK,
    QUICK_ACCESS
}

@Composable
internal fun GotProductHandyGuidanceSheet(
    subStep: ProductGuidanceSubStep = ProductGuidanceSubStep.INITIAL,
    onNotRightNow: () -> Unit,
    onHaveProduct: () -> Unit,
    onGotIt: () -> Unit,
    onGoToHome: () -> Unit
) {
    when (subStep) {
        ProductGuidanceSubStep.INITIAL -> {
            SheetHeader(
                title = "Ready to scan your\n first product?",
                subtitle = "Do you have any food product around you right now?",
                onBackClick = null
            )

            Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    title = "Not right now",
                    modifier = Modifier.weight(1f),
                    takeFullWidth = true,
                    width = 0.dp,
                    onClick = onNotRightNow,
                    textColor = lc.fungee.Ingredicheck.ui.theme.Primary800,
                    borderColor = Greyscale40,
                    disabledBackgroundColor = Greyscale40
                )
                PrimaryButton(
                    title = "Have a product",
                    modifier = Modifier.weight(1f),
                    takeFullWidth = true,
                    width = 0.dp,
                    onClick = onHaveProduct
                )
            }
        }
        ProductGuidanceSubStep.QUICK_LOOK -> {
            SheetHeader(
                title = "Here’s a quick look at how you can scan products when you’re ready.",
                subtitle = null,
                onBackClick = null
            )

            Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    title = "Got it",
                    onClick = onGotIt,
                    takeFullWidth = false,
                    width = 180.dp
                )
            }
        }
        ProductGuidanceSubStep.QUICK_ACCESS -> {
            SheetHeader(
                title = "Quick access needed",
                subtitle = "So we can scan products and personalize results for you.",
                onBackClick = null
            )

            Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    title = "Go to Home",
                    onClick = onGoToHome,
                    takeFullWidth = false,
                    width = 180.dp
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
internal fun AddFamilyLetsGoSheet(
    onLetsGo: () -> Unit
) {
    Spacer(modifier = Modifier.height(responsiveSpacerHeight(24.dp, 26.dp, 28.dp)))

    Text(
        text = "Personalize your Choices",
        style = sheetTitleTextStyle(),
        color = Greyscale150,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Let's get started with you! We'll create a profile just foryou and guide you through personalized food tips.",
        style = sheetSubtitleTextStyle(),
        maxLines = 2,
        color = Greyscale120,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(38.dp, 40.dp, 42.dp)))

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PrimaryButton(
            title = "Let's Go!",
            onClick = onLetsGo,
            takeFullWidth = false,
            width = 180.dp
        )
    }
}

// AddAllergiesSheet, AllergyChip, FlowRowWithRightAlignedButton, SimpleFlowRow moved to AllergyScreens.kt

@Composable
fun ChooseAvatarRow(
    title: String = "Choose Avatar",
    selectedAvatarId: String,
    avatarItems: List<Pair<String, Int>>,
    addEnabled: Boolean = true,
    onAddAvatarClick: () -> Unit,
    onAvatarSelect: (String) -> Unit
) {
    Text(
        text = title,
        style = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Greyscale150
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White)
                .alpha(if (addEnabled) 1f else 0.5f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (addEnabled) {
                        onAddAvatarClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_plus_icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .width(1.5.dp)
                .height(52.dp)
                .background(Greyscale60)
        )

        Spacer(modifier = Modifier.width(12.dp))

        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(avatarItems) { (id, res) ->
                val isSelected = selectedAvatarId == id

                // Parent box is NOT clipped so the tick icon can sit outside the circle
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner circle that holds the avatar image
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onAvatarSelect(id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Tick icon sits on top edge, outside circular clip
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 0.dp, y = 0.dp)
                                .size(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.tick_mark_green),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun EditFamilyMemberSheet(
    name: String,
    selectedAvatarId: String,
    generatedAvatarUrl: String,
    avatarBackgroundColor: Color,
    isSaveEnabled: Boolean,
    onNameChange: (String) -> Unit,
    onAvatarSelect: (String) -> Unit,
    onAddAvatarClick: () -> Unit,
    onBackClick: () -> Unit,
    onSave: () -> Unit,
    largeAvatarContent: @Composable () -> Unit
) {
    SheetHeader(
        title = "Update the name & avatar?",
        subtitle = "Update the name and give the avatar a look that truly matches their personality",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(20.dp, 24.dp, 28.dp)))

    // Large circular current avatar
    Box(
        modifier = Modifier
            .fillMaxWidth() ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(avatarBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            largeAvatarContent()
        }
    }

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(16.dp, 18.dp, 22.dp)))


    // Name input field with pen icon (grows as user types)
    val fieldShape = RoundedCornerShape(10.dp)
    val nameFieldTextStyle = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = Greyscale150,
        textAlign = TextAlign.Center
    )
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier

                .width(maxOf(250.dp, textWidth + 16.dp))
                .height(38.dp)
                .shadow(
                    elevation = 7.dp,
                    shape = fieldShape,
                    ambientColor = Color(0xFFD5D5D5),
                    spotColor = Color(0xFFD5D5D5),
                    clip = false
                )
                .background(Color.White, fieldShape)
                .border(1.dp, Greyscale80.copy(alpha = 1f), fieldShape)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.ifEmpty { " " },
                style = nameFieldTextStyle,
                modifier = Modifier
                    .alpha(0f)
                    .onGloballyPositioned { coords ->
                        textWidth = with(density) { coords.size.width.toDp() }
                    }
            )
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text
                ),
                textStyle = nameFieldTextStyle,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()
                    }
                }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .padding(start = 8.dp)
                ,
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pen_line_icon),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),

                    )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    ChooseAvatarRow(
        selectedAvatarId = selectedAvatarId,
        avatarItems = OnboardingChipData.editAvatarItems,
        onAddAvatarClick = onAddAvatarClick,
        onAvatarSelect = onAvatarSelect
    )

    Spacer(modifier = Modifier.height(12.dp))
    Row() {
        Image(
            painter = painterResource(id = R.drawable.bulb_svgrepo_com),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Choose an optional avatar or tap + to generate a new one",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Greyscale110
            )
        )
    }

    Spacer(modifier = Modifier.height(32.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PrimaryButton(
            title = "Save",
            modifier = Modifier.width(160.dp),
            takeFullWidth = false,
            onClick = onSave,
            width = 160.dp,
            isDisabled = !isSaveEnabled
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
@Composable
private fun EditFamilyMemberSheetPreview() {
    EditFamilyMemberSheet(
        name = "Bite Buddy",
        selectedAvatarId = "mom",
        generatedAvatarUrl = "",
        avatarBackgroundColor = Color(0xFFE3B8FF),
        isSaveEnabled = true,
        onNameChange = { },
        onAvatarSelect = { },
        onAddAvatarClick = { },
        onBackClick = { },
        onSave = { },
        largeAvatarContent = {
            Image(
                painter = painterResource(id = R.drawable.family_member_mom),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    )
}

@Composable
internal fun InviteConfirmationSheet(
    memberName: String,
    onMayBeLater: () -> Unit,
    onInvite: () -> Unit,
    isLoading: Boolean = false
) {
    SheetHeader(
        title = "Would you like to invite $memberName to\njoin IngrediFam?",
        subtitle = "No worries if you skip this step. You can share the code with $memberName later too.",
        onBackClick = null
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            title = "May be later",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onMayBeLater,
            textColor = Primary800,
            borderColor = Primary800,
            disabledBackgroundColor = Greyscale40
        )

        PrimaryButton(
            title = "Invite",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = if (!isLoading) onInvite else null,
            icon = if (!isLoading) R.drawable.share_icon_unfill else null,
            isLoading = isLoading,
            isDisabled = isLoading
        )
    }
}

@Composable
internal fun AddFamilyNameSheet(
    name: String,
    selectedAvatarId: String,
    generatedAvatarUrl: String,
    onNameChange: (String) -> Unit,
    onAvatarSelect: (String) -> Unit,
    onAddAvatarClick: () -> Unit,
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    isAdditionalMember: Boolean = false,
    isLoading: Boolean = false,
    showBackArrow: Boolean = true,
    isEditing: Boolean = false
) {
    val title =
        if (isEditing) {
            "Update the name & avatar?"
        } else if (isAdditionalMember) {
            "Add more members?"
        } else {
            "What’s your name?"
        }

    val subtitle =
        if (isEditing) {
            "Update the name and give the avatar a look that truly matches their personality"
        } else if (isAdditionalMember) {
            "Start by adding their name and a fun avatar—it’ll help us personalize food tips just for them."
        } else {
            "This helps us personalize your experience and scan tips—\njust for you!"
        }

    if (isEditing) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showBackArrow) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_chevron_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                        tint = Greyscale150
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = title,
                style = sheetTitleTextStyle(),
                maxLines = 1,
                color = Greyscale150,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = sheetSubtitleTextStyle(),
                color = Greyscale110,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        SheetHeader(
            title = title,
            subtitle = subtitle,
            onBackClick = if (showBackArrow) onBackClick else null
        )
    }

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(24.dp, 28.dp, 32.dp)))

    val fieldShape = RoundedCornerShape(16.dp)
    val placeholderColor = Greyscale110.copy(alpha = 0.45f)
    var showNameError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 7.dp, // closest to blur 9
                shape = fieldShape,
                ambientColor = Color(0xFFD5D5D5),
                spotColor = Color(0xFFD5D5D5),
                clip = false
            )
            .background(Color.White, fieldShape)
            .border(
                1.dp,
                if (showNameError) Fail100 else Greyscale60.copy(alpha = 1f),
                fieldShape
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = name,
            onValueChange = {
                if (showNameError && it.isNotBlank()) showNameError = false
                onNameChange(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            ),
            textStyle = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Greyscale150
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (name.isBlank()) {
            Text(
                text = "Enter your Name",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Greyscale100

                )
            )
        }
    }

    if (showNameError) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Enter Name.",
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Fail100
            ),
            modifier = Modifier.padding(start = 6.dp)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Use shared avatar list from OnboardingChipData to keep static data out of the UI layer.
    val avatarItems = OnboardingChipData.baseAvatarItems

    ChooseAvatarRow(
        title = "Choose Avatar (Optional)",
        selectedAvatarId = selectedAvatarId,
        avatarItems = avatarItems,
        addEnabled = name.isNotBlank(),
        onAddAvatarClick = {
            if (name.isBlank()) {
                showNameError = true
            } else {
                onAddAvatarClick()
            }
        },
        onAvatarSelect = onAvatarSelect
    )

    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            ,
        contentAlignment = Alignment.Center
    ) {
        val primaryLabel = when {
            isEditing -> "Save"
            isAdditionalMember -> "Add Member"
            else -> "Continue"
        }
        PrimaryButton(
            title = primaryLabel,
            modifier = Modifier.width(160.dp),
            onClick = if (!isLoading && name.isNotBlank()) onContinue else null,
            isDisabled = isLoading || name.isBlank(),
            isLoading = isLoading
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
@Composable
private fun AddFamilyNameFullPreview_Empty_Pixel4a() {
    OnboardingShell(
        onDismissRequest = { },
        backgroundContent = {
            SignInBackground(
                imageRes = R.drawable.family_img_add_family,
                showLogo = false,
                title = "Getting Started!",
                subtitle = "Add profiles so IngredientCheck can personalize results for each person.",
                aspectRatio = 1f
            )
        },
        sheetContent = {
            AddFamilyNameSheet(
                name = "",
                selectedAvatarId = "",
                generatedAvatarUrl = "",
                onNameChange = { },
                onAvatarSelect = { },
                onAddAvatarClick = { },
                onBackClick = { },
                onContinue = { }
            )
        }
    )
}


@Composable
private fun AddFamilyAllergiesFullPreview_Pixel8Pro() {
    val members = remember {
        listOf(
            lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel.FamilyOverviewMember(
                id = "preview_member_1",
                name = "Alex",
                avatarId = "mom",
                generatedAvatarUrl = "",
                joined = true,
                backgroundColorId = "",
                colorHex = "#BAFFC9",
                invitePending = false
            ),
            lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel.FamilyOverviewMember(
                id = "preview_member_2",
                name = "Sam",
                avatarId = "dad",
                generatedAvatarUrl = "",
                joined = true,
                backgroundColorId = "",
                colorHex = "#BAE1FF",
                invitePending = false
            )
        )
    }

    val selectedMemberIdState = remember { mutableStateOf(members.first().id) }
    val selectedAllergiesState = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    var dynamicLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(context) {
        DynamicStepsLoader.ensureLoaded(context)
        dynamicLoaded = true
    }
    val steps = remember(dynamicLoaded) {
        DynamicStepsLoader.getSteps()?.map { s ->
            CapsuleStep(s.id, s.header.name, OnboardingChipData.iconResForStepId(s.id))
        } ?: emptyList()
    }

    OnboardingShell(
        onDismissRequest = { },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F7))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(48.dp))
                    AnimatedProgressLine(progress = 0.1f)
                    Spacer(modifier = Modifier.height(10.dp))
                    CapsuleStepperRow(
                        steps = steps,
                        activeIndex = 0
                    )
                }
            }
        },
        sheetContent = {
            AddAllergiesSheet(
                members = members,
                selectedMemberId = selectedMemberIdState.value,
                selectedAllergies = selectedAllergiesState.toSet(),
                onMemberSelected = { selectedMemberIdState.value = it },
                onToggleAllergy = { allergyId ->
                    if (selectedAllergiesState.contains(allergyId)) {
                        selectedAllergiesState.remove(allergyId)
                    } else {
                        selectedAllergiesState.add(allergyId)
                    }
                },
                onNext = { },
                onSkipPreferences = { },
                showFineTuneDecision = false,
                showSummaryScreen = false,
                hasOtherSelection = selectedAllergiesState.any { it.contains("other", ignoreCase = true) },
                showChatBotIntro = false,
                showChatConversation = false,
                onChatBotLetsGo = {},
                onChatSkip = {},
                questionStepIndex = 0
            )
        }
    )
}
