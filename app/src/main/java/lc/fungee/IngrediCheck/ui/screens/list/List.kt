// Updated: app/src/main/java/lc/fungee/IngrediCheck/PreferenceList/List.kt
package lc.fungee.IngrediCheck.ui.screens.list
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
// produceState no longer needed in this file
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.material.pullrefresh.*

import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect

import kotlinx.serialization.json.Json
import java.net.URLEncoder
import com.google.gson.Gson

import lc.fungee.IngrediCheck.auth.AppleAuthViewModel
import lc.fungee.IngrediCheck.data.model.SupabaseSession
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.data.repository.FavoriteItem
import lc.fungee.IngrediCheck.data.repository.HistoryItem

import lc.fungee.IngrediCheck.data.repository.ListTabRepository
import lc.fungee.IngrediCheck.data.repository.ListTabViewModel
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.ui.component.BottomBar
import lc.fungee.IngrediCheck.ui.screens.check.CheckBottomSheet
import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
import lc.fungee.IngrediCheck.ui.theme.White

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: AppleAuthViewModel,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String
) {
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionJson = remember {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            .getString("session", null)
    }


    val session = remember {
        sessionJson?.let {
            Gson().fromJson(it, SupabaseSession::class.java)
        }
    }

    // Repo + VM for Lists tab
    val prefRepo = remember { PreferenceRepository(context, supabaseClient, functionsBaseUrl, anonKey) }
    val listRepo = remember { ListTabRepository(prefRepo, functionsBaseUrl, anonKey) }
    val listVm = remember { ListTabViewModel(listRepo) }
    val ui by listVm.uiState.collectAsState()
    LaunchedEffect(Unit) {
        { listVm.refreshAll() }
    }
//    val isRefreshing = ui.isLoadingFavorites || ui.isLoadingHistory
//    val pullRefreshState = rememberPullRefreshState(
//        refreshing = isRefreshing,
//        onRefresh = { listVm.refreshAll() }
//    )
    val favoritesRefreshState = rememberPullRefreshState(
        refreshing = ui.isLoadingFavorites,
        onRefresh = { listVm.refreshFavorites() }
    )

    val historyRefreshState = rememberPullRefreshState(
        refreshing = ui.isLoadingHistory,
        onRefresh = { listVm.refreshHistory() }
    )

    Scaffold(
        bottomBar = { BottomBar(navController = navController, onCheckClick = { showSheet = true }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(White)
                .fillMaxSize()
//                .pullRefresh(pullRefreshState)
        ) {
            if (ui.isSearching) {
                // Search screen overlay
                SearchHistoryView(
                    searchText = ui.searchText,
                    onSearchTextChange = { listVm.setSearchText(it) },
                    results = ui.searchResults,
                    onBack = { listVm.setSearching(false) },
                    onRefresh = { listVm.refreshSearch() },
                    onItemClick = { item ->
                        val json = URLEncoder.encode(
                            Json.encodeToString(HistoryItem.serializer(), item),
                            "UTF-8"
                        )
                        navController.navigate("historyItem?item=$json")
                    },
                    supabaseClient = supabaseClient
                )
            } else {
                // Default Lists tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Text(
                        text = "Lists",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            letterSpacing = (-0.41).sp,
                            color = Color(0xFF1B270C),
                            lineHeight = 22.sp
                        )
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    // Favorites section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pullRefresh(favoritesRefreshState)
                    ) {
                        FavoritesSection(
                            favorites = ui.favorites,
                            isLoading = ui.isLoadingFavorites,
                            onViewAll = { navController.navigate("favoritesAll") },
                            onItemClick = { item ->
                                val json = URLEncoder.encode(
                                    Json.encodeToString(FavoriteItem.serializer(), item),
                                    "UTF-8"
                                )
                                navController.navigate("favoriteItem?item=$json")
                            },
                            supabaseClient = supabaseClient
                        )
                        PullRefreshIndicator(
                            refreshing = ui.isLoadingFavorites,
                            state = favoritesRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                        Spacer(modifier = Modifier.height(24.dp))

                    // Recent Scans section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pullRefresh(historyRefreshState)
                    ) {
                    RecentScansSection(
                        history = ui.history,
                        isLoading = ui.isLoadingHistory,
                        onViewAll = { navController.navigate("recentScansAll") },
                        onSearch = { listVm.setSearching(true) },
                        onItemClick = { item ->
                            val json = URLEncoder.encode(
                                Json.encodeToString(HistoryItem.serializer(), item),
                                "UTF-8"
                            )
                            navController.navigate("historyItem?item=$json")
                        },
                        supabaseClient = supabaseClient
                    )
                        PullRefreshIndicator(
                            refreshing = ui.isLoadingHistory,
                            state = historyRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

//            PullRefreshIndicator(
////                refreshing = isRefreshing,
////                state = pullRefreshState,
//                modifier = Modifier.align(Alignment.TopCenter)
//            )
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

@Composable
private fun FavoritesSection(
    favorites: List<FavoriteItem>?,
    isLoading: Boolean,
    onViewAll: () -> Unit,
    onItemClick: (FavoriteItem) -> Unit,
    supabaseClient: io.github.jan.supabase.SupabaseClient
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorites",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color(0xFF1B270C),
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!favorites.isNullOrEmpty()) {
                TextButton(onClick = onViewAll) { Text("View all", color = PrimaryGreen100) }
            }
        }
        Spacer(Modifier.height(8.dp))

        when {
            isLoading && favorites == null -> {
                // Use the screen-level PullRefreshIndicator; keep space without inline spinner
                Box(Modifier.fillMaxWidth().height(130.dp)) { }
            }
            favorites == null -> {
                // Initial load state
                Box(Modifier.fillMaxWidth().height(130.dp)) {}
            }
            favorites.isEmpty() -> {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp), // force some height
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.foodemptylist),
                        contentDescription = "Empty Favorites",
                        modifier = Modifier
                            .size(width = 120.dp, height = 120.dp)
                    )
                    Spacer(Modifier.width(12.dp))
//                    Text(
//                        "No Favorite products yet",
//                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Gray)
//                    )
                }
            }
            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(min = 130.dp)
                ) {
                    items(favorites) { item ->
                        FavoriteItemBirdsEyeCard(
                            item = item,
                            supabaseClient = supabaseClient,
                            modifier = Modifier.clickable { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentScansSection(
    history: List<HistoryItem>?,
    isLoading: Boolean,
    onViewAll: () -> Unit,
    onSearch: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    supabaseClient: io.github.jan.supabase.SupabaseClient
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Scans",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp,
                    color = Color(0xFF1B270C),
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!history.isNullOrEmpty() && history.size > 4) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onViewAll) { Text("View all", color = PrimaryGreen100) }

//                    IconButton(onClick = onSearch) {
//                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        when {
            isLoading && history == null -> {
                Box(Modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
                }
            }
            history == null -> {
                Box(Modifier.fillMaxWidth().height(130.dp)) {}
            }
            history.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.emptyrecentscan),
                        contentDescription = "No Scans",
                        modifier = Modifier.size(width = 174.dp, height = 134.dp)
                    )
//                    Text(
//                        "No products scanned yet",
//                        style = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color.Gray)
//                    )
                }
            }
            else -> {

                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(history) { item ->
                        HistoryItemCard(
                            item = item,
                            supabaseClient = supabaseClient,
                            modifier = Modifier.clickable { onItemClick(item) }
                        )
                        Divider(color = Color(0xFFF3F2F9), thickness = 2.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemBirdsEyeCard(
    item: FavoriteItem,
    supabaseClient: io.github.jan.supabase.SupabaseClient,
    modifier: Modifier = Modifier
) {
    val firstImage = item.images.firstOrNull()
    val imageUrl by rememberResolvedImageUrl(firstImage, supabaseClient)
    Box(
        modifier = modifier
            .size(width = 120.dp, height = 120.dp)
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
                contentDescription = "Placeholder",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Use shared HistoryItemCard, SearchHistoryView, rememberResolvedImageUrl from FavoritesRecentScreens.kt
