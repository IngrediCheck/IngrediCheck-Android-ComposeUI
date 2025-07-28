package lc.fungee.IngrediCheck.onboarding
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerScreen(
    modifier: Modifier = Modifier,
    onAgree: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Disclaimer",
            fontSize = 28.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = """
                By using this app, you acknowledge that:
                
                • The app does not provide medical advice.
                • You should consult a healthcare professional before making dietary decisions.
                • We are not responsible for inaccuracies in product data.
                
                Continue only if you agree with these terms.
            """.trimIndent(),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Start
        )

        Button(
            onClick = onAgree,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("I Agree")
        }
    }
}
