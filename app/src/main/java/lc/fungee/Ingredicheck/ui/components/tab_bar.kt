package lc.fungee.Ingredicheck.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.components.buttons.primaryChipEffect
import lc.fungee.Ingredicheck.ui.theme.GrayScale50

/**
 * Custom tab bar matching iOS TabBar.swift:
 * - Capsule with two side buttons (History, IngrediRobo) and a raised center scanner button.
 * - When [isExpanded] is false (e.g. while scrolling), the capsule scales down and hides behind the center circle.
 * - When scrolling stops, [isExpanded] becomes true and the capsule animates back.
 */
@Composable
fun TabBar(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onRecentScansTap: () -> Unit = {},
    onChatBotTap: () -> Unit = {},
    onScannerTap: () -> Unit = {}
) {

    // Capsule shadow color (iOS: E9E9E9, radius 13.6, y: 12)
    val capsuleShadowColor = Color(0xFFE9E9E9)
    // Capsule stroke (iOS: grayScale50, 0.25)
    val capsuleStroke = GrayScale50
    val capsuleShape = RoundedCornerShape(percent = 50)
    // Fixed width for the capsule row (iOS: 196pt)
    val capsuleWidth = 196.dp
    val capsuleHorizontalPadding = 22.dp
    val capsuleVerticalPadding = 12.5.dp
    val sideIconSize = 26.dp
    val centerButtonSize = 60.dp
    val centerIconSize = 32.dp
    val centerButtonBottomPadding = 18.dp

    // iOS: when collapsed the capsule scales down so it hides more fully behind the center circle
    val capsuleScale by animateFloatAsState(
        // Collapse further (0.1f) but keep the long, eased animation for smoothness.
        targetValue = if (isExpanded) 1f else 0.1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "tabBarCapsuleScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        // Capsule row: History (left) + Spacer + IngrediRobo (right) — scales down when scrolling
        Row(
            modifier = Modifier
                .width(capsuleWidth)
                .graphicsLayer(
                    scaleX = capsuleScale,
                    scaleY = capsuleScale
                )
//                .shadow(
//                    elevation = 8.dp,
//                    shape = capsuleShape,
//                    spotColor = capsuleShadowColor,
//                    ambientColor = capsuleShadowColor
//                )
                .clip(capsuleShape)
                .background(Color.White)
                .border(0.25.dp, capsuleStroke, capsuleShape)
                .padding(horizontal = capsuleHorizontalPadding, vertical = capsuleVerticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRecentScansTap,
                modifier = Modifier.size(sideIconSize)
            ) {
                Icon(
                    painter = painterResource(R.drawable.tab_bar_history_icon),
                    contentDescription = "Recent scans",
                    modifier = Modifier.size(sideIconSize),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onChatBotTap,
                modifier = Modifier.size(sideIconSize)
            ) {
                Icon(
                    painter = painterResource(R.drawable.tab_bar_ingredirobo_icon),
                    contentDescription = "IngrediRobo",
                    modifier = Modifier.size(sideIconSize),
                    tint = Color.Unspecified
                )
            }
        }

        // Center scanner button: Primary effect + circular drop shadow (blur 8.5dp, spread 2dp, #85AF0A 44%)
        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .padding(bottom = centerButtonBottomPadding)
                .size(centerButtonSize)

                .primaryChipEffect(RoundedCornerShape(percent = 50))
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onScannerTap()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.tab_bar_scanner_icon),
                contentDescription = "Scan",
                modifier = Modifier.size(centerIconSize),
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TabBarPreview() {
    Box(
        modifier = Modifier
            .padding(32.dp)
            .size(300.dp, 120.dp)
    ) {
        TabBar(
            onRecentScansTap = {},
            onChatBotTap = {},
            onScannerTap = {}
        )
    }
}
