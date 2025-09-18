package lc.fungee.IngrediCheck

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.data.model.SupabaseSession

@Composable
fun HomeScreen(navController: NavController, viewModel: AppleAuthViewModel) {
    val context = LocalContext.current

    val sessionJson = remember {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
    }

    val session = remember {
        sessionJson?.let {
            Gson().fromJson(it, SupabaseSession::class.java)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Welcome to Home!")

        session?.user?.let { user ->
            Text("User ID: ${user.id}")
            Text("Email: ${user.email}")
        } ?: Text("No user data found.")


        Spacer(modifier = Modifier.height(16.dp))

        // 🚪 Logout Button
        Button(
            onClick = {
                // Clear session
                context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().apply()
                // Reset login state
                viewModel.resetState()
                // Navigate to welcome screen
                navController.navigate("welcome") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Text("Logout")
        }
    }
}