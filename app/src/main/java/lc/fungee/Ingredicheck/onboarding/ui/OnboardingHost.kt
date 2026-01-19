package lc.fungee.Ingredicheck.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel

@Composable
fun OnboardingHost(
    onExitOnboarding: () -> Unit
) {
    val vm: OnboardingViewModel = viewModel()
    val step = vm.currentStep

    BackHandler {
        if (vm.canGoBack()) {
            vm.back()
        } else {
            onExitOnboarding()
        }
    }

    if (step == OnboardingStep.GET_STARTED) {
        GetStatedScreen(
            onGetStarted = { vm.navigateTo(OnboardingStep.SIGN_IN_INITIAL) }
        )
        return
    }

    val sheetHeight = when (step) {
        OnboardingStep.SIGN_IN_INITIAL -> 243.dp
        OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> 330.dp
        OnboardingStep.SIGN_IN_INVITE_CODE -> 330.dp
        OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> 460.dp
        OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> 360.dp
        OnboardingStep.GET_STARTED -> 243.dp
    }

    OnboardingShell(
        onDismissRequest = onExitOnboarding,
        sheetHeight = sheetHeight,
        backgroundContent = {
            AnimatedContent(
                targetState = step,
                label = "onboardingBackground",
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) togetherWith
                        fadeOut(animationSpec = tween(180))
                }
            ) { s ->
                when (s) {
                    OnboardingStep.SIGN_IN_INITIAL,
                    OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> {
                        SignInBackground(imageRes = R.drawable.iphone_app_img)
                    }

                    OnboardingStep.SIGN_IN_INVITE_CODE,
                    OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> {
                        SignInBackground(imageRes = R.drawable.welcome_family_img)
                    }

                    OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> {
                        SignInBackground(imageRes = R.drawable.welcome_family_and_me_img)
                    }

                    OnboardingStep.GET_STARTED -> {
                    }
                }
            }
        },
        sheetContent = {
            AnimatedContent(
                targetState = step,
                label = "onboardingSheet",
                transitionSpec = {
                    (fadeIn(animationSpec = tween(180)) + slideInVertically(animationSpec = tween(180)) { it / 8 }) togetherWith
                        (fadeOut(animationSpec = tween(180)) + slideOutVertically(animationSpec = tween(180)) { it / 8 })
                }
            ) { s ->
                when (s) {
                    OnboardingStep.SIGN_IN_INITIAL -> {
                        SignInInitialSheet(
                            onExistingUserContinue = {
                                vm.navigateTo(OnboardingStep.SIGN_IN_SOCIAL_LOGIN)
                            },
                            onStartNew = {
                                vm.navigateTo(OnboardingStep.SIGN_IN_INVITE_CODE)
                            }
                        )
                    }

                    OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> {
                        SignInSocialLoginSheet(
                            onBackClick = { vm.back() },
                            onGoogleClick = { /* TODO */ },
                            onAppleClick = { /* TODO */ }
                        )
                    }

                    OnboardingStep.SIGN_IN_INVITE_CODE -> {
                        SignInInviteCodeSheet(
                            onBackClick = { vm.back() },
                            onEnterInviteCode = {
                                vm.navigateTo(OnboardingStep.SIGN_IN_ENTER_INVITE_CODE)
                            },
                            onNoContinue = {
                                vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR)
                            }
                        )
                    }

                    OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> {
                        SignInEnterInviteCodeSheet(
                            inviteCode = vm.inviteCode,
                            onInviteCodeChange = { vm.inviteCode = it },
                            onBackClick = { vm.back() },
                            onVerifyContinue = {
                                vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR)
                            }
                        )
                    }

                    OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> {
                        SignInWhoIsThisForSheet(
                            onBackClick = { vm.back() },
                            onJustMe = { onExitOnboarding() },
                            onAddFamily = { onExitOnboarding() }
                        )
                    }

                    OnboardingStep.GET_STARTED -> {
                    }
                }
            }
        }
    )
}
