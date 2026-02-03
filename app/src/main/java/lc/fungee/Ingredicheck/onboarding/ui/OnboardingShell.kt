package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.components.NonDraggableBottomSheet

@Composable
fun OnboardingShell(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPaddingEnabled: Boolean = true,
    backgroundContent: @Composable BoxScope.() -> Unit,
    sheetContent: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        backgroundContent()

        NonDraggableBottomSheet(
            onDismissRequest = onDismissRequest,
            horizontalPaddingEnabled = horizontalPaddingEnabled
        ) {
            sheetContent()
        }
    }
}
