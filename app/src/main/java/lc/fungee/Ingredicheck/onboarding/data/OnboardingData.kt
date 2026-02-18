package lc.fungee.Ingredicheck.onboarding.data

import lc.fungee.Ingredicheck.R

/**
 * Static configuration for the multi‑step fine‑tune flow (allergy/sensitivity/health/life‑stage chips)
 * and shared avatar lists. Keeping this data out of the UI layer keeps
 * `OnboardingStepScreens` lightweight and closer to MVVM.
 */

data class ChipDefinition(
    val id: String,
    val label: String,
    val iconPrefix: String
)

/**
 * Region definition used for the cultural / regional food traditions
 * step in the fine‑tune flow.
 *
 * Each region has a display name and a list of sub‑regions, which are
 * modelled as regular chips (so they share the same styling and state
 * handling as other chip-based steps).
 */
data class RegionDefinition(
    val name: String,
    val subRegions: List<ChipDefinition>
)

data class AvoidOptionDefinition(
    val id: String,
    val label: String,
    val iconPrefix: String
)

data class AvoidCardDefinition(
    val id: String,
    val title: String,
    val description: String,
    val colorHex: String,
    val options: List<AvoidOptionDefinition>
)

object OnboardingChipData {

    /**
     * Returns the (question, subtitle) pair for the given fine‑tune step.
     */
    fun questionForStep(step: Int): Pair<String, String> {
        return when (step.coerceIn(0, 5)) {
            0 -> "Does anyone in your IngrediFam have allergies we should know?" to
                "Select all that apply to keep meals worry-free."
            1 -> "Any sensitivities or intolerances in your IngrediFam?" to
                "We’ll avoid foods that cause discomfort."
            2 -> "Any doctor diets or health conditions in your IngrediFam?" to
                "This helps us tailor recommendations better."
            3 -> "Does anyone in your IngrediFam have special life stage needs?" to
                "Select all that apply so tips match every life stage."
            // 4 – Region / cultural practices
            4 -> "Where are you from? This helps us customize your experience!" to
                "Pick your region(s) or cultural practices."
            5 -> "Anything your IngrediFam avoids?" to
                "We’ll steer clear of those ingredients and products."
            else -> "Does anyone in your IngrediFam have allergies we should know?" to
                "Select all that apply to keep meals worry-free."
        }
    }

