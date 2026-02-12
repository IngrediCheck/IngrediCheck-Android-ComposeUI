package lc.fungee.Ingredicheck.onboarding.data

import androidx.compose.ui.layout.ContentScale
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.model.FamilyMember

internal fun avatarOptionsForCategory(categoryIndex: Int): List<FamilyMember> {
    return when (categoryIndex) {
        0 -> listOf(
            FamilyMember("baby_boy", R.drawable.family_member_baby, "Age (0-4)", ContentScale.Fit),
            FamilyMember("baby_girl", R.drawable.family_member_baby_girl, "Age (0-4)", ContentScale.Fit),
            FamilyMember("young_son", R.drawable.family_member_young_son, "Age (4-25)", ContentScale.Fit),
            FamilyMember("young_daughter", R.drawable.family_member_young_daugther, "Age (4-25)", ContentScale.Fit),
            FamilyMember("mom", R.drawable.family_member_mom, "Age (25-55)", ContentScale.Fit),
            FamilyMember("father", R.drawable.family_member_father, "Age (25-55)", ContentScale.Fit),
            FamilyMember("grandfather", R.drawable.family_member_grand_father, "Age (55+)", ContentScale.Fit),
            FamilyMember("grandmother", R.drawable.family_member_grand_mother, "Age (55+)", ContentScale.Fit),
        )

        1 -> listOf(
            FamilyMember("hand_thumbsup", R.drawable.hand_thumbsup_sigin, "Thumbs up", ContentScale.Fit),
            FamilyMember("hand_victory", R.drawable.hand_victory_sigin, "Victory", ContentScale.Fit),
            FamilyMember("hand_wave", R.drawable.wave_sigin, "Wave", ContentScale.Fit),
            FamilyMember("hand_pointing", R.drawable.pointed_sigin, "Pointing", ContentScale.Fit),
            FamilyMember("heart_hand", R.drawable.heart_hand_sigin, "Heart hand", ContentScale.Fit),
            FamilyMember("phone_sign", R.drawable.phone_sign, "Phone", ContentScale.Fit)
        )

        2 -> listOf(
            FamilyMember("hair_short", R.drawable.short_hair, "Short Hair", ContentScale.Fit),
            FamilyMember("hair_short_spiky", R.drawable.short_spiky_hair, "Short Spiky", ContentScale.Fit),
            FamilyMember("hair_curly", R.drawable.curly_hair, "Curly Hair", ContentScale.Fit),
            FamilyMember("hair_long", R.drawable.long_hair, "Long Hair", ContentScale.Fit),
            FamilyMember("hair_bun", R.drawable.bun_hair, "Bun", ContentScale.Fit),
            FamilyMember("hair_ponytail", R.drawable.ponytail_hair, "Ponytail", ContentScale.Fit),
            FamilyMember("hair_braided", R.drawable.braided_hair, "Braided", ContentScale.Fit),
            FamilyMember("hair_medium_curls", R.drawable.medium_curls_hair, "Medium Curls", ContentScale.Fit),
            FamilyMember("hair_bald", R.drawable.blad_hair, "Bald", ContentScale.Fit),
        )

        3 -> listOf(
            FamilyMember("skin_very_light", R.drawable.very_light, "Very Light", ContentScale.Fit),
            FamilyMember("skin_light", R.drawable.light, "Light", ContentScale.Fit),
            FamilyMember("skin_medium_light", R.drawable.medium_light, "Medium Light", ContentScale.Fit),
            FamilyMember("skin_medium", R.drawable.medium, "Medium", ContentScale.Fit),
            FamilyMember("skin_medium_dark", R.drawable.medium_dark, "Medium Dark", ContentScale.Fit),
            FamilyMember("skin_dark", R.drawable.dark, "Dark", ContentScale.Fit),
            FamilyMember("skin_very_dark", R.drawable.very_dark, "Very Dark", ContentScale.Fit),
        )

        4 -> listOf(
            FamilyMember("acc_specs", R.drawable.specs, "Specs", ContentScale.Fit),
            FamilyMember("acc_sunglasses", R.drawable.sunglasses, "Sunglasses", ContentScale.Fit),
            FamilyMember("acc_earrings", R.drawable.earrings, "Earrings", ContentScale.Fit),
            FamilyMember("acc_cap", R.drawable.cap, "Cap", ContentScale.Fit),
            FamilyMember("acc_hat", R.drawable.hat, "Hat", ContentScale.Fit),
        )

        5 -> listOf(
            FamilyMember("color_pastel_blue", R.drawable.pestle_blue, "Pastel Blue", ContentScale.Fit),
            FamilyMember("color_warm_pink", R.drawable.warm_pink, "Warm pink", ContentScale.Fit),
            FamilyMember("color_soft_green", R.drawable.soft_green, "Soft green", ContentScale.Fit),
            FamilyMember("color_lavender", R.drawable.lavender, "Lavender", ContentScale.Fit),
            FamilyMember("color_orange", R.drawable.orange, "Orange", ContentScale.Fit),
            FamilyMember("color_yellow", R.drawable.yellow, "Yellow", ContentScale.Fit),
            FamilyMember("color_transparent", R.drawable.transprint, "Transparent", ContentScale.Fit),
        )

        else -> emptyList()
    }
}

internal fun selectedMembersInOrder(selections: Map<Int, String>): List<FamilyMember> {
    return (0..5).mapNotNull { index ->
        val id = selections[index] ?: return@mapNotNull null
        avatarOptionsForCategory(index).firstOrNull { it.id == id }
    }
}
