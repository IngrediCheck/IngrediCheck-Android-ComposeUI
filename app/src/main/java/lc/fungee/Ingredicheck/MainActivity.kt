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
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.components.InviteCodeBottomSheet
import lc.fungee.Ingredicheck.ui.theme.IngrediCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IngrediCheckTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ButtonTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ButtonTestScreen(modifier: Modifier = Modifier) {
    var showSheet by remember { mutableStateOf(true) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Intentionally empty background content for now.
    }

    if (showSheet) {
        InviteCodeBottomSheet(
            onPrimaryClick = { showSheet = false },
            onSecondaryClick = { showSheet = false },
            onDismissRequest = { showSheet = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonTestPreview() {
    IngrediCheckTheme {
        ButtonTestScreen()
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