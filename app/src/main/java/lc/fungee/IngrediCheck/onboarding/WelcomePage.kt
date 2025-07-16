package lc.fungee.IngrediCheck.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = Modifier
                .width(343.dp)
                .height(488.dp)
        )

        // Gap after image (32dp as mentioned in comment)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        // Heading
        Text(
            text = item.heading,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp,
            letterSpacing = 0.36.sp,
            modifier = Modifier
                .width(304.dp)
                .height(68.dp)
                .padding(horizontal = 8.dp)
        )

        // Gap between heading and description
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(18.dp))

        // Description
        Text(
            text = item.description,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            lineHeight = 22.sp,
            letterSpacing = (-0.41).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(304.dp)
                .height(44.dp)
                .padding(horizontal = 8.dp)
        )


    }
}