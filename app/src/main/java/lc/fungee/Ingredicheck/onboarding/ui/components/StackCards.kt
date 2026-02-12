package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito

data class StackCardChip(
    val id: String,
    val name: String,
    val icon: String? = null
)

data class StackCard(
    val id: String,
    val title: String,
    val subTitle: String,
    val color: Color,
    val chips: List<StackCardChip>
)

@Composable
fun StackCards(
    cards: List<StackCard>,
    isChipSelected: (StackCard, StackCardChip) -> Boolean = { _, _ -> false },
    onChipTap: (StackCard, StackCardChip) -> Unit = { _, _ -> },
    onSwipe: (() -> Unit)? = null,
    modifier: Modifier = Modifier.Companion
) {
    val stateCards = remember(cards) { mutableStateListOf<StackCard>().apply { addAll(cards) } }
    val totalCardCount = stateCards.size

    var currentIndex by remember(stateCards) {
        mutableIntStateOf(if (stateCards.isEmpty()) 0 else 1)
    }

    val progressText by remember(currentIndex, totalCardCount) {
        derivedStateOf { if (totalCardCount > 0) "$currentIndex/$totalCardCount" else "" }
    }

    var dragX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    fun cycleCard() {
        if (stateCards.isEmpty()) return
        val first = stateCards.removeAt(0)
        stateCards.add(first)
        if (totalCardCount > 0) {
            currentIndex = (currentIndex % totalCardCount) + 1
        }
    }

    suspend fun animateSwipe(toX: Float) {
        animate(
            initialValue = dragX,
            targetValue = toX,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        ) { value, _ ->
            dragX = value
        }
        cycleCard()
        dragX = 0f
        onSwipe?.invoke()
    }

    BoxWithConstraints(modifier = modifier) {
        val cardHeight: Dp = maxHeight * 0.33f
        val shape = RoundedCornerShape(24.dp)
        val topCard = stateCards.getOrNull(0)
        val backCard = stateCards.getOrNull(1)
        val maxWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)

        Box {
            if (backCard != null) {
                StackCardSurface(
                    card = backCard,
                    progressText = progressText,
                    showContent = false,
                    shape = shape,
                    height = cardHeight,
                    isChipSelected = isChipSelected,
                    onChipTap = onChipTap,
                    modifier = Modifier.Companion
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .shadow(elevation = 2.dp, shape = shape)
                        .border(1.dp, Color.Companion.White.copy(alpha = 0.35f), shape)
                        .blur(4.dp, edgeTreatment = BlurredEdgeTreatment.Companion.Unbounded)
                        .alpha(0.52f)
                        .graphicsLayer { rotationZ = 4f }
                )
            }

            if (topCard != null) {
                val dragRotationZ = ((dragX / maxWidthPx) * 6f).coerceIn(-6f, 6f)
                StackCardSurface(
                    card = topCard,
                    progressText = progressText,
                    showContent = true,
                    shape = shape,
                    height = cardHeight,
                    isChipSelected = isChipSelected,
                    onChipTap = onChipTap,
                    modifier = Modifier.Companion
                        .graphicsLayer {
                            translationX = dragX
                            rotationZ = dragRotationZ
                        }
                        .pointerInput(topCard.id) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragX += dragAmount.x
                                },
                                onDragEnd = {
                                    scope.launch {
                                        when {
                                            dragX > 80f -> animateSwipe(600f)
                                            dragX < -80f -> animateSwipe(-600f)
                                            else -> {
                                                animate(
                                                    initialValue = dragX,
                                                    targetValue = 0f,
                                                    animationSpec = tween(
                                                        durationMillis = 280,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                ) { value, _ ->
                                                    dragX = value
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun StackCardSurface(
    card: StackCard,
    progressText: String,
    showContent: Boolean,
    shape: RoundedCornerShape,
    height: Dp,
    isChipSelected: (StackCard, StackCardChip) -> Boolean,
    onChipTap: (StackCard, StackCardChip) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    Surface(
        modifier = modifier
            .height(height),
        color = card.color,
        shape = shape
    ) {
        Box(modifier = Modifier.Companion.fillMaxSize()) {
            if (showContent) {
                Box(
                    modifier = Modifier.Companion
                        .align(Alignment.Companion.BottomEnd)
                        .padding(end = 10.dp)
                        .offset(y = 17.dp)
                        .alpha(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = Color.Companion.White,
                        modifier = Modifier.Companion.size(76.dp)
                    )
                }
            }

            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 20.dp)
            ) {
                if (showContent) {
                    Box(
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopEnd)
                    ) {
                        Text(
                            text = progressText,
                            fontFamily = Nunito,
                            fontSize = 14.sp,
                            color = Greyscale140
                        )
                    }

                    Box(modifier = Modifier.Companion.align(Alignment.Companion.TopStart)) {
                        Text(
                            text = card.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Companion.Normal,
                            color = Color.Companion.Black
                        )
                    }

                    Text(
                        text = card.subTitle,
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopStart)
                            .padding(top = 28.dp)
                            .alpha(0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Companion.Normal,
                        color = Color.Companion.Black
                    )

                    FlowLayout(
                        horizontalSpacing = 4.dp,
                        verticalSpacing = 8.dp,
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopStart)
                            .padding(top = 72.dp)
                    ) {
                        card.chips.forEach { chip ->
                            StackCardChipPill(
                                title = chip.name,
                                icon = chip.icon,
                                isSelected = isChipSelected(card, chip),
                                onClick = { onChipTap(card, chip) }
                            )
                        }
                    }
                } else {
                    Text(
                        text = progressText,
                        modifier = Modifier.Companion
                            .align(Alignment.Companion.TopEnd)
                            .alpha(0f),
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = Greyscale140
                    )
                }
            }
        }
    }
}

@Composable
private fun StackCardChipPill(
    title: String,
    icon: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val shape = RoundedCornerShape(percent = 50)
    val bg = if (isSelected) Color(0xFFF2F2F2) else Color.Companion.White
    Row(
        modifier = modifier
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, Greyscale40, shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (!icon.isNullOrBlank()) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.Companion.width(8.dp))
        }
        Text(
            text = title,
            fontFamily = Nunito,
            fontSize = 16.sp,
            fontWeight = FontWeight.Companion.Normal,
            color = Color(0xFF303030)
        )
    }
}

@Composable
private fun FlowLayout(
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    modifier: Modifier = Modifier.Companion,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val maxWidth = constraints.maxWidth
        val hSpace = horizontalSpacing.roundToPx()
        val vSpace = verticalSpacing.roundToPx()

        val placeables =
            measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        var x = 0
        var y = 0
        var rowHeight = 0
        val positions = ArrayList<Pair<Int, Int>>(placeables.size)

        placeables.forEach { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + vSpace
                rowHeight = 0
            }
            positions.add(x to y)
            x += p.width + hSpace
            rowHeight = maxOf(rowHeight, p.height)
        }

        val height = (y + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width = maxWidth, height = height) {
            placeables.forEachIndexed { index, p ->
                val (px, py) = positions[index]
                p.placeRelative(px, py)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_4A)
@Composable
private fun StackCardsPreview() {
    val sample = listOf(
        StackCard(
            id = "1",
            title = "one",
            subTitle = "This is the dummy sub-title, and this is the first text",
            color = Color(0xFFB04BFF),
            chips = listOf(
                StackCardChip("1", "High Protein", "🍗"),
                StackCardChip("2", "Low Carb", "🥒"),
                StackCardChip("3", "Low Fat", "🥑"),
                StackCardChip("4", "Balanced Marcos", "⚖️")
            )
        ),
        StackCard(
            id = "2",
            title = "two",
            subTitle = "This is the dummy sub-title, and this is the first text",
            color = Color(0xFFFBCB7F),
            chips = listOf(
                StackCardChip("1", "High Protein", "🍗"),
                StackCardChip("2", "Low Carb", "🥒"),
                StackCardChip("3", "Low Fat", "🥑"),
                StackCardChip("4", "Balanced Marcos", "⚖️"),
                StackCardChip("5", "High Protein", "🍗"),
                StackCardChip("6", "Low Carb", "🥒"),
                StackCardChip("7", "Low Fat", "🥑"),
                StackCardChip("8", "Balanced Marcos", "⚖️")
            )
        ),
        StackCard(
            id = "3",
            title = "three",
            subTitle = "This is the dummy sub-title, and this is the first text hughwrhugw oighwioghiowhgo woihgiowhgiow oigwhioghwiog owirhgiorwhgiowrh woighiowrhgiowrg oighrwioghiorwhgiohrwg",
            color = Color(0xFFFF3D6E),
            chips = listOf(
                StackCardChip("1", "High Protein", "🍗"),
                StackCardChip("2", "Low Carb", "🥒"),
                StackCardChip("3", "Low Fat", "🥑"),
                StackCardChip("4", "Balanced Marcos", "⚖️")
            )
        )
    )

    MaterialTheme {
        Column(
            modifier = Modifier.Companion.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.Companion
                    .background(Color(0xFFF5F5F0))
                    .padding(16.dp)
            ) {
                StackCards(cards = sample, modifier = Modifier.Companion)
            }
        }
    }
}