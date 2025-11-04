package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Malerope

@Preview(showBackground = true,   name = "ritika Card")
@Composable
fun OnbordingCard() {
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(color = Color.White)) {
        Box(
            modifier = Modifier
//                .offset(x = 38.dp, y = 136.dp)
                .size(width = 325.dp, height = 100.dp)

                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
//                   ambientColor = Color(0xFFF30404),
//                   spotColor = Color(0xFFECECEC),
                    clip = false
                )
                .background(Color(0xFFFBFBFB), shape = RoundedCornerShape(16.dp))
                .padding(
                    start = 12.dp,
                    top = 9.dp,
                    end = 12.dp,
                    bottom = 12.dp
                )  .alpha(1f),
//                    contentAlignment = Alignment.Center // ✅ centers horizontally

        ) {
            // Your inner content here
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.width(255.dp).padding(top = 9.dp)
                    , verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Text(
                        text = "Ritika.",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily =  Malerope ,
                        fontWeight = FontWeight.Medium
                        ,

                    )
                    Box(
                        modifier = Modifier.fillMaxWidth()
//                            .padding(top = 6.dp),
                                ,
                        contentAlignment = Alignment.CenterStart// align to right if needed
                    ) {
                        val icons = listOf(
                            R.drawable.tdesign_nut,
                            R.drawable.mingcute_alert_line,
                            R.drawable.lucide_stethoscope,
                            R.drawable.lucide_baby,
                            R.drawable.nrk_globe,
                            R.drawable.charmcirclecross,
                            R.drawable.hugeicons_plant_01,
                            R.drawable.fluent_emoji_high_contrast_fork_and_knife_with_plate,
                            R.drawable.streamline_recycle_1_solid,
                            R.drawable.iconoir_chocolate
                        )

                        icons.forEachIndexed { index, resId ->
                            IconItem(
                                resId = resId,
                                contentDesc = "icon $index",
                                modifier = Modifier
                                    .offset(x = (index * 24).dp) // small overlap leftward
                                    .zIndex((icons.size - index).toFloat()) // 👈 reverse stacking order
                            )
                        }
                    }


//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(-8.dp,
////                  Alignment.End
//                        )
//
//                        , verticalAlignment = Alignment.CenterVertically,
//
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    {
//                        IconItem(resId = R.drawable.tickmark, contentDesc = "Home")
//                        IconItem(resId = R.drawable.tickmark, contentDesc = "Camera")
//                        IconItem(resId = R.drawable.tickmark, contentDesc = "Settings")
//                        IconItem(resId = R.drawable.tickmark, contentDesc = "Profile")
//
//
//                    }


                }
                Image(
                    painter = painterResource(id = R.drawable.famaliymember4
                        ) , contentDescription = null
                    , modifier = Modifier.fillMaxSize()
                )
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = null,
//                    tint = Color.Gray
//                )
//                Text(
//                    text = "Example Card",
//                    color = Color.Black,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
            }
        }
    }
//}

@Composable
fun IconItem(resId: Int, contentDesc: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(35.dp)
            .background(color = Color(0xFFF7F7F7), shape = CircleShape) .border(
                width = 1.dp,
                color = Color.White, // ✅ 2dp white border
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    )
    {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDesc,
            modifier = Modifier.size(16.dp), // Adjust to match your Figma size
            contentScale = ContentScale.Fit
        )
    }
}