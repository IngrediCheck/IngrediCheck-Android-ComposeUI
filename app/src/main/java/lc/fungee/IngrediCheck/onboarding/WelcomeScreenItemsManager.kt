package lc.fungee.IngrediCheck.onboarding

import lc.fungee.IngrediCheck.R

class WelcomeOnboardingItem (
    val heading:String, val description:String,val imageResId:Int
)
object WelcomeScreenItemsManager {


    fun getOnboardingItems(): List<WelcomeOnboardingItem> {
        return listOf(
            WelcomeOnboardingItem(
                heading = "Personalize your dietary preferences",
                description = "Enter dietary needs in plain language to tailor your food choices",
                imageResId = R.drawable.welcome1
            ),
            WelcomeOnboardingItem(
                heading = "Simplify your food label checks",
                description = "Scan barcodes for a detailed breakdown of ingredients",
                imageResId = R.drawable.welcome2
            ),
            WelcomeOnboardingItem(
                heading = "Never forget your favorite items again.",
                description = "Save items to your custom list for quick access and easy reference",
                imageResId = R.drawable.welcome3
            )
        )
    }


    fun getOnboardingItemsCount(): Int = getOnboardingItems().size


    fun getOnboardingItem(index: Int): WelcomeOnboardingItem? {
        val items = getOnboardingItems()
        return if (index in items.indices) items[index] else null
    }
}
