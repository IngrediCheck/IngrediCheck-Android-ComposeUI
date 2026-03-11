package lc.fungee.Ingredicheck.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.Ingredicheck.ui.components.buttons.primaryButtonEffect
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale130
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Greyscale40

private const val TAG = "AllergySummaryCard"

val MyIcon: ImageVector
    get() {
        if (_MyIcon != null) return _MyIcon!!
        _MyIcon = ImageVector.Builder(
            name = "MyIcon",
            defaultWidth = 193.0.dp,
            defaultHeight = 214.0.dp,
            viewportWidth = 193.0f,
            viewportHeight = 214.0f,
        ).apply {

            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(32.0f, 205.0f)
                curveTo(19.2975f, 205.0f, 9.0f, 194.703f, 9.0f, 182.0f)
                lineTo(8.99998f, 32.0f)
                curveTo(8.99998f, 19.2975f, 19.2974f, 9.00001f, 32.0f, 9.00001f)
                lineTo(161.0f, 9.0f)
                curveTo(173.703f, 9.0f, 184.0f, 19.2975f, 184.0f, 32.0f)
                lineTo(184.0f, 126.294f)
                curveTo(184.0f, 140.491f, 172.491f, 152.0f, 158.294f, 152.0f)
                curveTo(144.097f, 152.0f, 132.588f, 163.509f, 132.588f, 177.706f)
                lineTo(132.588f, 178.5f)
                curveTo(132.588f, 193.136f, 120.723f, 205.0f, 106.088f, 205.0f)
                lineTo(32.0f, 205.0f)
                close()
            }

            // Border stroke (#ABAAAA) matching original iOS-style card outline
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFFABAAAA)),
                strokeLineWidth = 0.1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(32.0f, 205.0f)
                curveTo(19.2975f, 205.0f, 9.0f, 194.703f, 9.0f, 182.0f)
                lineTo(8.99998f, 32.0f)
                curveTo(8.99998f, 19.2975f, 19.2974f, 9.00001f, 32.0f, 9.00001f)
                lineTo(161.0f, 9.0f)
                curveTo(173.703f, 9.0f, 184.0f, 19.2975f, 184.0f, 32.0f)
                lineTo(184.0f, 126.294f)
                curveTo(184.0f, 140.491f, 172.491f, 152.0f, 158.294f, 152.0f)
                curveTo(144.097f, 152.0f, 132.588f, 163.509f, 132.588f, 177.706f)
                lineTo(132.588f, 178.5f)
                curveTo(132.588f, 193.136f, 120.723f, 205.0f, 106.088f, 205.0f)
                lineTo(32.0f, 205.0f)
                close()
            }

        }.build()
        return _MyIcon!!
    }

private var _MyIcon: ImageVector? = null

// Base padding so text is comfortably inside the card, similar to iOS GeometryReader logic.
// We keep 10.dp all around, then add some extra space on the right/bottom so text
// doesn't run into the inward curve or the green circle button.
private val cardContentPaddingStart = 10.dp
private val cardContentPaddingTop = 10.dp
private val cardContentPaddingEnd = 10.dp
private val cardContentPaddingBottom = 10.dp
private val circleButtonSize = 38.dp
private val circleButtonPadding = 10.dp

/**
 * Very small markdown parser for the AI summary:
 * - Treats **text** as bold, everything else normal.
 * Mirrors the implementation used in AiSummaryPreferenceCard so
 * iOS/Android render the same emphasis.
 */
private fun parseAiSummaryMarkdown(raw: String): AnnotatedString {
    if (raw.isEmpty()) return AnnotatedString("")

    val builder = AnnotatedString.Builder()
    val regex = Regex("\\*\\*(.+?)\\*\\*") // match **bold**
    var currentIndex = 0

    for (match in regex.findAll(raw)) {
        val range = match.range
        // Append text before the match
        if (range.first > currentIndex) {
            builder.append(raw.substring(currentIndex, range.first))
        }
        // Append bold segment without the ** markers
        val boldText = match.groupValues.getOrNull(1) ?: ""
        val start = builder.length
        builder.append(boldText)
        val end = builder.length
        builder.addStyle(
            SpanStyle(fontWeight = FontWeight.SemiBold),
            start,
            end
        )
        currentIndex = range.last + 1
    }

    // Append any remaining text after the last match
    if (currentIndex < raw.length) {
        builder.append(raw.substring(currentIndex))
    }

    return builder.toAnnotatedString()
}

/**
 * Inject food emojis into the AI summary text based on dynamicJsonData.json icons,
 * mirroring the behavior used in the AI summary bottom sheet. This keeps the
 * Home screen summary visually consistent with iOS.
 */
