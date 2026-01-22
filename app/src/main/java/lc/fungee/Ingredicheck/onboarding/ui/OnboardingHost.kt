package lc.fungee.Ingredicheck.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import lc.fungee.Ingredicheck.ui.theme.responsiveSheetHeight
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

    val backgroundKey = when (step) {
        OnboardingStep.SIGN_IN_INITIAL,
        OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> 1
        OnboardingStep.SIGN_IN_INVITE_CODE,
        OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> 2
        OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> 3
        else -> 0
    }

    OnboardingShell(
        onDismissRequest = onExitOnboarding,
        backgroundContent = {
            AnimatedContent(
                targetState = backgroundKey,
                label = "onboardingBackground",
                transitionSpec = {
                    fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
                }
            ) { k ->
                when (k) {
                    1 -> {
                        SignInBackground(imageRes = R.drawable.iphone_app_img, showLogo = true)
                    }
                    2 -> {
                        SignInBackground(
                            imageRes = R.drawable.welcome_family_img,
                            showLogo = false,
                            title = "Welcome to IngrediFam !",
                            subtitle = "Join your family space and personalize\nfood choices together.",
                            aspectRatio = 1f
                        )
                    }
                    3 -> {
                        SignInBackground(
                            imageRes = R.drawable.welcome_family_and_me_img,
                            showLogo = false,
                            title = "Welcome to Ingredicheck",
                            subtitle = "Create a space for yourself or the people\nyou care about.",
                            aspectRatio = 1f
                        )
                    }
                }
            }
        },
        sheetContent = {
            AnimatedContent(
                targetState = step,
                label = "onboardingSheet",
                transitionSpec = {
                    val duration = 300
                    val easing = FastOutSlowInEasing
                    fadeIn(animationSpec = tween(duration, easing = easing)) togetherWith
                        fadeOut(animationSpec = tween(duration, easing = easing))
                }
            ) { s ->
                Column {
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
                                isError = vm.inviteCodeError,
                                onInviteCodeChange = { vm.inviteCode = it },
                                onBackClick = { vm.back() },
                                onVerifyContinue = {
                                    if (vm.inviteCode == "ABCXYZ") {
                                        vm.inviteCodeError = true
                                    } else {
                                        vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR)
                                    }
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
        }

    )
}
