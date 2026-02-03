package lc.fungee.Ingredicheck

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lc.fungee.Ingredicheck.auth.AuthState
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.onboarding.ui.OnboardingHost
import lc.fungee.Ingredicheck.ui.theme.IngrediCheckTheme

class MainActivity : ComponentActivity() {

    private val deepLinkUriState = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(
            "MainActivity",
            "onCreate intent action=${intent?.action}, data=${intent?.data}"
        )
        deepLinkUriState.value = intent?.data
        setContent {
            IngrediCheckTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppRoot(
                        deepLinkUri = deepLinkUriState.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d(
            "MainActivity",
            "onNewIntent action=${intent.action}, data=${intent.data}"
        )
        deepLinkUriState.value = intent.data
    }
}

@Composable
fun AppRoot(
    deepLinkUri: Uri? = null,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.state.collectAsState()
    var showOnboarding by remember { mutableStateOf(true) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            showOnboarding = false
        }
    }

    LaunchedEffect(deepLinkUri) {
        authViewModel.handleDeepLink(deepLinkUri)
    }

    if (showOnboarding) {
        OnboardingHost(
            authViewModel = authViewModel,
            onExitOnboarding = { showOnboarding = false }
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonTestPreview() {
    IngrediCheckTheme {
        AppRoot(deepLinkUri = null)
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