package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Nunitosemibold

@Preview(showBackground = true ,
    showSystemUi = true ,
//    widthDp = 500, heightDp = 500
    //    , backgroundColor = 0xFFFFFF
)
@Composable
fun test()
{
    Box( modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
        CapsuleButton()

    }
}
@Composable
fun CapsuleButton( text :String
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(56.dp)

            .border(
                width = 1.dp,
                color = Color.White ,
                shape = RoundedCornerShape(percent = 50)
            )
            .shadow( // outer soft shadow
                elevation = 3.dp,
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

            .clickable {  }
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
fun CapsuleButton1(
    text: String,
    icon: Int? = null, // Pass drawable resource ID
    width: Dp? = 160.dp,
    height: Dp = 56.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .then(if (width != null) Modifier.width(width) else Modifier.wrapContentWidth())
            .height(height)

            // Outer drop shadow
            .shadow(
                elevation = 11.dp,
                shape = RoundedCornerShape(percent = 50),
                clip = false,
                ambientColor = Color(0xFFC5C5C5).copy(alpha = 0.57f),
                spotColor = Color(0xFFC5C5C5).copy(alpha = 0.57f)
            )
//            .background(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        Color(0xFF9DCF10), // top
//                        Color(0xFF6B8E06)  // bottom
//                    )
//                ),
//                shape = RoundedCornerShape(percent = 50)
//            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF9DCF10), // top Color(0xFF9DCF10),
                        Color(0xFF6B8E06)  // bottom
                    ),

                    start = Offset(100f, 0f),             // top-left corner
                    end = Offset(
                        200f,
                        200f
                    )            // bottom-right direction// diagonal direction
                ),
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        )
        {


//        // Inner shadow effects as overlays
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            // Inner shadow 1 (light from top-right)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(1.dp)
//                    .background(
//                        brush = Brush.radialGradient(
//                            colors = listOf(
//                                Color(0xFFEDEDED).copy(alpha = 0.25f),
//                                Color.Transparent
//                            ),
//                            center = Offset(0.7f, 0.3f),
//                            radius = 800f
//                        ),
//                        shape = RoundedCornerShape(percent = 50)
//                    )
//            )

            // Inner shadow 2 (darker inner shadow)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(1.dp)
//                    .background(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                Color(0xFF72930A).copy(alpha = 0.3f),
//                                Color.Transparent
//                            ),
//                            startY = 0f,
//                            endY = 100f
//                        ),
//                        shape = RoundedCornerShape(percent = 50)
//                    )
//            )

            // Content

            Image(
                painter = painterResource(id = R.drawable.lucide_stars),
                contentDescription = null,
                modifier = Modifier
                    .width(23.dp)
                    .height(21.dp)
                    .padding(end = 3.dp),
                contentScale = ContentScale.Crop,

                )
            Text(
                text = text,
                fontFamily = Nunitosemibold,

                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9DCF10), // top
                            Color(0xFF6B8E06)  // bottom
                        ),
//                                start = Offset.Zero,
//                        end = Offset.Infinite  // diagonal direction
                    )
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp)
            )

        }
    }
}
