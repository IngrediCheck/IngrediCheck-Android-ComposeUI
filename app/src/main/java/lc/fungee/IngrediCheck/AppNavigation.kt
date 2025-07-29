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
            HomeScreen(
                onSignOut = {
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("welcome") {
<<<<<<< HEAD
//            WelcomeScreen(
//                onGoogleSignIn = {
//                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
//                },
//                viewModel = viewModel
//            )
=======
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
>>>>>>> main
        }
    }
}


@Composable
fun HomeScreen(onSignOut: () -> Unit) {
    Column {
        Text(text = "Welcome to Ingredicheck")
        // Add your sign out button here that calls onSignOut
    }
}