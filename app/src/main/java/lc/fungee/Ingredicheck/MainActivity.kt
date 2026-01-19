package lc.fungee.Ingredicheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import lc.fungee.Ingredicheck.onboarding.ui.OnboardingHost
import lc.fungee.Ingredicheck.ui.theme.IngrediCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IngrediCheckTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    var showOnboarding by remember { mutableStateOf(true) }

    if (showOnboarding) {
        OnboardingHost(
            onExitOnboarding = { showOnboarding = false }
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Intentionally empty background content for now.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonTestPreview() {
    IngrediCheckTheme {
        AppRoot()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Hello Ingredicheck!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IngrediCheckTheme {
        Greeting("Android")
    }
}