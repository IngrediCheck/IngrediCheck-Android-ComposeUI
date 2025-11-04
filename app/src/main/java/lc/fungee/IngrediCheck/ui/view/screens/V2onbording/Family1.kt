@file:OptIn(ExperimentalMaterial3Api::class)

package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Nunito
import lc.fungee.IngrediCheck.ui.theme.Nunitosemibold

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true , device = PIXEL_9)
@Composable
fun Famaliy() {

    val bottomSheetState = rememberStandardBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 450.dp, // partially visible
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        modifier = Modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//                   ambientColor = Color(0xFFF30404),
//                   spotColor = Color(0xFFECECEC),
            clip = false
        ),
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp,
                        bottom = 11.dp
                    )
                    .width(80.dp)                     // 
                    .height(5.dp)                     // 
                    .clip(RoundedCornerShape(50))     // pill shape
                    .background(Color(color = 0xFFDFE2E5))
//                        .align(Alignment.CenterHorizontally)
            )
        },
        sheetContent = {

Famaliy3()


        }
    ) { innerPadding ->
        // 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFFFF)) // light background for contrast
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 60.dp,
                    bottom = 16.dp
                ) // space around the card
                .shadow(
                    elevation = 8.dp,

                    shape = RoundedCornerShape(24.dp),
                    clip = false
                ),

            contentAlignment = Alignment.Center
        )
        {


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)) // rounded corners
                    .background(Color.White)

                    .border(0.2.dp, Color(0xFFE3E3E3), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.TopCenter


            )
            {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Let’s meet your IngrediFam!", fontSize = 20.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303030),
                        modifier = Modifier
//             .align(Alignment.TopCenter)
                            .padding(bottom = 20.dp)

                    )


                    OnbordingCard()
                    Spacer(Modifier.padding(20.dp))
                    OnbordingCard()
                    Spacer(Modifier.padding(20.dp))
                    OnbordingCard()

                }


            }
        }
    }
}






@Composable
fun Famaly1() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, end = 21.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            painter = painterResource(id = R.drawable.familygroup1),
            contentDescription = " ",
            modifier = Modifier
                .fillMaxWidth().height(166.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Let’s meet your IngrediFam!", fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF303030),
            modifier = Modifier

//                            .padding(top = 20.dp)
        )

        Text(
            text = "Add everyone’s name and a fun avatar so we can tailor tips and scans just for them.",
            fontWeight = FontWeight.W500,
            fontSize = 18.sp,
            color = Color(0xFF949494),
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp)
        )
        Box(
            modifier = Modifier
                .width(142.dp)
                .height(62.dp)
                .padding(top = 16.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(percent = 50)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9DCF10),
                            Color(0xFF6B8E06)
                        )
                    ),
                    shape = RoundedCornerShape(percent = 50)
                )
                .clickable {

                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Add Members",
                color = Color(0xFFFFFFFF),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600
            )
        }


    }
}

@Composable
fun CapsuleButton(
    text: String = "Add Members",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(56.dp)
            .shadow( // outer soft shadow
                elevation = 8.dp,
                shape = RoundedCornerShape(percent = 50),
                clip = false
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF9DCF10), // top-left
                        Color(0xFF6B8E06)  // bottom-right
                    )
                ),
                shape = RoundedCornerShape(percent = 50)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp), // internal padding
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CapsuleButton2(
    text: String = "Add Members",
    icon: Int? = null, // Pass drawable resource ID
    width: Dp? = 160.dp,
    height: Dp = 70.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .then(if (width != null) Modifier.width(width) else Modifier.wrapContentWidth())
            .height(height)
            .padding(top = 20.dp)

            // Outer drop shadow

            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF9DCF10), // top
                        Color(0xFF6B8E06)  // bottom
                    )
                ),
                shape = RoundedCornerShape(percent = 50)
            )
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Inner shadow effects as overlays
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center

        ) {
            // Inner shadow 1 (light from top-right)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF000000).copy(alpha = 0.25f),
                                Color.Transparent
                            ),

                                    start = Offset(0f, 0f),
                                     end = Offset(0f ,1000f)

                        // top-left
                        ),
                        shape = RoundedCornerShape(percent = 50)
                    )
            )

            // Inner shadow 2 (darker inner shadow)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF9DCF10).copy(alpha = 0.3f),
                                Color(0xFF6B8E06)
                            ),
                            startY = 0f,
                            endY = 100f
                        ),
                        shape = RoundedCornerShape(percent = 50)
                    )
            )

            // Content


            Text(
                text = "Add Member",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = Nunitosemibold
            )

        }
    }
}

//@Composable
//fun CapsuleButton2(
//    text: String = "Add Members",
//    onClick: () -> Unit = {}
//) {
//    Box(
//        modifier = Modifier
//            .height(52.dp)
//            .shadow(
//                elevation = 16.dp,
//                shape = RoundedCornerShape(percent = 50),
//                clip = false
////                ambientColor = Color.Black.copy(alpha = 0.2f),
////                spotColor = Color.Black.copy(alpha = 0.2f)
//            )
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(
//                        Color(0xFF9DCF10), // lighter green
//                        Color(0xFF6B8E06)  // darker green
//                    ),
//                    start = Offset(0f, 0f),
//                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
//                ),
//                shape = RoundedCornerShape(percent = 50)
//            )
//            .clickable { onClick() }
//            .padding(horizontal = 32.dp, vertical = 14.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            color = Color.White,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}






