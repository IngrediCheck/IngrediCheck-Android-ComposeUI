package lc.fungee.Ingredicheck.onboarding.ui

import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import lc.fungee.Ingredicheck.ui.theme.responsiveSheetHeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.auth.AuthEnv
import lc.fungee.Ingredicheck.auth.AuthState
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.auth.GoogleAuthDataSource
import lc.fungee.Ingredicheck.auth.rememberAppleLoginLauncher
import lc.fungee.Ingredicheck.auth.rememberGoogleSignInLauncher
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingPersistence
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModelFactory
import androidx.compose.ui.platform.LocalContext

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun OnboardingHost(
    authViewModel: AuthViewModel,
    onExitOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val savedStateOwner = LocalSavedStateRegistryOwner.current
    val persistence = remember(context) { OnboardingPersistence(context.applicationContext) }
    val factory = remember(savedStateOwner, persistence) {
        OnboardingViewModelFactory(owner = savedStateOwner, persistence = persistence)
    }

    val vm: OnboardingViewModel = viewModel(factory = factory)
    val step = vm.currentStep
    val isRestored = vm.isRestored
    val authState by authViewModel.state.collectAsState()
    val memojiState by authViewModel.memojiState.collectAsState()
    val isAuthLoading = authState is AuthState.Loading

    if (!isRestored) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    LaunchedEffect(step) {
        if (isRestored) {
            authViewModel.syncOnboardingMetadata(step)
        }
        if (
            step == OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR ||
            step == OnboardingStep.ADD_FAMILY_WELCOME ||
            step == OnboardingStep.ADD_FAMILY_NAME ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_PICKER ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING
        ) {
            authViewModel.debugLogCurrentSession("Entered $step (before ensureAnonymousSession)")
            authViewModel.ensureAnonymousSession()
        }
    }

    val activity = LocalContext.current.findActivity()
    val googleLauncher = rememberGoogleSignInLauncher(activity = activity, authViewModel = authViewModel)
    val appleLauncher = rememberAppleLoginLauncher(activity = activity, authViewModel = authViewModel)

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
        OnboardingStep.ADD_FAMILY_WELCOME,
        OnboardingStep.ADD_FAMILY_NAME,
        OnboardingStep.ADD_FAMILY_AVATAR_PICKER,
        OnboardingStep.ADD_FAMILY_AVATAR_GENERATING -> 4
        else -> 0
    }

    OnboardingShell(
        onDismissRequest = onExitOnboarding,
        horizontalPaddingEnabled = step != OnboardingStep.ADD_FAMILY_AVATAR_PICKER,
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
                    4 -> {
                        SignInBackground(
                            imageRes = R.drawable.family_img_add_family,
                            showLogo = false,
                            title = "Getting Started!",
                            subtitle = "Add profiles so IngredientCheck can personalize results for each person.",
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
                                onGoogleClick = {
                                    if (activity != null) {
                                        val client = GoogleAuthDataSource.getClient(activity)
                                        googleLauncher.launch(client.signInIntent)
                                    }
                                },
                                onAppleClick = {
                                    if (activity != null) {
                                        val redirectUri = "${AuthEnv.OAUTH_REDIRECT_SCHEME}://${AuthEnv.OAUTH_REDIRECT_HOST}"
                                        val authUrl = Uri.parse(AuthEnv.SUPABASE_URL).buildUpon()
                                            .appendPath("auth")
                                            .appendPath("v1")
                                            .appendPath("authorize")
                                            .appendQueryParameter("provider", "apple")
                                            .appendQueryParameter("redirect_to", redirectUri)
                                            .appendQueryParameter("flow_type", "implicit")
                                            .build()

                                        val intent = Intent(activity, AppleLoginWebViewActivity::class.java).apply {
                                            putExtra("auth_url", authUrl.toString())
                                            putExtra("redirect_uri", redirectUri)
                                        }
                                        appleLauncher.launch(intent)
                                    }
                                }
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
                                isLoading = isAuthLoading,
                                onJustMe = {
                                    authViewModel.debugLogCurrentSession("Just Me clicked")
                                    authViewModel.signInAsGuest()
                                },
                                onAddFamily = {
                                    authViewModel.debugLogCurrentSession("Add Family clicked")
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_WELCOME)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_WELCOME -> {
                            AddFamilyWelcomeSheet(
                                onBackClick = { vm.back() },
                                onContinue = {
                                    authViewModel.debugLogCurrentSession("Add Family welcome continue")
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_NAME -> {
                            AddFamilyNameSheet(
                                name = vm.addFamilyName,
                                selectedAvatarId = vm.addFamilyAvatarId,
                                generatedAvatarUrl = vm.addFamilyGeneratedAvatarUrl,
                                onNameChange = { vm.addFamilyName = it },
                                onAvatarSelect = {
                                    vm.addFamilyAvatarId = it
                                    vm.addFamilyAvatarSelections = mapOf(0 to it)
                                },
                                onAddAvatarClick = { vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_PICKER) },
                                onBackClick = { vm.back() },
                                onContinue = {
                                    authViewModel.debugLogCurrentSession("Add Family name continue")
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_AVATAR_PICKER -> {
                            AddFamilyAvatarPickerSheet(
                                displayName = vm.addFamilyName,
                                selections = vm.addFamilyAvatarSelections,
                                onBackClick = { vm.back() },
                                onAvatarSelected = {
                                    vm.addFamilyAvatarSelections = it
                                },
                                onGenerateClick = {
                                    authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections)
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_GENERATING)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_AVATAR_GENERATING -> {
                            AddFamilyAvatarGeneratingSheet(
                                state = memojiState,
                                selections = vm.addFamilyAvatarSelections,
                                onBackClick = { vm.back() },
                                onRetry = { authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections) },
                                onRegenerate = { authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections) },
                                onAssign = { imageUrl ->
                                    vm.addFamilyGeneratedAvatarUrl = imageUrl
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME)
                                }
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
