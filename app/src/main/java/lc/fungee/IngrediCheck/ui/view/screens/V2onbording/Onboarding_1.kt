package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R

@Preview(showBackground = true)
@Composable
fun Onboarding1Preview() {
    val navController = rememberNavController() // fake NavController for preview
    Onboarding1(
        navController, // pass it in
        onSkip = {},
        onFinish = {}
    )
}

// Data class for page
data class OnboardingPage(
    val title: String,
    val description: String,
)
@Composable
fun Onboarding1(
    navController: NavController, // pass it in
    onSkip: () -> Unit = {},
    onFinish: () -> Unit = {}
) {
    val baseWidth = 375f
    val baseHeight = 812f

    val pages = listOf(
        OnboardingPage(
            title = "Know What's Inside, Instantly",
            description = "Scan any product and get clear, simple answers—no more confusing labels."
        ),
        OnboardingPage(
            title = "Track Your Health Easily",
            description = "Monitor your diet and make smarter choices for your health."
        ),
        OnboardingPage(
            title = "Get Personalized Suggestions",
            description = "Receive tips and recommendations based on your preferences."
        )
    )

    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val widthFactor = screenWidth.value / baseWidth
        val heightFactor = screenHeight.value / baseHeight

        val ellipseWidth = 282.dp * widthFactor
        val ellipseHeight = 377.dp * heightFactor

        // Skip button with click handler
        TextButton(
            onClick = {navController.navigate("onboarding4") {
                launchSingleTop = true
            }
                      },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp)
        ) {
            Text(
                text = "Skip",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager content for ellipse and text
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f, fill = false)
            ) { page ->
                val currentPage = pages[page]

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ellipse
                    Canvas(
                        modifier = Modifier
                            .size(width = ellipseWidth, height = ellipseHeight)
                    ) {
                        drawOval(color = Color(0xFFD9D9D9))
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Text content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = currentPage.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        Text(
                            text = currentPage.description,
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        )
                    }
                }
            }

            // Fixed spacing after text
            Spacer(modifier = Modifier.height(20.dp))

            // Fixed Pager indicator and navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                CustomPagerIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage
                )

                // Animate between IconButton and Get Started button
                AnimatedContent(
                    targetState = pagerState.currentPage == pages.size - 1,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    label = "button_transition"
                ) { isLastPage ->
                    if (isLastPage) {
                        // Get Started button
                        Box(
                            modifier = Modifier
                                .width(109.dp)
                                .height(36.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors =  listOf(
                                            Color(0xFF8DB90D),  // Start color (green)
                                            Color(0xFF6B8E06)
                                        )
                                    ),
                                    shape = RoundedCornerShape(percent =  50)
                                )
                                .clickable {
                                    onFinish()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Get Started",
                                color = Color(0xFFFFFFFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Next arrow button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors =  listOf(
                                            Color(0xFF8DB90D),  // Start color (green)
                                            Color(0xFF6B8E06)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.rightarrow),
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    }
@Composable
fun CustomPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    val dotSize = 4.dp
    val dotSpacing = 6.dp
    val lineHeight = 4.dp

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(lineHeight)
    ) {
        // Background dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(dotSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            repeat(pageCount + 1) { // pageCount + 1 dots
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .background(Color(0xFF75990E), shape = CircleShape)
                )
            }
        }

        // Active indicator line
        Box(
            modifier = Modifier
                .offset(x = (dotSize + dotSpacing) * currentPage)
                .width((dotSize * 2) + dotSpacing+(2.dp)) // cover 2 dots
                .height(lineHeight)
                .background(brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8DB90D),  // Start color (green)
                        Color(0xFF6B8E06)   // End color (light green)
                    )
                ), shape = RoundedCornerShape(2.dp))
        )
    }
}