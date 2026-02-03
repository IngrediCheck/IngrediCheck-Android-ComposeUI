package lc.fungee.Ingredicheck.onboarding.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding")

class OnboardingPersistence(
    private val context: Context
) {
    private companion object {
        private val KEY_CURRENT_STEP = stringPreferencesKey("onboarding_current_step")
        private val KEY_STEP_HISTORY = stringPreferencesKey("onboarding_step_history")
    }

    data class SavedState(
        val currentStep: OnboardingStep,
        val history: List<String>
    )

    val savedStateFlow: Flow<SavedState> = context.onboardingDataStore.data
        .map { prefs ->
            val step = prefs[KEY_CURRENT_STEP]?.let { runCatching { OnboardingStep.valueOf(it) }.getOrNull() }
                ?: OnboardingStep.GET_STARTED
            val history = prefs[KEY_STEP_HISTORY]
                ?.takeIf { it.isNotBlank() }
                ?.split("|")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
            SavedState(currentStep = step, history = history)
        }
        .distinctUntilChanged()

    suspend fun setSavedState(currentStep: OnboardingStep, history: List<String>) {
        context.onboardingDataStore.edit { prefs ->
            prefs[KEY_CURRENT_STEP] = currentStep.name
            prefs[KEY_STEP_HISTORY] = history.joinToString("|")
        }
    }
}
