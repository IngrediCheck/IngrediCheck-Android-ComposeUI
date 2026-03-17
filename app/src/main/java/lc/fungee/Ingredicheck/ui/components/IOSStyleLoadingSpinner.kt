package lc.fungee.Ingredicheck.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Preview(showBackground = true , backgroundColor = 0xFF9DCF10)
@Composable
fun IOSStyleLoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = ""
    )

    Canvas(
        modifier = modifier
            .size(size)
            .rotate(rotation)
    ) {

        val lineCount = 12
        val radius = size.toPx() / 2
        val lineLength = radius * 0.35f

        repeat(lineCount) { i ->

            val alpha = (i + 1) / lineCount.toFloat()

            rotate(i * (360f / lineCount)) {
                drawLine(
                    color = color.copy(alpha = alpha),
                    start = Offset(radius, radius - lineLength),
                    end = Offset(radius, radius - radius),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}