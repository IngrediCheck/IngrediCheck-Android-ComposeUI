package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.memoji.FillingPipeLine
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Fail100
import lc.fungee.Ingredicheck.ui.theme.Fail25
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale90
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize
import lc.fungee.Ingredicheck.ui.theme.sheetSubtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetTitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.subtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle

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
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = if (showLogo) 68.dp else 0.dp)
    ) {
        val heightPx = constraints.maxHeight.toFloat()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 44.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.ingredicheck_text_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(mockWidthFraction).height(40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(44.dp))
                }
                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(mockAspectRatio).fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.matchParentSize().background(
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
        FillingPipeLine(onComplete = onFillComplete)
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
            textColor = lc.fungee.Ingredicheck.ui.theme.Primary800,
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
            iconHeight = buttonIconSize() - 2.dp,
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
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    SheetHeader(
        title = "Enter your invite code",
        subtitle = "This connects you to your family or shared IngrediCheck space.",
        onBackClick = onBackClick
    )
    Spacer(modifier = Modifier.height(responsiveSpacerHeight(36.dp, 40.dp, 44.dp)))
    Box(contentAlignment = Alignment.Center) {
        BasicTextField(
            value = inviteCode,
            onValueChange = { if (it.length <= 6) onInviteCodeChange(it.uppercase()) },
            modifier = Modifier
                .size(1.dp)
                .drawWithContent { }
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
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
                InviteCodeBox(index = index, inviteCode = inviteCode, isError = isError)
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
                InviteCodeBox(index = index + 3, inviteCode = inviteCode, isError = isError)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (isError) {
        Text(
            text = "We couldn't verify your code. Please try again..",
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
        title = "Hey there! Who's this for?",
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You can always add or edit members later.",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale90
            ),
            textAlign = TextAlign.Center
        )
    }
}
