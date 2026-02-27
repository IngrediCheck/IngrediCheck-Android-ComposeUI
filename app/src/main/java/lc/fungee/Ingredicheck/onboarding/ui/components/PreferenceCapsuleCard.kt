package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale130
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

@Composable
fun PreferenceCapsuleCard(
    modifier: Modifier = Modifier,
    selectedChipIds: Set<String> = emptySet(),
    sectionTitle: String = "Allergies",
    @DrawableRes sectionIconRes: Int = R.drawable.ic_step_allergies,
    trailingAvatarsForChip: ((String) -> (@Composable () -> Unit)?)? = null,
    showEditAction: Boolean = false,
    onEditClick: (() -> Unit)? = null
) {
    val showSelectedChips = selectedChipIds.isNotEmpty()
    val resolvedChips = remember(selectedChipIds) {
        selectedChipIds.mapNotNull { id -> OnboardingChipData.chipForId(id) }
    }
    val hasOtherSelection = remember(selectedChipIds) {
        selectedChipIds.any { it.contains("other", ignoreCase = true) }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (showSelectedChips) Modifier.heightIn(min = 130.dp)
                else Modifier.height(130.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .border((0.25).dp, Greyscale60, RoundedCornerShape(20.dp))
            .background(Greyscale10)
            .padding(12.dp)
    ) {
        if (showSelectedChips && resolvedChips.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(sectionIconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Greyscale110
                    )
                    Text(
                        text = sectionTitle,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Greyscale110
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (showEditAction) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clip(shape = RoundedCornerShape(16.dp))
                                .background(color = Greyscale30)
                                .padding(horizontal = 10.dp, vertical = 4.dp)

                                .clickable(enabled = onEditClick != null) {
                                    onEditClick?.invoke()
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pen_line_icon),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Greyscale130
                            )
                            Text(
                                text = "Edit",
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = Greyscale130
                            )

                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRowChips(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    resolvedChips.forEach { def ->
                        val trailing = trailingAvatarsForChip?.invoke(def.id)
                        SelectedChipPill(
                            emoji = def.iconPrefix,
                            label = def.label,
                            trailingAvatars = trailing
                        )
                    }
                }
                if (hasOtherSelection) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.emoji_warning),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = androidx.compose.ui.graphics.Color.Unspecified
                        )
                        Text(
                            text = "Something else too, don't worry we'll ask later!",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Greyscale100
                        )
                    }
                }
            }
        } else {
            val maxWidth = maxWidth
            val spacing = 8.dp
            val minFirst = 90.dp
            val minSecond = 110.dp

            fun randomRowWidths(): Pair<Dp, Dp> {
                val maxExtra = (maxWidth - spacing - minFirst - minSecond).coerceAtLeast(0.dp)
                if (maxExtra == 0.dp) {
                    val second = (maxWidth - spacing - minFirst).coerceAtLeast(minSecond)
                    return minFirst to second
                }
                val extraFraction = kotlin.random.Random.nextFloat()
                val first = minFirst + maxExtra * extraFraction
                val second = maxWidth - spacing - first
                return first to second
            }

            val (row1First, row1Second) = remember(maxWidth) { randomRowWidths() }
            val (row2First, row2Second) = remember(maxWidth) { randomRowWidths() }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(165.dp)
                        .height(13.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Greyscale30)
                )
                Spacer(modifier = Modifier.height(12.dp))
                PreferenceCapsuleSkeletonRow(firstWidth = row1First, secondWidth = row1Second)
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCapsuleSkeletonRow(firstWidth = row2First, secondWidth = row2Second)
            }
        }
    }
}

@Composable
private fun PreferenceCapsuleSkeletonRow(
    firstWidth: Dp,
    secondWidth: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(firstWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Greyscale30)
        )
        Box(
            modifier = Modifier
                .width(secondWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Greyscale30)
        )
    }
}

