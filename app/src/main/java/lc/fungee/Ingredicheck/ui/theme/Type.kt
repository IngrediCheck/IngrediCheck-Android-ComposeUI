package lc.fungee.Ingredicheck.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.nunito_black, FontWeight.Black),
    Font(R.font.nunito_black_italic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.nunito_extralight, FontWeight.ExtraLight),
    Font(R.font.nunito_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic)
)

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_extralight, FontWeight.ExtraLight),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_extrabold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold)
)

val NunitoRegular = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal)
val NunitoMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Medium)
val NunitoSemiBold = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold)
val NunitoBold = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold)
val NunitoExtraBold = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold)
val NunitoBlack = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Black)
val NunitoLight = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Light)
val NunitoExtraLight = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.ExtraLight)

// Individual weight aliases for easy one-line usage
val nunito_regular = FontFamily(Font(R.font.nunito_regular))
val nunito_medium = FontFamily(Font(R.font.nunito_medium))
val nunito_semibold = FontFamily(Font(R.font.nunito_semibold))
val nunito_bold = FontFamily(Font(R.font.nunito_bold))
val nunito_extrabold = FontFamily(Font(R.font.nunito_extrabold))
val nunito_black = FontFamily(Font(R.font.nunito_black))
val nunito_light = FontFamily(Font(R.font.nunito_light))
val nunito_extralight = FontFamily(Font(R.font.nunito_extralight))

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)