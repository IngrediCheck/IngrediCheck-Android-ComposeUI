package lc.fungee.IngrediCheck

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lc.fungee.IngrediCheck.ui.theme.IngrediCheckTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Handle the OAuth redirect
        handleIntent(intent)
        setContent {
            IngrediCheckTheme {

                AppNavigation()
            }
        }
    }


override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntent(intent)
}

private fun handleIntent(intent: Intent) {
    intent?.data?.let { uri ->
        if (uri.toString().startsWith("lc.fungee.IngrediCheck://")) {
            // Supabase will automatically handle this
        }
    }
}
}