    /**
     * Returns the chip set (id, label, emoji prefix) for the given fine‑tune step.
     */
    fun chipsForStep(step: Int): List<ChipDefinition> {
        return when (step.coerceIn(0, 9)) {
            // 0 – Allergies
            0 -> listOf(
                ChipDefinition("peanuts", "Peanuts", "🥜  "),
                ChipDefinition("tree_nuts", "Tree nuts", "🌰  "),
                ChipDefinition("dairy", "Dairy", "🥛  "),
                ChipDefinition("eggs", "Eggs", "🥚  "),
                ChipDefinition("soy", "Soy", "🌱  "),
                ChipDefinition("wheat", "Wheat", "🌾  "),
                ChipDefinition("fish", "Fish", "🐟  "),
                ChipDefinition("shellfish", "Shellfish", "🍤 "),
                ChipDefinition("sesame", "Sesame", "✨ "),
                ChipDefinition("celery", "Celery", "🥬  "),
                ChipDefinition("lupin", "Lupin", "🫘  "),
                ChipDefinition("sulphites", "Sulphites", "🧂  "),
                ChipDefinition("mustard", "Mustard", "🟡 "),
                ChipDefinition("molluscs", "Molluscs", "🐚  "),
                ChipDefinition("other", "Other", "✏️  ")
            )

            // 1 – Sensitivities / intolerances
            1 -> listOf(
                ChipDefinition("lactose", "Lactose", "🥛  "),
                ChipDefinition("fructose", "Fructose", "🍓  "),
                ChipDefinition("histamine", "Histamine", "🍷  "),
                ChipDefinition("gluten_wheat", "Gluten / wheat", "🌾  "),
                ChipDefinition("fodmap", "FODMAP", "🧄  "),
                ChipDefinition("other_sens", "Other", "✏️  ")
            )

            // 2 – Health conditions / doctor diets
            2 -> listOf(
                ChipDefinition("diabetes", "Diabetes", "🍭  "),
                ChipDefinition("hypertension", "Hypertension", "💊  "),
                ChipDefinition("kidney_disease", "Kidney disease", "🩺 "),
                ChipDefinition("heart_health", "Heart health", "\uD83E\uDEC0  "),
                ChipDefinition("pku", "PKU (phenylalanine-sensitive)", "🧬  "),
                ChipDefinition("anti_inflammatory", "Anti-inflammatory / medical diet", "🥗  "),
                ChipDefinition("celiac_disease", "Celiac disease", "🥖  "),
                ChipDefinition("other_health", "Other", "✏️  ")
            )

            // 3 – Life stage needs
            3 -> listOf(
                ChipDefinition("kids_baby_friendly", "Kids / Baby-friendly foods", "👶 "),
                ChipDefinition("toddler_picky", "Toddler picky-eating adaptations", "🙄 "),
                ChipDefinition("pregnancy_prenatal", "Pregnancy / Prenatal nutrition", "🤰 "),
                ChipDefinition("breastfeeding", "Breastfeeding diets", "🍼 "),
                ChipDefinition("senior_friendly", "Senior-friendly", "👴 "),
                ChipDefinition("none_lifestage", "None of these apply", "✅ ")
            )

            // 8 – Ethical preferences
            8 -> listOf(
                ChipDefinition("ethical_animal_welfare", "Animal welfare focused", "🐄  "),
                ChipDefinition("ethical_fair_trade", "Fair trade", "🤝  "),
                ChipDefinition(
                    "ethical_sustainable_fishing",
                    "Sustainable fishing / no overfished species",
                    "🐟  "
                ),
                ChipDefinition("ethical_low_carbon", "Low carbon footprint foods", "♻️  "),
                ChipDefinition("ethical_water_footprint", "Water footprint concerns", "💧  "),
                ChipDefinition("ethical_palm_oil_free", "Palm-oil free", "🌴  "),
                ChipDefinition("ethical_plastic_free_packaging", "Plastic-free packaging", "🚫  "),
                ChipDefinition("ethical_other", "Other", "✏️  ")
            )

            // 9 – Taste preferences
            9 -> listOf(
                ChipDefinition("taste_spicy_lover", "Spicy lover", "🌶️  "),
                ChipDefinition("taste_avoid_spicy", "Avoid Spicy", "🚫  "),
                ChipDefinition("taste_sweet_tooth", "Sweet tooth", "🍰  "),
                ChipDefinition("taste_avoid_slimy", "Avoid slimy textures", "🥒  "),
                ChipDefinition("taste_avoid_bitter", "Avoid bitter foods", "🍵  "),
                ChipDefinition("taste_other", "Other", "✏️  "),
                ChipDefinition("taste_crunchy_soft", "Crunchy / Soft preferences", "🍪  "),
                ChipDefinition("taste_low_sweet", "Low-sweet preference", "🍯  ")
            )

            else -> chipsForStep(0)
        }
    }

