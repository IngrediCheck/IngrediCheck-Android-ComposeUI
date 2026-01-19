package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.core.animateDpAsState
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
    sheetHeight: Dp = 243.dp,
    backgroundContent: @Composable BoxScope.() -> Unit,
    sheetContent: @Composable () -> Unit
) {
    val animatedSheetHeight by animateDpAsState(targetValue = sheetHeight, label = "onboardingSheetHeight")

    Box(modifier = modifier.fillMaxSize()) {
        backgroundContent()

        NonDraggableBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetHeight = animatedSheetHeight
        ) {
            sheetContent()
        }
    }
}
