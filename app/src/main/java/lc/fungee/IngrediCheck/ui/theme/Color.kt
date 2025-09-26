package lc.fungee.IngrediCheck.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
//custom color
val Greyscale600=Color(0xFF4F5449)
val PrimaryGreen100 = Color(0xFF789D0E)
val Greyscale500=Color(0xFF7E8379)
val demo =Color(0xFFEBEDF0)
val Greyscale200=Color(0xFFDDDEDA)
val Greyscale50 =Color(0xFFF9F9F8)
val Greyscale400=Color(0xFF9EA19B)
val White =Color(0xFFFFFFFF)
val PrimarayGreen50=Color(0xFFF6FCEE)
val Greyscale700=Color(0xFF1B270C)
val  Statusfail = Color(0xFFF04438)
val LabelsPrimary = Color(0xFF000000)
val  Greyscale800  = Color(0xFF121A08)
val Grey75 = Color(0xFFBFBFBF)

// Additional centralized colors (migrated from hardcoded usages)
val OverlayScrim = Color(0x88000000)
val Greyscale100 = Color(0xFFF3F2F9)
val Greyscale100Alt = Color(0xFFF3F2F8)
val BrandGlow = Color(0xFFCBEB6E)
val BrandDeepGreen = Color(0xFF2B7A0B)
val DestructiveRed = Color(0xFFD03B35)
val DividerLight = Color(0xFFEBECE9)

// Status and feedback colors
val StatusMatchBg = Color(0xFFF3FFF7)
val StatusMatchFg = Color(0xFF047D4B)
val StatusUncertainBg = Color(0xFFFFFBF0)
val StatusUncertainFg = Color(0xFF955102)
val StatusMaybeBg = Color(0xFFFFF9EA)
val StatusUnmatchedBg = Color(0xFFFFF5F4)
val StatusUnmatchedFg = Color(0xFF972D26)

// Ingredient safety item colors
val SafetySafeBg = Color(0xFFE8F5E8)
val SafetyMaybeUnsafeBg = Color(0xFFFFF3E0)
val SafetyDefinitelyUnsafeBg = Color(0xFFFFEBEE)
val SafetyNoneBg = Color(0xFFF5F5F5)
val StatusSafeIcon = Color(0xFF4CAF50)
val StatusMaybeUnsafeIcon = Color(0xFFFF9800)
val StatusDefinitelyUnsafeIcon = Color(0xFFF44336)
val StatusNoneIcon = Color(0xFF9E9E9E)

// Misc UI surfaces
val TooltipBg = Color(0xFF212121)

// Centralized color access for the app. Prefer using these going forward.
object AppColors {
    // Brand palette
    val Brand = PrimaryGreen100           // #789D0E (primary accent used across the app)
    val BrandLight = PrimarayGreen50      // Light surface tint for brand
    val BrandDark = Greyscale700          // Dark companion used in some headers
    val OnBrand = Color.White             // Text/icon on brand surfaces

    // Neutrals (alias existing greys)
    val Neutral50 = Greyscale50
    val Neutral100 = Greyscale100
    val Neutral200 = Greyscale200
    val Neutral400 = Greyscale400
    val Neutral500 = Greyscale500
    val Neutral600 = Greyscale600
    val Neutral700 = Greyscale700
    val Neutral800 = Greyscale800

    // Semantic defaults
    val Background = Color.White
    val Surface = Neutral50
    val SurfaceMuted = Neutral100
    val OnBackground = LabelsPrimary
    val OnSurface = Greyscale600
    val Error = Statusfail
    val ErrorStrong = DestructiveRed
    val Scrim = OverlayScrim
    val Divider = DividerLight
}

// Optional: convenience alias for primary brand color
val BrandColor = AppColors.Brand
