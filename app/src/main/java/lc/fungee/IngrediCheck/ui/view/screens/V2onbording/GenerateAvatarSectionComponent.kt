package lc.fungee.IngrediCheck.ui.view.screens.V2onbording

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.ui.theme.Malerope

data class FamilyMember(
    val name: String,
    val iconRes: Int
)


val familyMembers = listOf(
    FamilyMember("Grandfather", R.drawable.famaliymember4),
    FamilyMember("Father", R.drawable.famaliymember3),
    FamilyMember("Sister", R.drawable.famaliymember2),
    FamilyMember("Grandmother", R.drawable.famaliymember5),
    FamilyMember("Mother", R.drawable.famalymember1)
)

@Composable
fun FamaliySelectionCard() {
    var expandCard by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf(familyMembers.first()) }
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val menuWidth = maxWidth

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)

                .shadow(
                    elevation = 0.5.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable { expandCard = true }
                .border(
                    width = 0.5.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFEEEEEE)
                )
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFF9F9F9), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = selectedMember.iconRes),
                            contentDescription = selectedMember.name,
                            modifier = Modifier
                                .matchParentSize()
                                .padding(6.dp)
                        )
                    }
                    Text(
                        text = selectedMember.name,
                        modifier = Modifier.padding(start = 8.dp),
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF303030),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = if (expandCard) R.drawable.dropupicon else R.drawable.dropdownicon),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Dropdown menu anchored to the same Box and matching its width
        DropdownMenu(
            expanded = expandCard,
            onDismissRequest = { expandCard = false },
            modifier = Modifier
                .width(menuWidth)
                .border(
                    width = 1.dp,
                    color = Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(24.dp)
                ),
            offset = DpOffset(0.dp, -8.dp),
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            familyMembers.forEach { member ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFF9F9F9), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = member.iconRes),
                                    contentDescription = selectedMember.name,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .padding(6.dp)
                                )
                            }
                            Text(
                                text = member.name,
                                modifier = Modifier.padding(start = 8.dp),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF303030),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    },
                    onClick = {
                        selectedMember = member
                        expandCard = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun GestureSectionCard(
    selectedGesture: Gesture? = null ,
    onGestureSelected: (Gesture) -> Unit = {}
) {
    val gestures = listOf(
        Pair("👋","Wave"),
        Pair("👍","Thumbs Up"),
        Pair("✌️" ,"Peace"),
        Pair("🫶" ,"Heart Hands"),
        Pair( "📱","Phone"),
        Pair( "👉","Pointing"),

        )
    val coroutineScope = rememberCoroutineScope()
val pagerState = rememberPagerState (initialPage = 0, pageCount = {gestures.size})

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(79.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage -1
                    if(prevPage >=0){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFF7F7F7),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }
        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (emoji, name) = gestures[page]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Text(
                        text = emoji,
                        style = TextStyle(
                            fontSize = 50.72.sp,
                            lineHeight = 49.66.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.clickable{}
                    )

                    Text(
                        text = name,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontFamily = Malerope,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF616161),
                            textAlign = TextAlign.Center
                        )
                    )
                }

        }


        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage +1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFF7F7F7),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun HairStyleSectionCard() {
    val gestures = listOf(
        Pair("💇‍♀️","Short Hair"),
        Pair("💁‍♀️","Long Hair"),
        Pair("🧑‍🦱","Curly Hair"),
        Pair("👩","Straigh Hair"),
        Pair("🧑‍🦲","Blad"),
        Pair( "👱‍♀️","Ponytail"),
        Pair("👵","Bun")

        )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState (initialPage = 0, pageCount = {gestures.size})

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(79.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage -1
                    if(prevPage >=0){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }
        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (emoji, name) = gestures[page]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = emoji,
                    style = TextStyle(
                        fontSize = 50.72.sp,
                        lineHeight = 49.66.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontFamily = Malerope,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }


        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage +1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun SkinToneSectionCard() {
    val gestures = listOf(
        Pair("👶🏻️️","Light"),
        Pair("🧑️️","Medium"),
        Pair("🧑🏿","Deep"),
        Pair("☀️","Tan"),
        Pair("🧒","Freckles"),


    )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState (initialPage = 0, pageCount = {gestures.size})

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(79.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage -1
                    if(prevPage >=0){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }
        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (emoji, name) = gestures[page]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = emoji,
                    style = TextStyle(
                        fontSize = 50.72.sp,
                        lineHeight = 49.66.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontFamily = Malerope,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }


        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage +1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }

    }

}




@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun AccessoriesSectionCard() {
    val gestures = listOf(
        Pair("🙂","None"),
        Pair("🤓","Glasses"),
        Pair("😎","Sunglasses"),
        Pair("👂","Earrings"),
        Pair("👒","Hat"),
        Pair("🧢","Cap"),


        )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState (initialPage = 0, pageCount = {gestures.size})

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(79.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage -1
                    if(prevPage >=0){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }
        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (emoji, name) = gestures[page]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = emoji,
                    style = TextStyle(
                        fontSize = 50.72.sp,
                        lineHeight = 49.66.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontFamily = Malerope,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }


        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage +1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun ColorSectionCard() {
    val gestures = listOf(
        Pair(0xFFA7D8F0,"Pastel Blue"),
        Pair(0xFFF9C6D0,"Warm Pink"),
        Pair(0xFFBFF0D4,"Soft Green"),
        Pair(0xFFDCC7F6,"Lavender"),
        Pair(0xFFFFD9B5,"Cream"),
        Pair(0xFF98FF98,"Mint"),
        Pair(0xFF98FF98,"Transparent"),



        )
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState (initialPage = 0, pageCount = {gestures.size})

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage -1
                    if(prevPage >=0){
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }
        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (color1, name) = gestures[page]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Child views.
                Box(modifier = Modifier
                    .size(70.dp)
                    .background(color = Color(color1) , shape = CircleShape)
                )

                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontFamily = Malerope,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }


        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage +1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                }
                .background(
                    color = Color(0xFFE3E3E3),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }

    }

}
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun ColorSectionCard1() {
    val gestures = listOf(
        Pair(0xFFA7D8F0, "Pastel Blue"),
        Pair(0xFFF9C6D0, "Warm Pink"),
        Pair(0xFFBFF0D4, "Soft Green"),
        Pair(0xFFDCC7F6, "Lavender"),
        Pair(0xFFFFD9B5, "Cream"),
        Pair(0xFF98FF98, "Mint"),
        Pair(null, "Transparent") // 👈 use null color for last
    )

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { gestures.size })

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Arrow
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable {
                    val prevPage = pagerState.currentPage - 1
                    if (prevPage >= 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }
                }
                .background(Color(0xFFE3E3E3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_left),
                contentDescription = null
            )
        }

        // Pager (Middle)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val (colorHex, name) = gestures[page]

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (colorHex != null) {
                    // Normal color circle
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(colorHex), CircleShape)
                    )
                } else {
                    // 👇 Transparent icon instead of color box
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                    Text(
                        text = "🚫",
                        style = TextStyle(
                            fontSize = 50.72.sp,
                            lineHeight = 49.66.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Text(
                    text = name,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontFamily = Malerope,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Right Arrow
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable {
                    val nextPage = pagerState.currentPage + 1
                    if (nextPage < gestures.size) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                }
                .background(Color(0xFFE3E3E3), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.swipe_arrow_right),
                contentDescription = null
            )
        }
    }
}





