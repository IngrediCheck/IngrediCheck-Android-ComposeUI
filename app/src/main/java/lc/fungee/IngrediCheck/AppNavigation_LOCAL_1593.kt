package lc.fungee.IngrediCheck

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lc.fungee.IngrediCheck.onboarding.WelcomeScreen

@Composable
fun AppNavigation(


) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }

        composable("welcome") {
//            WelcomeScreen(
//                onGoogleSignIn = {
//                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
//                },
//                viewModel = viewModel
//            )
        }
    }
}


@Composable
fun HomeScreen() {
    Column {
        Text(text = "Welcome to Ingredicheck")
    }
}
