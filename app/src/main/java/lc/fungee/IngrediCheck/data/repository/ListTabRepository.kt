package lc.fungee.IngrediCheck.data.repository

import android.util.Log
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.data.model.ImageLocationInfo
import lc.fungee.IngrediCheck.data.model.Ingredient
import lc.fungee.IngrediCheck.data.model.IngredientRecommendation
import lc.fungee.IngrediCheck.data.model.SafeEatsEndpoint
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class HistoryItem(
    @SerialName("client_activity_id") val clientActivityId: String,
    val barcode: String? = null,
    val name: String? = null,
    val brand: String? = null,
    val images: List<ImageLocationInfo> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    @SerialName("ingredient_recommendations") val ingredientRecommendations: List<IngredientRecommendation> = emptyList(),
    val favorited: Boolean = false,
    val rating: Int? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class FavoriteItem(
    @SerialName("list_item_id") val listItemId: String,
    val barcode: String? = null,
    val name: String? = null,
    val brand: String? = null,
    val images: List<ImageLocationInfo> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)

class ListTabRepository(
    private val prefRepo: PreferenceRepository,
    private val functionsBaseUrl: String,
    private val anonKey: String
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchHistory(searchText: String? = null): List<HistoryItem> = withContext(Dispatchers.IO) {
        val token = prefRepo.currentToken() ?: throw Exception("Not authenticated")
        val base = "$functionsBaseUrl/${SafeEatsEndpoint.HISTORY.format()}"
        val url = if (!searchText.isNullOrBlank())
            base + "?searchText=" + URLEncoder.encode(searchText, Charsets.UTF_8.name())
        else base
        Log.d("ListTabRepo", "GET $url")

        val req = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
            .build()

        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            Log.d("ListTabRepo", "History code=${resp.code}, body=${body.take(200)}")
            when (resp.code) {
                200 -> if (body.isBlank()) emptyList() else json.decodeFromString(
                    ListSerializer(HistoryItem.serializer()), body
                )
                204 -> emptyList()
                401 -> throw Exception("Authentication failed. Please log in again.")
                else -> throw Exception("Failed to fetch history: ${resp.code} ${body}")
            }
        }
    }

    suspend fun getFavorites(listId: String = DEFAULT_FAVORITES_LIST_ID): List<FavoriteItem> = withContext(Dispatchers.IO) {
        val token = prefRepo.currentToken() ?: throw Exception("Not authenticated")
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.LIST_ITEMS.format(listId)}"
        Log.d("ListTabRepo", "GET $url")

        val req = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
            .build()

        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            Log.d("ListTabRepo", "Favorites code=${resp.code}, body=${body.take(200)}")
            when (resp.code) {
                200 -> if (body.isBlank()) emptyList() else json.decodeFromString(
                    ListSerializer(FavoriteItem.serializer()), body
                )
                204 -> emptyList()
                401 -> throw Exception("Authentication failed. Please log in again.")
                else -> throw Exception("Failed to fetch favorites: ${resp.code} ${body}")
            }
        }
    }

    companion object {
        // Confirm with backend; iOS uses a Favorites list. Adjust if a specific UUID is required.
        const val DEFAULT_FAVORITES_LIST_ID: String = "favorites"
    }
}
