package lc.fungee.Ingredicheck.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenCategory {
    Small, Normal, Large
}

@Composable
fun rememberScreenCategory(): ScreenCategory {
    val screenHeight = LocalConfiguration.current.screenHeightDp

    return when {
        screenHeight < 700 -> ScreenCategory.Small
        screenHeight < 820 -> ScreenCategory.Normal
        else -> ScreenCategory.Large
    }
}

@Composable
fun titleTextStyle(): TextStyle {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )
        ScreenCategory.Normal -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 22.sp
        )
        ScreenCategory.Large -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp
        )
    }
}

@Composable
fun subtitleTextStyle(): TextStyle {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        ScreenCategory.Normal -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        ScreenCategory.Large -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun buttonHeight(): Dp {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> 48.dp
        ScreenCategory.Normal -> 52.dp
        ScreenCategory.Large -> 56.dp
    }
}

@Composable
fun buttonTextSize(): androidx.compose.ui.unit.TextUnit {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> 16.sp
        ScreenCategory.Normal -> 18.sp
        ScreenCategory.Large -> 20.sp
    }
}

@Composable
fun buttonIconSize(): Dp {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> 18.dp
        ScreenCategory.Normal -> 20.dp
        ScreenCategory.Large -> 24.dp
    }
}

@Composable
fun sheetTitleTextStyle(): TextStyle {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 24.sp
        )
        ScreenCategory.Normal -> TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 26.sp
        )
        ScreenCategory.Large -> TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 28.sp
        )
    }
}

@Composable
fun sheetSubtitleTextStyle(): TextStyle {
    return when (rememberScreenCategory()) {
        ScreenCategory.Small -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
        ScreenCategory.Normal -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        ScreenCategory.Large -> TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun responsiveSheetHeight(baseFigmaHeight: Float): Dp {
    val category = rememberScreenCategory()
    
    return when (category) {
        ScreenCategory.Small -> (LocalConfiguration.current.screenHeightDp * (baseFigmaHeight / 812f)).dp
        ScreenCategory.Normal, ScreenCategory.Large -> baseFigmaHeight.dp
    }
}
