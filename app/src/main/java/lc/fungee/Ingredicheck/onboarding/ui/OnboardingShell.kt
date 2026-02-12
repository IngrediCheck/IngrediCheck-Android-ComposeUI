package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet

@Composable
fun OnboardingShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPaddingEnabled: Boolean = true,
    showFocusedShadow: Boolean = false,
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
            onSheetHeightChanged = onSheetHeightChanged
        ) {
            sheetContent()
        }
    }
}
