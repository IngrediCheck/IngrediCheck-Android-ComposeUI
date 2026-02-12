package lc.fungee.Ingredicheck.onboarding.model

import androidx.compose.ui.layout.ContentScale

data class CategoryIcon(
    val unselectedRes: Int,
    val selectedRes: Int
)

data class FamilyMember(
    val id: String,
    val iconRes: Int,
    val label: String,
    val contentScale: ContentScale = ContentScale.Crop
)
