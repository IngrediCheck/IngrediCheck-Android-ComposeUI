package lc.fungee.Ingredicheck.ui.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

/**
 * Intro screen for IngrediBot chat, matching the iOS "Hey! I'm IngrediBot" design.
 * This only covers the UI; chat behaviour is wired separately.
 */
@Composable
fun ChatBotIntroScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Make the sheet take roughly 90% of the height when used inside NonDraggableBottomSheet.
            .fillMaxHeight(0.86f),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            // Floating IngrediBot image at the top‑center, rotated by 10 degrees.
            Image(
                painter = painterResource(id = R.drawable.ingredi_robo2),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 190.dp, height = 180.dp)
                    .rotate(10f),
                contentScale = ContentScale.Fit
            )

            // "Hey! 👋 I’m IngrediBot," subtitle
            val introText = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Greyscale100
                    )
                ) {
                    append("Hey! 👋 I’m ")
                }
                withStyle(
                    SpanStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF91B640)
                    )
                ) {
                    append("IngrediBot,")
                }
            }

            Text(
                text = introText,
                textAlign = TextAlign.Center
            )

            // Title: "How about making food choices easier together?"
            Text(
                text = "How about making food choices easier together?",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Greyscale150,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(182.dp))
            Text(
                text = "Shall we get started?",
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Greyscale110,
                textAlign = TextAlign.Center,


            )


            val introsubText = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Greyscale110
                    )
                ) {
                    append("I noticed you selected ")
                }
                withStyle(
                    SpanStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Greyscale140
                    )
                ) {
                    append("“Other”")
                }
                withStyle(
                    SpanStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Greyscale110
                    )
                ) {
                    append(" earlier, that’s great! Could you tell me a bit more about it?")
                }
            }
            Text(
                text = introsubText,
                textAlign = TextAlign.Center
            )
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp))
            {
                SecondaryButton("Maybe later" ,
                    textColor = Color(color =0xFF75990E),
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton("Yes, let’s go" , modifier = Modifier.weight(1f))

            }

            Text(text = "No problem! You can come back anytime — I’ll be here when you’re ready.",
                fontWeight = FontWeight.Normal,
                fontSize =  12.sp ,
                fontFamily = Manrope ,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatBotIntroScreenPreview() {
    NonDraggableBottomSheet(
        onDismissRequest = {},
        horizontalPaddingEnabled = true
    ) {
        ChatBotIntroScreen()
    }
}

