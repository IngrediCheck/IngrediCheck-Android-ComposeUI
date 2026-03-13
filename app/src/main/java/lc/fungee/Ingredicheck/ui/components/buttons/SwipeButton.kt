package lc.fungee.Ingredicheck.ui.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.theme.Manrope

@Composable
fun ScannerSwipeButton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: pill with circles and arrows
        Row(
            modifier = Modifier
                .size(width = 230.dp, height = 66.dp)
                .clip(RoundedCornerShape(40.3.dp))
                .background(Color(0x14E8E8E8))
                .padding(horizontal = 3.5.dp, vertical = 3.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left primary-effect circle with scanner icon
            Row(
                modifier = Modifier
                    .size(59.dp)
                    .clip(CircleShape)
                    .primaryChipEffect(RoundedCornerShape(percent = 50)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tab_bar_scanner_icon),
                    contentDescription = "Scanner",
                    modifier = Modifier.size(width = 28.dp, height = 28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Middle row of three arrows
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.2.dp)
            ) {
                val arrowPainter = painterResource(id = R.drawable.icon_arrow_right_side_1)
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Right gradient circle
            Row(
                modifier = Modifier
                    .size(59.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF).copy(alpha = 0.25f),// 👈 25% opacity
                                Color(0xFFA6A6A6).copy(alpha = 0.25f)
                            )
                        )
                    )

                   ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tab_bar_scanner_icon),
                    contentDescription = "Scanner",
                    modifier = Modifier.size(width = 28.dp, height = 28.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Bottom row: "Barcode" .... "Photo"
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.size(width = 230.dp, height = 18.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Barcode",
                color = Color.White,
                fontFamily = Manrope,
                fontSize = 10.5.sp
            )
            Text(
                text = "Photo",
                color = Color.White,
                fontFamily = Manrope,
                fontSize = 10.5.sp
            )
        }
    }
}

@Preview(showBackground = true , backgroundColor = 0xFF3DA8F5)
@Composable
fun ScannerSwipeButtonPreview() {
    ScannerSwipeButton()
}
