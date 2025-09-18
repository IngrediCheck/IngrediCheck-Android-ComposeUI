package lc.fungee.IngrediCheck.data.source

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun hapticSuccess(haptic: HapticFeedback) {


    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
    // Get Vibrator in a backward-compatible way
//    val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        val vm = context.getSystemService(VibratorManager::class.java)
//        vm?.defaultVibrator
//    } else {
//        @Suppress("DEPRECATION")
//        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//    }
//
//    vibrator ?: return
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        // “success” feel: short-short-long
//        val timings = longArrayOf(0, 30, 40, 30, 50, 60)      // on/off pattern (ms)
//        val amps    = intArrayOf(0, 225, 0, 255, 0, 255)      // 0=silence, 1–255 strength
//        val effect = VibrationEffect.createWaveform(timings, amps, -1)
//        vibrator.vibrate(effect)
//    } else {
//        @Suppress("DEPRECATION")
//        vibrator.vibrate(120) // simple fallback
//    }
}
