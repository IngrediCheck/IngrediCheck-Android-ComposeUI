package lc.fungee.IngrediCheck.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Greyscale500
import lc.fungee.IngrediCheck.ui.theme.Greyscale600
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100

@Composable
fun DisclaimerScreen(
    modifier: Modifier = Modifier,
    onAgree: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Image(
                painter = painterResource(id = R.drawable.disclimerlogo),
                contentDescription = "Disclaimer Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(53.76.dp)
                    .height(61.44.dp)
                    .clip(RoundedCornerShape(bottomStart = 9.6.dp, bottomEnd = 9.6.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to IngrediCheck!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.88.sp,
                color = Greyscale600,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Thanks for choosing IngrediCheck!\n" +
                        "Our AI helps match foods to your dietary \nneeds. " +
                        "While our AI is constantly learning, it’s not perfect—please double-check " +
                        "product labels to ensure they meet your specific requirements.\n\n" +
                        "Your feedback is vital—it trains our AI to be more accurate. " +
                        "Spot a mistake or have a suggestion? Let us know and help improve everyone's shopping experience!",
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 21.12.sp,
                color = Greyscale500
            )
        }

        // This spacer takes up remaining space so button is at bottom
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onAgree() },
            modifier = Modifier
                .fillMaxWidth()
                .height(63.76.dp)
                .clip(RoundedCornerShape(100.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen100
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 21.12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