private fun injectFoodEmojisIntoSummary(raw: String): String {
    if (raw.isEmpty()) return raw

    // Build a mapping from lowercased chip labels to their emoji/iconPrefix.
    val mapping = mutableMapOf<String, String>()
    val stepIds = OnboardingChipData.foodNotesStepIds
    for (stepIndex in stepIds.indices) {
        val chips = OnboardingChipData.chipsForStep(stepIndex)
        for (chip in chips) {
            val emoji = chip.iconPrefix.trim()
            if (emoji.isNotEmpty()) {
                mapping[chip.label.lowercase()] = emoji
            }
        }
    }

    // Add a few common aliases similar to iOS to make summaries richer.
    mapping["red meat"] = mapping["red meat"] ?: "🥩"
    mapping["meat"] = mapping["meat"] ?: "🥩"
    mapping["chicken"] = mapping["chicken"] ?: "🍗"
    mapping["poultry"] = mapping["poultry"] ?: "🍗"
    mapping["soda"] = mapping["soda"] ?: "🥤"
    mapping["sugar"] = mapping["sugar"] ?: "🍬"
    mapping["salt"] = mapping["salt"] ?: "🧂"
    mapping["fried food"] = mapping["fried food"] ?: "🍟"
    mapping["fried foods"] = mapping["fried foods"] ?: "🍟"
    mapping["fast food"] = mapping["fast food"] ?: "🍔"

    if (mapping.isEmpty()) return raw

    var result = raw

    // Sort keys by length descending so longer phrases match first.
    val sortedNames = mapping.keys.sortedByDescending { it.length }

    for (name in sortedNames) {
        val emoji = mapping[name] ?: continue
        if (name.isBlank()) continue

        // Case-insensitive whole-word match using word boundaries.
        val pattern = "\\b${Regex.escape(name)}\\b"
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)

        result = regex.replace(result) { matchResult ->
            val matchedText = matchResult.value
            "$emoji $matchedText"
        }
    }

    return result
}

/**
 * Allergy summary card with MyIcon shape: tag and summary text on top,
 * small primary-effect circle button in the bottom-right curve. Text is padded
 * so it never overlaps the curve or the button.
 */
@Composable
fun AllergySummaryCard(
    summary: String?,
    tagLabel: String? = null,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    val trimmed = summary?.trim().orEmpty()
    val isEmptyState = trimmed.isEmpty() || trimmed == "No Food Notes yet."
    val displaySummary = summary?.takeIf { !isEmptyState && it.isNotBlank() }
        ?: "Add allergies or dietary needs for your family members to make meal choices easier for everyone ."

    // Log what text this card is showing so we can verify behavior across restarts.
    Log.d(
        TAG,
        "[HomeSummary] isEmptyState=$isEmptyState rawSummary='${summary ?: "null"}' " +
            "displaySummary='${displaySummary}'"
    )

    Box(
        modifier = modifier
            // Responsive size: fill available width and keep original aspect ratio
            .fillMaxWidth()
            .aspectRatio(193f / 214f)
            .then(
                if (onTap != null) Modifier.clickable(onClick = onTap) else Modifier
            )

    ) {
        // 1. Card background: MyIcon shape (unchanged) — white card on optional gray
        Image(
            painter = rememberVectorPainter(image = MyIcon),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .clipToBounds()
                .graphicsLayer(
                    // Slight over-scale so the vector fully covers the bounds
                    scaleX = 1.06f,
                    scaleY = 1.06f
                ),
            contentScale = ContentScale.FillBounds
        )

        // 2. Content: summary text (padded so it never enters the bottom-right curve)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, end = 10.dp)
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            if (isEmptyState) {
                // Empty state: "No Data Yet" badge + placeholder summary,
                // matching iOS behavior when there is no AI summary yet.
                Box(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            color = Greyscale40,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .background(Greyscale30, RoundedCornerShape(percent = 50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "No Data Yet",
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 8.sp
                        ),
                        color = Greyscale130
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val emptyParsed = remember(displaySummary) {
                    // Reuse markdown/emoji pipeline so placeholder matches AI style.
                    val withEmojis = injectFoodEmojisIntoSummary(displaySummary)
                    parseAiSummaryMarkdown(withEmojis)
                }

                Text(
                    text = emptyParsed,
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 14.sp
                    ),
                    color = Greyscale140,
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                val parsedSummary = remember(displaySummary) {
                    val withEmojis = injectFoodEmojisIntoSummary(displaySummary)
                    parseAiSummaryMarkdown(withEmojis)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Top ~70%: normal width, red background for debugging
                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxWidth()

                    ) {
                        Text(
                            text = parsedSummary,
                            style = TextStyle(
                                fontFamily = Manrope,
                                fontSize = 14.sp
                            ),
                            color = Greyscale140,
                            maxLines = Int.MAX_VALUE
                        )
                    }
                    // Bottom ~30%: extra right padding, darker red to visualize narrower text column
                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(end = 60.dp)

                        ) {
                            Text(
                                text = parsedSummary,
                                style = TextStyle(
                                    fontFamily = Manrope,
                                    fontSize = 14.sp
                                ),
                                color = Greyscale140,
                                maxLines = Int.MAX_VALUE
                            )
                        }
                    }
                }
            }
        }

        // 3. Bottom-right: small circle button with Primary effect
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = circleButtonPadding, end = circleButtonPadding)
                .size(circleButtonSize)
                .clip(RoundedCornerShape(percent = 50))
                .primaryButtonEffect(
                    isDisabled = false,
                    shape = RoundedCornerShape(percent = 50),
                    disabledBackgroundColor = Greyscale40
                )
                .then(
                    if (onTap != null) Modifier.clickable(onClick = onTap) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.CallMade,
                contentDescription = null,
                tint = Greyscale10,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun MyIconPreview() {
//    Box(
//        modifier = Modifier
//            .background(Color(0xFFFF0000)),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = rememberVectorPainter(image = MyIcon),
//            contentDescription = "MyIcon preview",
//            modifier = Modifier
//                .size(193.dp, 214.dp)
//                .clipToBounds()
//                .graphicsLayer(
//                    scaleX = 1.06f,
//                    scaleY = 1.06f
//                )
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
private fun AllergySummaryCardPreview() {
    Box(
        modifier = Modifier
            .background(Color(0xFFF5F5F5))
    ) {
        AllergySummaryCard(
            summary = "Your family avoids dairy, 🦀, eggs, gluten, red meat 🥩, alcohol, making meal choices simpler and safer for everyone.",
            tagLabel = "25% Allergies",
            onTap = {}
        )
    }
}

