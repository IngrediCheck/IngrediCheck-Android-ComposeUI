package lc.fungee.IngrediCheck.data.model

sealed class ValidationResult {
    data object Idle : ValidationResult()
    data object Validating : ValidationResult()
    data object Success : ValidationResult()
    data class Failure(val message: String) : ValidationResult()
}



