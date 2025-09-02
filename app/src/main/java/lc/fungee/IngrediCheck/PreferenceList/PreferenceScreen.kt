
package lc.fungee.IngrediCheck.PreferenceList

import lc.fungee.IngrediCheck.R

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: Int,

)

// List of items for the BottomBar
val items = listOf(
    BottomNavigationItem(
        title = "Home",
        route = "home",
        selectedIcon = R.drawable.homelogo
    ),
    BottomNavigationItem(
        title = "check",
        route = "check",
        selectedIcon = R.drawable.scanner

    ),
    BottomNavigationItem(
        title = "list",
        route = "list",
        selectedIcon = R.drawable.listlogo)
    // Add more items as needed
)