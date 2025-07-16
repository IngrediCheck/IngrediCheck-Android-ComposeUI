package lc.fungee.IngrediCheck.onboarding

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lc.fungee.IngrediCheck.R

@Composable
fun WelcomeScreen() {

    val welcomeScreenItem = listOf(
        WelcomeOnboardingItem(
            heading = "Personalize your dietary preferences",
            description = "Enter dietary needs in plain language to tailor your food choices",
            R.drawable.ingredichecklogo
        ),
        WelcomeOnboardingItem(
            heading = "Simplify your food label checks",
            description = "Scan barcodes for a detailed breakdown of ingredients",
            R.drawable.ingredichecklogo
        ),
        WelcomeOnboardingItem(
            heading = "Never forget your favorite items again.",
            description = "Save items to your custom list for quick access and easy reference",
            R.drawable.ingredichecklogo
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
    ) {
        // HorizontalPager takes most of the screen
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            WelcomePager(
                item = welcomeScreenItem[page],
                modifier = Modifier.fillMaxSize()
            )
        }

        // Page indicators at the bottom
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // ✅ keep alignment
                .padding(16.dp)                      // ✅ keep padding
        ) {
            repeat(welcomeScreenItem.size) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (pagerState.currentPage == index) {
                                Color(0xFFFF9500) // active dot (orange)
                            } else {
                                Color(0xFF8D8C8C) // inactive dot (white)
                            },
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

    }
    }
