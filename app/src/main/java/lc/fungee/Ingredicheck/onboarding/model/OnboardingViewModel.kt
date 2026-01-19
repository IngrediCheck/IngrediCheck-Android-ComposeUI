package lc.fungee.Ingredicheck.onboarding.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        private const val KEY_CURRENT_STEP = "onboarding_current_step"
        private const val KEY_STEP_HISTORY = "onboarding_step_history"
        private const val KEY_INVITE_CODE = "onboarding_invite_code"
    }

    private var currentStepState: OnboardingStep by mutableStateOf(
        savedStateHandle.get<String>(KEY_CURRENT_STEP)?.let(OnboardingStep::valueOf)
            ?: OnboardingStep.GET_STARTED
    )

    val currentStep: OnboardingStep
        get() = currentStepState

    fun canGoBack(): Boolean = history().isNotEmpty()

    fun navigateTo(step: OnboardingStep) {
        if (step == currentStep) return

        val stack = history()
        stack.add(currentStep.name)
        savedStateHandle[KEY_STEP_HISTORY] = stack
        savedStateHandle[KEY_CURRENT_STEP] = step.name
        currentStepState = step
    }

    fun back() {
        val stack = history()
        if (stack.isEmpty()) return

        val previous = stack.removeAt(stack.lastIndex)
        savedStateHandle[KEY_STEP_HISTORY] = stack
        savedStateHandle[KEY_CURRENT_STEP] = previous
        currentStepState = OnboardingStep.valueOf(previous)
    }

    fun reset() {
        savedStateHandle[KEY_STEP_HISTORY] = arrayListOf<String>()
        savedStateHandle[KEY_CURRENT_STEP] = OnboardingStep.GET_STARTED.name
        savedStateHandle[KEY_INVITE_CODE] = ""
        currentStepState = OnboardingStep.GET_STARTED
        inviteCodeState = ""
    }

    private fun history(): ArrayList<String> {
        return savedStateHandle.get<ArrayList<String>>(KEY_STEP_HISTORY) ?: arrayListOf()
    }

    private var inviteCodeState: String by mutableStateOf(
        savedStateHandle.get<String>(KEY_INVITE_CODE) ?: ""
    )

    var inviteCode: String
        get() = inviteCodeState
        set(value) {
            inviteCodeState = value
            savedStateHandle[KEY_INVITE_CODE] = value
        }
}
