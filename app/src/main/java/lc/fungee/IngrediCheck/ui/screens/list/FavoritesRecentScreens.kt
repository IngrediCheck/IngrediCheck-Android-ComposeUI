package lc.fungee.IngrediCheck.ui.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.data.model.ImageLocationInfo
import lc.fungee.IngrediCheck.data.repository.FavoriteItem
import lc.fungee.IngrediCheck.data.repository.HistoryItem
import lc.fungee.IngrediCheck.data.repository.ListTabRepository
import lc.fungee.IngrediCheck.data.repository.ListTabViewModel
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.data.model.Product
import lc.fungee.IngrediCheck.ui.screens.analysis.AnalysisResultSection
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import lc.fungee.IngrediCheck.ui.screens.check.DynamicPagerIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import lc.fungee.IngrediCheck.ui.component.BottomBar
import lc.fungee.IngrediCheck.ui.screens.check.CheckBottomSheet
import lc.fungee.IngrediCheck.ui.theme.White

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesPageScreen(
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val prefRepo =
        remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val vm = remember { ListTabViewModel(listRepo) }
    val ui by vm.uiState.collectAsState()

    val pull = rememberPullRefreshState(
        refreshing = ui.isLoadingFavorites,
        onRefresh = { vm.refreshFavorites() }
    )
    var showSheet by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            if (navController != null) BottomBar(
                navController = navController,
                onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .pullRefresh(pull)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Favorites",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color(0xFF1B270C)
            )
            Spacer(Modifier.height(12.dp))

            when {
                ui.favorites == null && ui.isLoadingFavorites ->
                    Box(
                        Modifier.fillMaxSize()
                    ) { }

                ui.favorites == null -> Box(Modifier.fillMaxSize())
                ui.favorites!!.isEmpty() -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painterResource(id = R.drawable.foodemptylist),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
//                    Spacer(Modifier.width(12.dp))
//                    Text("No Favorite products yet", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(ui.favorites!!) { item ->
                            FavoriteItemListCard(
                                item = item, supabaseClient = supabaseClient,
                                modifier = Modifier.clickable {
                                    val json = java.net.URLEncoder.encode(
                                        Json.encodeToString(FavoriteItem.serializer(), item),
                                        "UTF-8"
                                    )
                                    navController?.navigate("favoriteItem?item=$json")
                                }
                            )
                            Divider(color = Color(0xFFF3F2F9), thickness = 2.dp)
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = ui.isLoadingFavorites,
                state = pull,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentScansPageScreen(
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    navController: NavController? = null
) {
    val context = LocalContext.current
    val prefRepo =
        remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val vm = remember { ListTabViewModel(listRepo) }
    val ui by vm.uiState.collectAsState()

    var isSearching by remember { mutableStateOf(false) }

    if (isSearching) {
        SearchHistoryView(
            searchText = ui.searchText,
            onSearchTextChange = { vm.setSearchText(it) },
            results = ui.searchResults,
            onBack = { isSearching = false },
            onRefresh = { vm.refreshSearch() },
            onItemClick = { item ->
                val json = java.net.URLEncoder.encode(
                    Json.encodeToString(HistoryItem.serializer(), item),
                    "UTF-8"
                )
                navController?.navigate("historyItem?item=$json")
            },
            supabaseClient = supabaseClient
        )
        return
    }

    // ✅ Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = ui.isLoadingHistory,
        onRefresh = { vm.refreshHistory() }
    )

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (navController != null) {
                BottomBar(
                    navController = navController,
                    onCheckClick = { showSheet = true }
                )
            }
        }
    ) { paddingValues ->
        // ✅ Box for pull-to-refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Header
                item {
                    Text(
                        text = "Recent Scans",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Color(0xFF1B270C),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))
                }

                // Loading state
                if (ui.history == null && ui.isLoadingHistory) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }
                // Empty state
                else if (ui.history?.isEmpty() == true) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painterResource(id = R.drawable.emptyrecentscan),
                                contentDescription = null,
                                modifier = Modifier.size(width = 174.dp, height = 134.dp)
                            )
                            Text("No products scanned yet", color = Color.Gray)
                        }
                    }
                }
                // History items
                else if (!ui.history.isNullOrEmpty()) {
                    items(ui.history!!) { item ->
                        HistoryItemCard(
                            item = item,
                            supabaseClient = supabaseClient,
                            modifier = Modifier.clickable {
                                val json = java.net.URLEncoder.encode(
                                    Json.encodeToString(HistoryItem.serializer(), item),
                                    "UTF-8"
                                )
                                navController?.navigate("historyItem?item=$json")
                            }
                        )
                        Divider(color = Color(0xFFF3F2F9), thickness = 2.dp)
                    }
                }
            }

            // ✅ Pull-to-refresh indicator (always overlayed at top)
            PullRefreshIndicator(
                refreshing = ui.isLoadingHistory,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // ✅ Auto refresh first time screen opens
    LaunchedEffect(Unit) {
        vm.refreshHistory()
    }
}


