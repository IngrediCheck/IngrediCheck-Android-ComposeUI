package lc.fungee.IngrediCheck.model.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.model.entities.ImageInfo
import lc.fungee.IngrediCheck.model.entities.IngredientRecommendation
import lc.fungee.IngrediCheck.model.entities.Product
import lc.fungee.IngrediCheck.model.entities.SafeEatsEndpoint
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * AnalysisRepository encapsulates network calls for analysis features using OkHttp/Json.
 * This avoids networking in the ViewModel and keeps MVVM without adding Retrofit/Room.
 */
class AnalysisRepository(
    private val preferenceRepository: PreferenceRepository,
    private val functionsBaseUrl: String,
    private val anonKey: String,
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    suspend fun fetchProduct(barcode: String, clientActivityId: String): Product =
        withContext(Dispatchers.IO) {
            val token = preferenceRepository.currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.INVENTORY.format(barcode)}?clientActivityId=$clientActivityId"
            Log.d("AnalysisRepo", "GET $url")

            val req = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()

            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                Log.d("AnalysisRepo", "Product code=${resp.code}, body=${body.take(200)}")
                if (resp.code == 401) throw Exception("Authentication failed. Please log in again.")
                if (!resp.isSuccessful) throw Exception("Failed to fetch product: ${resp.code}")
                json.decodeFromString(Product.serializer(), body)
            }
        }

    suspend fun fetchProductFromImages(
        images: List<ImageInfo>,
        clientActivityId: String
    ): Product = withContext(Dispatchers.IO) {
        val token = preferenceRepository.currentToken() ?: throw Exception("Not authenticated")
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.EXTRACT.format()}"
        Log.d("AnalysisRepo", "POST $url (extract)")

        val imagesJson = Json.encodeToString(ListSerializer(ImageInfo.serializer()), images)
        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("clientActivityId", clientActivityId)
            .addFormDataPart("productImages", imagesJson)
            .build()

        val req = Request.Builder()
            .url(url)
            .post(multipart)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
            .build()

        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            Log.d("AnalysisRepo", "Extract code=${resp.code}, body=${body.take(200)}")
            if (resp.code == 401) throw Exception("Authentication failed. Please log in again.")
            if (!resp.isSuccessful) throw Exception("Failed to extract product: ${resp.code}")
            json.decodeFromString(Product.serializer(), body)
        }
    }

    suspend fun fetchRecommendations(
        clientActivityId: String,
        prefs: String,
        barcode: String? = null
    ): List<IngredientRecommendation> = withContext(Dispatchers.IO) {
        val token = preferenceRepository.currentToken() ?: throw Exception("Not authenticated")
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.ANALYZE.format()}"
        Log.d("AnalysisRepo", "POST $url")

        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("clientActivityId", clientActivityId)
            .apply {
                if (!barcode.isNullOrBlank()) {
                    addFormDataPart("barcode", barcode)
                }
            }

        if (prefs.isNotBlank()) {
            multipartBuilder.addFormDataPart("userPreferenceText", prefs)
            Log.d("AnalysisRepo", "Sending userPreferenceText to backend")
        } else {
            Log.d("AnalysisRepo", "Omitting userPreferenceText - backend will use server-stored preferences")
        }

        val multipart = multipartBuilder.build()

        val req = Request.Builder()
            .url(url)
            .post(multipart)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
            .build()

        client.newCall(req).execute().use { resp ->
            val code = resp.code
            val body = resp.body?.string().orEmpty()
            Log.d("AnalysisRepo", "Analyze code=$code, body=${body.take(200)}")
            if (code == 401) throw Exception("Authentication failed. Please log in again.")
            if (code !in listOf(200, 204)) throw Exception("Analyze failed: $code")
            if (body.isBlank()) emptyList()
            else json.decodeFromString(ListSerializer(IngredientRecommendation.serializer()), body)
        }
    }
}
