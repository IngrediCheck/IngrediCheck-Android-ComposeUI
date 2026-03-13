package lc.fungee.Ingredicheck.ui.components.buttons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
    // Local mode state: true = Scanner (left primary), false = Photo (right primary)
    var isScannerMode by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 30.dp.toPx() }

    // Shimmer-style arrow animation phase: cycles 0 -> 1 -> 2 -> 0...
    val infiniteTransition = rememberInfiniteTransition(label = "arrowShimmer")
    val phaseValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrowPhaseValue"
    )
    // Continuous shimmer colors (smooth fade) based on phaseValue
    val c1 = Color(0xFFE9E9E9) // most faded
    val c2 = Color(0xFFE3E3E3) // medium
    val c3 = Color(0xFFDCDCDC) // darkest

    fun colorForArrow(index: Int): Color {
        // Shift phase per arrow so the bright spot moves left -> right
        val position = (phaseValue + index) % 3f
        val segment = position.toInt()          // 0, 1, or 2
        val t = position - segment              // 0..1 within the segment

        return when (segment) {
            0 -> lerp(c1, c2, t) // fade from c1 -> c2
            1 -> lerp(c2, c3, t) // fade from c2 -> c3
            else -> lerp(c3, c1, t) // fade from c3 -> c1
        }
    }

    // Inactive circle gradient (matches current right circle look)
    val inactiveCircleBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF).copy(alpha = 0.25f),
            Color(0xFFA6A6A6).copy(alpha = 0.25f)
        )
    )

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
                .padding(horizontal = 3.5.dp, vertical = 3.5.dp)
                .pointerInput(isScannerMode) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = {
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        },
                        onDragEnd = {
                            when {
                                totalDrag > swipeThresholdPx -> {
                                    // Swipe right -> activate Photo (right circle primary)
                                    isScannerMode = false
                                }
                                totalDrag < -swipeThresholdPx -> {
                                    // Swipe left -> activate Scanner (left circle primary)
                                    isScannerMode = true
                                }
                            }
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left primary-effect circle with scanner icon (tap -> Scanner mode)
            Row(
                modifier = Modifier
                    .size(59.dp)
                    .clip(CircleShape)
                    .then(
                        if (isScannerMode) {
                            Modifier.primaryChipEffect(RoundedCornerShape(percent = 50))
                        } else {
                            Modifier.background(
                                brush = inactiveCircleBrush,
                                shape = CircleShape
                            )
                        }
                    )
                    .clickable {
                        isScannerMode = true
                    },
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

            // Middle row of three arrows with shimmer-style tint
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.2.dp)
            ) {
                val arrowPainter = painterResource(id = R.drawable.icon_arrow_right_side_1)
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 1",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(0))
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 2",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(1))
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 3",
                    modifier = Modifier.size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(2))
                )
            }

            // Right circle: primary when in Photo mode, gradient when inactive (tap -> Photo mode)
            Row(
                modifier = Modifier
                    .size(59.dp)
                    .clip(CircleShape)
                    .then(
                        if (isScannerMode) {
                            Modifier.background(
                                brush = inactiveCircleBrush,
                                shape = CircleShape
                            )
                        } else {
                            Modifier.primaryChipEffect(RoundedCornerShape(percent = 50))
                        }
                    )
                    .clickable {
                        isScannerMode = false
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.photo_capture_mode_img),
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
    