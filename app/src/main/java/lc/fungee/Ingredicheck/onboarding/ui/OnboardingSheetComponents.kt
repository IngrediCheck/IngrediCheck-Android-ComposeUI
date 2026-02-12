package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.ScreenCategory
import lc.fungee.Ingredicheck.ui.theme.rememberScreenCategory
import lc.fungee.Ingredicheck.ui.theme.sheetSubtitleTextStyle
import lc.fungee.Ingredicheck.ui.theme.sheetTitleTextStyle

@Composable
internal fun responsiveSpacerHeight(
    small: Dp,
    medium: Dp,
    large: Dp
): Dp {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> small
        ScreenCategory.Normal -> medium
        ScreenCategory.Large -> large
    }
}

@Composable
internal fun SheetHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = sheetTitleTextStyle(),
            maxLines = 1,
            color = Greyscale150,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
//                    .padding(start = 21 .dp) // Adjusting for IconButton default 48dp size (12dp internal padding)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ion_chevron_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = Greyscale150
                )
            }
        }
    }

    if (subtitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = sheetSubtitleTextStyle(),
            color = Greyscale110,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
