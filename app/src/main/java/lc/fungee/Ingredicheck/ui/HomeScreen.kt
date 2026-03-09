package lc.fungee.Ingredicheck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle

class ConcaveCornerShape(
    private val cornerRadius: Float,
    private val concaveRadius: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(
        Path().apply {

            val w = size.width
            val h = size.height

            // Start top left
            moveTo(cornerRadius, 0f)

            // Top edge
            lineTo(w - cornerRadius, 0f)

            // Top right corner
            arcTo(
                rect = Rect(
                    w - 2 * cornerRadius,
                    0f,
                    w,
                    2 * cornerRadius
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right edge
            lineTo(w, h - concaveRadius * 2)

            // Concave inward curve
            arcTo(
                rect = Rect(
                    w - concaveRadius * 2,
                    h - concaveRadius * 2,
                    w,
                    h
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            // Bottom edge
            lineTo(cornerRadius, h)

            // Bottom left
            arcTo(
                rect = Rect(
                    0f,
                    h - 2 * cornerRadius,
                    2 * cornerRadius,
                    h
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Left edge
            lineTo(0f, cornerRadius)

            // Top left
            arcTo(
                rect = Rect(
                    0f,
                    0f,
                    2 * cornerRadius,
                    2 * cornerRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ConcaveShapePreview() {

    val shape = with(LocalDensity.current) {
        ConcaveCornerShape(
            cornerRadius = 64.dp.toPx(),
            concaveRadius = 64.dp.toPx()
        )
    }

    Box(
        modifier = Modifier.Companion
            .size(300.dp)
            .clip(shape)
            .background(Color.Companion.LightGray)
    )
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Text(
            text = "Home Screen",
            style = titleTextStyle()
        )
    }
}