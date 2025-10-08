package lc.fungee.IngrediCheck.model.source

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Plays a standard "confirm" haptic. By default uses Compose's HapticFeedback Confirm.
 * If useBypass is true and a Context is provided, falls back to a one-shot Vibrator effect
 * approximating Confirm for consistency across devices.
 */
fun hapticSuccess(
    haptic: HapticFeedback,
    context: Context? = null,
    useBypass: Boolean = false
) {
    if (!useBypass) {
        // System-provided Confirm haptic (preferred)
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        return
    }

    // Bypass path: approximate Confirm with a short one-shot vibration
    val ctx = context ?: return
    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = ctx.getSystemService(VibratorManager::class.java)
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    vibrator ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(40)
    }
}
