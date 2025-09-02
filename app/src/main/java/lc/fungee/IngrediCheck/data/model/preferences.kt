sealed class ValidationResult {
    object Idle : ValidationResult()
    object Validating : ValidationResult()
    object Success : ValidationResult()
    data class Failure(val message: String) : ValidationResult()
}