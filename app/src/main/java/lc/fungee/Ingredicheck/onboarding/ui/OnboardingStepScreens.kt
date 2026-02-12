package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.auth.MemojiGenState
import lc.fungee.Ingredicheck.memoji.FillingPipeLine
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.onboarding.data.avatarOptionsForCategory
import lc.fungee.Ingredicheck.onboarding.ui.components.AvatarCategoryTabs
import lc.fungee.Ingredicheck.onboarding.ui.components.SelectedSummaryBar
import lc.fungee.Ingredicheck.onboarding.ui.components.FamilyMemberSelector

import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Fail100
import lc.fungee.Ingredicheck.ui.theme.Fail25
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale80
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary100
import lc.fungee.Ingredicheck.ui.theme.Primary700
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle
import lc.fungee.Ingredicheck.ui.theme.subtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetTitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetSubtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize

@Composable
internal fun SignInBackground(
    imageRes: Int,
    modifier: Modifier = Modifier,
    showLogo: Boolean = true,
    title: String? = null,
    subtitle: String? = null,
    aspectRatio: Float = 8f / 16f
) {
    SignInBackgroundTunable(
        imageRes = imageRes,
        modifier = modifier,
        mockWidthFraction = 0.85f,
        mockAspectRatio = aspectRatio,
        showLogo = showLogo,
        title = title,
        subtitle = subtitle
    )
}

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
                painter = painterResource(id = R.drawable.ion_chevron_back),
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

    val botX by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "botX"
    )

    val botY by infinite.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, delayMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "botY"
    )

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
                Spacer(modifier = Modifier.height(10.dp))

                // Determine the background color from the user's selected color category (index 5)
                val selectedColorId = selections[5]
                val avatarBackgroundColor = remember(selectedColorId) {
                    when (selectedColorId) {
                        "color_pastel_blue" -> Color(0xFFA5D8FF)
                        "color_warm_pink" -> Color(0xFFFFB3C1)
                        "color_soft_green" -> Color(0xFFB9FBC0)
                        "color_lavender" -> Color(0xFFE3B8FF)
                        "color_orange" -> Color(0xFFFFB74D)
                        "color_yellow" -> Color(0xFFFFE082)
                        "color_transparent" -> Color.Transparent
                        else -> Color.White
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = state.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(avatarBackgroundColor),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val selectedMembers = remember(selections) {
                    (0..5).mapNotNull { index ->
                        val id = selections[index] ?: return@mapNotNull null
                        avatarOptionsForCategory(index).firstOrNull { it.id == id }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Selected",
                        style = sheetSubtitleTextStyle(),
                        color = Greyscale110
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
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
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = member.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryButton(
                            title = "Regenerate",
                            textColor = Primary800,
                            icon = R.drawable.two_star_image  ,
                            takeFullWidth = true,
                            modifier = Modifier.weight(1f),
                            onClick = onRegenerate
                        )

                        PrimaryButton(
                            title = "Assign",
                            takeFullWidth = true,
                            modifier = Modifier.weight(1f),
                            onClick = { onAssign(state.imageUrl) }
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
private fun SignInBackgroundTunable(
    imageRes: Int,
    mockWidthFraction: Float,
    mockAspectRatio: Float,
    modifier: Modifier = Modifier,
    showLogo: Boolean = true,
    title: String? = null,
    subtitle: String? = null
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color.White
            )

            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = if (showLogo) 68.dp else 0.dp)
    ) {
        val heightPx = constraints.maxHeight.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.ingredicheck_text_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(mockWidthFraction)
                            .height(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(44.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {


                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(mockAspectRatio)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = titleTextStyle(),
                                color = Greyscale150,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                style = subtitleTextStyle(),
                                maxLines = 2,
                                color = Greyscale110,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFFFFFFFF)),
                            startY = heightPx * 0.55f,
                            endY = heightPx
                        )
                    )
            )
        }
    }
}

