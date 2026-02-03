package lc.fungee.Ingredicheck.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import lc.fungee.Ingredicheck.ui.theme.GrayScale10
import lc.fungee.Ingredicheck.ui.theme.GrayScale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary300
import lc.fungee.Ingredicheck.ui.theme.Primary800

@Composable
fun MatchingRateCard(
    matchedCount: Int = 0,
    uncertainCount: Int = 0,
    unmatchedCount: Int = 0,
    increaseValue: Int? = null,
    modifier: Modifier = Modifier
) {
    val totalCount = matchedCount + uncertainCount + unmatchedCount
    val isEmptyState = totalCount <= 0
    val matchedPercentage = if (totalCount > 0) {
        ((matchedCount.toDouble() / totalCount.toDouble()) * 100.0).roundToInt().coerceIn(0, 100)
    } else {
        0
    }

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(289.dp)
            .then(
                if (isEmptyState) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = 9.dp,
                        shape = shape,
                        ambientColor = Color(0xFFECECEC),
                        spotColor = Color(0xFFECECEC)
                    )
                }
            )
            .clip(shape)
            .background(GrayScale10)
    ) {
        Text(
            text = "Matching Rate",
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Greyscale150,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp, bottom = 14.dp)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
                    .align(Alignment.TopCenter)
            ) {
                MatchingRateProgressBar(
                    matchedCount = matchedCount,
                    uncertainCount = uncertainCount,
                    unmatchedCount = unmatchedCount,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                        .padding(top = 120.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$matchedPercentage",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Greyscale150
                        )
                        Text(
                            text = "%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Greyscale150
                        )
                    }

                    Text(
                        text = "Matched",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.15.sp,
                        color = Greyscale100
                    )
                }
            }

//            if (!isEmptyState) {
//                MatchingRateLegendRow(
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(top = 176.dp)
//                )
//            }

//            if (isEmptyState) {
//                Text(
//                    text = "Start scanning to unlock your matching insights",
//                    fontFamily = Manrope,
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 10.sp,
//                    color = Greyscale120,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .clip(CircleShape)
//                        .background(Color.White)
//                        .border(1.dp, GrayScale40, CircleShape)
//                        .padding(horizontal = 12.dp, vertical = 8.dp)
//                )
//            } else if (increaseValue != null) {
////                MatchingRateIncreasePill(
////                    increaseValue = increaseValue,
////                    modifier = Modifier
////                        .align(Alignment.BottomCenter)
////                )
//            }
        }
    }
}

@Composable
private fun MatchingRateLegendRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MatchingRateLegendItem(color = Color(0xFF82B611), label = "Matched")
        MatchingRateLegendItem(color = Color(0xFFFFBE18), label = "Uncertain")
        MatchingRateLegendItem(color = Color(0xFFFF1606), label = "Unmatched")
    }
}

@Composable
private fun MatchingRateLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = Greyscale120
        )
    }
}

@Composable
private fun MatchingRateIncreasePill(
    increaseValue: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Your matching rate increased by",
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Greyscale150
        )

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Primary300)
                .padding(vertical = 6.dp)
                .padding(horizontal = 12.dp)
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Build,
                contentDescription = null,
                tint = Primary800,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "+$increaseValue",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Primary800
            )
        }
    }
}

