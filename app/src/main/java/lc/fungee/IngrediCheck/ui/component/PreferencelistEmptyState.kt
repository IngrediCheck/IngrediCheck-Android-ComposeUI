package lc.fungee.IngrediCheck.ui.component

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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Greyscale200
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.PrimarayGreen50
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.White

@Composable
fun PreferenceEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.emptystateillustration),
            contentDescription = "Your image",
            modifier = Modifier
                .width(201.dp)
                .height(180.dp)
                .alpha(1.0f)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(104.dp))
        Text(
            text = "Try the following",
            color = Greyscale500,
            modifier = Modifier
                .width(120.dp)
                .height(21.dp)
                .align(Alignment.CenterHorizontally),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                letterSpacing = (-0.32).sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
            ) { page ->
                Box(
                    modifier = Modifier
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
                    contentAlignment = Alignment.Center
                ) {
                    val annotatedText = when (page) {
                        0 -> buildAnnotatedString {
                            append("I follow a ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("vegetarian")
                            }
                            append(" diet, but I'm okay with eating ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("fish")
                            }
                            append(".")
                        }

                        1 -> buildAnnotatedString {
                            append("Scan ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("food ingredients")
                            }
                            append(" easily.")
                        }

                        2 -> buildAnnotatedString {
                            append("Get alerts on ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("dietary restrictions")
                            }
                            append(".")
                        }

                        else -> buildAnnotatedString { append("") }
                    }
                    Text(
                        text = annotatedText,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 15.sp,
                        color = AppColors.Brand
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) AppColors.Brand else Greyscale200,
                                shape = CircleShape
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}







