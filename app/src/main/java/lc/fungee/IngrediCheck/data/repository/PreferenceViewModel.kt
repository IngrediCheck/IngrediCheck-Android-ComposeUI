package lc.fungee.IngrediCheck.data.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.auth.isInternetAvailable
import lc.fungee.IngrediCheck.data.model.DietaryPreference
import lc.fungee.IngrediCheck.data.model.PreferenceValidationResult
import lc.fungee.IngrediCheck.data.model.ValidationState
import java.util.UUID

class PreferenceViewModel(
    private val repo: PreferenceRepository
) : ViewModel() {


    val preferences = mutableStateListOf<DietaryPreference>()
    var newPreferenceText by mutableStateOf("")
    var validationState by mutableStateOf<ValidationState>(ValidationState.Idle)
    private var validationJob: Job? = null
    var editingId: Int? = null
    private var indexOfCurrentlyEdited = 0
    private var clientActivityId = UUID.randomUUID().toString()
    private set

    var isOnline by mutableStateOf(true)
        private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Process-scoped flag to avoid reopening scanner when navigating back to Home in the same app run
    private val _autoOpenedThisProcess = MutableStateFlow(false)
    val autoOpenedThisProcess: StateFlow<Boolean> = _autoOpenedThisProcess
    fun markAutoOpenedThisProcess() { _autoOpenedThisProcess.value = true }

//    init {
//        monitorInternet() // 
//    }
//    fun monitorInternet() {
//        viewModelScope.launch {
//            while (true) {
//                val available = isInternetAvailable(appcontext)
//                if (isOnline != available) {
//                    isOnline = available
//                }
//                delay(2000)
//            }
//        }
//    }


    init {
        // Load local data immediately
        viewModelScope.launch {
            repo.getLocal().collectLatest {
                preferences.clear()
                preferences.addAll(it)
            }
        }
        // Also fetch from backend
        refreshPreferences()
    }


    fun refreshPreferences() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repo.fetchAndStore()
            } catch (e: Exception) {
                              Log.e("PreferenceVM", "handle error (network, token, etc.)", e)
            }finally {
                _isRefreshing.value = false
            }

        }
    }

    fun startEditPreference(pref: DietaryPreference) {
        editingId = pref.id
        indexOfCurrentlyEdited = preferences.indexOfFirst { it.id == pref.id }.coerceAtLeast(0)
        newPreferenceText = pref.text
    }

    fun cancelEdit() {
        editingId = null
        newPreferenceText = ""
        clientActivityId = UUID.randomUUID().toString()
    }

    fun deletePreference(pref: DietaryPreference) {
        viewModelScope.launch {
            try {
                if (repo.deletePreference(pref.id,clientActivityId)) {
                    preferences.removeIf { it.id == pref.id }
                    repo.saveLocal(preferences) // update local storage
                }
            } catch (_: Exception) {}
        }
    }

//    fun clearNewPreferenceText() {
//        newPreferenceText = ""
//        validationState = ValidationState.Idle
//        validationJob?.cancel()
//    }
//
//    fun inputActive() {
//        validationState = ValidationState.Idle
//        validationJob?.cancel()
//    }

    fun inputComplete() {
        if (newPreferenceText.isNotBlank()) {
            validationState = ValidationState.Validating
            validationJob?.cancel()
            validationJob = viewModelScope.launch {
                try {
                    Log.d("PreferenceVM", "Submitting preference: $newPreferenceText with clientActivityId: $clientActivityId editingId: $editingId")
                    val result = repo.addOrEditPreference(clientActivityId, newPreferenceText, editingId)
                    Log.d("PreferenceVM", "Result from repo: $result")

                    when (result) {
                        is PreferenceValidationResult.Failure -> {
                            Log.e("PreferenceVM", "Validation failed: ${result.explanation}")
                            validationState = ValidationState.Failure(result.explanation)
                        }
                        is PreferenceValidationResult.Success -> {
                            Log.d("PreferenceVM", "Preference saved successfully: ${result.pref}")
                            if (editingId != null) {
                                preferences[indexOfCurrentlyEdited] = result.pref
                            } else {
                                preferences.add(0, result.pref)
                            }
                            repo.saveLocal(preferences)
                            newPreferenceText = ""
                            editingId = null
                            indexOfCurrentlyEdited = 0
                            clientActivityId = UUID.randomUUID().toString()
                            validationState = ValidationState.Success
                        }
                    }
                } catch (e: Exception) {
                    if (e !is kotlinx.coroutines.CancellationException) {
                        Log.e("PreferenceVM", "Exception while adding/editing preference", e)
                        validationState = ValidationState.Failure(
                            e.message ?: "Something went wrong. Please try again later."
                        )
                    }
                }
            }
        }
    }
    fun buildBoldAnnotatedString(input: String): AnnotatedString {
        return buildAnnotatedString {
            var remaining = input
            while (remaining.contains("**")) {
                val start = remaining.indexOf("**")
                val end = remaining.indexOf("**", start + 2)

                // before bold
                append(remaining.substring(0, start))

                if (end != -1) {
                    val boldText = remaining.substring(start + 2, end)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldText)
                    }
                    remaining = remaining.substring(end + 2)
                } else {
                    append(remaining.substring(start))
                    remaining = ""
                }
            }
            append(remaining)
        }
    }


    // --- Settings: Auto-scan on app start (DataStore) ---
    // Expose the DataStore-backed flow so Composables can observe it.
    val autoScanFlow = repo.autoScanFlow

    // Update the flag in DataStore via the repository.
    fun setAutoScan(enabled: Boolean) {
        viewModelScope.launch {
            repo.setAutoScan(enabled)
        }
    }

    // One-shot pending flag to auto-open scanner once on next app start
    val autoScanPendingFlow = repo.autoScanPendingFlow

    fun setAutoScanPending(pending: Boolean) {
        viewModelScope.launch {
            repo.setAutoScanPending(pending)
        }
    }

    suspend fun consumeAutoScanPending(): Boolean {
        return repo.consumeAutoScanPending()
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            repo.clearAllLocalData()
        }
    }


    // --- Account deletion (remote) ---
    suspend fun deleteAccountRemote(): Boolean {
        return try {
            repo.deleteAccountRemote()
        } catch (e: Exception) {
            Log.e("PreferenceVM", "deleteAccountRemote failed", e)
            false
        }
    }
}
