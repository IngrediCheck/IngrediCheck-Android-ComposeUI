package lc.fungee.Ingredicheck.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.ScreenCategory
import lc.fungee.Ingredicheck.ui.theme.rememberScreenCategory

@Composable
fun NonDraggableBottomSheet(
    onDismissRequest: () -> Unit,
    horizontalPaddingEnabled: Boolean = true,
    showFocusedShadow: Boolean = false,
    modifier: Modifier = Modifier,
    onSheetHeightChanged: ((androidx.compose.ui.unit.Dp) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    // No Dialog anymore – this is an inline bottom sheet that can be placed in any layout.
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    var measuredHeight by remember { mutableStateOf(0.dp) }
    val currentSheetHeight = measuredHeight

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Soft gradient fade behind the sheet
        if (showFocusedShadow) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentSheetHeight + 161.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 1f)
                            ),
                            startY = 0f,
                            endY = with(density) { 161.dp.toPx() }
                        )
                    )
            )
        }

        // Focused grey shadow (30dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentSheetHeight + 30.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD9D9D9).copy(alpha = 0f),
                            Color(0xFFD9D9D9).copy(alpha = 0.2f)
                        ),
                        startY = 0f,
                        endY = with(density) { 30.dp.toPx() }
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    alignment = Alignment.BottomCenter
                )
                .onGloballyPositioned { coordinates ->
                    val h = with(density) { coordinates.size.height.toDp() }
                    measuredHeight = h
                    onSheetHeightChanged?.invoke(h)
                }
                .shadow(
                    elevation = 27.5.dp,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    ambientColor = Color(0x6BD9D9D9),
                    spotColor = Color(0x6BD9D9D9)
                )
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                )
        ) {
            val category = rememberScreenCategory()

            val startPadding = if (horizontalPaddingEnabled) {
                when (category) {
                    ScreenCategory.Large -> 24.dp
                    else -> 20.dp
                }
            } else {
                0.dp
            }
            val endPadding = if (horizontalPaddingEnabled) {
                when (category) {
                    ScreenCategory.Large -> 24.dp
                    ScreenCategory.Normal -> 20.dp
                    ScreenCategory.Small -> 18.dp
                }
            } else {
                0.dp
            }
            val topPadding = when (category) {
                ScreenCategory.Large -> 26.dp
                ScreenCategory.Normal -> 24.dp
                ScreenCategory.Small -> 22.dp
            }
            val baseBottomPadding = when (category) {
                ScreenCategory.Large -> 34.dp
                ScreenCategory.Normal -> 32.dp
                ScreenCategory.Small -> 30.dp
            }

            // Add navigation bar / IME height to ensure content is above system buttons or keyboard
            val navBarPadding = WindowInsets.navigationBars
                .asPaddingValues().calculateBottomPadding()
            val imePadding = WindowInsets.ime
                .asPaddingValues().calculateBottomPadding()

            // When keyboard is visible, keep content ~10.dp above it.
            val totalBottomPadding = if (imePadding > 0.dp) {
                imePadding + 10.dp
            } else {
                baseBottomPadding + navBarPadding
            }

            val contentPadding = PaddingValues(
                start = startPadding,
                top = topPadding,
                end = endPadding,
                bottom = 0.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                horizontalAlignment = Alignment.Start
            ) {
                content()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalBottomPadding)
                )
            }
        }
    }
}

@Composable
fun InviteCodeBottomSheet(
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    NonDraggableBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Do you have an invite code?",
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Greyscale150
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                title = "Enter invite code",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                isDisabled = true,
                onClick = onSecondaryClick,
                textColor = Greyscale110,
                disabledTextColor = Greyscale110,
                disabledBackgroundColor = Greyscale40,
                borderColor = Greyscale40
            )

            PrimaryButton(
                title = "No, Continue",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                onClick = onPrimaryClick
            )
        }
    }
}
