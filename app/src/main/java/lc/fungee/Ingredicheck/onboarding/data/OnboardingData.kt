package lc.fungee.Ingredicheck.onboarding.data

import lc.fungee.Ingredicheck.R

/** Member id used for "Everyone" in add-family and just-me flows. */
const val EVERYONE_MEMBER_ID = "ALL"

/**
 * Flow type for onboarding questions - determines which question text to show.
 */
enum class OnboardingFlowType {
    /** "Just Me" / Individual flow */
    INDIVIDUAL,
    /** "Add Family" flow */
    FAMILY
}

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

    private fun slug(name: String): String =
        name.lowercase().replace(Regex("[^a-z0-9]"), "_").replace(Regex("_+"), "_").trim('_')

    private fun dynamicStepToChips(step: DynamicStep): List<ChipDefinition> {
        return when (step.type) {
            // Simple chip lists (e.g., allergies, intolerances, health conditions).
            // Prefix chip id with the step id so that common labels like "Other"
            // remain independent per step (e.g., "allergies_other" vs "healthConditions_other").
            "type-1" -> step.content.options?.map { o ->
                ChipDefinition("${step.id}_${slug(o.name)}", o.name, o.icon + "  ")
            } ?: emptyList()
            "type-2" -> step.content.subSteps?.flatMap { sub ->
                (sub.options ?: emptyList()).map { o ->
                    ChipDefinition("${sub.id}_${slug(o.name)}", o.name, o.icon + "  ")
                }
            } ?: emptyList()
            "type-3" -> step.content.regions?.flatMap { r ->
                r.subRegions.map { o ->
                    ChipDefinition("${slug(r.name)}_${slug(o.name)}", o.name, o.icon + "  ")
                }
            } ?: emptyList()
            else -> emptyList()
        }
    }

    private fun dynamicSteps(): List<DynamicStep>? = DynamicStepsLoader.getSteps()

    /**
     * Returns the (question, subtitle) pair for the given fine‑tune step.
     * @param step Step index (0-9)
     * @param flowType Flow type (INDIVIDUAL for "Just Me", FAMILY for "Add Family")
     */
    fun questionForStep(step: Int, flowType: OnboardingFlowType = OnboardingFlowType.FAMILY): Pair<String, String> {
        val steps = dynamicSteps()
        if (steps != null && step in steps.indices) {
            val h = steps[step].header
            val v = if (flowType == OnboardingFlowType.INDIVIDUAL) h.individual else h.family
            return v.question to (v.description ?: "")
        }
        return "" to ""
    }

    /**
     * Returns the chip set (id, label, emoji prefix) for the given fine‑tune step.
     */
    fun chipsForStep(step: Int): List<ChipDefinition> {
        val steps = dynamicSteps()
        if (steps != null && step in steps.indices) {
            return dynamicStepToChips(steps[step])
        }
        return emptyList()
    }

    // Avoid stacked cards (type-2) from dynamic JSON "avoid" step.
    val avoidCards: List<AvoidCardDefinition>
        get() {
            dynamicSteps()?.find { it.id == "avoid" }?.content?.subSteps?.let { subSteps ->
                return subSteps.map { sub ->
                    AvoidCardDefinition(
                        id = sub.id,
                        title = sub.title,
                        description = sub.description,
                        colorHex = sub.color,
                        options = (sub.options ?: emptyList()).map { o ->
                            AvoidOptionDefinition("${sub.id}_${slug(o.name)}", o.name, o.icon + "  ")
                        }
                    )
                }
            }
            return emptyList()
        }

    /** LifeStyle stacked cards from dynamic JSON "lifeStyle" step. */
    val lifestyleCards: List<AvoidCardDefinition>
        get() {
            dynamicSteps()?.find { it.id == "lifeStyle" }?.content?.subSteps?.let { subSteps ->
                return subSteps.map { sub ->
                    AvoidCardDefinition(
                        id = sub.id,
                        title = sub.title,
                        description = sub.description,
                        colorHex = sub.color,
                        options = (sub.options ?: emptyList()).map { o ->
                            AvoidOptionDefinition("${sub.id}_${slug(o.name)}", o.name, o.icon + "  ")
                        }
                    )
                }
            }
            return emptyList()
        }

    /** Nutrition stacked cards from dynamic JSON "nutrition" step. */
    val nutritionCards: List<AvoidCardDefinition>
        get() {
            dynamicSteps()?.find { it.id == "nutrition" }?.content?.subSteps?.let { subSteps ->
                return subSteps.map { sub ->
                    AvoidCardDefinition(
                        id = sub.id,
                        title = sub.title,
                        description = sub.description,
                        colorHex = sub.color,
                        options = (sub.options ?: emptyList()).map { o ->
                            AvoidOptionDefinition("${sub.id}_${slug(o.name)}", o.name, o.icon + "  ")
                        }
                    )
                }
            }
            return emptyList()
        }

    /** Cultural / regional food traditions from dynamic JSON "region" step. */
    val regions: List<RegionDefinition>
        get() {
            dynamicSteps()?.find { it.id == "region" }?.content?.regions?.let { regs ->
                return regs.map { r ->
                    RegionDefinition(
                        name = r.name,
                        subRegions = r.subRegions.map { o ->
                            ChipDefinition("${slug(r.name)}_${slug(o.name)}", o.name, o.icon + "  ")
                        }
                    )
                }
            }
            return emptyList()
        }

    /**
     * Resolves a chip id to its definition (label + emoji) from any step.
     * Used to display selected chips in the CapsuleSkeletonBox.
     */
    fun chipForId(id: String): ChipDefinition? {
        val steps = dynamicSteps()
        val maxStep = (steps?.size ?: 0) - 1
        for (step in 0..maxStep) {
            chipsForStep(step).find { it.id == id }?.let { return it }
        }
        return null
    }

    /**
     * Base avatar items mapping avatar IDs to drawable resource IDs.
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

    /**
     * Avatar items for editing (same as baseAvatarItems).
     */
    val editAvatarItems: List<Pair<String, Int>> = baseAvatarItems

    /**
     * Resolve avatar id to drawable resource id; null if not found.
     * Shared by Host and screens.
     */
    fun avatarResOrNull(avatarId: String): Int? =
        baseAvatarItems.firstOrNull { (id, _) -> id == avatarId }?.second

    /** Resolve chip id to display label for dietary preference sync; returns id if not found. */
    fun labelForChipId(chipId: String): String {
        val steps = dynamicSteps()
        val maxStep = (steps?.size ?: 0) - 1
        return (0..maxStep).flatMap { chipsForStep(it) }.firstOrNull { it.id == chipId }?.label ?: chipId
    }

    /** Step IDs in order from dynamic JSON (food-notes API). */
    val foodNotesStepIds: List<String>
        get() = dynamicSteps()?.map { it.id } ?: emptyList()

    /** Map step id to drawable for CapsuleStepperRow / section headers. */
    fun iconResForStepId(stepId: String): Int = when (stepId) {
        "allergies" -> R.drawable.ic_step_allergies
        "intolerances" -> R.drawable.ic_step_intolerances
        "healthConditions" -> R.drawable.ic_step_health_conditions
        "lifeStage" -> R.drawable.ic_step_life_style
        "region" -> R.drawable.ic_step_region
        "avoid" -> R.drawable.ic_step_avoid_cross
        "lifeStyle" -> R.drawable.ic_step_diet_preferences
        "nutrition" -> R.drawable.ic_step_meals
        "ethical" -> R.drawable.ic_step_ethical
        "taste" -> R.drawable.iconoir_chocolate
        else -> R.drawable.ic_step_allergies
    }

    /**
     * Build food-notes API content from a set of selected chip IDs (for one member or Everyone).
     * Matches iOS buildContentFromPreferences: step id -> list of { "name", "iconName" }.
     */
    fun buildFoodNotesContentFromChipIds(chipIds: Set<String>): Map<String, List<Map<String, String>>> {
        if (chipIds.isEmpty()) return emptyMap()
        val content = mutableMapOf<String, MutableList<Map<String, String>>>()
        val stepIds = foodNotesStepIds
        for (stepIndex in stepIds.indices) {
            val stepId = stepIds.getOrNull(stepIndex) ?: continue
            val chips = chipsForStep(stepIndex)
            val selected = chips.filter { it.id in chipIds }.map { chip ->
                mapOf(
                    "name" to chip.label,
                    "iconName" to (chip.iconPrefix.trim().ifEmpty { "" })
                )
            }
            if (selected.isNotEmpty()) {
                content[stepId] = selected.toMutableList()
            }
        }
        return content
    }
}

