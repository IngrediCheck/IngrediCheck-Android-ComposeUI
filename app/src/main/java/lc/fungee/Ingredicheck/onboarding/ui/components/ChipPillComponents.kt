package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Secondary200

@Composable
fun FlowRowChips(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }
        val density = this
        val spacingX = with(density) { horizontalSpacing.roundToPx() }
        val spacingY = with(density) { verticalSpacing.roundToPx() }
        val placeable = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val maxWidth = constraints.maxWidth
        var x = 0
        var y = 0
        var rowHeight = 0
        val positions = placeable.map { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + spacingY
                rowHeight = 0
            }
            val pos = x to y
            x += p.width + spacingX
            rowHeight = maxOf(rowHeight, p.height)
            pos
        }
        val totalHeight = (y + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(maxWidth, totalHeight) {
            placeable.forEachIndexed { i, p ->
                val (px, py) = positions[i]
                p.placeRelative(px, py)
            }
        }
    }
}

private val SelectedPillBackground = Secondary200
private val PillShape = RoundedCornerShape(30.dp)

@Composable
fun SelectedChipPill(
    emoji: String,
    label: String,
    trailingAvatars: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(SelectedPillBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji.trim(),
            fontSize = 16.sp,
            color = Greyscale150
        )
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Greyscale150,
            maxLines = 1
        )
        if (trailingAvatars != null) {
            Spacer(modifier = Modifier.width(6.dp))
            trailingAvatars()
        }
    }
}

