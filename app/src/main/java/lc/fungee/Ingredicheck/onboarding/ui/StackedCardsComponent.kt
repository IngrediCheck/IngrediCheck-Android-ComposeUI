package lc.fungee.Ingredicheck.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

/**
 * A composable that displays a stack of cards with swipe-to-rotate functionality.
 * The second card (from top) is rotated by 6 degrees to show the stack effect.
 * When a card is swiped left, it moves to the bottom and the next card becomes the top.
 */
@Composable
fun StackedCardsComponent(
    modifier: Modifier = Modifier,
    cardContent: @Composable (index: Int, isTop: Boolean) -> Unit
) {
    // Create 6 cards with initial indices
    val initialCards = remember {
        mutableStateListOf(0, 1, 2, 3, 4, 5)
    }
    val cards = remember { initialCards }

    // Track drag offset for the top card
    val dragOffsetX = remember { Animatable(0f) }
    val dragOffsetY = remember { Animatable(0f) }
    val isDragging = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    val swipeThreshold = with(density) { 200.dp.toPx() } // Fallback absolute threshold
    
    // Track card width so we can compute 20% drag distance.
    var cardWidth by remember { mutableStateOf(0f) }
    val thirtyPercentThreshold = cardWidth * 0.2f // 20% of card width

    // Reset drag offset immediately when cards are reordered
    // Use the top card index as a key to detect when it changes
    val topCardIndex = remember(cards.size) { cards.lastOrNull() ?: -1 }
    
    LaunchedEffect(topCardIndex) {
        // When top card changes, reset drag offset immediately
        dragOffsetX.snapTo(0f)
        dragOffsetY.snapTo(0f)
    }

    val containerPadding = with(density) { 20.dp.toPx() }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 20.dp)
    ) {
        // Render cards from bottom to top (so top card is rendered last and appears on top)
        cards.forEachIndexed { stackIndex, cardIndex ->
            val isTopCard = stackIndex == cards.lastIndex
            val isSecondCard = stackIndex == cards.lastIndex - 1

            // Calculate z-index offset (cards behind are slightly offset)
            val zIndexOffset = (cards.lastIndex - stackIndex) * 2.dp

            // Rotation:
            // - Top card should be 0°
            // - Second card (just behind top) should rest at 6°
            //   and animate smoothly back to 0° when it becomes the top card.
            val targetRotation = when {
                isTopCard -> 0f
                isSecondCard -> 6f
                else -> 0f
            }
            val rotation by animateFloatAsState(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = 1800, // extra slow, very smooth glide 6° -> 0°
                    easing = FastOutSlowInEasing
                ),
                label = "stackCardRotation"
            )

            // Alpha: only show top 2 cards (top card and second card), hide the rest
            val alpha = if (stackIndex >= cards.lastIndex - 1) 1f else 0f

            // Scale: only top 2 cards are visible, so no scaling needed
            val scale = 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .offset(x = zIndexOffset, y = zIndexOffset)
                    .alpha(alpha)
                    .rotate(rotation)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    // Back card visual treatment: subtle blur layer like iOS
                    .let { base ->
                        if (isSecondCard && !isTopCard) {
                            base.blur(
                                radius = 4.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                        } else {
                            base
                        }
                    }
                    .then(
                        if (isTopCard) {
                            Modifier
                                .onGloballyPositioned { coordinates ->
                                    cardWidth = coordinates.size.width.toFloat()
                                }
                                .offset {
                                    IntOffset(
                                        dragOffsetX.value.roundToInt(),
                                        dragOffsetY.value.roundToInt()
                                    )
                                }
                                .pointerInput(cardIndex) {
                                    detectDragGestures(
                                        onDragStart = { isDragging.value = true },
                                        onDragEnd = {
                                            isDragging.value = false
                                            val currentOffsetX = dragOffsetX.value
                                            // Use 30% of card width when known, otherwise fall back to a
                                            // fixed px threshold; support BOTH directions (left & right).
                                            val baseThreshold =
                                                if (cardWidth > 0f) thirtyPercentThreshold
                                                else swipeThreshold

                                            // If swiped beyond threshold to LEFT or RIGHT, complete swipe
                                            // with a slower, smooth animation so the user can see the card
                                            // travelling off-screen.
                                            if (currentOffsetX <= -baseThreshold ||
                                                currentOffsetX >= baseThreshold
                                            ) {
                                                coroutineScope.launch {
                                                    val width = if (cardWidth > 0f) cardWidth else swipeThreshold
                                                    val targetX =
                                                        if (currentOffsetX >= 0f) width * 1.4f
                                                        else -width * 1.4f

                                                    // Animate card fully out of the screen
                                                    dragOffsetX.animateTo(
                                                        targetX,
                                                        animationSpec = tween(
                                                            durationMillis = 420,
                                                            easing = FastOutSlowInEasing
                                                        )
                                                    )

                                                    // Reorder: move top card to bottom
                                                    val topCard = cards.removeAt(cards.lastIndex)
                                                    cards.add(0, topCard)

                                                    // Reset offsets for the new top card
                                                    dragOffsetX.snapTo(0f)
                                                    dragOffsetY.snapTo(0f)
                                                }
                                            } else {
                                                // Snap back if not swiped far enough
                                                coroutineScope.launch {
                                                    dragOffsetX.animateTo(
                                                        0f,
                                                        animationSpec = tween(
                                                            durationMillis = 280,
                                                            easing = FastOutSlowInEasing
                                                        )
                                                    )
                                                    dragOffsetY.animateTo(
                                                        0f,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                                            stiffness = Spring.StiffnessMedium
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            coroutineScope.launch {
                                                // Free horizontal dragging (primarily left); no clamping,
                                                // so the card can visually reach / go past the screen edge.
                                                val newOffsetX = dragOffsetX.value + dragAmount.x
                                                dragOffsetX.snapTo(newOffsetX)
                                                // Slight vertical offset for natural feel
                                                dragOffsetY.snapTo(dragAmount.y * 0.3f)
                                            }
                                        }
                                    )
                                }
                        } else {
                            Modifier
                        }
                    )
            ) {
                // Only the top card should reveal its full content (text, etc.).
                // Cards behind can render a simplified/placeholder state if desired.
                cardContent(cardIndex, isTopCard)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun StackedCardsAvoidPreview() {
    val cards = OnboardingChipData.avoidCards
    val total = cards.size

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        StackedCardsComponent(
//            modifier = Modifier.padding(horizontal = 20.dp),
            cardContent = { index, isTop ->
                val card = cards[index % cards.size]

                val bgColor = try {
                    Color(android.graphics.Color.parseColor(card.colorHex))
                } catch (_: IllegalArgumentException) {
                    Color(0xFFFFF6B3)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(bgColor)
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    if (isTop) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = card.title,
                                    fontFamily = Nunito,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Greyscale150
                                )
                                Text(
                                    text = "${index + 1}/$total",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Greyscale140
                                )
                            }

                            Text(
                                text = card.description,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Greyscale140
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            SimpleFlowRow(
                                horizontalSpacing = 8.dp,
                                verticalSpacing = 8.dp
                            ) {
                                card.options.forEach { opt ->
                                    AvoidOptionChip(
                                        option = opt,
                                        isSelected = false,
                                        onClick = {}
                                    )
                                }
                            }
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.leaf_arrow_circlepath),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .height(56.dp)
                            .alpha(0.22f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        )
    }
}
