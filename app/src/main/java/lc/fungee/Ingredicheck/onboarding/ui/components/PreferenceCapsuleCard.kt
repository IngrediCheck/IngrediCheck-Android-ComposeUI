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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border((0.25).dp, Greyscale60, RoundedCornerShape(20.dp))
            .background(Greyscale10)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 130.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header row (icon + title + optional Edit) – always shown so user can edit even when empty.
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
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = Greyscale30)
                            .clickable(enabled = onEditClick != null) {
                                onEditClick?.invoke()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
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

            if (showSelectedChips && resolvedChips.isNotEmpty()) {
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
            } else {
                // Empty state: centered circle + texts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Greyscale30),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.file_list_edit),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Greyscale100
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nothing added yet",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Greyscale100
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You can add details anytime by tapping Edit.",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = lc.fungee.Ingredicheck.ui.theme.Greyscale80,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

/**
 * AI summary card for food notes, styled like PreferenceCapsuleCard but with a custom
 * "Summarized with AI" capsule header and free-form summary text.
 */
@Composable
fun AiSummaryPreferenceCard(
    modifier: Modifier = Modifier,
    summaryText: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border((0.25).dp, Greyscale60, RoundedCornerShape(20.dp))
            .background(Greyscale10)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Gradient capsule: icon + "Summarized with AI"
            val capsuleBackground = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFEF2F2),
                    Color(0xFFF9EDF9),
                    Color(0xFFEBF3FE)
                )
            )
            val textGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFB4889),
                    Color(0xFF9A64D4),
                    Color(0xFF0B77FF)
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(capsuleBackground)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.two_star_image_genai),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = buildAnnotatedString {
                        pushStyle(
                            SpanStyle(
                                brush = textGradient,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        )
                        append("Summarized with AI")
                        pop()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val parsedSummary = remember(summaryText) {
                // Inject food emojis into the AI summary (like iOS) before parsing markdown.
                val withEmojis = injectFoodEmojisIntoSummary(summaryText)
                parseAiSummaryMarkdown(withEmojis)
            }
            Text(
                text = parsedSummary,
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
        color = Greyscale150
            )
        }
    }
}

/**
 * Very small markdown parser for the AI summary:
 * - Treats **text** as bold, everything else normal.
 * - Mirrors iOS behavior where the server sends markdown with **bold** emphasis.
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
 * mirroring iOS FoodEmojiMapper behavior. For example:
 * - "peanuts" -> "🥜 peanuts"
 * - "milk" -> "🥛 milk"
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
            val range = matchResult.range
            // Check if there's already an emoji immediately before this match to avoid duplicates.
            val hasEmojiBefore = if (range.first > 0) {
                val prevChar = result[range.first - 1]
                // Many emojis are surrogate pairs; treat any surrogate as "likely emoji".
                Character.getType(prevChar).toInt() == Character.SURROGATE.toInt() ||
                    prevChar.isEmojiCompat()
            } else {
                false
            }

            val matchedText = matchResult.value
            if (hasEmojiBefore) {
                matchedText
            } else {
                "$emoji $matchedText"
            }
        }
    }

    return result
}

/**
 * Very small helper to guess if a Char is an emoji (best‑effort; used only to avoid
 * double‑injecting emojis in the AI summary text).
 */
private fun Char.isEmojiCompat(): Boolean {
    val block = Character.UnicodeBlock.of(this)
    return block == Character.UnicodeBlock.EMOTICONS ||
        block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS ||
        block == Character.UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS ||
        block == Character.UnicodeBlock.SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS
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

