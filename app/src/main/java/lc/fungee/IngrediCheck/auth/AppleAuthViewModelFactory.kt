package lc.fungee.IngrediCheck.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppleAuthViewModelFactory(
    private val repository: AppleAuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppleAuthViewModel::class.java)) {
            return AppleAuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
