
package lc.fungee.IngrediCheck.PreferenceList
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Greyscale200
import lc.fungee.IngrediCheck.ui.theme.Greyscale50
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.PrimarayGreen50
import lc.fungee.IngrediCheck.ui.theme.White
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize().background(color = White)
                .padding(paddingValues) // Respect bottom bar
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
            text = "Your dietary preference",
            modifier = Modifier
                .padding(top = 54.dp) // top: 10 from Figma
                .width(189.dp)        // optional, if fixed width needed
                .height(22.dp),       // optional, if fixed height needed
            style = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp,
                letterSpacing = (-0.41).sp,
                textAlign = TextAlign.Center
            )
        )


        Spacer(modifier = Modifier.height(32.dp))

//            TextField(
//                value = "",
//                onValueChange = {},
//                placeholder = { Text("Enter dietary preference here") },
//                modifier = Modifier
//               //     .padding(top = 118.dp ) // Top and Left positioning
//                    .width(326.dp)
//                    .height(53.dp)//.padding(horizontal = 8.dp)
//                    .background(
//                        color = Greyscale200,
//                        shape = RoundedCornerShape(8.dp)
//
//                    ),
//                colors = TextFieldDefaults.textFieldColors(
//                    backgroundColor = Color.Transparent, // keeps bg from doubling
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//            )

            TextField(
                value ="" ,
                onValueChange = {  },
                placeholder = {
                    Text(
                        text = "Enter dietary preference here",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 21.sp,
                            letterSpacing = (-0.32).sp
                        ), modifier = Modifier.alpha(0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
                    .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )



            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id =R.drawable.emptystateillustration),
                contentDescription = "Your image",
                modifier = Modifier
                    .width(201.dp)
                    .height(180.dp)
                    .alpha(1.0f)
                    .align(Alignment.CenterHorizontally)
            )



            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Try the following",
                color = Greyscale500,
                modifier = Modifier.width(120.dp).height(21.dp).align(alignment = Alignment.CenterHorizontally),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif, // Replace with SF Pro if available
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    letterSpacing = (-0.32).sp
                )

            )

            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .padding(horizontal = 24.dp) // to center and simulate width = 326dp on 375dp screen
                    .background(
                        color = PrimarayGreen50,
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .padding(
                        start = 16.dp,
                        top = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp
                    )
            ) {
                // Add content here
            }

            // Minimal pager with 3 pages and dot indicators (after your Box)
            val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })



            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Adjust height as needed
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // Minimal content, or leave empty
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Optional: show page number or leave blank
                        // Text(text = "Page ${page + 1}")
                    }
                }
            }

// Dot indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (pagerState.currentPage == index) PrimaryGreen100 else Greyscale200,
                                shape = CircleShape
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

//                Text(
//                    text = buildAnnotatedString {
//                      //  append(""I follow a "")
//                        withStyle(
//                            style = SpanStyle(
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFF5C7D0E)
//                            )
//                        ) {
//                            append("vegetarian")
//                        }
//                        append(" diet, but I'm okay with eating ")
//                        withStyle(
//                            style = SpanStyle(
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFF5C7D0E)
//                            )
//                        ) {
//                            append("fish")
//                        }
//                     //   append("."")
//                    },
//                    style = TextStyle(fontSize = 14.sp)
//                )
//            }
        }
    }
}