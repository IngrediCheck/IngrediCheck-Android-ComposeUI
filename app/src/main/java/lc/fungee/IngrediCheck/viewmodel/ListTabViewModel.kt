package lc.fungee.IngrediCheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.repository.FavoriteItem
import lc.fungee.IngrediCheck.model.repository.HistoryItem
import lc.fungee.IngrediCheck.model.repository.ListTabRepository

class ListTabViewModel(private val repo: ListTabRepository) : ViewModel() {

    data class UiState(
        val favorites: List<FavoriteItem>? = null,
        val history: List<HistoryItem>? = null,
        val isLoadingFavorites: Boolean = false,
        val isLoadingHistory: Boolean = false,
        val error: String? = null,
        val isSearching: Boolean = false,
        val searchText: String = "",
        val searchResults: List<HistoryItem> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private var initialLoadJob: Job? = null

    init {
        // Initial load
        initialLoadJob = viewModelScope.launch { refreshAll() }

        // Debounced search
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.update { it.copy(searchResults = emptyList()) }
                    } else {
                        runCatching { repo.fetchHistory(query) }
                            .onSuccess { result ->
                                _uiState.update { it.copy(searchResults = result) }
                            }
                            .onFailure { e ->
                                _uiState.update { it.copy(error = e.message ?: "Search failed") }
                            }
                    }
                }
        }
    }

    fun setSearching(enabled: Boolean) {
        _uiState.update { it.copy(isSearching = enabled) }
        if (!enabled) setSearchText("")
    }

    fun setSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        searchQuery.value = text
    }

    fun refreshAll() {
        refreshFavorites()
        refreshHistory()
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFavorites = true, error = null) }
            runCatching { repo.getFavorites() }
                .onSuccess { list -> _uiState.update { it.copy(favorites = list) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            _uiState.update { it.copy(isLoadingFavorites = false) }
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true, error = null) }
            runCatching { repo.fetchHistory() }
                .onSuccess { list -> _uiState.update { it.copy(history = list) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
            _uiState.update { it.copy(isLoadingHistory = false) }
        }
    }

    fun refreshSearch() {
        val text = _uiState.value.searchText
        if (text.isNotBlank()) setSearchText(text) // trigger debounced search
    }
}