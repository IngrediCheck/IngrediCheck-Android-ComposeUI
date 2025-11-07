package lc.fungee.IngrediCheck.ui.view.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.Greyscale200
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.PrimarayGreen50
import lc.fungee.IngrediCheck.ui.theme.White

@Composable
fun PreferenceEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White),
        verticalArrangement = Arrangement.Center, // center vertically
        horizontalAlignment = Alignment.CenterHorizontally // center horizontally

    ) {
        Image(
            painter = painterResource(id = R.drawable.emptystateillustration),
            contentDescription = "Your image",
            modifier = Modifier
                .width(201.dp)
                .height(180.dp)
                .alpha(1.0f)
                .align(Alignment.Companion.CenterHorizontally)
        )
        Spacer(modifier = Modifier.Companion.height(104.dp))
        Text(
            text = "Try the following",
            color = Greyscale500,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.Companion.CenterHorizontally),
            style = TextStyle(
                fontFamily = FontFamily.Companion.SansSerif,
                fontWeight = FontWeight.Companion.Normal,
                fontSize = 20.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.32).sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))
        // Base pages (real content)
        val basePages = listOf(
            buildAnnotatedString {
                append("\"Avoid ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("Sugar") }
               append(".\"")
            },
            buildAnnotatedString {
                append("\"No ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("Plam oil ") }
                append("for me.\"")

            },
            buildAnnotatedString {
                append("\"No ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("animal products") }
                append(", but ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("eggs") }
                append(" & ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("dairy ") }
                append("are ok.\"")
            },
            buildAnnotatedString {
                append("\"NO ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("peanuts ") }
                append("but other ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("nuts ") }
                append("are ok.\"")
            }, buildAnnotatedString {
                append("\"gluten ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("free") }
                append(".\"")
            }, buildAnnotatedString {
                append("\"i can,t stand ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Companion.Bold)) { append("garlic") }
                append(".\"")
            }

        )
        // Add 3 dummy pages after the first real swipe
        val totalPages = basePages.size
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })
        Column(
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            // Static container box stays fixed; only the text content inside swipes
            Box(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(112.dp)
                    .padding(horizontal = 8.dp)
                    .background(
                        color = PrimarayGreen50,
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    ),
                contentAlignment = Alignment.Companion.Center,

            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(112.dp)
                ) { page ->
                    val annotatedText = if (page < basePages.size) {
                        basePages[page]
                    } else {
                        buildAnnotatedString { append(" Add Prefrences...") }
                    }
                    // Center the text in the Box
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = annotatedText,
                            modifier = Modifier.Companion.padding(horizontal = 16.dp),
                            fontSize = 20.sp,
                            color = AppColors.Brand
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Map many pages to 3 dots: first swipe moves to middle dot and stays for next 4 swipes
                val activeDot = when {
                    pagerState.currentPage <= 0 -> 0
                    pagerState.currentPage in 1..4 -> 1
                    else -> 2
                }
                repeat(3) { index ->
                    Box(
                        modifier = Modifier.Companion
                            .height(8.dp)
                            .width(8.dp)
                            .background(
                                color = if (activeDot == index) AppColors.Brand else Greyscale200,
                                shape = CircleShape
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.Companion.width(8.dp))
                }
            }
        }
    }
}