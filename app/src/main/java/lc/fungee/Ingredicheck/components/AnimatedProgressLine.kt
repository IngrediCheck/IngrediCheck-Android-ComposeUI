@file:Suppress("PackageName")

package lc.fungee.Ingredicheck.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.theme.NunitoBold

/**
 * Animated progress line with rounded corners and an icon that transitions
 * from orange_fruits to orange_fruits_scope as progress goes from 0% to 10%.
 *
 * @param progress Current progress (0f to 1f)
 * @param backgroundColor Color of the unfilled background line
 * @param fillColor Color of the filled progress line
 * @param modifier Modifier for the component
 */
@Composable
fun AnimatedProgressLine(
    progress: Float,
    backgroundColor: Color = Color(0xFFE5E5E5),
    fillColor: Color = Color(0xFFFBCB7F),
    modifier: Modifier = Modifier
) {
    // Icon transition: 0% to 10% progress maps to 0f to 1f alpha/visibility
    val iconTransitionProgress = (progress / 0.1f).coerceIn(0f, 1f)

    // Animate icon height from 24.dp at 0% to 27.dp by 10% progress
    val animatedIconHeight by animateDpAsState(
        targetValue = 24.dp + (iconTransitionProgress * 3.dp),
        animationSpec = tween(durationMillis = 7000, easing = LinearEasing),
        label = "iconHeight"
    )

    val iconWidth = 24.dp
    val iconWidthPx = with(LocalDensity.current) { iconWidth.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(44.dp),
    ) {
        val maxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val clampedProgress = progress.coerceIn(0f, 1f)
        val maxIconOffsetPx = (maxWidthPx - iconWidthPx).coerceAtLeast(0f)
        val iconOffsetXPx = (clampedProgress * maxIconOffsetPx)
        val iconOffsetXDp = with(LocalDensity.current) { iconOffsetXPx.toDp() }

        val snappedPercent = (((clampedProgress * 100f) / 10f).toInt() * 10).coerceIn(0, 100)

        // Background line (rounded corners)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(backgroundColor)
                    .align(Alignment.CenterStart)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(clampedProgress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(fillColor)
                    .align(Alignment.CenterStart)
            )
        }

        // Moving VStack (percentage + icon) that travels with progress
        Column(
            modifier = Modifier
                .offset(x = iconOffsetXDp)
                .widthIn(min = iconWidth)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .align(Alignment.BottomStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (snappedPercent >= 10) {
                Text(
                    text = "${snappedPercent}%",
                    style = NunitoBold.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF91B640)
                    ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Box(
                modifier = Modifier
                    .width(iconWidth)
                    .height(animatedIconHeight)
            ) {
                // Crossfade between the two icons based on progress (0% -> 10%)
                AnimatedContent(
                    targetState = iconTransitionProgress >= 0.5f,
                    label = "iconTransition",
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }
                ) { showScope ->
                    Icon(
                        painter = painterResource(
                            id = if (showScope) R.drawable.orange_fruits_scope else R.drawable.orange_fruits
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimatedProgressLinePreview() {
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Animate progress from 0 to 1 over 7 seconds, then loop
    LaunchedEffect(Unit) {
        while (true) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(durationMillis = 7000, easing = LinearEasing)
            ) { value, _ ->
                progress = value
            }
            // Reset and loop
            progress = 0f
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top =  200.dp) ,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        AnimatedProgressLine(
            progress = progress,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
