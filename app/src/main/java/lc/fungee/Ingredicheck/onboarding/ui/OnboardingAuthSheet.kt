package lc.fungee.Ingredicheck.onboarding.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel

/**
 * Renders the sheet content for all sign-in / auth steps in one place.
 * Keeps [OnboardingHost] thin by moving the auth step branch and callbacks here.
 */
@Composable
internal fun OnboardingAuthSheetContent(
    step: OnboardingStep,
    vm: OnboardingViewModel,
    authViewModel: AuthViewModel,
    context: Context,
    onBack: () -> Unit,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    isJustMeLoading: Boolean,
    isAuthLoading: Boolean,
    buildBiteBuddyFamilyRequest: () -> CreateFamilyRequest,
    setCreatingBiteBuddyFamily: (Boolean) -> Unit,
    onNavigateToAddFamilyWelcome: () -> Unit,
    onNavigateToFallingCapsules: () -> Unit
) {
    when (step) {
        OnboardingStep.SIGN_IN_INITIAL -> {
            SignInInitialSheet(
                onExistingUserContinue = { vm.navigateTo(OnboardingStep.SIGN_IN_SOCIAL_LOGIN) },
                onStartNew = { vm.navigateTo(OnboardingStep.SIGN_IN_INVITE_CODE) }
            )
        }

        OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> {
            SignInSocialLoginSheet(
                onBackClick = onBack,
                onGoogleClick = onGoogleClick,
                onAppleClick = onAppleClick
            )
        }

        OnboardingStep.SIGN_IN_INVITE_CODE -> {
            SignInInviteCodeSheet(
                onBackClick = onBack,
                onEnterInviteCode = { vm.navigateTo(OnboardingStep.SIGN_IN_ENTER_INVITE_CODE) },
                onNoContinue = { vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR) }
            )
        }

        OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> {
            SignInEnterInviteCodeSheet(
                inviteCode = vm.inviteCode,
                isError = vm.inviteCodeError,
                onInviteCodeChange = { vm.inviteCode = it },
                onBackClick = onBack,
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
                onBackClick = onBack,
                isJustMeLoading = isJustMeLoading,
                isAddFamilyLoading = false,
                isAuthLoading = isAuthLoading,
                onJustMe = {
                    Log.d("OnboardingHost", "Just Me: Creating Bite Buddy family then navigating to FALLING_CAPSULES")
                    authViewModel.debugLogCurrentSession("Just Me clicked")
                    setCreatingBiteBuddyFamily(true)
                    val req = buildBiteBuddyFamilyRequest()
                    authViewModel.createFamily(req) { result ->
                        setCreatingBiteBuddyFamily(false)
                        result.fold(
                            onSuccess = {
                                Log.d("OnboardingHost", "Just Me: Bite Buddy family created, navigating to FALLING_CAPSULES")
                                onNavigateToFallingCapsules()
                            },
                            onFailure = { e ->
                                Log.e("OnboardingHost", "Just Me: createFamily (Bite Buddy) failed", e)
                                Toast.makeText(
                                    context,
                                    e.localizedMessage ?: "Failed to create profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                },
                onAddFamily = {
                    authViewModel.debugLogCurrentSession("Add Family clicked")
                    onNavigateToAddFamilyWelcome()
                }
            )
        }

        else -> {
            // Not an auth step; caller should not use this composable for other steps.
        }
    }
}
