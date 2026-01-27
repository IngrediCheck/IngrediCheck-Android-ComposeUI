@file:Suppress("PackageName")

package lc.fungee.Ingredicheck.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
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
    modifier: Modifier = Modifier,
    onStepClick: ((Int) -> Unit)? = null,
    inactiveColor: Color = Color(0xFFF6FCED),
    activeColor: Color = Color(0xFF91B640),
    lineHeight: Dp = 15.dp,
    horizontalPadding: Dp = 20.dp,
    itemSpacing: Dp = 12.dp,
    animationDurationMs: Int = 280
) {
    if (steps.isEmpty()) return

    val clampedActive = activeIndex.coerceIn(0, steps.lastIndex)

    // Layout model (fixed widths so we can compute the progress fill length deterministically)
    val collapsedHeight = 44.dp
    val collapsedWidth = 64.dp
    val expandedWidth = 180.dp

    val itemWidth: (Int) -> Dp = { index -> if (index == clampedActive) expandedWidth else collapsedWidth }

    val fillToStartOfActive by animateDpAsState(
        targetValue = ((collapsedWidth + itemSpacing) * clampedActive),
        animationSpec = tween(animationDurationMs),
        label = "fillToStartOfActive"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .heightIn(min = 64.dp)
    ) {
        val scrollState = rememberScrollState()
        val fillOverlap = lineHeight / 2
        val totalContentWidth = (collapsedWidth * (steps.size - 1)) + expandedWidth + (itemSpacing * (steps.size - 1))
        val filledWidth = (fillToStartOfActive + fillOverlap).coerceAtMost(totalContentWidth)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .align(Alignment.Center)
        ) {
            // Background line (scrolls with content)
            Box(
                modifier = Modifier
                    .width(totalContentWidth)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(lineHeight / 2))
                    .background(inactiveColor)
                    .align(Alignment.CenterStart)
            )

            // Filled line (scrolls with content)
            Box(
                modifier = Modifier
                    .width(filledWidth)
                    .height(lineHeight)
                    .clip(RoundedCornerShape(lineHeight / 2))
                    .background(activeColor)
                    .align(Alignment.CenterStart)
            )

            Row(
                modifier = Modifier.width(totalContentWidth),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    val isActive = index == clampedActive
                    val isCompleted = index < clampedActive

                    val width by animateDpAsState(
                        targetValue = itemWidth(index),
                        animationSpec = tween(animationDurationMs),
                        label = "capsuleWidth"
                    )

                    val bg = if (isActive || isCompleted) activeColor else inactiveColor
                    val iconTint = if (isActive || isCompleted) Color.White else Color(0xFFC4E092)

                    Row(
                        modifier = Modifier
                            .height(collapsedHeight)
                            .width(width)
                            .clip(RoundedCornerShape(999.dp))
                            .background(bg)
                            .then(
                                if (isActive) {
                                    Modifier.padding(horizontal = 12.dp)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                enabled = onStepClick != null,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onStepClick?.invoke(index) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier.size(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(step.iconRes),
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step.title,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(step.iconRes),
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(18.dp)
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .padding(top = 200.dp)
        ,

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CapsuleStepperRow(
            steps = listOf(
                CapsuleStep("allergies", "Allergies", R.drawable.ic_step_allergies),
                CapsuleStep("intolerances", "Intolerances", R.drawable.ic_step_intolerances),
                CapsuleStep("health_conditions", "Health Conditions", R.drawable.ic_step_health_conditions),
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

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            lc.fungee.Ingredicheck.components.buttons.PrimaryButton(
                title = "Prev",
                takeFullWidth = false,
                width = 120.dp,
                onClick = { active = (active - 1).coerceAtLeast(0) }
            )
            lc.fungee.Ingredicheck.components.buttons.PrimaryButton(
                title = "Next",
                takeFullWidth = false,
                width = 120.dp,
                onClick = { active = (active + 1).coerceAtMost(9) }
            )
        }
    }
}