    // Avoid stacked cards (type-2) used for the Avoid step.
    val avoidCards: List<AvoidCardDefinition> = listOf(
        AvoidCardDefinition(
            id = "avoid_oils_fats",
            title = "Oils & Fats",
            description = "In fats or oils, what do you avoid?",
            colorHex = "#FFF6B3",
            options = listOf(
                AvoidOptionDefinition("avoid_oils_trans_fats", "Hydrogenated oils / Trans fats", "🧈 "),
                AvoidOptionDefinition("avoid_oils_seed", "Canola / Seed oils", "🌾 "),
                AvoidOptionDefinition("avoid_oils_palm", "Palm oil", "🌴 "),
                AvoidOptionDefinition("avoid_oils_corn_hfcs", "Corn / High-fructose corn syrup", "🌽 ")
            )
        ),
        AvoidCardDefinition(
            id = "avoid_animal_based",
            title = "Animal-Based",
            description = "Any animal products you don't consume?",
            colorHex = "#DCC7F6",
            options = listOf(
                AvoidOptionDefinition("avoid_animal_pork", "Pork", "🐖 "),
                AvoidOptionDefinition("avoid_animal_beef", "Beef", "🐄 "),
                AvoidOptionDefinition("avoid_animal_honey", "Honey", "🍯 "),
                AvoidOptionDefinition("avoid_animal_gelatin", "Gelatin / Rennet", "🧂 "),
                AvoidOptionDefinition("avoid_animal_shellfish", "Shellfish", "🦐 "),
                AvoidOptionDefinition("avoid_animal_insects", "Insects", "🐜 "),
                AvoidOptionDefinition("avoid_animal_seafood", "Seafood (fish)", "🐟 "),
                AvoidOptionDefinition("avoid_animal_lard", "Lard / Animal fat", "🍖 ")
            )
        ),
        AvoidCardDefinition(
            id = "avoid_stimulants_substances",
            title = "Stimulants & Substances",
            description = "Do you avoid these?",
            colorHex = "#BFF0D4",
            options = listOf(
                AvoidOptionDefinition("avoid_stim_alcohol", "Alcohol", "🍷 "),
                AvoidOptionDefinition("avoid_stim_caffeine", "Caffeine", "☕ ")
            )
        ),
        AvoidCardDefinition(
            id = "avoid_additives_sweeteners",
            title = "Additives & Sweeteners",
            description = "Do you stay away from processed ingredients?",
            colorHex = "#FFD9B5",
            options = listOf(
                AvoidOptionDefinition("avoid_add_msg", "MSG", "⚗️ "),
                AvoidOptionDefinition("avoid_add_artificial_sweeteners", "Artificial sweeteners", "🍬 "),
                AvoidOptionDefinition("avoid_add_preservatives", "Preservatives", "🧂 "),
                AvoidOptionDefinition("avoid_add_refined_sugar", "Refined sugar", "🍚 "),
                AvoidOptionDefinition("avoid_add_corn_syrup", "Corn syrup / HFCS", "🌽 "),
                AvoidOptionDefinition("avoid_add_stevia_monk", "Stevia / Monk fruit", "🍈 ")
            )
        ),
        AvoidCardDefinition(
            id = "avoid_plant_based_restrictions",
            title = "Plant-Based Restrictions",
            description = "Any plant foods you avoid?",
            colorHex = "#F9C6D0",
            options = listOf(
                AvoidOptionDefinition("avoid_plant_nightshades", "Nightshades (paprika, peppers, etc.)", "🍅 "),
                AvoidOptionDefinition("avoid_plant_garlic_onion", "Garlic / Onion", "🧄 ")
            )
        )
    )

