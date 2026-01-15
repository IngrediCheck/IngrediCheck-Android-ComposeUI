package lc.fungee.Ingredicheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.components.buttons.SecondaryButton
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
    var isDisabled by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(200.dp)
                .then(
                    if (isDisabled) {
                        Modifier.clickable { isDisabled = false }
                    } else {
                        Modifier
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                title = if (isDisabled) "Primary Disabled" else "Primary Enabled",
                isDisabled = isDisabled,
                takeFullWidth = false,
                width = 152.dp,
                onClick = { isDisabled = true }
            )

            SecondaryButton(
                title = if (isDisabled) "Secondary Disabled" else "Secondary Enabled",
                isDisabled = isDisabled,
                takeFullWidth = false,
                width = 152.dp,
                onClick = { isDisabled = true }
            )
        }
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
    Text(
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