@Composable
internal fun GetStartedBackground(
    modifier: Modifier = Modifier,
    onFillComplete: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 16.dp)
            .padding(horizontal = 14.dp)
    ) {
        FillingPipeLine(
            onComplete = onFillComplete
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 18.dp, bottom = 18.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.onbording_getstartedimg),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
internal fun GetStartedSheet(
    isFillingComplete: Boolean,
    onPrimaryClick: () -> Unit
) {
    PrimaryButton(
        title = "Get Started",
        takeFullWidth = true,
        width = 0.dp,
        isDisabled = !isFillingComplete,
        disabledBackgroundColor = Greyscale40,
        onClick = onPrimaryClick
    )
}

@Composable
internal fun SignInInitialSheet(
    onExistingUserContinue: () -> Unit,
    onStartNew: () -> Unit
) {
    SheetHeader(
        title = "Are you an existing user?",
        subtitle = "Have you used IngrediCheck earlier? If yes, continue.\nIf not, start new."
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                title = "Yes, continue",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                onClick = onExistingUserContinue,
                textColor = Primary800,
                borderColor = Greyscale40,
                disabledBackgroundColor = Greyscale40
            )

            PrimaryButton(
                title = "No, start new",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                onClick = onStartNew
            )
        }

}

@Composable
internal fun SignInSocialLoginSheet(
    onBackClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit
) {
    SheetHeader(
        title = "Welcome back !",
        subtitle = "Log in to your existing IngrediCheck account.",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            title = "Google",
            icon = R.drawable.google_logo,
            iconHeight =  (buttonIconSize()-2.dp),
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onGoogleClick,
            textColor = Greyscale150,
            borderColor = Greyscale40,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )

        SecondaryButton(
            title = "Apple",
            icon = R.drawable.apple_logo,
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onAppleClick,
            textColor = Greyscale150,
            borderColor = Greyscale40,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
internal fun SignInInviteCodeSheet(
    onBackClick: () -> Unit,
    onEnterInviteCode: () -> Unit,
    onNoContinue: () -> Unit
) {
    SheetHeader(
        title = "Do you have an invite code?",
        subtitle = "Got a family invite to IngrediFam? Enter code.",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        SecondaryButton(
            title = "Enter invite code",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onEnterInviteCode,
            textColor = Greyscale150,
            borderColor = Greyscale40,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        PrimaryButton(
            title = "No, Continue",
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            onClick = onNoContinue
        )
    }
}

@Composable
internal fun SignInEnterInviteCodeSheet(
    inviteCode: String,
    isError: Boolean = false,
    onInviteCodeChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onVerifyContinue: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SheetHeader(
        title = "Enter your invite code",
        subtitle = "This connects you to your family or shared IngrediCheck space.",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))
    Box(contentAlignment = Alignment.Center) {
        BasicTextField(
            value = inviteCode,
            onValueChange = {
                if (it.length <= 6) {
                    onInviteCodeChange(it.uppercase())
                }
            },
            modifier = Modifier
                .size(1.dp)
                .drawWithContent { } // Alternative to alpha(0f) to ensure no cursor is drawn
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
            )
        )

        Row(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusRequester.requestFocus() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                InviteCodeBox(
                    index = index,
                    inviteCode = inviteCode,
                    isError = isError
                )
            }

            Text(
                text = "—",
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isError) Fail25 else Greyscale40
                )
            )

            repeat(3) { index ->
                InviteCodeBox(
                    index = index + 3,
                    inviteCode = inviteCode,
                    isError = isError
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isError) {
        Text(
            text = "We couldn’t verify your code. Please try again..",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Fail100
            )
        )
    } else {
        Text(
            text = "You can add this later if you receive one.",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale110.copy(alpha = 0.6f)
            )
        )
    }

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))
    PrimaryButton(
        title = "Verify & Continue",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        onClick = onVerifyContinue,
        isDisabled = inviteCode.length < 6
    )

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
       Image(
           painter = painterResource(R.drawable.shield_half),
           contentDescription = null,
           modifier = Modifier.size(16.dp)
       )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "By continuing, you agree to our Terms & Privacy Policy.",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Greyscale110.copy(alpha = 0.6f)
            )
        )
    }
}



