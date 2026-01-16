package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.PIXEL_5
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Primary800

@Composable
fun SignInOptionsScreen(
    onExistingUserContinue: () -> Unit,
    onStartNew: () -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    var showSocialLogin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xF7F7F7F7)
                )
            ))
            .padding(horizontal =  68.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp)
                .navigationBarsPadding(),
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
                    .height(450.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iphone_app_img),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Linear gradient shadow can increse the height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 223.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFFFFFFFF))
                    )
                )
        )

        NonDraggableBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = Modifier
        ) {
            if (!showSocialLogin) {
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
                        onClick = { showSocialLogin = true },
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
            } else {
                // Welcome Back View
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { showSocialLogin = false },
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                            onClick = { /* Handle Google Sign-in */ },
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
                            onClick = { /* Handle Apple Sign-in */ },
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
            }
        }
    }
}

@Preview(showBackground = true , showSystemUi =  true , device = PIXEL_6)
@Composable
private fun SignInOptionsScreenPreview() {
    SignInOptionsScreen(
        onExistingUserContinue = {},
        onStartNew = {}
    )
}
