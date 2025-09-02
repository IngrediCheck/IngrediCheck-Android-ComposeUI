package lc.fungee.IngrediCheck.PreferenceList
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.PrimarayGreen50
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import lc.fungee.IngrediCheck.ui.theme.White
@Preview(showBackground = true
)

@Composable
fun BottomBarPreview() {
    val navController = rememberNavController()
    BottomBar(navController = navController)
}

@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    // Or any color you like
    NavigationBar(
        containerColor = White,
        tonalElevation = 0.dp,
        modifier = Modifier.shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            clip = false // Don't clip the shadow
        )
            .height(100.dp) // Set height
            .padding(top = 1.dp) // Top and bottom padding
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Rounded top corners
    ) {
        val middleIndex = 1

        items.forEachIndexed { index, item ->
            val selected = currentRoute == item.route
            val interactionSource = remember { MutableInteractionSource() } // disables ripple
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (index == middleIndex) {
                        // Center (Middle) Button
                        Box(
                            modifier = Modifier
                                .size(56.dp) // width: 56, height: 56
                                .background(
                                    color = PrimaryGreen100,
                                    shape = CircleShape

                                ) // your manual background color
                                .padding(14.dp), // padding inside the circle
                            contentAlignment = Alignment.Center // center the icon
                        ) {
                            Icon(
                                painter = painterResource(id = item.selectedIcon),
                                contentDescription = item.title,
                                modifier = Modifier.size(28.dp), // 56 - 14*2 = 28 → icon fits inside padded circle
                                tint = Color.Unspecified // or your desired tint
                            )
                        }



                    } else {
                        // Side Buttons
                        // Side Buttons
                        Box(
                            modifier = Modifier
                                .size(48.dp) // width and height = 48
//
//                                .background(if (selected) PrimarayGreen50 else Color.Transparent), // background only when selected
                            , contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = item.selectedIcon
                                ),
                                contentDescription = item.title,
                                modifier = Modifier.size(19.5.dp), // icon size
                                tint = if (selected) PrimaryGreen100 else Greyscale500 // tint when selected or not
                            )
                        }

                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PrimarayGreen50, // disables background highlight
                    selectedIconColor = PrimarayGreen50,
                    unselectedIconColor = PrimarayGreen50,
                ),
//                modifier = Modifier.indication(interactionSource, null),
//                interactionSource = interactionSource,
                alwaysShowLabel = false
            )
        }
    }
}
