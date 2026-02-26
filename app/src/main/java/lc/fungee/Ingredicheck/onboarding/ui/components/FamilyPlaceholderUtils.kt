package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

fun familyPlaceholderColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF9AD0FF),
        Color(0xFFFFB3C1),
        Color(0xFFB9F6CA),
        Color(0xFFFFE29A),
        Color(0xFFD7B9FF),
        Color(0xFFFFC59A)
    )
    val idx = (seed.hashCode().absoluteValue % palette.size)
    return palette[idx]
}

