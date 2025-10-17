package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import android.R.attr.maxHeight
import android.R.attr.maxWidth
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.ui.theme.White

@Preview( showBackground = true)
@Composable
fun demo()
{
    Onboarding4()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Onboarding4() {
    val baseWidth = 375f
    val baseHeight = 812f


    val scope = rememberCoroutineScope()

    // State of the bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
//        initialValue = SheetValue.Expanded // <-- open by default
    )

    ModalBottomSheet(
        onDismissRequest = { /* Handle dismiss if needed */ },
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxWidth(),

       dragHandle = {
           BottomSheetDefaults.DragHandle(
                   color = Color(0xFFDFE2E5), // your custom color

               )

       }
    ) {
        // Content of the bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hey there! Who’s this for?", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Is it just you, or your whole IngrediFam — family, friends,\n anyone you care about?")
            Spacer(modifier = Modifier.height(20.dp))
            Row()
            {
                Box(
                    modifier = Modifier
                        .width(109.dp)
                        .height(36.dp)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .background(
                            brush = Brush.linearGradient( colors = listOf(
                                Color(0xFF9DCF10),
                                Color(0xFF6B8E06)
                            )),
                            shape = RoundedCornerShape(percent =  50)
                        )
//                    .clickable {
//                        onFinish()
//                    },
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Just me",
                        color = Color(0xFFFFFFFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
  Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .width(109.dp)
                        .height(36.dp)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .background(
                            brush = Brush.linearGradient( colors = listOf(
                                Color(0xFF9DCF10),
                                Color(0xFF6B8E06)
                            )),
                            shape = RoundedCornerShape( percent =  50)
                        )
//                    .clickable {
//                        onFinish()
//                    },
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add Famaliy",
                        color = Color(0xFFFDFDFD),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("You can always add or edit members later.", color = Color(0xFF8C8C8C),)



        }

        }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        contentAlignment = Alignment.Center

    ) {
        val screenWidth = maxWidth.value
        val screenHeight = maxHeight.value

        val widthFactor = screenWidth / baseWidth
        val heightFactor = screenHeight / baseHeight

        val ellipseWidth = 282.dp * widthFactor
        val ellipseHeight = 377.dp * heightFactor
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ellipse
            Canvas(
                modifier = Modifier
                    .size(width = ellipseWidth, height = ellipseHeight)
            ) {
                drawOval(color = Color(0xFFD9D9D9))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Let’s get started!\nYour IngrediFam will appear here as\nyou set things up.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )


        }
    }
}
