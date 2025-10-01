package lc.fungee.IngrediCheck.model.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class Ingredient(
    val name: String? = null,
    /** Backend returns categorical values (e.g., yes/maybe/no) so keep String */
    val vegan: String? = null,
    /** Backend returns categorical values (e.g., yes/maybe/no) so keep String */
    val vegetarian: String ? = null,
    val ingredients: List<Ingredient> = emptyList()
)

enum class SafetyRecommendation { MaybeUnsafe, DefinitelyUnsafe, Safe, None }
enum class ProductRecommendation { Match, NeedsReview, NotMatch }

@Serializable
data class IngredientRecommendation(
    @SerialName("ingredientName") val ingredientName: String,
    @SerialName("safetyRecommendation") val safetyRecommendation: SafetyRecommendation,
    val reasoning: String,
    val preference: String
)

@Serializable
data class ImageLocationInfo(
    @SerialName("url") val url: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    // Support multiple server shapes:
    // - image_file_hash (snake_case)
    // - imageFileHash (camelCase)
    // - "image File Hash" (spaced; seen in some payloads)
    @SerialName("image_file_hash") val imageFileHashSnake: String? = null,
    @SerialName("imageFileHash") val imageFileHashCamel: String? = null,
    @SerialName("image File Hash") val imageFileHashSpaced: String? = null
)

/** Unified accessor used across UI code for different backend shapes */
val ImageLocationInfo.imageFileHash: String?
    get() = imageFileHashSnake ?: imageFileHashCamel ?: imageFileHashSpaced

@Serializable
data class Product(
    val barcode: String? = null,
    val name: String ? = null,
    val brand: String? = null,

    val ingredients: List<Ingredient>,
    val images: List<ImageLocationInfo> = emptyList()
)


// Recursive search to determine if the product's ingredient tree contains the given name (case-insensitive)
private fun containsIngredientName(list: List<Ingredient>, keyword: String): Boolean {
    for (ing in list) {
        val n = ing.name
        if (n != null && n.contains(keyword, ignoreCase = true)) return true
        if (ing.ingredients.isNotEmpty() && containsIngredientName(ing.ingredients, keyword)) return true
    }
    return false
}

fun Product.productHasIngredient(ingredientName: String): Boolean =
    containsIngredientName(this.ingredients, ingredientName)

// Determine overall product recommendation based on matching recommendations
fun Product.calculateMatch(recommendations: List<IngredientRecommendation>): ProductRecommendation {
    // Trust backend analysis which already considers the scanned product.
    // Do not perform local fuzzy matching against ingredient names here to avoid missing synonyms (e.g., sugar vs sucrose).
    return when {
        recommendations.any { it.safetyRecommendation == SafetyRecommendation.DefinitelyUnsafe } ->
            ProductRecommendation.NotMatch
        recommendations.any { it.safetyRecommendation == SafetyRecommendation.MaybeUnsafe } ->
            ProductRecommendation.NeedsReview
        else -> ProductRecommendation.Match
    }
}

private data class AnnotatedIngredient(
    val name: String,
    val safetyRecommendation: SafetyRecommendation,
    val reasoning: String?,
    val preference: String?,
    val ingredients: List<AnnotatedIngredient>
)

@Serializable
data class DecoratedIngredientListFragment(
    val fragment: String,
    val safetyRecommendation: SafetyRecommendation,
    val reasoning: String?,
    val preference: String?
)

/**
 * Decorate ingredient list with highlights for unsafe/maybe-unsafe ingredients.
 * This mirrors the iOS logic so UI can render colored spans reliably.
 */
fun decoratedIngredientsList(
    ingredients: List<Ingredient>,
    ingredientRecommendations: List<IngredientRecommendation>?
): List<DecoratedIngredientListFragment> {

    fun String.capitalized(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun annotate(list: List<Ingredient>): List<AnnotatedIngredient> {
        return list.map { ingredient ->
            val name = ingredient.name ?: ""
            val rec = ingredientRecommendations?.firstOrNull { r ->
                name.contains(r.ingredientName, ignoreCase = true)
            }
            if (rec != null) {
                AnnotatedIngredient(
                    name = name,
                    safetyRecommendation = rec.safetyRecommendation,
                    reasoning = rec.reasoning,
                    preference = rec.preference,
                    ingredients = annotate(ingredient.ingredients)
                )
            } else {
                AnnotatedIngredient(
                    name = name,
                    safetyRecommendation = SafetyRecommendation.Safe,
                    reasoning = null,
                    preference = null,
                    ingredients = annotate(ingredient.ingredients)
                )
            }
        }
    }

    fun decoratedFragmentsFromAnnotated(list: List<AnnotatedIngredient>): List<DecoratedIngredientListFragment> {
        val out = mutableListOf<DecoratedIngredientListFragment>()
        list.forEachIndexed { index, ai ->
            if (ai.ingredients.isEmpty()) {
                var fragment = ai.name.capitalized()
                if (index != list.lastIndex) fragment += ", "
                out += DecoratedIngredientListFragment(
                    fragment = fragment,
                    safetyRecommendation = ai.safetyRecommendation,
                    reasoning = ai.reasoning,
                    preference = ai.preference
                )
            } else {
                out += DecoratedIngredientListFragment(
                    fragment = ai.name.capitalized() + " ",
                    safetyRecommendation = ai.safetyRecommendation,
                    reasoning = ai.reasoning,
                    preference = ai.preference
                )
                out += DecoratedIngredientListFragment(
                    fragment = "(",
                    safetyRecommendation = SafetyRecommendation.None,
                    reasoning = null,
                    preference = null
                )
                val sub = decoratedFragmentsFromAnnotated(ai.ingredients).toMutableList()
                val suffix = if (index == list.lastIndex) ")" else "), "
                val last = sub.last()
                sub[sub.lastIndex] = last.copy(fragment = last.fragment + suffix)
                out += sub
            }
        }
        return out
    }

    fun splitStringPreservingSpaces(input: String): List<String> {
        val parts = input.split(' ')
        val result = mutableListOf<String>()
        for ((idx, part) in parts.withIndex()) {
            if (part.isEmpty()) continue
            val value = if (idx != parts.lastIndex) "$part " else part
            result += value
        }
        return result
    }

    fun splitDecoratedFragmentsIfNeeded(
        decorated: List<DecoratedIngredientListFragment>
    ): List<DecoratedIngredientListFragment> {
        val result = mutableListOf<DecoratedIngredientListFragment>()
        for (f in decorated) {
            if (f.safetyRecommendation == SafetyRecommendation.Safe) {
                // Split safe fragments into word pieces for better wrapping
                for (word in splitStringPreservingSpaces(f.fragment)) {
                    result += f.copy(fragment = word, reasoning = null, preference = null)
                }
            } else {
                // Keep unsafe/maybe-unsafe as a whole to show one continuous highlight
                result += f
            }
        }
        return result
    }

    val annotated = annotate(ingredients)
    val decorated = decoratedFragmentsFromAnnotated(annotated)
    return splitDecoratedFragmentsIfNeeded(decorated)
}