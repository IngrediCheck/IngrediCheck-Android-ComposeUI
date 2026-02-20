package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp

/**
 * Shared animation specs for onboarding UI (e.g. avatar selection size/border).
 */
object OnboardingAnimations {
    /** Tween used for avatar size and border when selection changes (180ms, FastOutSlowInEasing). */
    val AvatarSelectionTween: TweenSpec<Dp> = tween(
        durationMillis = 180,
        easing = FastOutSlowInEasing
    )
}
