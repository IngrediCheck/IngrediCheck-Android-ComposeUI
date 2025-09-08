package lc.fungee.IngrediCheck.onboarding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Image(
//                painter = painterResource(id = R), // <-- your logo resource
//                contentDescription = "App Logo",
//                modifier = Modifier
//                    .size(40.dp)
//                    .padding(end = 8.dp)
//            )

            Text(
                text = "Welcome to IngrediCheck!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Thanks for choosing IngrediCheck!\n\n" +
                    "Our AI helps match foods to your dietary needs. " +
                    "While our AI is constantly learning, it’s not perfect—" +
                    "please double-check product labels to ensure they meet your specific requirements.\n\n" +
                    "Your feedback is vital—it trains our AI to be more accurate. " +
                    "Spot a mistake or have a suggestion? Let us know and help improve everyone's shopping experience!",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onAgree() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Get Started")
        }
    }
}
