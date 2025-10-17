package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
@Preview(showBackground = true)
@Composable
fun Justme ()
{
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.weight(1f)) // pushes content to 50%
        Row() {
            Text(
                text = "Dietary",
                fontSize = 20.sp,
                color = Color.Black
            )



        }
        Spacer(modifier = Modifier.weight(1f)) // optional: keep it centered
    }


}