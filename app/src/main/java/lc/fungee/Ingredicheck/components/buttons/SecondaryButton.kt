package lc.fungee.Ingredicheck.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale20
import lc.fungee.Ingredicheck.ui.theme.Greyscale200
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale400
import lc.fungee.Ingredicheck.ui.theme.Greyscale600
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.NunitoBold
import lc.fungee.Ingredicheck.ui.theme.NunitoSemiBold
import lc.fungee.Ingredicheck.ui.theme.Primary800
import lc.fungee.Ingredicheck.ui.theme.buttonHeight
import lc.fungee.Ingredicheck.ui.theme.buttonTextSize
import lc.fungee.Ingredicheck.ui.theme.buttonIconSize
import androidx.compose.foundation.border
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor


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
    backgroundColor: Color = Color.White,
    disabledBackgroundColor: Color = Greyscale40,
    borderColor: Color = Greyscale40,
    textColor: Color = Primary800,
    disabledTextColor: Color = Greyscale110,
    iconTint: Color? = null,
    textStyle: TextStyle = NunitoBold.copy(
        fontSize = buttonTextSize(),
        color = textColor
    )
) {
    val enabled = !isDisabled && !isLoading && onClick != null
    val shape = RoundedCornerShape(percent = 50)

    val containerColor = if (isDisabled) disabledBackgroundColor else backgroundColor
    val resolvedBorderColor = if (isDisabled) Color.Transparent else borderColor
    val resolvedTextStyle = if (isDisabled) {
        textStyle.copy(color = disabledTextColor)
    } else {
        textStyle.copy(color = textColor)
    }

    val resolvedIconTint = when {
        isDisabled -> disabledTextColor
        iconTint != null -> iconTint
        else -> resolvedTextStyle.color
    }

    Box(
        modifier = modifier
            .then(
                if (takeFullWidth) {
                    Modifier.fillMaxWidth().defaultMinSize(minWidth = width)
                } else {
                    Modifier.width(width)
                }
            )
            .defaultMinSize(minHeight = height)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                spotColor = Color(0xFFCECECE).copy(alpha = 0.39f),
                ambientColor = Color(0xFFCECECE).copy(alpha = 0.39f)
            )
            .clip(shape)
            .background(containerColor)
            .border(width = 1.5.dp, color = resolvedBorderColor, shape = shape)
            .alpha(if (isDisabled) 0.6f else 1f)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick!!
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
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = resolvedTextStyle.color
                )
            } else {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = resolvedIconTint,
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

@Preview(showBackground = true , )
@Composable
private fun SecondaryButtonPreview() {
    Row( modifier = Modifier.fillMaxSize() ,horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//        SecondaryButton(
//            title = "Get Started",
//            takeFullWidth = false,
//            width = 180.dp,
//            onClick = {}
//        )
//        Spacer(modifier =Modifier.padding(12.dp))

        SecondaryButton(
            title = "All Set Ashle!",
            takeFullWidth = false,
            width = 180.dp,
            isDisabled = true,
            onClick = {} ,
            textColor = Greyscale150

        )
    }
}

