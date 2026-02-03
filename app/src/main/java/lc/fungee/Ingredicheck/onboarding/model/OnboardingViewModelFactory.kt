package lc.fungee.Ingredicheck.onboarding.model

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class OnboardingViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val persistence: OnboardingPersistence,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return OnboardingViewModel(savedStateHandle = handle, persistence = persistence) as T
    }
}