@Composable
fun FavoriteItemDetailScreen(
    itemJson: String,
    supabaseClient: io.github.jan.supabase.SupabaseClient
) {
    val item = remember(itemJson) {
        runCatching {
            Json.decodeFromString(
                FavoriteItem.serializer(),
                itemJson
            )
        }.getOrNull()
    }
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

                .padding(16.dp)
        ) {
            Text(item?.name ?: "Unknown Name", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            if (!item?.brand.isNullOrBlank()) Text(
                item!!.brand!!,


                fontSize = 15.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            val first = item?.images?.firstOrNull()
            val imageUrl by rememberResolvedImageUrl(first, supabaseClient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF3F2F9))
            ) {
                if (!imageUrl.isNullOrBlank()) AsyncImage(
                    model = imageUrl,
                    contentDescription = item?.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Ingredients", fontWeight = FontWeight.SemiBold)
            val ingredientsText = remember(item) {
                item?.ingredients?.flatMap { flattenNames(it) }?.joinToString(", ") ?: ""
            }
            if (ingredientsText.isNotBlank()) Text(ingredientsText) else Text(
                "No ingredient details available",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun HistoryItemDetailScreen(
    itemJson: String,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    navController: NavController? = null,
    functionsBaseUrl: String = "",
    anonKey: String = ""
) {
    val item = remember(itemJson) {
        runCatching {
            Json.decodeFromString(
                HistoryItem.serializer(),
                itemJson
            )
        }.getOrNull()
    }
    val product = remember(item) {
        item?.let {
            Product(
                barcode = it.barcode,
                brand = it.brand,
                name = it.name,
                ingredients = it.ingredients,
                images = it.images
            )
        }
    }
    val recs = item?.ingredientRecommendations ?: emptyList()

    var showSheet by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            if (navController != null) BottomBar(
                navController = navController,
                onCheckClick = { showSheet = true })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(paddingValues)
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val actionIconModifier = Modifier.size(24.dp)
                    Row(
                        modifier = Modifier
                            .clickable { navController?.popBackStack() }
                            .padding(8.dp), // touch-friendly area
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF2B7A0B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Lists",
                            color = Color(0xFF2B7A0B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.9f))
                    var isLike by remember { mutableStateOf(item?.favorited == true) }
                    Icon(
                        imageVector = if (isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Heart logo",
                        modifier = actionIconModifier.clickable { isLike = !isLike },
                        tint = if (isLike) Color.Red else Color(0xFF2B7A0B)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.flaglogo),
                        contentDescription = "Flag logo",
                        modifier = actionIconModifier.clickable { /* TODO: Feedback flow */ },
                        tint = Color.Unspecified
                    )
                }

                // Title and brand
                Text(
                    text = item?.name ?: "Unknown Product",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                item?.brand?.let { brand ->
                    Text(
                        text = brand,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Images pager (same pattern as AnalysisScreen)
                val pagerState = rememberPagerState()
                val totalPages = (item?.images?.size ?: 0) + 1
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(
                        count = totalPages,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F2F9))
                            .height(350.dp)
                    ) { page ->
                        val imgs = item?.images ?: emptyList()
                        if (page < imgs.size) {
                            val img = imgs[page]
                            val imageUrl by rememberResolvedImageUrl(img, supabaseClient)
                            if (!imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = item?.name,
                                    modifier = Modifier.fillMaxSize(0.8f),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.clickimageplaceholder),
                                    contentDescription = "click image",
                                    modifier = Modifier
                                        .width(220.dp)
                                        .height(220.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                    // Simple dots indicator like AnalysisScreen
                    DynamicPagerIndicator(
                        currentPage = pagerState.currentPage,
                        totalPages = totalPages,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Analysis result + ingredients
                if (product != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AnalysisResultSection(product = product, recommendations = recs)
                    }
                } else {
                    Text("No ingredient details available", color = Color.Gray)
                }
            }
        }
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
    }
}

private fun flattenNames(ingredient: lc.fungee.IngrediCheck.data.model.Ingredient): List<String> {
    val name = ingredient.name?.let { listOf(it) } ?: emptyList()
    return name + ingredient.ingredients.flatMap { flattenNames(it) }
}

@Composable
fun FavoriteItemListCard(
    item: FavoriteItem,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    modifier: Modifier = Modifier
) {
    val firstImage = item.images.firstOrNull()
    val imageUrl by rememberResolvedImageUrl(firstImage, supabaseClient)
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFF3F2F9))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.foodemptylist),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.brand ?: "Unknown Brand", fontSize = 14.sp)
            Text(item.name ?: "Unknown Name", fontSize = 13.sp, color = Color.Gray)
            val date = item.createdAt ?: ""
            if (date.isNotBlank()) Text(
                date.replace('T', ' ').take(19),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    modifier: Modifier = Modifier
) {
    val firstImage = item.images.firstOrNull()
    val imageUrl by rememberResolvedImageUrl(firstImage, supabaseClient)
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF3F2F9))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.foodemptylist),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.brand ?: "Unknown Brand", fontSize = 14.sp)
            Text(item.name ?: "Unknown Name", fontSize = 13.sp, color = Color.Gray)
            val rawDate = item.createdAt ?: ""
            val date = if (rawDate.isNotBlank()) {
                try {

                    val parsed = LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME)
                    parsed.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))

                } catch (e: Exception) {
                    rawDate
                }
            } else " "

            if (date.isNotBlank()) Text(
                date.replace('T', ' ').take(19),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun rememberResolvedImageUrl(
    first: ImageLocationInfo?,
    supabaseClient: io.github.jan.supabase.SupabaseClient
): State<String?> {
    return produceState<String?>(initialValue = first?.url) {
        if (value.isNullOrBlank() && first?.imageFileHash != null) {
            val bucket = supabaseClient.storage.from("productimages")
            value = try {
                bucket.publicUrl(first.imageFileHash!!)
            } catch (e: Exception) {
                null
            }
            if (value.isNullOrBlank()) {
                value = try {
                    bucket.createSignedUrl(first.imageFileHash!!, 3600.seconds)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchHistoryView(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    results: List<HistoryItem>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    supabaseClient: io.github.jan.supabase.SupabaseClient
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pull = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .pullRefresh(pull)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search history") },
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            androidx.compose.material3.TextButton(onClick = onBack) {
                Text(
                    "Cancel",
                    color = Color(0xFF2B7A0B)
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (searchText.isNotBlank() && results.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No Results",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(results) { item ->
                    HistoryItemCard(
                        item = item,
                        supabaseClient = supabaseClient,
                        modifier = Modifier.clickable { onItemClick(item) })
                    Divider(color = Color(0xFFF3F2F9), thickness = 2.dp)
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pull,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
