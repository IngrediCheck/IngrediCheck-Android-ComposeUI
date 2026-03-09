package lc.fungee.Ingredicheck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.ui.components.TabBar
import lc.fungee.Ingredicheck.ui.theme.GrayScale30
import lc.fungee.Ingredicheck.ui.theme.titleTextStyle

class ConcaveCornerShape(
    private val cornerRadius: Float,
    private val concaveRadius: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(
        Path().apply {

            val w = size.width
            val h = size.height

            // Start top left
            moveTo(cornerRadius, 0f)

            // Top edge
            lineTo(w - cornerRadius, 0f)

            // Top right corner
            arcTo(
                rect = Rect(
                    w - 2 * cornerRadius,
                    0f,
                    w,
                    2 * cornerRadius
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Right edge
            lineTo(w, h - concaveRadius * 2)

            // Concave inward curve
            arcTo(
                rect = Rect(
                    w - concaveRadius * 2,
                    h - concaveRadius * 2,
                    w,
                    h
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            // Bottom edge
            lineTo(cornerRadius, h)

            // Bottom left
            arcTo(
                rect = Rect(
                    0f,
                    h - 2 * cornerRadius,
                    2 * cornerRadius,
                    h
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Left edge
            lineTo(0f, cornerRadius)

            // Top left
            arcTo(
                rect = Rect(
                    0f,
                    0f,
                    2 * cornerRadius,
                    2 * cornerRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            close()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ConcaveShapePreview() {

    val shape = with(LocalDensity.current) {
        ConcaveCornerShape(
            cornerRadius = 64.dp.toPx(),
            concaveRadius = 64.dp.toPx()
        )
    }

    Box(
        modifier = Modifier.Companion
            .size(300.dp)
            .clip(shape)
            .background(Color.Companion.LightGray)
    )
}

/** Dummy items for testing scroll and tab bar. */
private val dummyHomeItems = List(32) { i ->
    "Item ${i + 1} — Scroll to test that the tab bar stays fixed at the bottom."
}

@Composable
fun HomeScreen(
    onRecentScansTap: () -> Unit = {},
    onChatBotTap: () -> Unit = {},
    onScannerTap: () -> Unit = {}
) {
    // Extra bottom padding so content doesn't sit under the tab bar
    val tabBarBottomPadding = 52.dp
    val tabBarHeight = 80.dp

    var isTabBarExpanded by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // iOS-style: collapse tab bar only when scrolling down; expand when scrolling up enough or when scroll stops.
    LaunchedEffect(listState, density) {
        var expandJob: Job? = null
        var initialized = false
        var prevScroll = 0f
        var maxScroll = 0f

        val minScrollDeltaPx = with(density) { 5.dp.toPx() }       // minimum change to react
        val bottomThresholdPx = with(density) { 100.dp.toPx() }    // how far up from bottom before expanding

        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                // Collapse/expand based on scroll direction and distance, similar to iOS geometry-based logic.
                val currentScroll = index * 100_000f + offset.toFloat()

                if (!initialized) {
                    initialized = true
                    prevScroll = currentScroll
                    maxScroll = currentScroll
                    return@collect
                }

                if (currentScroll > maxScroll) {
                    maxScroll = currentScroll
                }

                val delta = currentScroll - prevScroll
                var nextExpanded = isTabBarExpanded

                if (currentScroll <= 0f) {
                    // At or near the very top: keep tab bar expanded.
                    nextExpanded = true
                } else {
                    when {
                        // Scrolling down (finger swiping up; content moves up): collapse.
                        delta > minScrollDeltaPx -> {
                            nextExpanded = false
                        }
                        // Scrolling up: only expand once we've moved up a bit from the deepest scroll.
                        delta < -minScrollDeltaPx -> {
                            if (currentScroll < maxScroll - bottomThresholdPx) {
                                nextExpanded = true
                            }
                        }
                    }
                }

                if (nextExpanded != isTabBarExpanded) {
                    isTabBarExpanded = nextExpanded
                }

                prevScroll = currentScroll

                // Auto re-expand shortly after scrolling stops while collapsed.
                expandJob?.cancel()
                if (!isTabBarExpanded) {
                    expandJob = launch {
                        delay(150L)
                        isTabBarExpanded = true
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = tabBarHeight + tabBarBottomPadding
            )
        ) {
            item {
                Text(
                    text = "Home Screen",
                    style = titleTextStyle(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            itemsIndexed(dummyHomeItems) { index, text ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth().background(Color.Black)
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = GrayScale30),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.background(Color.Black).padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        TabBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = tabBarBottomPadding),
            isExpanded = isTabBarExpanded,
            onRecentScansTap = onRecentScansTap,
            onChatBotTap = onChatBotTap,
            onScannerTap = onScannerTap
        )
    }
}