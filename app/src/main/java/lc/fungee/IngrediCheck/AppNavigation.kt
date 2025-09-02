// Updated: app/src/main/java/lc/fungee/IngrediCheck/AppNavigation.kt

package lc.fungee.IngrediCheck

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import lc.fungee.IngrediCheck.PreferenceList.CameraScreen
import lc.fungee.IngrediCheck.PreferenceList.HistoryScreen
import lc.fungee.IngrediCheck.PreferenceList.HomeScreen
import lc.fungee.IngrediCheck.PreferenceList.SettingsScreen
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

        composable("welcome") {
            WelcomeScreen(
                onGoogleSignIn = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                },
                viewModel = viewModel,
                navController = navController,
                googleSignInClient = googleSignInClient
            )
        }

        composable("disclaimer") {
            DisclaimerScreen(
                modifier = Modifier.fillMaxSize(),
                onAgree = {
                    navController.navigate("home") {
                        popUpTo("disclaimer") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Updated: Pass navController to all screens
        composable("home") {
            HomeScreen(navController)
        }

        composable("Check") {
            CameraScreen(navController)
        }

        composable("history") {
            HistoryScreen(navController)
        }

        composable("List") {
            SettingsScreen(navController=navController,viewModel=viewModel)
        }
    }
}


//package lc.fungee.IngrediCheck
//
//import android.content.Intent
//import android.preference.PreferenceScreen
//import androidx.activity.result.ActivityResultLauncher
//
//import androidx.compose.foundation.layout.fillMaxSize
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//
//import lc.fungee.IngrediCheck.PreferenceList.HomeScreen
//import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
//import lc.fungee.IngrediCheck.onboarding.DisclaimerScreen
//import lc.fungee.IngrediCheck.onboarding.WelcomeScreen
//
//@Composable
//fun AppNavigation(
//    viewModel: AppleAuthViewModel,
//    googleSignInLauncher: ActivityResultLauncher<Intent>,
//    googleSignInClient: GoogleSignInClient
//) {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "splash"
//    ) {
//        composable("splash") {
//            SplashScreen { isLoggedIn ->
//                navController.navigate(if (isLoggedIn) "home" else "welcome") {
//                    popUpTo("splash") { inclusive = true }
//                }
//            }
//        }
//
//        composable("welcome") {
//            WelcomeScreen(
//                onGoogleSignIn = {
//                    googleSignInClient.signOut().addOnCompleteListener {
//                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
//                    }
//                },
//                viewModel = viewModel,
//                navController = navController,
//                googleSignInClient = googleSignInClient // <-- Pass client for sign out
//            )
//        }
//
//        composable("disclaimer") {
//            DisclaimerScreen(
//                modifier = Modifier.fillMaxSize(),
//                onAgree = {
//                    navController.navigate("home") {
//                        popUpTo("disclaimer") { inclusive = true }
//                    }
//                }
//            )
//        }
//        composable("home") {
//        HomeScreen()
//        }
//
////        composable("home") {
////            HomeScreen(navController = navController, viewModel = viewModel)
////        }
//
////    composable(PreferenceScreen.Home.route) { PreferenceScreen.Home() }
////    composable(PreferenceScreen.Check.route) { CameraScreen() }
////    composable(PreferenceScreen.History.route) { PreferenceScreen.History() }
////    composable(PreferenceScreen.Settings.route) { SettingsScreen() }
//    }
//}
