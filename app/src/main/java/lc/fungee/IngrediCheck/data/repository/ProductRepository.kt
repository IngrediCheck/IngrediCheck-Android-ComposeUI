package lc.fungee.IngrediCheck.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lc.fungee.IngrediCheck.data.model.IngredientRecommendation
import lc.fungee.IngrediCheck.data.model.Product
import lc.fungee.IngrediCheck.data.model.SafeEatsEndpoint
import lc.fungee.IngrediCheck.data.model.ImageInfo
import lc.fungee.IngrediCheck.data.model.calculateMatch
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import android.util.Log
 
// Two-stage analysis phase indicator for UI
enum class AnalysisPhase { Idle, LoadingProduct, Analyzing, Done, Error }

class AnalysisViewModel(
    private val repo: PreferenceRepository,
    private val functionsBaseUrl: String,
    private val anonKey: String
) : ViewModel() {

    var product by mutableStateOf<Product?>(null)
        private set

    var recommendations by mutableStateOf<List<IngredientRecommendation>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Replaces isLoading with a richer phase to mirror iOS flow
    var phase by mutableStateOf(AnalysisPhase.Idle)
        private set

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    fun analyzeBarcode(clientActivityId: String, barcode: String) {
        viewModelScope.launch {
            phase = AnalysisPhase.LoadingProduct
            error = null
            recommendations = emptyList()
            try {
                Log.d("AnalysisVM", "Start analyze, barcode=$barcode")
                // 1) Fetch product details
                val prod = fetchProduct(barcode, clientActivityId)
                product = prod
                Log.d("AnalysisVM", "Product loaded: ${prod.ingredients}")

                // Switch to analyzing while backend computes recommendations
                phase = AnalysisPhase.Analyzing

                // 2) Ensure we use the latest dietary prefs
                val freshPrefs = runCatching { repo.fetchAndStore() }.getOrNull()
                val prefs = freshPrefs ?: repo.getLocal().first()
                Log.d("AnalysisVM", "Fetched ${prefs.size} preferences from repo")
                
                val prefsText = if (prefs.isEmpty()) "" else prefs.joinToString("\n") { it.text }
                if (prefsText.isBlank()) {
                    Log.d("AnalysisVM", "No local prefs found → rely on server-stored preferences")
                } else {
                    Log.d("AnalysisVM", "Using prefsText=\n$prefsText")
                    Log.d("AnalysisVM", "Individual prefs: ${prefs.map { "ID:${it.id} TEXT:'${it.text}'" }}")
                }

                // 3) Call analyze endpoint
                val recs = fetchRecommendations(clientActivityId, prefsText, barcode)
                Log.d("AnalysisVM", "Recs size: ${recs.size}")

                if (recs.isEmpty()) {
                    Log.w("AnalysisVM", "Backend returned EMPTY recommendations list - this may indicate:")
                    Log.w("AnalysisVM", "1. No preferences found on server")
                    Log.w("AnalysisVM", "2. Product ingredients don't match any preferences")
                    Log.w("AnalysisVM", "3. Backend analysis failed silently")
                } else {
                    // DEBUG: Log each recommendation
                    recs.forEachIndexed { index, rec ->
                        Log.d("AnalysisVM-DEBUG", "Rec[$index]: ingredient=${rec.ingredientName}, safety=${rec.safetyRecommendation}, preference=${rec.preference}")
                    }
                }

                // DEBUG: Log final status
                val status = product?.calculateMatch(recs)
                Log.d("AnalysisVM-DEBUG", "Final status: $status (based on ${recs.size} recommendations)")

                recommendations = recs
                phase = AnalysisPhase.Done

            } catch (e: Exception) {
                Log.e("AnalysisVM", "Analyze error", e)
                error = e.message
                phase = AnalysisPhase.Error
            }
        }
    }

    private suspend fun fetchProductFromImages(
        images: List<ImageInfo>,
        clientActivityId: String
    ): Product = withContext(Dispatchers.IO) {
        val token = repo.currentToken() ?: throw Exception("Not authenticated")
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.EXTRACT.format()}"
        Log.d("AnalysisVM", "POST $url (extract)")

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
            Log.d("AnalysisVM", "Extract code=${resp.code}, body=${body.take(200)}")
            if (resp.code == 401) throw Exception("Authentication failed. Please log in again.")
            if (!resp.isSuccessful) throw Exception("Failed to extract product: ${resp.code}")
            Json.decodeFromString(Product.serializer(), body)
        }
    }

    /**
     * New flow: analyze a set of label images (uploaded separately) using /extract first,
     * then fetch ingredient recommendations via /analyze with optional barcode (if available).
     */
    fun analyzeImages(clientActivityId: String, images: List<ImageInfo>) {
        viewModelScope.launch {
            phase = AnalysisPhase.LoadingProduct
            error = null
            recommendations = emptyList()
            try {
                Log.d("AnalysisVM", "Start analyzeImages, images count=${images.size}")
                // 1) Extract product details from label images
                val prod = fetchProductFromImages(images, clientActivityId)
                product = prod

                // 2) Switch to analyzing while backend computes recommendations
                phase = AnalysisPhase.Analyzing

                // 3) Load user preferences
                val freshPrefs = runCatching { repo.fetchAndStore() }.getOrNull()
                val prefs = freshPrefs ?: repo.getLocal().first()
                val prefsText = if (prefs.isEmpty()) "" else prefs.joinToString("\n") { it.text }

                // 4) Fetch ingredient recommendations; barcode might be absent
                val recs = fetchRecommendations(clientActivityId, prefsText, product?.barcode)

                recommendations = recs
                phase = AnalysisPhase.Done
            } catch (e: Exception) {
                Log.e("AnalysisVM", "AnalyzeImages error", e)
                error = e.message
                phase = AnalysisPhase.Error
            }
        }
    }

    private suspend fun fetchProduct(barcode: String, clientActivityId: String): Product =
        withContext(Dispatchers.IO) {
            val token = repo.currentToken() ?: throw Exception("Not authenticated")
            val url = "$functionsBaseUrl/${SafeEatsEndpoint.INVENTORY.format(barcode)}?clientActivityId=$clientActivityId"
            Log.d("AnalysisVM", "GET $url")

            val req = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("apikey", anonKey)
                .build()

            client.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                Log.d("AnalysisVM","Json response =$body")
                Log.d("AnalysisVM", "Product code=${resp.code}, body=${body.take(200)}")
                if (resp.code == 401) throw Exception("Authentication failed. Please log in again.")
                if (!resp.isSuccessful) throw Exception("Failed to fetch product: ${resp.code}")
                Json.decodeFromString(Product.serializer(), body)
            }
        }

    private suspend fun fetchRecommendations(
        clientActivityId: String,
        prefs: String,
        barcode: String? = null
    ): List<IngredientRecommendation> = withContext(Dispatchers.IO) {
        val token = repo.currentToken() ?: throw Exception("Not authenticated")
        val url = "$functionsBaseUrl/${SafeEatsEndpoint.ANALYZE.format()}"
        Log.d("AnalysisVM", "POST $url")

        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("clientActivityId", clientActivityId)
            .apply {
                if (!barcode.isNullOrBlank()) {
                    addFormDataPart("barcode", barcode)
                }
            }
        
        // Only send userPreferenceText if we have actual preferences
        if (prefs.isNotBlank()) {
            multipartBuilder.addFormDataPart("userPreferenceText", prefs)
            Log.d("AnalysisVM", "Sending userPreferenceText to backend")
        } else {
            Log.d("AnalysisVM", "Omitting userPreferenceText - backend will use server-stored preferences")
        }
        
        val multipart = multipartBuilder.build()

        val req = Request.Builder()
            .url(url)
            .post(multipart)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", anonKey)
            .build()

        client.newCall(req).execute().use { resp ->
            try {
                val code = resp.code
                val body = resp.body?.string().orEmpty()
                Log.d("AnalysisVM", "Analyze code=$code, body=${body.take(200)}")
                if (code == 401) throw Exception("Authentication failed. Please log in again.")
                if (code !in listOf(200, 204)) throw Exception("Analyze failed: $code")
                if (body.isBlank()) emptyList()
                else Json.decodeFromString(ListSerializer(IngredientRecommendation.serializer()), body)
            } catch (e: Exception) {
                Log.e("AnalysisVM", "Error reading response", e)
                throw e
            }
        }
    }
}
