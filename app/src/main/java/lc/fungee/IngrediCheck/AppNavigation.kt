package lc.fungee.IngrediCheck
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.google.android.gms.auth.api.signin.GoogleSignInClient

import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.onboarding.DisclaimerScreen
import lc.fungee.IngrediCheck.onboarding.WelcomeScreen

@Composable
fun AppNavigation(

    viewModel: AppleAuthViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    googleSignInClient: GoogleSignInClient
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen { isLoggedIn ->
                navController.navigate(if (isLoggedIn) "home" else "welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("home") {
            HomeScreen(navController = navController, viewModel = viewModel)
        }

        composable("welcome") {
            // Pass googleSignInClient to WelcomeScreen so it can sign out before launching sign-in
            WelcomeScreen(
                onGoogleSignIn = {
                    // This lambda will be called after signOut in WelcomeScreen
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                },
                viewModel = viewModel,
                navController = navController,
                googleSignInClient = googleSignInClient // <-- Pass client for sign out
            )
        }
        composable("disclaimer") {
            DisclaimerScreen(
                modifier =Modifier.fillMaxSize(),

                onAgree = {
                    navController.navigate("home") {
                        popUpTo("disclaimer") { inclusive = true }
                    }
                }
            )
        }

    }
}





