package lc.fungee.IngrediCheck.ui.view.screens.onboarding

import lc.fungee.IngrediCheck.R

class WelcomeOnboardingItem (
    val heading:String, val description:String,val imageResId:Int
)

object WelcomeScreenItemsManager {


    fun getOnboardingItems(): List<WelcomeOnboardingItem> {
        return listOf(
            WelcomeOnboardingItem(
                heading = "Personalize your\ndietary preferences",
                description = "Enter dietary needs in plain language to\ntailor your food choices",
                imageResId = R.drawable.welcome1
            ),
            WelcomeOnboardingItem(
                heading = "Simplify your food\nlabel checks",
                description = "Scan barcodes for a detailed\nbreakdown of ingredients",
                imageResId = R.drawable.welcome2
            ),
            WelcomeOnboardingItem(
                heading = "Never forget your\nfavorite items again.",
                description = "Save items to your custom list for quick\naccess and easy reference",
                imageResId = R.drawable.welcome3
            )
        )
    }



}