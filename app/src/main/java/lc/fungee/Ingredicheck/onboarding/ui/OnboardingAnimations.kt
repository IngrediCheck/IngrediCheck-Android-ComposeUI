    package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp

/**
 * Shared animation specs for onboarding UI (e.g. avatar selection size/border, floating robot).
 */
object OnboardingAnimations {
    /** Tween used for avatar size and border when selection changes (180ms, FastOutSlowInEasing). */
    val AvatarSelectionTween: TweenSpec<Dp> = tween(
        durationMillis = 180,
        easing = FastOutSlowInEasing
    )

    /**
     * Smooth easing curve that mimics SwiftUI's .easeInOut (cubic bezier: 0.42, 0.0, 0.58, 1.0).
     * This provides smoother, more natural motion for floating animations.
     */
    private val EaseInOutEasing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)

    /**
     * Floating robot animation offsets (X, Y) used for both ingredi_robo1 and ingredi_robo2.
     * Matches iOS IngrediBotWithText.swift animation timings with smoother easing:
     * - bot float X: easeInOut 3.0s repeatForever autoreverse (0f to 8f)
     * - bot float Y: easeInOut 2.5s repeatForever autoreverse with ~0.5s delay (0f to -6f)
     *
     * Uses a custom cubic bezier easing curve for smoother, more natural floating motion.
     *
     * @param label Optional label for the infinite transition (useful for debugging)
     * @return Pair of Float values: (botX offset in dp, botY offset in dp)
     */
    @Composable
    fun rememberFloatingRobotOffsets(label: String = "floatingRobot"): Pair<Float, Float> {
        val infinite = rememberInfiniteTransition(label = label)
        val botX by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = EaseInOutEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "botX"
        )
        val botY by infinite.animateFloat(
            initialValue = 0f,
            targetValue = -6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2500, delayMillis = 500, easing = EaseInOutEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "botY"
        )
        return Pair(botX, botY)
    }
}
