package lc.fungee.IngrediCheck.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.entities.ImageInfo
import lc.fungee.IngrediCheck.model.entities.IngredientRecommendation
import lc.fungee.IngrediCheck.model.entities.Product
import lc.fungee.IngrediCheck.model.entities.calculateMatch
import lc.fungee.IngrediCheck.model.repository.AnalysisRepository
import lc.fungee.IngrediCheck.model.repository.ListTabRepository
import lc.fungee.IngrediCheck.model.repository.PreferenceRepository
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

    // Replaces isLoading with a richer phase to mirror iOS flow
    var phase by mutableStateOf(AnalysisPhase.Idle)
        private set

    private val analysisRepo by lazy { AnalysisRepository(repo, functionsBaseUrl, anonKey) }
    private val listRepo by lazy { ListTabRepository(repo, functionsBaseUrl, anonKey) }

    fun analyzeBarcode(clientActivityId: String, barcode: String) {
        viewModelScope.launch {
            phase = AnalysisPhase.LoadingProduct
            error = null
            recommendations = emptyList()
            try {
                Log.d("AnalysisVM", "Start analyze, barcode=$barcode")
                // 1) Fetch product details
                val prod = analysisRepo.fetchProduct(barcode, clientActivityId)
                product = prod
                Log.d("AnalysisVM", "Product loaded: ${prod.ingredients}")

                // Switch to analyzing while backend computes recommendations
                phase = AnalysisPhase.Analyzing
                // 3) Load user preferences (fresh from server if possible)
                val freshPrefs = runCatching { repo.fetchAndStore() }.getOrNull()
                val prefs = freshPrefs ?: repo.getLocal().first()
                val prefsText = if (prefs.isEmpty()) "" else prefs.joinToString("\n") { it.text }

                // 4) Fetch ingredient recommendations from backend
                val recs = analysisRepo.fetchRecommendations(clientActivityId, prefsText, barcode)
                recommendations = recs

                // 5) Done
                phase = AnalysisPhase.Done
            } catch (e: Exception) {
                Log.e("AnalysisVM", "Error analyzing product", e)
                phase = AnalysisPhase.Error
                error = e.message
            }
        }
    }

    fun analyzeImages(clientActivityId: String, images: List<ImageInfo>) {
        viewModelScope.launch {
            phase = AnalysisPhase.LoadingProduct
            error = null
            recommendations = emptyList()
            try {
                Log.d("AnalysisVM", "Start analyzeImages, images count=${images.size}")
                // 1) Extract product details from label images
                val prod = analysisRepo.fetchProductFromImages(images, clientActivityId)
                product = prod

                // 2) Switch to analyzing while backend computes recommendations
                phase = AnalysisPhase.Analyzing

                // 3) Load user preferences
                val freshPrefs = runCatching { repo.fetchAndStore() }.getOrNull()
                val prefs = freshPrefs ?: repo.getLocal().first()
                val prefsText = if (prefs.isEmpty()) "" else prefs.joinToString("\n") { it.text }

                // 4) Fetch ingredient recommendations; barcode might be absent
                val recs = analysisRepo.fetchRecommendations(clientActivityId, prefsText, product?.barcode)

                recommendations = recs
                phase = AnalysisPhase.Done
            } catch (e: Exception) {
                Log.e("AnalysisVM", "AnalyzeImages error", e)
                error = e.message
                phase = AnalysisPhase.Error
            }
        }
    }

    // Favorites toggle through repository
    suspend fun toggleFavoriteByClientActivity(clientActivityId: String, set: Boolean): Boolean {
        return if (set) listRepo.addToFavorites(clientActivityId) else listRepo.removeFromFavoritesByClientActivity(clientActivityId)
    }
}
