package lc.fungee.Ingredicheck.ui.components.buttons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.graphics.graphicsLayer
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
import lc.fungee.Ingredicheck.ui.theme.Greyscale70
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.PrimaryGradientEnd
import lc.fungee.Ingredicheck.ui.theme.PrimaryGradientStart

@Composable
fun ScannerSwipeButton(
    modifier: Modifier = Modifier
) {
    // Local mode state: true = Scanner (left primary), false = Photo (right primary)
    var isScannerMode by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 30.dp.toPx() }

    // Shimmer-style arrow animation phase: smooth loop from 0 -> 3 -> 0
    // (3 positions = 3 arrows)
    val infiniteTransition = rememberInfiniteTransition(label = "arrowShimmer")
    val phaseValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrowPhaseValue"
    )

    // Arrow flip animation per arrow (independent, with small delays)
    val modeTransition = updateTransition(targetState = isScannerMode, label = "arrowDirection")
    val arrow1Rotation by modeTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 240, easing = FastOutSlowInEasing, delayMillis = 0)
        },
        label = "arrow1Rotation"
    ) { scanner ->
        if (scanner) 0f else 180f
    }
    val arrow2Rotation by modeTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 240, easing = FastOutSlowInEasing, delayMillis = 40)
        },
        label = "arrow2Rotation"
    ) { scanner ->
        if (scanner) 0f else 180f
    }
    val arrow3Rotation by modeTransition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 240, easing = FastOutSlowInEasing, delayMillis = 80)
        },
        label = "arrow3Rotation"
    ) { scanner ->
        if (scanner) 0f else 180f
    }
    // Shimmer base color (we vary only opacity)
    val shimmerBase = Greyscale70

    /**
     * Smooth shimmer:
     * - phaseValue moves continuously 0..3
     * - each arrow has index 0,1,2
     * - distance d between phase and index (with wrap) controls brightness
     *   d = 0   -> alpha = 1.0   (bright)
     *   d = 0.5 -> alpha ≈ 0.7   (medium)
     *   d >= 1  -> alpha = 0.4   (faded)
     */
    fun colorForArrow(index: Int): Color {
        // Flip shimmer direction when mode changes:
        // - Scanner: phase moves 0 -> 1 -> 2 -> 3 (left to right)
        // - Photo:   effectively moves 3 -> 2 -> 1 -> 0 (right to left)
        val effectivePhase = if (isScannerMode) phaseValue else 3f - phaseValue

        // Distance along the 0..3 "track" with wrap-around
        val rawDistance = kotlin.math.abs(effectivePhase - index)
        val d = minOf(rawDistance, 3f - rawDistance) // shortest distance on the ring

        // Intensity 1 at center, linearly falling to 0 at d>=1
        val intensity = (1f - d).coerceIn(0f, 1f)
        val alpha = 0.4f + intensity * 0.6f
        return shimmerBase.copy(alpha = alpha)
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
            // Left circle (Scanner) — active uses shared primary green linear gradient
            Row(
                modifier = Modifier
                    .size(59.dp)
                    .clip(CircleShape)
                    .then(
                        if (isScannerMode) {
                            Modifier.background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                ),
                                shape = CircleShape
                            )
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

            // Middle row of three arrows with shimmer-style tint.
            // Each arrow flips independently (with staggered rotation) when mode changes.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.2.dp)
            ) {
                val arrowPainter = painterResource(id = R.drawable.icon_arrow_right_side_1)
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 1",
                    modifier = Modifier
                        .graphicsLayer(rotationZ = arrow1Rotation)
                        .size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(0))
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 2",
                    modifier = Modifier
                        .graphicsLayer(rotationZ = arrow2Rotation)
                        .size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(1))
                )
                Image(
                    painter = arrowPainter,
                    contentDescription = "Arrow 3",
                    modifier = Modifier
                        .graphicsLayer(rotationZ = arrow3Rotation)
                        .size(width = 10.dp, height = 18.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(colorForArrow(2))
                )
            }

            // Right circle (Photo) — active uses same shared primary green linear gradient
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
                            Modifier.background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                ),
                                shape = CircleShape
                            )
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
                    modifier = Modifier.size(width = 26.dp, height = 26.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Bottom row: "Barcode" .... "Photo"
//        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.size(width = 230.dp, height = 18.dp).padding(horizontal = 12.dp)            ,
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
    