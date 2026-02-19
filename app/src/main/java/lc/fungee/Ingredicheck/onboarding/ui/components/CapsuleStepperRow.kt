package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.Nunito

data class CapsuleStep(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun CapsuleStepperRow(
    steps: List<CapsuleStep>,
    activeIndex: Int,
    modifier: Modifier = Modifier.Companion,
    onStepClick: ((Int) -> Unit)? = null,
    inactiveColor: Color = Color(0xFFF6FCED),
    activeColor: Color = Color(0xFF91B640),
    lineHeight: Dp = 15.dp,
    horizontalPadding: Dp = 16.dp,
    itemSpacing: Dp = 10.dp,
    animationDurationMs: Int = 280,
    progressExcludedIndex: Int? = null // Step index that should not count toward progress fill
) {
    if (steps.isEmpty()) return

    val clampedActive = activeIndex.coerceIn(0, steps.lastIndex)

    var maxReachedIndex by remember { mutableIntStateOf(clampedActive) }
    if (clampedActive > maxReachedIndex) {
        maxReachedIndex = clampedActive
    }
    
    // Adjust maxReachedIndex for progress calculation: exclude the excluded step
    val effectiveMaxReachedIndex = if (progressExcludedIndex != null && maxReachedIndex > progressExcludedIndex) {
        maxReachedIndex - 1
    } else {
        maxReachedIndex
    }

    // Layout model (fixed widths so we can compute the progress fill length deterministically)
    val collapsedHeight = 44.dp
    val collapsedWidth = 64.dp
    val expandedWidth = 180.dp

    // Track the actual measured width of the active capsule so the total line length
    // matches the real UI instead of the old fixed 180.dp.
    var activeItemWidth by remember { mutableStateOf(expandedWidth) }

    // Calculate fill width to the max reached index.
    // If the active item is before the max reached index, we must add the expansion delta
    // because the active item (which is wider) pushes the subsequent items (up to max) further to the right.
    val baseFill = (collapsedWidth + itemSpacing) * maxReachedIndex
    // Use the *measured* active capsule width instead of the old fixed expandedWidth,
    // so the filled line stops exactly before the first *unvisited* capsule.
    val expansionDelta = if (clampedActive < maxReachedIndex) (activeItemWidth - collapsedWidth) else 0.dp
    val fillToStartOfActive = baseFill + expansionDelta

    val fillToStartOfActiveState by animateDpAsState(
        targetValue = fillToStartOfActive,
        animationSpec = tween(animationDurationMs),
        label = "fillToStartOfActive"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()

//            .padding(horizontal = horizontalPadding)
            .heightIn(min = 64.dp)
    ) {
        val scrollState = rememberScrollState()
        val fillOverlap = lineHeight / 2
        val density = LocalDensity.current

        // Total width: all capsules at collapsed width + extra space contributed
        // by the active capsule beyond the collapsed width, plus item spacing.
        val extraWidthFromActive = (activeItemWidth - collapsedWidth).coerceAtLeast(0.dp)
        val totalContentWidth =
            (collapsedWidth * steps.size) + extraWidthFromActive + (itemSpacing * (steps.size - 1))

        // We use the direct calculated value instead of animated state for instant fill as requested previously,
        // or we can re-introduce animation if "fill fast" allows it.
        // User previously asked: "fill fast without animatioin"
        // I will stick to the direct value 'fillToStartOfActive' for the Box width.
        val filledWidth = (fillToStartOfActive + fillOverlap).coerceAtMost(totalContentWidth)

        // When the active step changes, gently auto‑scroll so the active capsule stays in view,
        // but keep the *previous* capsule visible as context (don't scroll too far).
        LaunchedEffect(clampedActive, totalContentWidth) {
            // Approximate horizontal offset of the previous capsule's start based on fixed layout model.
            val stepSpan = collapsedWidth + itemSpacing
            val previousIndex = (clampedActive - 1).coerceAtLeast(0)
            val previousStartDp = stepSpan * previousIndex
            // Scroll so that the previous capsule starts near the left edge (or 0).
            val desiredScrollDp = previousStartDp.coerceAtLeast(0.dp)
            val desiredScrollPx = with(density) { desiredScrollDp.toPx() }
            val target = desiredScrollPx
                .toInt()
                .coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(target)
        }

        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                // Padding is inside the scrollable content so it scrolls away once the row moves.
                .horizontalScroll(scrollState)
                .padding(horizontal = horizontalPadding)
                .align(Alignment.Companion.Center)
        ) {
            // Background line (scrolls with content)
            Box(
                modifier = Modifier.Companion
                    .width(totalContentWidth)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(lineHeight / 2))
//                    .background(Color.Green)
                   .background(inactiveColor)
                    .align(Alignment.Companion.CenterStart)
            )

            // Filled line (scrolls with content)
            Box(
                modifier = Modifier.Companion.width(filledWidth)
                    .height(lineHeight)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(lineHeight / 2))
                    .background(activeColor)
                    .align(Alignment.Companion.CenterStart)
            )

            Row(
                modifier = Modifier.Companion.width(totalContentWidth),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    val isActive = index == clampedActive
                    // For progress fill, exclude the excluded step from "visited" calculation
                    val effectiveIndex = if (progressExcludedIndex != null && index > progressExcludedIndex) {
                        index - 1
                    } else {
                        index
                    }
                    val isVisited = effectiveIndex <= effectiveMaxReachedIndex
                    // Only allow clicking on visited steps (users must progress sequentially via forward arrow)
                    val isClickable = isVisited && onStepClick != null

                    val bg = if (isVisited) activeColor else inactiveColor
                    val iconTint = if (isVisited) Color.Companion.White else Color(0xFFC4E092)

                    Row(
                        modifier = Modifier.Companion
                            .height(collapsedHeight)
                            // Inactive capsules keep a fixed width; active capsules wrap content.
                            .then(
                                if (isActive) {
                                    Modifier.Companion.onGloballyPositioned { coordinates ->
                                        val widthPx = coordinates.size.width
                                        activeItemWidth = with(density) { widthPx.toDp() }
                                    }
                                } else {
                                    Modifier.Companion.width(collapsedWidth)
                                }
                            )
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                            .background(bg)
                            .then(
                                if (isActive) {
                                    Modifier.Companion.padding(horizontal = 12.dp)
                                } else {
                                    Modifier.Companion
                                }
                            )
                            .clickable(
                                enabled = isClickable,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onStepClick?.invoke(index) },
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier.Companion.size(28.dp),
                                contentAlignment = Alignment.Companion.Center
                            ) {
                                Icon(
                                    painter = painterResource(step.iconRes),
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.Companion.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.Companion.width(4.dp))
                            Text(
                                text = step.title,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Companion.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Companion.White,
                                maxLines = 1
                            )
                        } else {
                            Box(
                                modifier = Modifier.Companion.fillMaxSize(),
                                contentAlignment = Alignment.Companion.Center
                            ) {
                                Icon(
                                    painter = painterResource(step.iconRes),
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.Companion.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CapsuleStepperRowPreview() {
    var active by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .padding(top = 200.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CapsuleStepperRow(
            steps = listOf(
                CapsuleStep("allergies", "Allergies", R.drawable.ic_step_allergies),
                CapsuleStep("intolerances", "Intolerances", R.drawable.ic_step_intolerances),
                CapsuleStep(
                    "health_conditions",
                    "Health Conditions",
                    R.drawable.ic_step_health_conditions
                ),
                CapsuleStep("life_stage", "Life Stage", R.drawable.ic_step_life_style),
                CapsuleStep("region", "Region", R.drawable.ic_step_region),
                CapsuleStep("avoid", "Avoid", R.drawable.ic_step_avoid_cross),
                CapsuleStep("life_style", "Life Style", R.drawable.ic_step_diet_preferences),
                CapsuleStep("nutrition", "Nutrition", R.drawable.ic_step_meals),
                CapsuleStep("ethical", "Ethical", R.drawable.ic_step_ethical),
                CapsuleStep("taste", "Taste", R.drawable.iconoir_chocolate)
            ),
            activeIndex = active,
            onStepClick = { active = it }
        )


    }
}