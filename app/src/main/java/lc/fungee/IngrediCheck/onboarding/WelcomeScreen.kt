package lc.fungee.IngrediCheck.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R

@Composable
fun WelcomeScreen() {
    val welcomeScreenItem = listOf(
        WelcomeOnboardingItem(
            heading = "Personalize your dietary preferences",
            description = "Enter dietary needs in plain language to tailor your food choices",
            R.drawable.welcome1
        ),
        WelcomeOnboardingItem(
            heading = "Simplify your food label checks",
            description = "Scan barcodes for a detailed breakdown of ingredients",
            R.drawable.welcome2
        ),
        WelcomeOnboardingItem(
            heading = "Never forget your favorite items again.",
            description = "Save items to your custom list for quick access and easy reference",
            R.drawable.welcome3
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { welcomeScreenItem.size }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) { page ->
            WelcomePager(
                item = welcomeScreenItem[page],
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            repeat(welcomeScreenItem.size) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (pagerState.currentPage == index) {
                                Color(0xFF789D0E)
                            } else {
                                Color(0xFFDDDEDA)
                            },
                            shape = CircleShape
                        )
                )
            }
        }

        GoogleSignInButton(onClick = { /* Handle Sign-In */ })

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Continue as guest",
            color = Color(0xFF789D0E),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        val annotatedText = buildAnnotatedString {
            append("By continuing, you are agreeing to our ")
            pushStyle(SpanStyle(color = Color(0xFF789D0E), textDecoration = TextDecoration.Underline))
            append("Terms of use")
            pop()
            append(" and ")
            pushStyle(SpanStyle(color = Color(0xFF789D0E), textDecoration = TextDecoration.Underline))
            append("Privacy Policy")
            pop()
        }

        Text(
            text = annotatedText,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth(),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF789D0E)),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.social_icon),
                contentDescription = "Google logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign in with Google",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