    /**
     * Static definition of cultural / regional food traditions used on the
     * "Where does your IngrediFam draw its food traditions from?" step.
     *
     * Mirrors the iOS `regions` JSON structure (DynamicRegionsQuestionView),
     * but reuses `ChipDefinition` for sub‑regions so selections behave like
     * normal chips on Android.
     */
    val regions: List<RegionDefinition> = listOf(
        RegionDefinition(
            name = "India & South Asia",
            subRegions = listOf(
                ChipDefinition("region_india_ayurveda", "Ayurveda", "🌿  "),
                ChipDefinition("region_india_hindu_traditions", "Hindu food traditions", "🕉  "),
                ChipDefinition("region_india_jain_diet", "Jain diet", "🧘‍♂️ "),
                ChipDefinition("region_india_other", "Other", "✏️  ")
            )
        ),
        RegionDefinition(
            name = "Africa",
            subRegions = listOf(
                ChipDefinition("region_africa_rastafarian_ital", "Rastafarian Ital diet", "🥗  "),
                ChipDefinition("region_africa_ethiopian_orthodox", "Ethiopian Orthodox fasting", "🥖  "),
                ChipDefinition("region_africa_other", "Other", "✏️  ")
            )
        ),
        RegionDefinition(
            name = "Middle East & Mediterranean",
            subRegions = listOf(
                ChipDefinition("region_middleeast_halal", "Halal (Islamic dietary laws)", "☪️ "),
                ChipDefinition("region_middleeast_kosher", "Kosher (Jewish dietary laws)", "✡️ "),
                ChipDefinition("region_middleeast_mediterranean", "Greek / Mediterranean diets", "🫒 "),
                ChipDefinition("region_middleeast_other", "Other", "✏️  ")
            )
        ),
        RegionDefinition(
            name = "East Asia",
            subRegions = listOf(
                ChipDefinition("region_eastasia_tcm", "Traditional Chinese Medicine (TCM)", "🧧 "),
                ChipDefinition("region_eastasia_buddhist_rules", "Buddhist food rules", "🧘 "),
                ChipDefinition("region_eastasia_macrobiotic", "Japanese Macrobiotic diet", "🍙 "),
                ChipDefinition("region_eastasia_other", "Other", "✏️  ")
            )
        ),
        RegionDefinition(
            name = "Western / Native traditions",
            subRegions = listOf(
                ChipDefinition("region_western_native_american", "Native American traditions", "🪶 "),
                ChipDefinition("region_western_christian", "Christian traditions", "✝️ "),
                ChipDefinition("region_western_other", "Other", "✏️  ")
            )
        ),
        RegionDefinition(
            name = "Seventh-day Adventist",
            subRegions = listOf(
                ChipDefinition("region_sda_seventh_day_adventist", "Seventh-day Adventist", "✝️ ")
            )
        ),
        RegionDefinition(
            name = "Other",
            subRegions = listOf(
                ChipDefinition("region_other_other", "Other", "✏️  ")
            )
        )
    )

    /**
     * Resolves a chip id to its definition (label + emoji) from any step.
     * Used to display selected chips in the CapsuleSkeletonBox.
     */
    fun chipForId(id: String): ChipDefinition? {
        for (step in 0..9) {
            chipsForStep(step).find { it.id == id }?.let { return it }
        }
        return null
    }

    /**
     * Shared avatar lists used by multiple onboarding screens.
     */
    val baseAvatarItems: List<Pair<String, Int>> = listOf(
        "baby_boy"      to R.drawable.family_member_baby,
        "baby_girl"     to R.drawable.family_member_baby_girl,
        "young_daughter" to R.drawable.young_daughter_onehand,
        "young_son"     to R.drawable.family_member_young_son,
        "mom"           to R.drawable.family_member_mom,
        "father"        to R.drawable.family_member_father,
        "grand_mother"  to R.drawable.family_member_grand_mother,
        "grand_father"  to R.drawable.family_member_grand_father,
        "dog_avtar"     to R.drawable.avtar_dog,
        "cat_avtar"     to R.drawable.avtar_cat,
        "litch_avtar"   to R.drawable.avtar_lichi,
        "pear_avtar"    to R.drawable.avtar_pear,
        "potato_avtar"  to R.drawable.avtar_potatto,
        "tomato_avtar"  to R.drawable.avtar_tomato
    )

    val editAvatarItems: List<Pair<String, Int>> = baseAvatarItems

    /** Resolve avatar id to drawable resource id; null if not found. Shared by Host and screens. */
    fun avatarResOrNull(avatarId: String): Int? =
        baseAvatarItems.firstOrNull { (id, _) -> id == avatarId }?.second
}

