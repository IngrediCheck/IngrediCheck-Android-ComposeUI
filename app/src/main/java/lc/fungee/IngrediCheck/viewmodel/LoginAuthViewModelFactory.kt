package lc.fungee.IngrediCheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import lc.fungee.IngrediCheck.model.repository.LoginAuthRepository

class LoginAuthViewModelFactory(
    private val repository: LoginAuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppleAuthViewModel::class.java)) {
            return AppleAuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}