@Composable
private fun InviteCodeBox(
    index: Int,
    inviteCode: String,
    isError: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp, 50.dp)
            .background(
                color = when {
                    isError -> Fail25
                    inviteCode.length == index -> Color(0xFFFFF7EB)
                    else -> Greyscale40.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (index < inviteCode.length) inviteCode[index].toString() else "",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (isError) Fail100 else Greyscale150
            )
        )
        if (inviteCode.length == index) {
            val infiniteTransition = rememberInfiniteTransition(label = "cursorAnimation")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "cursorAlpha"
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .graphicsLayer(alpha = alpha)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
internal fun SignInWhoIsThisForSheet(
    onBackClick: () -> Unit,
    isLoading: Boolean,
    onJustMe: () -> Unit,
    onAddFamily: () -> Unit
) {
    SheetHeader(
        title = "Hey there! Who’s this for?",
        subtitle = "Is it just you, or your whole IngrediFam — family, friends, anyone you care about?",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            title = "Just Me",
            onClick = onJustMe,
            icon = R.drawable.user_green,
            iconHeight = buttonIconSize() - 4.dp,
            iconWidth = buttonIconSize() - 4.dp,
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            isLoading = isLoading,
            isDisabled = isLoading,
            textColor = Greyscale150,
            borderColor = Greyscale40,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )

        SecondaryButton(
            title = "Add Family",
            onClick = onAddFamily,
            icon = R.drawable.users_group,
            iconHeight = buttonIconSize() - 4.dp,
            iconWidth = buttonIconSize() - 4.dp,
            modifier = Modifier.weight(1f),
            takeFullWidth = true,
            width = 0.dp,
            isLoading = isLoading,
            isDisabled = isLoading,
            textColor = Greyscale150,
            borderColor = Greyscale40,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
    Text(
        text = "You can always add or edit members later.",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Greyscale110.copy(alpha = 0.6f)
        ),
        textAlign = TextAlign.Center
    )
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
            color = Greyscale110.copy(alpha = 0.6f),
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
        text = "Let's get started with you! We'll create a profile just for\nyou and guide you through personalized food tips.",
        style = sheetSubtitleTextStyle(),
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

@Composable
internal fun AddFamilyAllergiesSheet(
    members: List<lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel.FamilyOverviewMember>,
    selectedMemberId: String,
    selectedAllergies: Set<String>,
    onMemberSelected: (String) -> Unit,
    onToggleAllergy: (String) -> Unit,
    onNext: () -> Unit
) {
    val fallbackMembers = remember(members) {
        if (members.isNotEmpty()) members else emptyList()
    }

    val resolvedSelectedId = remember(selectedMemberId, fallbackMembers) {
        if (fallbackMembers.any { it.id == selectedMemberId }) selectedMemberId
        else fallbackMembers.firstOrNull()?.id.orEmpty()
    }

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(16.dp, 20.dp, 24.dp)))

    Text(
        text = "Does anyone in your IngrediFam\nhave allergies we should know ?",
        style = titleTextStyle(),
        color = Greyscale150,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Select all that apply to keep meals worry-free.",
        style = subtitleTextStyle(),
        color = Greyscale120,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    if (fallbackMembers.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(fallbackMembers) { m ->
                val isSelected = m.id == resolvedSelectedId
                val avatarRes = familyAvatarResOrNull(m.avatarId)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onMemberSelected(m.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Primary700 else Greyscale40,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarRes != null) {
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Greyscale40)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (m.name.length > 8) m.name.take(8) + "…" else m.name,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (isSelected) Primary700 else Greyscale120,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.bulb_svgrepo_com),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Select members one by one to personalize their choices.",
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Greyscale120
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    val allergies = remember {
        listOf(
            "peanuts" to "Peanuts",
            "tree_nuts" to "Tree nuts",
            "dairy" to "Dairy",
            "eggs" to "Eggs",
            "soy" to "Soy",
            "wheat" to "Wheat",
            "fish" to "Fish",
            "shellfish" to "Shellfish",
            "sesame" to "Sesame",
            "celery" to "Celery",
            "lupin" to "Lupin",
            "sulphites" to "Sulphites",
            "mustard" to "Mustard",
            "molluscs" to "Molluscs",
            "other" to "Other"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            SimpleFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = 10.dp,
                verticalSpacing = 10.dp
            ) {
                allergies.forEach { (id, label) ->
                    val isSelected = selectedAllergies.contains(id)
                    AllergyChip(
                        label = label,
                        selected = isSelected,
                        onClick = { onToggleAllergy(id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Primary700)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
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
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) Primary100 else Color.White)
            .border(1.dp, Greyscale40, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Greyscale150,
            maxLines = 1
        )
    }
}

@Composable
private fun SimpleFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val spacingX = horizontalSpacing.roundToPx()
        val spacingY = verticalSpacing.roundToPx()

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

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

        val height = (y + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(width = maxWidth, height = height) {
            placeables.forEachIndexed { i, p ->
                val pos = positions[i]
                p.placeRelative(pos[0], pos[1])
            }
        }
    }
}

private fun familyAvatarResOrNull(avatarId: String): Int? {
    return when (avatarId) {
        "baby_boy" -> R.drawable.family_member_baby
        "baby_girl" -> R.drawable.family_member_baby_girl
        "young_daughter" -> R.drawable.young_daughter_onehand
        "young_son" -> R.drawable.family_member_young_son
        "mom" -> R.drawable.family_member_mom
        "father" -> R.drawable.family_member_father
        "grand_mother" -> R.drawable.family_member_grand_mother
        "grand_father" -> R.drawable.family_member_grand_father
        "dog_avtar" -> R.drawable.avtar_dog
        "cat_avtar" -> R.drawable.avtar_cat
        "litch_avtar" -> R.drawable.avtar_lichi
        "pear_avtar" -> R.drawable.avtar_pear
        "potato_avtar" -> R.drawable.avtar_potatto
        "tomato_avtar" -> R.drawable.avtar_tomato
        else -> null
    }
}

private val editSheetAvatarItems = listOf(
    "baby_boy" to R.drawable.family_member_baby,
    "baby_girl" to R.drawable.family_member_baby_girl,
    "young_daughter" to R.drawable.young_daughter_onehand,
    "young_son" to R.drawable.family_member_young_son,
    "mom" to R.drawable.family_member_mom,
    "father" to R.drawable.family_member_father,
    "grand_mother" to R.drawable.family_member_grand_mother,
    "grand_father" to R.drawable.family_member_grand_father,
    "dog_avtar" to R.drawable.avtar_dog,
    "cat_avtar" to R.drawable.avtar_cat,
    "litch_avtar" to R.drawable.avtar_lichi,
    "pear_avtar" to R.drawable.avtar_pear,
    "potato_avtar" to R.drawable.avtar_potatto,
    "tomato_avtar" to R.drawable.avtar_tomato
)

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
        avatarItems = editSheetAvatarItems,
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
                        painter = painterResource(id = R.drawable.ion_chevron_back),
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

    val avatarItems = listOf(
        "baby_boy" to R.drawable.family_member_baby,
        "baby_girl" to R.drawable.family_member_baby_girl,
        "young_daughter" to R.drawable.young_daughter_onehand,
        "young_son" to R.drawable.family_member_young_son,
        "mom" to R.drawable.family_member_mom,
        "father" to R.drawable.family_member_father,
        "grand_mother" to R.drawable.family_member_grand_mother,
        "grand_father" to R.drawable.family_member_grand_father,
        "dog_avtar" to R.drawable.avtar_dog,
        "cat_avtar" to R.drawable.avtar_cat,
        "litch_avtar" to R.drawable.avtar_lichi,
        "pear_avtar" to R.drawable.avtar_pear,
        "potato_avtar" to R.drawable.avtar_potatto,
        "tomato_avtar" to R.drawable.avtar_tomato
    )

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
//        backgroundContent = {
//            SignInBackground(
//                imageRes = R.drawable.family_img_add_family,
//                showLogo = false,
//                title = "Getting Started!",
//                subtitle = "Add profiles so IngredientCheck can personalize results for each person.",
//                aspectRatio = 1f
//            )
//        },
//        sheetContent = {
//            AddFamilyNameSheet(
//                name = "Alex",
//                selectedAvatarId = "baby_boy",
//                onNameChange = { },
//                onAvatarSelect = { },
//                onBackClick = { },
//                onContinue = { }
//            )
//        }
//    )
//}



//@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
//@Composable
//private fun SignInBackgroundPreview_Pixel8Pro_Ratio9x16() {
//    SignInBackgroundTunable(
//        imageRes = R.drawable.iphone_app_img,
//        mockWidthFraction = 0.85f,
//        mockAspectRatio = 9f / 16f
//    )
//}
//
//@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
//@Composable
//private fun SignInBackgroundPreview_Pixel8Pro_Ratio10x16() {
//    SignInBackgroundTunable(
//        imageRes = R.drawable.iphone_app_img,
//        mockWidthFraction = 0.88f,
//        mockAspectRatio = 9f / 16f
//    )
//}
