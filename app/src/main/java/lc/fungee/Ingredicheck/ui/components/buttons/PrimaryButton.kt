package lc.fungee.Ingredicheck.ui.components.buttons

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.drawscope.Stroke
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.NunitoSemiBold
import lc.fungee.Ingredicheck.ui.theme.PrimaryGradientEnd
import lc.fungee.Ingredicheck.ui.theme.PrimaryGradientStart
import lc.fungee.Ingredicheck.ui.theme.buttonHeight
import lc.fungee.Ingredicheck.ui.theme.buttonTextSize
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize

@Composable
fun PrimaryButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: Int? = null,
    iconWidth: Dp = buttonIconSize(),
    iconHeight: Dp = buttonIconSize(),
    width: Dp = 152.dp,
    height: Dp = buttonHeight(),
    takeFullWidth: Boolean = true,
    isLoading: Boolean = false,
    isDisabled: Boolean = false,
    disabledBackgroundColor: Color = Greyscale40,
    textStyle: TextStyle = NunitoSemiBold.copy(
        fontSize = buttonTextSize()
    )
) {
    val enabled = !isDisabled && !isLoading && onClick != null
    val shape = RoundedCornerShape(percent = 50)
    val clickAction = onClick
    val resolvedTextStyle = if (isDisabled) {
        textStyle.copy(color = Greyscale110)
    } else {
        textStyle.copy(color = Greyscale10)
    }

    Box(
        modifier = modifier
            .then(
                if (takeFullWidth) {
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minWidth = 152.dp)
                } else {
                    Modifier.width(width)
                }
            )
            .defaultMinSize(minHeight = height)
            .primaryButtonEffect(
                isDisabled = isDisabled,
                shape = shape,
                disabledBackgroundColor = disabledBackgroundColor
            )
            .alpha(if (isDisabled) 0.6f else 1f)
            .clip(shape)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = clickAction
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Greyscale10
                )
            } else {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Greyscale10,
                        modifier = Modifier.size(width = iconWidth, height = iconHeight)
                    )
                }

                Text(
                    text = title,
                    style = resolvedTextStyle,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun Modifier.primaryButtonEffect(
    isDisabled: Boolean,
    shape: RoundedCornerShape,
    disabledBackgroundColor: Color
) = this.drawBehind {
    val size = size
    val outline = shape.createOutline(size, layoutDirection, this)
    
    if (isDisabled) {
        // Disabled: grayScale40 fill and grayScale40 border (as per iOS logic)
        drawOutline(
            outline = outline,
            color = disabledBackgroundColor
        )
        // Border
        drawOutline(
            outline = outline,
            color = disabledBackgroundColor,
            style = Stroke(width = 1.dp.toPx())
        )
    } else {
        // 1. Drop Shadow
        // box-shadow: 0px 4px 11px 0px #C5C5C591; (91 hex = 57% alpha)
        drawIntoCanvas { canvas ->
            val paint = Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(
                    11.dp.toPx(),
                    0.dp.toPx(),
                    4.dp.toPx(),
                    android.graphics.Color.argb((0.57f * 255).toInt(), 197, 197, 197)
                )
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                size.height / 2, size.height / 2,
                paint
            )
        }

        // 2. Background Gradient
        // linear-gradient(143.74deg, #9DCF10 21.16%, #6B8E06 68.87%);
        // We'll approximate 143.74 deg as top-leading to bottom-trailing stop points
        val brush = Brush.linearGradient(
            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
        drawOutline(outline, brush)

        // 3. Inner Shadows
        drawIntoCanvas { canvas ->
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width, size.height),
                        cornerRadius = CornerRadius(size.height / 2)
                    )
                )
            }
            canvas.save()
            canvas.clipPath(path)

            // Inner Shadow 1: 2px 9px 7.5px 0px #EDEDED40; (25% alpha)
            val shadow1Paint = Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.TRANSPARENT
                strokeWidth = 10.dp.toPx() // Thicker stroke for inner shadow effect
                style = android.graphics.Paint.Style.STROKE
                setShadowLayer(
                    7.5.dp.toPx(),
                    2.dp.toPx(),
                    9.dp.toPx(),
                    android.graphics.Color.argb((0.25f * 255).toInt(), 237, 237, 237)
                )
            }
            
            // Draw an inset rect/rounded rect to cast the shadow inwards
            canvas.nativeCanvas.drawRoundRect(
                -5.dp.toPx(), -5.dp.toPx(), size.width + 5.dp.toPx(), size.height + 5.dp.toPx(),
                (size.height / 2) + 5.dp.toPx(), (size.height / 2) + 5.dp.toPx(),
                shadow1Paint
            )

            // Inner Shadow 2: 0px 4px 5.7px 0px #72930A;
            val shadow2Paint = Paint().asFrameworkPaint().apply {
                color = android.graphics.Color.TRANSPARENT
                strokeWidth = 10.dp.toPx()
                style = android.graphics.Paint.Style.STROKE
                setShadowLayer(
                    5.7.dp.toPx(),
                    0.dp.toPx(),
                    4.dp.toPx(),
                    android.graphics.Color.parseColor("#72930A")
                )
            }
            
            canvas.nativeCanvas.drawRoundRect(
                -5.dp.toPx(), -5.dp.toPx(), size.width + 5.dp.toPx(), size.height + 5.dp.toPx(),
                (size.height / 2) + 5.dp.toPx(), (size.height / 2) + 5.dp.toPx(),
                shadow2Paint
            )

            canvas.restore()
        }

        // 4. White Border
        // border: 1px solid #FFFFFF
        drawOutline(
            outline = outline,
            color = Color.White,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/**
 * Primary-style pill effect without the outer white border.
 *
 * Used for selected allergy chips so they visually match the primary
 * button (gradient, shadows) but without the 1px white stroke.
 */
fun Modifier.primaryChipEffect(
    shape: RoundedCornerShape
): Modifier = this.drawBehind {
    val size = size
    val outline = shape.createOutline(size, layoutDirection, this)

    // 1. Drop Shadow (same as primaryButtonEffect)
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(
                11.dp.toPx(),
                0.dp.toPx(),
                4.dp.toPx(),
                android.graphics.Color.argb((0.57f * 255).toInt(), 197, 197, 197)
            )
        }
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            size.height / 2, size.height / 2,
            paint
        )
    }

    // 2. Background Gradient (same as primaryButtonEffect)
    val brush = Brush.linearGradient(
        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd),
        start = Offset(0f, 0f),
        end = Offset(size.width, size.height)
    )
    drawOutline(outline, brush)

    // 3. Inner Shadows (same as primaryButtonEffect, but we intentionally
    // skip the final white border so the chip has a clean edge)
    drawIntoCanvas { canvas ->
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height),
                    cornerRadius = CornerRadius(size.height / 2)
                )
            )
        }
        canvas.save()
        canvas.clipPath(path)

        // Inner Shadow 1
        val shadow1Paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.TRANSPARENT
            strokeWidth = 10.dp.toPx()
            style = android.graphics.Paint.Style.STROKE
            setShadowLayer(
                7.5.dp.toPx(),
                2.dp.toPx(),
                9.dp.toPx(),
                android.graphics.Color.argb((0.25f * 255).toInt(), 237, 237, 237)
            )
        }
        canvas.nativeCanvas.drawRoundRect(
            -5.dp.toPx(), -5.dp.toPx(), size.width + 5.dp.toPx(), size.height + 5.dp.toPx(),
            (size.height / 2) + 5.dp.toPx(), (size.height / 2) + 5.dp.toPx(),
            shadow1Paint
        )

        // Inner Shadow 2
        val shadow2Paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.TRANSPARENT
            strokeWidth = 10.dp.toPx()
            style = android.graphics.Paint.Style.STROKE
            setShadowLayer(
                5.7.dp.toPx(),
                0.dp.toPx(),
                4.dp.toPx(),
                android.graphics.Color.parseColor("#72930A")
            )
        }
        canvas.nativeCanvas.drawRoundRect(
            -5.dp.toPx(), -5.dp.toPx(), size.width + 5.dp.toPx(), size.height + 5.dp.toPx(),
            (size.height / 2) + 5.dp.toPx(), (size.height / 2) + 5.dp.toPx(),
            shadow2Paint
        )

        canvas.restore()
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    Box(modifier = Modifier.padding(20.dp).padding(top = 200.dp)) {
        PrimaryButton(
            title = "Get Started",
            takeFullWidth = true,
            onClick = {}
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PrimaryButtonDisabledPreview() {
//    Box(modifier = Modifier.padding(20.dp)) {
//        PrimaryButton(
//            title = "Get Started",
//            takeFullWidth = false,
//            isDisabled = true,
//            onClick = {}
//        )
//    }
//}
