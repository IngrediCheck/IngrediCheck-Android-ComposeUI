package lc.fungee.Ingredicheck.memoji

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GetStartedUiState(
    val isFillingComplete: Boolean = false
)

class GetStartedViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GetStartedUiState(
            isFillingComplete = savedStateHandle.get<Boolean>("isFillingComplete") ?: false
        )
    )
    val uiState: StateFlow<GetStartedUiState> = _uiState.asStateFlow()

    fun onFillingComplete() {
        _uiState.value = _uiState.value.copy(isFillingComplete = true)
        savedStateHandle["isFillingComplete"] = true
    }
}

