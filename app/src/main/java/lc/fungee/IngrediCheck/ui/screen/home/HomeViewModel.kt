package lc.fungee.IngrediCheck.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.data.model.DietaryPreference
import lc.fungee.IngrediCheck.data.model.ValidationResult
import lc.fungee.IngrediCheck.data.repository.PreferenceRepository
import lc.fungee.IngrediCheck.data.repository.PreferenceRepositoryImpl
import lc.fungee.IngrediCheck.data.source.SessionLocalDataSource

data class HomeUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val validation: ValidationResult = ValidationResult.Idle,
    val preferences: List<DietaryPreference> = emptyList()
)

class HomeViewModel(
    private val context: Context,
    private val repo: PreferenceRepository
) : ViewModel() {

    private val sessionLocal = SessionLocalDataSource(context)

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    fun onInputChange(value: String) {
        _state.value = _state.value.copy(input = value, validation = ValidationResult.Idle)
    }

    fun load() {
        val userId = sessionLocal.getUserId() ?: return
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val items = repo.getPreferencesForUser(userId)
                _state.value = _state.value.copy(preferences = items, isLoading = false)
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoading = false, validation = ValidationResult.Failure("Failed to load"))
            }
        }
    }

    fun save() {
        val text = _state.value.input.trim()
        if (text.isEmpty()) return
        val userId = sessionLocal.getUserId() ?: run {
            _state.value = _state.value.copy(validation = ValidationResult.Failure("Not authenticated"))
            return
        }
        _state.value = _state.value.copy(isLoading = true, validation = ValidationResult.Validating)
        viewModelScope.launch {
            try {
                val created = repo.savePreference(userId, text)
                val list = _state.value.preferences.toMutableList()
                list.add(0, created)
                _state.value = _state.value.copy(
                    input = "",
                    isLoading = false,
                    validation = ValidationResult.Success,
                    preferences = list
                )
            } catch (_: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    validation = ValidationResult.Failure("Something went wrong. Please try again.")
                )
            }
        }
    }
}

class HomeViewModelFactory(
    private val context: Context,
    private val supabaseUrl: String,
    private val supabaseAnonKey: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo: PreferenceRepository = PreferenceRepositoryImpl(
            context = context,
            supabaseUrl = supabaseUrl,
            supabaseAnonKey = supabaseAnonKey
        )
        return HomeViewModel(context, repo) as T
    }
}



