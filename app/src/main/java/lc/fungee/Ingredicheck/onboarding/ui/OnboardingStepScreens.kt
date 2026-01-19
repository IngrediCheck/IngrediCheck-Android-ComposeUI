package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Primary800

@Composable
internal fun SignInBackground(
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    SignInBackgroundTunable(
        imageRes = imageRes,
        modifier = modifier,
        mockWidthFraction = 0.85f,
        mockAspectRatio = 8f / 16f
    )
}

@Composable
private fun SignInBackgroundTunable(
    imageRes: Int,
    mockWidthFraction: Float,
    mockAspectRatio: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
               Color.White
                )

            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 68.dp)
    ) {
        val heightPx = constraints.maxHeight.toFloat()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ingredicheck_text_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(44.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ,
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
//                           .fillMaxWidth(mockWidthFraction)
                            .aspectRatio(mockAspectRatio)
                            .fillMaxWidth()
//
                            ,
                        contentScale = ContentScale.Fit
                    )
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

        Text(
            text = "Are you an existing user?",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Greyscale150
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Have you used IngrediCheck earlier? If yes, continue.\nIf not, start new.",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale110,
                lineHeight = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

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
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
                tint = Greyscale150
            )
        }

        Text(
            text = "Welcome back !",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Greyscale150
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Log in to your existing IngrediCheck account.",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Greyscale110
        ),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryButton(
            title = "Google",
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
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
                tint = Greyscale150
            )
        }

        Text(
            text = "Do you have an invite code?",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Greyscale150
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Got a family invite to IngrediFam? Enter code.",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Greyscale110
        ),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

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
    onInviteCodeChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onVerifyContinue: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
                tint = Greyscale150
            )
        }

        Text(
            text = "Enter your invite code",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Greyscale150
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "This connects you to your family or shared IngrediCheck space.",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Greyscale110,
            lineHeight = 18.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

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
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Characters
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
                    inviteCode = inviteCode
                )
            }

            Text(
                text = "—",
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Greyscale40
                )
            )

            repeat(3) { index ->
                InviteCodeBox(
                    index = index + 3,
                    inviteCode = inviteCode
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "You can add this later if you receive one.",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Greyscale110.copy(alpha = 0.6f)
        )
    )

    Spacer(modifier = Modifier.height(32.dp))

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
        Icon(
            imageVector = Icons.Sharp.AccountCircle,
            contentDescription = null,
            tint = Greyscale110.copy(alpha = 0.6f),
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
    inviteCode: String
) {
    Box(
        modifier = Modifier
            .size(48.dp, 60.dp)
            .background(
                color = if (inviteCode.length == index) Color(0xFFFFF7EB) else Greyscale40.copy(alpha = 0.3f),
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
                color = Greyscale150
            )
        )
        if (inviteCode.length == index) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
internal fun SignInWhoIsThisForSheet(
    onBackClick: () -> Unit,
    onJustMe: () -> Unit,
    onAddFamily: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
                tint = Greyscale150
            )
        }

        Text(
            text = "Hey there! Who’s this for?",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Greyscale150
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Is it just you, or your whole IngrediFam — family, friends, anyone you care about?",
        style = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Greyscale110,
            lineHeight = 18.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionChip(
            title = "Just Me",
            onClick = onJustMe,
            modifier = Modifier.weight(1f)
        )

        OptionChip(
            title = "Add Family",
            onClick = onAddFamily,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

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

@Composable
private fun OptionChip(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .height(52.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                spotColor = Color(0xFFCECECE).copy(alpha = 0.39f),
                ambientColor = Color(0xFFCECECE).copy(alpha = 0.39f)
            )
            .background(Color.White, shape)
            .border(1.5.dp, Greyscale40, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Primary800
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4A)
@Composable
private fun SignInBackgroundPreview_Pixel4a_Ratio9x16() {
    SignInBackgroundTunable(
        imageRes = R.drawable.iphone_app_img,
        mockWidthFraction = 0.85f,
        mockAspectRatio = 9f / 16f
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
@Composable
private fun SignInBackgroundPreview_Pixel8Pro_Ratio9x16() {
    SignInBackgroundTunable(
        imageRes = R.drawable.iphone_app_img,
        mockWidthFraction = 0.85f,
        mockAspectRatio = 9f / 16f
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_8_PRO)
@Composable
private fun SignInBackgroundPreview_Pixel8Pro_Ratio10x16() {
    SignInBackgroundTunable(
        imageRes = R.drawable.iphone_app_img,
        mockWidthFraction = 0.88f,
        mockAspectRatio = 9f / 16f
    )
}
