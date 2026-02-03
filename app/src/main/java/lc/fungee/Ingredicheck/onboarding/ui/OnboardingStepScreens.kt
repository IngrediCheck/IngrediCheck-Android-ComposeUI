package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.animation.core.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.components.buttons.SecondaryButton

import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Fail100
import lc.fungee.Ingredicheck.ui.theme.Fail25
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle
import lc.fungee.Ingredicheck.ui.theme.subtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetTitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetSubtitleTextStyle
import androidx.compose.ui.platform.LocalConfiguration
import lc.fungee.Ingredicheck.ui.theme.ScreenCategory
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize
import lc.fungee.Ingredicheck.ui.theme.rememberScreenCategory

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
private fun responsiveSpacerHeight(
    small: Dp,
    medium: Dp,
    large: Dp
): Dp {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> small
        ScreenCategory.Normal -> medium
        ScreenCategory.Large -> large
    }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selected",
                style = sheetSubtitleTextStyle(),
                color = Greyscale110
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                selectedMembers.forEach { member ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = member.iconRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = member.contentScale
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
            width = 170.dp,
            isDisabled = currentSelections[0].isNullOrBlank(),
            onClick = if (!currentSelections[0].isNullOrBlank()) onGenerateClick else null
        )
    }
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

    val shimmerX by infinite.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
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
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Back is optional on this state; keep behavior consistent with other sheets
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ion_chevron_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = Greyscale150
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (state) {
            is MemojiGenState.Loading,
            is MemojiGenState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(199.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ingredi_robo1_frame),
                        contentDescription = null,
                        modifier = Modifier
                            .width(335.dp)
                            .height(199.dp)
                            .alpha(bgAlpha),
                        contentScale = ContentScale.Fit
                    )

                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ingredi_robo1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(147.dp)
                            .offset(x = botX.dp, y = botY.dp)
                            .drawWithContent {
                                drawContent()
                                // Shimmer band that sweeps horizontally
                                val bandWidth = 70.dp.toPx()
                                val startX = shimmerX.dp.toPx() - bandWidth
                                drawRect(
                                    brush = shimmerBrush,
                                    topLeft = androidx.compose.ui.geometry.Offset(startX, 0f),
                                    size = androidx.compose.ui.geometry.Size(bandWidth, size.height),
                                    blendMode = BlendMode.Overlay
                                )
                            },
                        contentScale = ContentScale.Fit
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
                            .clip(CircleShape),
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
                            .clip(RoundedCornerShape(30.dp))
                            .background(Greyscale40.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        selectedMembers.forEach { member ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
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
                            icon = R.drawable.lucide_stars_,
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

            is MemojiGenState.Error -> {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = state.message,
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
private fun SheetHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = sheetTitleTextStyle(),
            maxLines = 1,
            color = Greyscale150,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
//                    .padding(start = 21 .dp) // Adjusting for IconButton default 48dp size (12dp internal padding)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ion_chevron_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = Greyscale150
                )
            }
        }
    }

    if (subtitle != null) {
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
            iconTint = Color.Unspecified,
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
            iconTint = Color.Unspecified,
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        onClick = onVerifyContinue,
        isDisabled = inviteCode.length < 6
    )

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
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
            iconTint = Color.Unspecified,
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
            iconTint = Color.Unspecified,
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
internal fun AddFamilyNameSheet(
    name: String,
    selectedAvatarId: String,
    generatedAvatarUrl: String,
    onNameChange: (String) -> Unit,
    onAvatarSelect: (String) -> Unit,
    onAddAvatarClick: () -> Unit,
    onBackClick: () -> Unit,
    onContinue: () -> Unit
) {
    SheetHeader(
        title = "What’s your name?",
        subtitle = "This helps us personalize your experience and scan tips—\njust for you!",
        onBackClick = onBackClick
    )

    Spacer(modifier = Modifier.height(responsiveSpacerHeight(24.dp, 28.dp, 32.dp)))

    val fieldShape = RoundedCornerShape(16.dp)
    val placeholderColor = Greyscale110.copy(alpha = 0.45f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.White, fieldShape)
            .border(1.dp, Greyscale40.copy(alpha = 0.7f), fieldShape)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            ),
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Greyscale150
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (name.isBlank()) {
            Text(
                text = "Enter your Name",
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = placeholderColor
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(22.dp))

    Text(
        text = "Choose Avatar (Optional)",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Greyscale150
        )
    )

    Spacer(modifier = Modifier.height(12.dp))

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isAddEnabled = name.isNotBlank()
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White)
                .alpha(if (isAddEnabled) 1f else 0.4f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (isAddEnabled) onAddAvatarClick() },
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
                .width(1.dp)
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
                Box(
                    modifier = Modifier
                        .size(52.dp)
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
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    PrimaryButton(
        title = "Continue",
        onClick = if (name.isNotBlank()) onContinue else null,
        isDisabled = name.isBlank()
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4A)
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
