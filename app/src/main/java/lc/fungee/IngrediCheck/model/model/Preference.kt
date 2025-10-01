package lc.fungee.IngrediCheck.model.model

import kotlinx.serialization.Serializable


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

