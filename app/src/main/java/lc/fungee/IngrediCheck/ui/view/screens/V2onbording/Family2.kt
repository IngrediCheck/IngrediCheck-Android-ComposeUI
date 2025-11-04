package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Malerope
import lc.fungee.IngrediCheck.ui.theme.Nunito
import lc.fungee.IngrediCheck.ui.theme.Nunitosemibold

@Composable
fun Famaly2 ()
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, end = 21.dp)
            .imePadding( )
//            .imeNestedScroll()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "What’s your name?", fontSize = 28.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold, color = Color(0xFF303030),
            modifier = Modifier

                .padding(bottom = 16.dp)
        )
        Text(
            text = "This helps us personalize your experience and scan tips—just for you!",
            fontSize = 16.sp,

            textAlign = TextAlign.Center,
            color = Color(color = 0xFF949494),
            fontFamily = Malerope,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        var text by remember { mutableStateOf("") }

        CustomTextField()

        Text(
            "Choose Avatar (Optional)", fontSize = 18.sp,
            fontFamily = Malerope,
            fontWeight = FontWeight.Bold, color = Color(0xFF303030),
            lineHeight = 21.sp,
            modifier = Modifier

                .padding(top = 24.dp, bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )
        ImageLazyRow()



        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            horizontalArrangement = Arrangement.Center
            , verticalAlignment = Alignment.CenterVertically
//                        .spacedBy(16.dp)
        )
        {
            CapsuleButton1("")
            Spacer(Modifier.width(16.dp))
            CapsuleButton()
        }

    }
}
@Composable
fun CustomTextField() {
    var text by remember { mutableStateOf("") }
    val bringRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .shadow(
                elevation = 2.dp,                //
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp)) //
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)) //
            .bringIntoViewRequester(bringRequester)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontFamily = Malerope,
                fontWeight = FontWeight.Normal
                ,
                lineHeight = 24.sp //
            ),
            placeholder = {
                Text(
                    "Name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Malerope, color = Color(color = 0xFFBDBDBD),
                    lineHeight = 21.sp
                )
            }, //
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent, RoundedCornerShape(16.dp))
                .onFocusEvent { if (it.isFocused) { scope.launch { bringRequester.bringIntoView() } } }
                .padding(2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent, // border handled by Box
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFF91B640),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )



        @Composable
        fun EvenShadowCard() {
            Box(
                modifier = Modifier
                    .padding(32.dp)
                    //
                    .graphicsLayer {
                        shadowElevation = 0f
                        shape = RoundedCornerShape(24.dp)
                        clip = false
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x22000000), // darker near card
                                Color.Transparent  // fade out
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(4.dp) // space for the blur spread
                    //
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Even shadow from all sides",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        }

    }
}
@Composable
fun ImageLazyRow() {
    var selectedIndex by remember { mutableStateOf(-1) } // no image selected by default
    //
//    val overscrollEffect = StretchOverscrollEffect()
    //

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(21.dp), // gap between images
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageList = listOf(
            R.drawable.famalymember1,
            R.drawable.famaliymember2,
            R.drawable.famaliymember3,
            R.drawable.famaliymember4,
            R.drawable.famaliymember5,

            )
        imageList.forEachIndexed { index, imageRes ->
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        selectedIndex = if (selectedIndex == index) -1 else index
                    },
                contentAlignment = Alignment.TopEnd
            )
            {

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                if (selectedIndex == index) {
                    Image(
                        painter = painterResource(id = R.drawable.tickmark), // your small logo
                        contentDescription = "Selected",
                        modifier = Modifier
                            .size(17.dp)
                            .offset(x = (-4).dp, y = 4.dp) // adjust position nicely
                            .background(Color.White, CircleShape)
                            .padding(0.dp)
                    )
                }

//                    modifier = Modifier
//                        .size(50.dp)
                //
//                    .clip(CircleShape)
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
//                )
            }
        }
    }
}
//}