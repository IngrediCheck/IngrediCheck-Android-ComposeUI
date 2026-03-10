package lc.fungee.Ingredicheck.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.ui.components.buttons.primaryButtonEffect
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale130
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Greyscale40

val MyIcon: ImageVector
    get() {
        if (_MyIcon != null) return _MyIcon!!
        _MyIcon = ImageVector.Builder(
            name = "MyIcon",
            defaultWidth = 193.0.dp,
            defaultHeight = 214.0.dp,
            viewportWidth = 193.0f,
            viewportHeight = 214.0f,
        ).apply {

            path(
                fill = SolidColor(Color(0xFF2EA4D9)),
            ) {
                moveTo(32.0f, 205.0f)
                curveTo(19.2975f, 205.0f, 9.0f, 194.703f, 9.0f, 182.0f)
                lineTo(8.99998f, 32.0f)
                curveTo(8.99998f, 19.2975f, 19.2974f, 9.00001f, 32.0f, 9.00001f)
                lineTo(161.0f, 9.0f)
                curveTo(173.703f, 9.0f, 184.0f, 19.2975f, 184.0f, 32.0f)
                lineTo(184.0f, 126.294f)
                curveTo(184.0f, 140.491f, 172.491f, 152.0f, 158.294f, 152.0f)
                curveTo(144.097f, 152.0f, 132.588f, 163.509f, 132.588f, 177.706f)
                lineTo(132.588f, 178.5f)
                curveTo(132.588f, 193.136f, 120.723f, 205.0f, 106.088f, 205.0f)
                lineTo(32.0f, 205.0f)
                close()
            }

        }.build()
        return _MyIcon!!
    }

private var _MyIcon: ImageVector? = null

/** Padding so text stays clear of the bottom-right inward curve and the circle button */
private val cardContentPaddingStart = 12.dp
private val cardContentPaddingTop = 12.dp
private val cardContentPaddingEnd = 56.dp
private val cardContentPaddingBottom = 56.dp
private val circleButtonSize = 38.dp
private val circleButtonPadding = 3.dp

/**
 * Allergy summary card with MyIcon shape: tag and summary text on top,
 * small primary-effect circle button in the bottom-right curve. Text is padded
 * so it never overlaps the curve or the button.
 */
@Composable
fun AllergySummaryCard(
    summary: String?,
    tagLabel: String? = null,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    val displaySummary = summary?.takeIf { it.isNotBlank() }
        ?: "Add allergies or dietary needs for your family members to make meal choices easier for everyone."

    Box(
        modifier = modifier
            .size(width = 193.dp, height = 214.dp)
            .then(
                if (onTap != null) Modifier.clickable(onClick = onTap) else Modifier
            )
    ) {
        // 1. Card background: MyIcon shape (unchanged) — white card on optional gray
        Image(
            painter = rememberVectorPainter(image = MyIcon),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Content: tag + text (padded so it never enters the bottom-right curve)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = cardContentPaddingStart,
                    top = cardContentPaddingTop,
                    end = cardContentPaddingEnd,
                    bottom = cardContentPaddingBottom
                )
        ) {
            if (tagLabel != null) {
                Text(
                    text = tagLabel,
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 8.sp
                    ),
                    color = Greyscale130,
                    modifier = Modifier
                        .background(Greyscale30, RoundedCornerShape(percent = 50))
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
                Box(modifier = Modifier.padding(top = 8.dp))
            }
            Text(
                text = displaySummary,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 14.sp
                ),
                color = Greyscale140,
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 3. Bottom-right: small circle button with Primary effect
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = circleButtonPadding, end = circleButtonPadding)
                .size(circleButtonSize)
                .clip(RoundedCornerShape(percent = 50))
                .primaryButtonEffect(
                    isDisabled = false,
                    shape = RoundedCornerShape(percent = 50),
                    disabledBackgroundColor = Greyscale40
                )
                .then(
                    if (onTap != null) Modifier.clickable(onClick = onTap) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.CallMade,
                contentDescription = null,
                tint = Greyscale10,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyIconPreview() {
    Box(
        modifier = Modifier
            .background(Color(0xFFFF0000)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberVectorPainter(image = MyIcon),
            contentDescription = "MyIcon preview",
            modifier = Modifier.size(193.dp, 214.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AllergySummaryCardPreview() {
    Box(
        modifier = Modifier
            .background(Color(0xFFF5F5F5))
    ) {
        AllergySummaryCard(
            summary = "Your family avoids dairy, 🦀, eggs, gluten, red meat 🥩, alcohol, making meal choices simpler and safer for everyone.",
            tagLabel = "25% Allergies",
            onTap = {}
        )
    }
}