@Immutable
private enum class SegmentKind {
    Matched,
    Uncertain,
    Unmatched
}

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
private fun MatchingRateProgressBar(
    matchedCount: Int,
    uncertainCount: Int,
    unmatchedCount: Int,
    modifier: Modifier = Modifier,
    totalSegments: Int = 12
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val outerRadius = min(widthPx / 2f, heightPx) * 0.95f
        val innerRadius = outerRadius * (74f / 130f)

        val segmentStep = Math.PI / maxOf(1, totalSegments - 1)
        val fullWidth = (segmentStep * 0.88).toFloat()

        val matchedColor = Color(0xFF82B611)
        val uncertainColor = Color(0xFFFFBE18)
        val unmatchedColor = Color(0xFFFF1606)

        val kinds = segmentsForBreakdown(
            matched = matchedCount,
            uncertain = uncertainCount,
            unmatched = unmatchedCount,
            totalSegments = totalSegments
        )

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(maxHeight)) {
            val center = Offset(size.width / 2f, size.height)
            val cornerPx = 4.dp.toPx()
            val pathEffect = PathEffect.cornerPathEffect(cornerPx)

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    style = PaintingStyle.Fill
                    this.pathEffect = pathEffect
                }

                for (i in 0 until totalSegments) {
                    val kind = kinds?.getOrNull(i)
                    val fill = when (kind) {
                        SegmentKind.Matched -> matchedColor
                        SegmentKind.Uncertain -> uncertainColor
                        SegmentKind.Unmatched -> unmatchedColor
                        null -> GrayScale40
                    }

                    val centerAngle =
                        (i.toDouble() * Math.PI / maxOf(1, totalSegments - 1).toDouble()) - Math.PI
                    val segmentPath = taperedSegmentPath(
                        center = center,
                        angle = centerAngle.toFloat(),
                        width = fullWidth,
                        innerRadius = innerRadius,
                        outerRadius = outerRadius
                    )

                    paint.color = fill
                    canvas.drawPath(segmentPath, paint)
                }
            }
        }
    }
}

private fun segmentsForBreakdown(
    matched: Int,
    uncertain: Int,
    unmatched: Int,
    totalSegments: Int
): List<SegmentKind>? {
    val total = matched + uncertain + unmatched
    if (total <= 0) return null

    val counts = listOf(
        SegmentKind.Matched to matched,
        SegmentKind.Uncertain to uncertain,
        SegmentKind.Unmatched to unmatched
    )

    val raw = counts.map { (kind, count) ->
        kind to (count.toDouble() / total.toDouble()) * totalSegments.toDouble()
    }

    val base = raw.associate { (kind, value) -> kind to floor(value).toInt() }.toMutableMap()
    val used = base.values.sum()
    var remainder = maxOf(0, totalSegments - used)

    val fractionalSorted = raw
        .map { (kind, value) -> kind to (value - floor(value)) }
        .sortedWith(compareByDescending<Pair<SegmentKind, Double>> { it.second }.thenBy { it.first.name })

    var idx = 0
    while (remainder > 0 && fractionalSorted.isNotEmpty()) {
        val kind = fractionalSorted[idx % fractionalSorted.size].first
        base[kind] = (base[kind] ?: 0) + 1
        remainder -= 1
        idx += 1
    }

    val result = ArrayList<SegmentKind>(totalSegments)
    repeat(base[SegmentKind.Matched] ?: 0) { result.add(SegmentKind.Matched) }
    repeat(base[SegmentKind.Uncertain] ?: 0) { result.add(SegmentKind.Uncertain) }
    repeat(base[SegmentKind.Unmatched] ?: 0) { result.add(SegmentKind.Unmatched) }

    return when {
        result.size > totalSegments -> result.take(totalSegments)
        result.size < totalSegments -> result + List(totalSegments - result.size) { SegmentKind.Unmatched }
        else -> result
    }
}

private fun taperedSegmentPath(
    center: Offset,
    angle: Float,
    width: Float,
    innerRadius: Float,
    outerRadius: Float
): Path {
    val half = width / 2f
    val start = angle - half
    val end = angle + half

    fun point(r: Float, a: Float): Offset {
        return Offset(
            x = center.x + cos(a) * r,
            y = center.y + sin(a) * r
        )
    }

    val innerLeft = point(innerRadius, start)
    val innerRight = point(innerRadius, end)
    val outerLeft = point(outerRadius, start)
    val outerRight = point(outerRadius, end)

    return Path().apply {
        moveTo(innerLeft.x, innerLeft.y)
        lineTo(outerLeft.x, outerLeft.y)
        lineTo(outerRight.x, outerRight.y)
        lineTo(innerRight.x, innerRight.y)
        close()
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchingRateCardFilledPreview() {
    Column(modifier = Modifier.background(Color(0x1A000000))) {
        Box(modifier = Modifier.padding(20.dp)) {
            MatchingRateCard(
                matchedCount = 0,
                uncertainCount = 0,
                unmatchedCount = 47,



                increaseValue = 20
            )
        }
        Box(modifier = Modifier.padding(20.dp)) {
            MatchingRateCard(
                matchedCount = 0,
                uncertainCount = 0,
                unmatchedCount = 0,
                increaseValue = null
            )
        }

    }
}


