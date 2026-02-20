package lc.fungee.Ingredicheck.onboarding.data

import androidx.compose.ui.graphics.Color

/**
 * Shared avatar/memoji background color resolution used by onboarding UI
 * (AllergyScreens, OnboardingHost, OnboardingStepScreens) and memoji flows.
 */

fun avatarBackgroundColorForId(colorId: String?): Color {
    return when (colorId) {
        "color_pastel_blue" -> Color(0xFFA5D8FF)
        "color_warm_pink" -> Color(0xFFFFB3C1)
        "color_soft_green" -> Color(0xFFB9FBC0)
        "color_lavender" -> Color(0xFFE3B8FF)
        "color_orange" -> Color(0xFFFFB74D)
        "color_yellow" -> Color(0xFFFFE082)
        "color_transparent" -> Color.Transparent
        else -> Color.White
    }
}

/**
 * Resolves member avatar background: memoji color if set, else random pastel (colorHex) from member creation.
 */
fun memberAvatarBackgroundColor(backgroundColorId: String, colorHex: String): Color {
    if (backgroundColorId.isNotBlank()) return avatarBackgroundColorForId(backgroundColorId)
    if (colorHex.isNotBlank()) {
        return kotlin.runCatching {
            Color(android.graphics.Color.parseColor(colorHex))
        }.getOrElse { Color.White }
    }
    return Color.White
}
