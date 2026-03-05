package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet

@Composable
fun OnboardingShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPaddingEnabled: Boolean = true,
    showFocusedShadow: Boolean = false,
    // Optional override for the base bottom padding of this sheet.
    // When null, the default per‑screen category padding is used.
    baseBottomPaddingOverride: Dp? = null,
    // When false, the sheet will ignore IME insets (keyboard) and stay anchored,
    // allowing the keyboard to overlap lower content (used for Just Me name edit).
    respectImePadding: Boolean = true,
    onSheetHeightChanged: ((androidx.compose.ui.unit.Dp) -> Unit)? = null,
    backgroundContent: @Composable BoxScope.() -> Unit,
    sheetContent: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        backgroundContent()

        NonDraggableBottomSheet(
            onDismissRequest = onDismissRequest,
            horizontalPaddingEnabled = horizontalPaddingEnabled,
            showFocusedShadow = showFocusedShadow,
            baseBottomPaddingOverride = baseBottomPaddingOverride,
            respectImePadding = respectImePadding,
            onSheetHeightChanged = onSheetHeightChanged
        ) {
            sheetContent()
        }
    }
}
