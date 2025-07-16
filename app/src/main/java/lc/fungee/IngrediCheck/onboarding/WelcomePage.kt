package lc.fungee.IngrediCheck.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomePager(
    item: WelcomeOnboardingItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Image - Slightly increased height
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = Modifier
                .width(320.dp)
                .height(360.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Heading - Increased size
        Text(
            text = item.heading,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            letterSpacing = 0.36.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description - Slightly increased
        Text(
            text = item.description,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            lineHeight = 22.sp,
            letterSpacing = (-0.2).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
    }
}
