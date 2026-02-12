package lc.fungee.Ingredicheck.memoji

import kotlin.random.Random

internal object MemojiRequestMapper {

    private fun mapColorThemeToApi(id: String?): String? {
        if (id.isNullOrBlank()) return null
        val raw = id.removePrefix("color_")
        return raw.replace('_', '-').lowercase()
    }

    private fun mapHairToApi(id: String): String {
        val raw = id.removePrefix("hair_")
        return when (raw) {
            "short" -> "short"
            "long" -> "long"
            "curly" -> "curly"
            "medium_curls" -> "curly"
            "short_spiky" -> "short spiky"
            "braided" -> "braided"
            "ponytail" -> "ponytail"
            "bun" -> "bun"
            "bald" -> "bald"
            else -> raw.replace('_', ' ').lowercase()
        }
    }

    private fun mapGestureToApi(id: String): String {
        return id.removePrefix("hand_")
    }

    // Defaults and random fallbacks
    private fun defaultSkinForFamilyType(familyType: String): String {
        return when (familyType) {
            // Male: default Light
            "baby_boy", "young_son", "father", "grandfather" -> "skin_light"
            // Female: default Very Light
            "baby_girl", "young_daughter", "mom", "grandmother" -> "skin_very_light"
            else -> "skin_medium_light"
        }
    }

    private fun defaultHairForFamilyType(familyType: String): String? {
        return when (familyType) {
            // Males
            "baby_boy" -> "hair_short"
            "young_son" -> "hair_short_spiky"
            "father" -> "hair_curly"
            "grandfather" -> "hair_bald"
            // Females
            "baby_girl" -> "hair_medium_curls"
            "young_daughter" -> "hair_ponytail"
            "mom" -> "hair_long"
            "grandmother" -> "hair_braided"
            else -> null
        }
    }

    private val gestureIds = listOf(
        "hand_thumbsup",
        "hand_victory",
        "hand_wave",
        "hand_pointing",
        "heart_hand",
        "phone_sign"
    )

    private val hairIds = listOf(
        "hair_short",
        "hair_short_spiky",
        "hair_curly",
        "hair_long",
        "hair_bun",
        "hair_ponytail",
        "hair_braided",
        "hair_medium_curls",
        "hair_bald"
    )

    private val accessoryIds = listOf(
        "acc_specs",
        "acc_sunglasses",
        "acc_earrings",
        "acc_cap",
        "acc_hat"
    )

    private val colorThemeIds = listOf(
        "color_pastel_blue",
        "color_warm_pink",
        "color_soft_green",
        "color_lavender",
        "color_orange",
        "color_yellow",
        "color_transparent"
    )

    private fun randomGestureId(): String = gestureIds.random(Random)
    private fun randomHairId(): String = hairIds.random(Random)
    private fun randomAccessoryIdOrNull(): String? =
        if (accessoryIds.isEmpty()) null else accessoryIds.random(Random)

    private fun randomColorThemeId(): String = colorThemeIds.random(Random)

    fun fromSelections(selections: Map<Int, String>): MemojiRequest {
        val familyType = selections[0].orEmpty()

        // Gesture: random if user didn't select
        val gestureId = selections[1]
            ?.takeIf { it.isNotBlank() }
            ?: randomGestureId()

        // Hair: default by family type if not selected, otherwise random
        val hairId = selections[2]
            ?.takeIf { it.isNotBlank() }
            ?: (defaultHairForFamilyType(familyType) ?: randomHairId())

        // Skin tone: default by family type if not selected
        val skinToneId = selections[3]
            ?.takeIf { it.isNotBlank() }
            ?: defaultSkinForFamilyType(familyType)

        // Accessory: random if not selected
        val accessoryId = selections[4]
            ?.takeIf { it.isNotBlank() }
            ?: randomAccessoryIdOrNull()

        // Color theme: random if not selected
        val colorThemeId = selections[5]
            ?.takeIf { it.isNotBlank() }
            ?: randomColorThemeId()

        return MemojiRequest(
            familyType = familyType,
            gesture = mapGestureToApi(gestureId),
            hair = mapHairToApi(hairId),
            skinTone = skinToneId,
            accessories = accessoryId?.let { listOf(it) } ?: emptyList(),
            background = "transparent",
            size = "1024x1024",
            model = "gpt-image-1",
            subscriptionTier = "monthly_basic",
            colorTheme = mapColorThemeToApi(colorThemeId),
            mood = null
        )
    }
}
