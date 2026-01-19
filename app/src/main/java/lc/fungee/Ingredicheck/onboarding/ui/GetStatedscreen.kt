package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.GrayScale80
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.layout.ContentScale


@Composable
fun FillingPipeLine(
    onComplete: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5_000, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val radiusPx = 2.dp.toPx()
        val strokePx = 1.dp.toPx()

        drawRoundRect(
            color = Greyscale40,
            topLeft = Offset.Zero,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = strokePx)
        )

        drawRoundRect(
            color = GrayScale80,
            topLeft = Offset.Zero,
            size = Size(size.width * progress.value, size.height),
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )
    }
}


@Composable
fun GetStatedScreen(
    onGetStarted: () -> Unit = {}
) {
    var isFillingComplete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 16.dp)
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FillingPipeLine(
                onComplete = { isFillingComplete = true }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 18.dp, bottom = 18.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.onbording_getstartedimg),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()

            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .padding(bottom = 66.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (!isFillingComplete) {
                PrimaryButton(
                    title = "Get Started",
                    takeFullWidth = true,
                    isDisabled = true,
                    disabledBackgroundColor = Greyscale40,
                    onClick = onGetStarted
                )
            }

            AnimatedVisibility(
                visible = isFillingComplete,
                enter = scaleIn() + fadeIn()
            ) {
                PrimaryButton(
                    title = "Get Started",
                    takeFullWidth = true,

                    onClick = onGetStarted
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//private fun FillingPipeLinePreview() {
//    Box(modifier = Modifier.padding(20.dp)) {
//        FillingPipeLine(onComplete = {})
//    }
//}


@Preview(showBackground = true)
@Composable
private fun GetStatedScreenPreview() {
    GetStatedScreen()
}
