package lc.fungee.IngrediCheck.data.model

import io.github.jan.supabase.SupabaseClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class DietaryPreference(
    val text: String,
    val annotatedText: String,
    val id: Int
)

sealed class PreferenceValidationResult {
    data class Success(val pref: DietaryPreference) : PreferenceValidationResult()
    data class Failure(val explanation: String) : PreferenceValidationResult()
}

sealed class ValidationState {
    object Idle : ValidationState()
    object Validating : ValidationState()
    object Success : ValidationState()
    data class Failure(val message: String) : ValidationState()
}
//sealed class CheckSheetState {
//    object Scanner : CheckSheetState()
//    data class Analysis(
//        val barcode: String,
//        val supabaseClient: io.github.jan.supabase.SupabaseClient,
//        val functionsBaseUrl: String,
//        val anonKey: String
//    ) : CheckSheetState()
//}
sealed class CheckSheetState {
    object Scanner : CheckSheetState()
    data class Analysis(
        val barcode: String? = null,
        val images: List<ImageInfo>? = null
    ) : CheckSheetState()
}
