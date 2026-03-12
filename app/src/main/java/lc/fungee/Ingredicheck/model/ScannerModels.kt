package lc.fungee.Ingredicheck.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanResponse(
    val id: String,
    val state: String,
    val barcode: String? = null,
    @SerialName("product_info")
    val productInfo: ProductInfo? = null,
    @SerialName("analysis_result")
    val analysisResult: AnalysisResult? = null,
    val images: List<ScanImage> = emptyList(),
    val error: String? = null,
    @SerialName("latest_guidance")
    val latestGuidance: String? = null
)

@Serializable
data class ScanImage(
    val url: String,
    val type: String? = null
)

@Serializable
data class ProductInfo(
    val name: String? = null,
    val brand: String? = null,
    val claims: List<String> = emptyList(),
    val ingredients: List<Ingredient> = emptyList()
)

@Serializable
data class Ingredient(
    val name: String,
    val contains: List<Ingredient> = emptyList()
)

@Serializable
data class AnalysisResult(
    val id: String? = null,
    @SerialName("overall_match")
    val overallMatch: String? = null,
    @SerialName("ingredient_analysis")
    val ingredientAnalysis: List<IngredientAnalysis> = emptyList(),
    val error: String? = null,
    @SerialName("is_stale")
    val isStale: Boolean = false
)

@Serializable
data class IngredientAnalysis(
    val ingredient: String,
    val match: String
)
