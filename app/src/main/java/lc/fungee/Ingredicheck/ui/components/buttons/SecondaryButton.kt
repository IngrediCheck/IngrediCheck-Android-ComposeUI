package lc.fungee.Ingredicheck.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.NunitoSemiBold
import lc.fungee.Ingredicheck.ui.theme.buttonHeight
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize
import lc.fungee.Ingredicheck.ui.theme.buttonTextSize

@Composable
fun SecondaryButton(
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
    textColor: Color = Greyscale110,
    disabledTextColor: Color = Greyscale110,
    borderColor: Color = Greyscale40,
    disabledBackgroundColor: Color = Greyscale40,
    textStyle: TextStyle = NunitoSemiBold.copy(
        fontSize = buttonTextSize()
    )
) {
    val enabled = !isDisabled && !isLoading && onClick != null
    val shape = RoundedCornerShape(percent = 50)
    val clickAction = onClick

    val resolvedTextStyle = if (isDisabled) {
        textStyle.copy(color = disabledTextColor)
    } else {
        textStyle.copy(color = textColor)
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
            .clip(shape)
            .background(if (isDisabled) disabledBackgroundColor else Color.White, shape)
            .border(width = 1.5.dp, color = borderColor, shape = shape)
            .alpha(if (isDisabled) 0.6f else 1f)
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
                    color = textColor
                )
            } else {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(width = iconWidth, height = iconHeight),
                        tint = Color.Unspecified
                    )
                }
                Text(
                    text = title,
                    style = resolvedTextStyle,
                    maxLines = 1
                )
            }
        }
    }
}