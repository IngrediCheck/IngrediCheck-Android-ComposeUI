package lc.fungee.Ingredicheck.onboarding.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.activity.compose.BackHandler
import lc.fungee.Ingredicheck.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import com.russhwolf.settings.BuildConfig

import lc.fungee.Ingredicheck.auth.AppleLoginWebViewActivity
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.auth.AuthEnv
import lc.fungee.Ingredicheck.auth.AuthState
import lc.fungee.Ingredicheck.auth.MemojiGenState
import lc.fungee.Ingredicheck.auth.GoogleAuthDataSource
import lc.fungee.Ingredicheck.auth.rememberAppleLoginLauncher
import lc.fungee.Ingredicheck.auth.rememberGoogleSignInLauncher
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.family.FamilyMemberDto
import lc.fungee.Ingredicheck.memoji.GetStatedScreen
import lc.fungee.Ingredicheck.onboarding.model.OnboardingPersistence
import lc.fungee.Ingredicheck.onboarding.data.EVERYONE_MEMBER_ID
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.data.DynamicStepsLoader
import lc.fungee.Ingredicheck.onboarding.data.avatarBackgroundColorForId
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModelFactory
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.onboarding.ui.components.FallingCapsulesScreen
import lc.fungee.Ingredicheck.onboarding.ui.components.PreferenceCapsuleCard
import lc.fungee.Ingredicheck.onboarding.ui.components.FamilyOverviewBackground
import lc.fungee.Ingredicheck.onboarding.ui.components.FlowRowChips
import lc.fungee.Ingredicheck.onboarding.ui.components.SelectedChipPill
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale120
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Secondary200
import lc.fungee.Ingredicheck.onboarding.ui.components.familyPlaceholderColor
import lc.fungee.Ingredicheck.ui.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.ui.theme.Primary800

import kotlin.random.Random

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun shareInviteCode(context: Context, code: String) {
    val msg = "You've been invited to join my IngrediCheck family.\n\nInvite code: $code"
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, msg)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Invite"))
}

/** Builds the default "Bite Buddy" family request for the Just Me flow (matches iOS createBiteBuddyFamily). */
private fun buildBiteBuddyFamilyRequest(): CreateFamilyRequest {
    val selfId = java.util.UUID.randomUUID().toString()
    val selfMember = FamilyMemberDto(
        id = selfId,
        name = "Bite Buddy",
        color = "#FFFFBA",
        joined = true,
        invitePending = null,
        imageFileHash = "memoji_3"
    )
    return CreateFamilyRequest(
        name = "Bite Buddy",
        selfMember = selfMember,
        otherMembers = null
    )
}

/**
 * Bottom‑sheet content for the preferences‑added success state.
 * Mirrors iOS's `PreferencesAddedSuccessSheet`.
 */
@Composable
private fun PreferenceSummarySheetContent(
    isFamilyFlow: Boolean,
    onContinue: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isFamilyFlow) "All set to join your family!" else "Preferences added successfully!",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Greyscale150,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isFamilyFlow) {
                    "Your family’s food preferences are already added."
                } else {
                    "Your food preferences are saved. You can review them anytime,"
                },
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "or edit a specific preference section by tapping Edit.",
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Greyscale120,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        PrimaryButton(
            title = "Continue",
            takeFullWidth = false,
            onClick = onContinue
        )
    }
}

private fun buildCreateFamilyRequestFromMembers(
    members: List<OnboardingViewModel.FamilyOverviewMember>
): CreateFamilyRequest? {
    if (members.isEmpty()) return null
    val self = members.first()
    val others = members.drop(1)

    fun toDto(m: OnboardingViewModel.FamilyOverviewMember, joined: Boolean): FamilyMemberDto {
        val imageFileHash = m.generatedAvatarUrl.trim().ifBlank { null }
        return FamilyMemberDto(
            id = m.id,
            name = m.name,
            color = m.colorHex.ifBlank { "#BAE1FF" },
            joined = joined,
            invitePending = null,
            imageFileHash = imageFileHash
        )
    }

    return CreateFamilyRequest(
        name = "${self.name}'s Family",
        selfMember = toDto(self, joined = true),
        otherMembers = if (others.isEmpty()) null else others.map { toDto(it, joined = false) }
    )
}

/** Build a single preference string from onboarding chip selections for backend sync (same as iOS). */
private fun buildDietaryPreferenceText(selectedAllergiesByMember: Map<String, MutableSet<String>>): String {
    val allChipIds = selectedAllergiesByMember.values.flatMap { it.toList() }.toSet()
    return allChipIds.map { OnboardingChipData.labelForChipId(it) }.joinToString(", ")
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun OnboardingHost(
    authViewModel: AuthViewModel,
    onExitOnboarding: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val savedStateOwner = androidx.savedstate.compose.LocalSavedStateRegistryOwner.current
    val persistence = remember(context) { OnboardingPersistence(context.applicationContext) }
    val factory = remember(savedStateOwner, persistence) {
        OnboardingViewModelFactory(owner = savedStateOwner, persistence = persistence)
    }

    val vm: OnboardingViewModel = viewModel(factory = factory)
    val step = vm.currentStep
    var isCreatingFamily by remember { mutableStateOf(false) }
    var isCreatingBiteBuddyFamily by remember { mutableStateOf(false) }
    var isInviting by remember { mutableStateOf(false) }
    val isRestored = vm.isRestored
    val authState by authViewModel.state.collectAsState()
    val emojiState by authViewModel.memojiState.collectAsState()
    val isAuthLoading = authState is AuthState.Loading
    val currentFamily by authViewModel.currentFamily.collectAsState()
    val foodNotesSummary by authViewModel.foodNotesSummary.collectAsState()

    var sheetHeight by remember { mutableStateOf(0.dp) }
    var memberToInvite by remember { mutableStateOf<OnboardingViewModel.FamilyOverviewMember?>(null) }

    // While onboarding state is still restoring from persistence, show a simple white screen
    // instead of flashing the default GET_STARTED UI.
    if (!isRestored) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )
        return
    }

    LaunchedEffect(step) {
        if (isRestored) {
            authViewModel.syncOnboardingMetadata(step)
        }
        if (
            step == OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR ||
            step == OnboardingStep.FALLING_CAPSULES ||
            step == OnboardingStep.ADD_FAMILY_WELCOME ||
            step == OnboardingStep.ADD_FAMILY_NAME ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_PICKER ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING ||
            step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE ||
            step == OnboardingStep.ADD_FAMILY_EDIT_MEMBER
        ) {
            authViewModel.debugLogCurrentSession("Entered $step (before ensureAnonymousSession)")
            authViewModel.ensureAnonymousSession()
        }

        // Note: signInAsGuest() should NOT be called here because it sets authState to Success,
        // which causes MainActivity to immediately exit onboarding. Guest sign-in should happen
        // only when onboarding is actually completed (e.g., when user exits after completing allergies).

        // Restore memoji generation UI state after process death:
        // - If we are on the generating screen and have a saved image URL with
        //   memojiGenerationCompleted == true, restore Success state.
        if (step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING) {
            val currentEmoji = authViewModel.memojiState.value
            if (currentEmoji is MemojiGenState.Idle && vm.memojiGenerationCompleted) {
                val url = vm.addFamilyGeneratedAvatarUrl.trim()
                if (url.isNotBlank()) {
                    authViewModel.restoreMemojiSuccess(url)
                }
            }
        }
    }

    val activity = LocalContext.current.findActivity()
    val googleLauncher =
        rememberGoogleSignInLauncher(activity = activity, authViewModel = authViewModel)
    val appleLauncher =
        rememberAppleLoginLauncher(activity = activity, authViewModel = authViewModel)

    // Debounce back to prevent double-tap from going 2 screens back
    var lastBackTime by remember { mutableStateOf(0L) }
    val vmRef = rememberUpdatedState(vm)
    val onExitRef = rememberUpdatedState(onExitOnboarding)
    val handleBack: () -> Unit = handleBack@{
        val now = System.currentTimeMillis()
        if (now - lastBackTime < 400L) return@handleBack
        lastBackTime = now
        val v = vmRef.value
        if (v.canGoBack()) {
            v.back()
        } else {
            onExitRef.value()
        }
    }

    val isAddMoreMemberNoBack =
        step == OnboardingStep.ADD_FAMILY_NAME && vm.familyOverviewMembers.size == 1
    val isAllSetOrMoreScreen = step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE
    val isFallingCapsulesScreen = step == OnboardingStep.FALLING_CAPSULES
    BackHandler(enabled = true) {
        if (!isAddMoreMemberNoBack && !isAllSetOrMoreScreen && !isFallingCapsulesScreen) {
            handleBack()
        }
    }

    // On first launch show "Everyone" as selected (ALL); user can switch to a member later.
    // Local state for allergy selections (restored asynchronously from DataStore).
    val selectedAllergyMemberIdState = remember(vm.familyOverviewMembers.size) {
        mutableStateOf(EVERYONE_MEMBER_ID)
    }
    val selectedAllergies = remember {
        mutableStateListOf<String>()
    }
    // memberKey ("ALL" or member.id) -> set of chipIds selected for that member
    val selectedAllergiesByMember = remember {
        mutableStateMapOf<String, MutableSet<String>>()
    }
    // Bump on every chip toggle so the sheet reliably recomposes (workaround for SnapshotStateMap).
    var allergySelectionRevision by remember { mutableStateOf(0) }
    // Allergy step index (restored asynchronously below)
    var allergyStepIndex by remember {
        mutableStateOf(0)
    }
    // Active member's chip selections
    var activeMemberSelections by remember {
        mutableStateOf<Set<String>>(emptySet())
    }
    // Last allergy sub-phase restored from persistence (chips / summary_robot / chat_intro /
    // chat_conversation / preference_summary). Used to re-open AI summary/chat after restart.
    var restoredAllergyPhase by remember { mutableStateOf<String?>(null) }
    // Becomes true once we have applied any restored allergy selections from DataStore.
    // This prevents the persistence effect from writing default/empty state back to
    // DataStore before restore has finished (which would overwrite the real values).
    var hasAppliedRestoredAllergies by remember { mutableStateOf(false) }

    // Restore allergy selections state from persistence (per‑member chip ids + active member + step index)
    // without blocking the main thread.
    LaunchedEffect(Unit) {
        try {
            val restoredState = persistence.getAllergySelectionsState()
            val restoredSelections = restoredState.selectedAllergiesByMember
            val restoredMemberId = restoredState.selectedAllergyMemberId
            val restoredStepIndex = restoredState.allergyStepIndex

            if (restoredSelections.isNotEmpty()) {
                // Update member id
                val activeKey = restoredMemberId.ifBlank { EVERYONE_MEMBER_ID }
                selectedAllergyMemberIdState.value = activeKey

                // Rebuild per-member map
                selectedAllergiesByMember.clear()
                restoredSelections.forEach { (memberKey, chipIds) ->
                    selectedAllergiesByMember[memberKey] = chipIds.toMutableSet()
                }

                // Rebuild flat union and active member selections
                val union = restoredSelections.values.flatten().toSet()
                selectedAllergies.clear()
                selectedAllergies.addAll(union)
                activeMemberSelections = restoredSelections[activeKey] ?: emptySet()

                // Restore step index
                allergyStepIndex = restoredStepIndex

                // Remember the restored allergy sub-phase so we can re-open the appropriate
                // sheet (AI summary / chat) after restart.
                restoredAllergyPhase = restoredState.allergyPhase

                if (BuildConfig.DEBUG) {
                    Log.d(
                        "OnboardingAllergies",
                        "[RESTORE_APPLY] restoredSelections=$restoredSelections " +
                                "restoredMemberId=$restoredMemberId restoredStepIndex=$restoredStepIndex " +
                                "union=$union activeMemberSelections=$activeMemberSelections " +
                                "phase=${restoredState.allergyPhase}"
                    )
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "OnboardingAllergies",
                        "[RESTORE_APPLY] no restoredSelections found; keeping defaults"
                    )
                }
            }
            // Mark that we have finished applying any restored allergy selections so that
            // the persistence effect can start writing changes safely.
            hasAppliedRestoredAllergies = true
        } catch (e: Exception) {
            Log.w("OnboardingAllergies", "[RESTORE] getAllergySelectionsState failed", e)
        }
    }

    // Progress tracking within the fine‑tune flow (allergies, intolerances, etc.)
    // These same steps drive both the CapsuleStepperRow and the AnimatedProgressLine.
    // Load dynamic JSON from assets (same as iOS) so step order/copy can be driven from dynamicJsonData.json.
    var dynamicStepsLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(context) {
        DynamicStepsLoader.ensureLoaded(context)
        dynamicStepsLoaded = true
    }
    val allergySteps = remember(dynamicStepsLoaded) {
        val steps = DynamicStepsLoader.getSteps()?.map { s ->
            CapsuleStep(s.id, s.header.name, OnboardingChipData.iconResForStepId(s.id))
        } ?: emptyList()
        if (BuildConfig.DEBUG) {
            Log.d(
                "DynamicJsonData",
                "JSON data: UI using ${steps.size} steps from dynamicJsonData.json (same after restart)"
            )
        }
        steps
    }

    // Persist allergy selections whenever they change
    LaunchedEffect(
        selectedAllergiesByMember,
        selectedAllergyMemberIdState.value,
        allergyStepIndex
    ) {
        if (isRestored && hasAppliedRestoredAllergies && step == OnboardingStep.ADD_FAMILY_ALLERGIES) {
            val snapshot = selectedAllergiesByMember.mapValues { it.value.toSet() }
            if (BuildConfig.DEBUG) {
                Log.d(
                    "OnboardingAllergies",
                    "[PERSIST_EFFECT] step=$step isRestored=$isRestored " +
                            "selectedMember=${selectedAllergyMemberIdState.value} " +
                            "stepIndex=$allergyStepIndex selections=$snapshot"
                )
            }
            persistence.setAllergySelectionsState(
                selectedAllergiesByMember = snapshot,
                selectedAllergyMemberId = selectedAllergyMemberIdState.value,
                allergyStepIndex = allergyStepIndex
            )
        }
    }
    // When true, show the fine‑tune decision screen between Life Style and Nutrition
    var showFineTuneDecision by remember { mutableStateOf(false) }
    // When true, show the summary screen with floating robot after completing fine-tune flow
    var showSummaryScreen by remember { mutableStateOf(false) }
    // When true, show the IngrediBot chat intro screen after the summary completes.
    var showChatBotIntro by remember { mutableStateOf(false) }
    // When true, show the AI chat conversation sheet after the intro.
    var showChatConversation by remember { mutableStateOf(false) }
    // When true, show the AI summary screen with PreferenceCapsuleCard list.
    var showPreferenceSummary by remember { mutableStateOf(false) }
    // When non-null, indicates we are editing a specific preference section from the
    // summary screen (iOS EditSectionBottomSheet equivalent).
    var editingSummaryStepIndex by remember { mutableStateOf<Int?>(null) }
    // When non-null, indicates which member's food notes are currently filtered in the
    // preference summary background (null = show all members).
    var summarySelectedMemberId by remember { mutableStateOf<String?>(null) }

    // On cold start, if we restored a non-chips allergy sub-phase, re-open the correct
    // sheet. We intentionally never resume on the transient summary_robot animation:
    // for that phase we fall back to showing the chips/question UI at the current step.
    LaunchedEffect(restoredAllergyPhase) {
        val phase = restoredAllergyPhase
        if (phase != null && isRestored && step == OnboardingStep.ADD_FAMILY_ALLERGIES) {
            when (phase) {
                "preference_summary" -> {
                    showPreferenceSummary = true
                    showChatConversation = false
                    showChatBotIntro = false
                    showSummaryScreen = false
                }
                "chat_conversation" -> {
                    showChatConversation = true
                    showPreferenceSummary = false
                    showChatBotIntro = false
                    showSummaryScreen = false
                }
                "chat_intro" -> {
                    showChatBotIntro = true
                    showChatConversation = false
                    showPreferenceSummary = false
                    showSummaryScreen = false
                }
                "summary_robot", "chips", "", null -> {
                    // Do not resume onto robot; show normal chips UI.
                    showSummaryScreen = false
                    showChatBotIntro = false
                    showChatConversation = false
                    showPreferenceSummary = false
                }
                else -> {
                    // Unknown phase: be safe and show chips UI.
                    showSummaryScreen = false
                    showChatBotIntro = false
                    showChatConversation = false
                    showPreferenceSummary = false
                }
            }
            // Consume so this effect doesn't re-run unnecessarily.
            restoredAllergyPhase = null
        }
    }

    // Persist allergy sub-phase (summary_robot / chat_intro / chat_conversation / preference_summary) so user
    // lands on a consistent screen after kill/launch. Treat summary_robot as transient; after restart we prefer
    // to show the chips/question UI at the same step index rather than the robot itself.
    LaunchedEffect(
        step,
        showSummaryScreen,
        showChatBotIntro,
        showChatConversation,
        showPreferenceSummary
    ) {
        if (isRestored && step == OnboardingStep.ADD_FAMILY_ALLERGIES) {
            val phase = when {
                showPreferenceSummary -> "preference_summary"
                showChatConversation -> "chat_conversation"
                showChatBotIntro -> "chat_intro"
                showSummaryScreen -> "summary_robot"
                else -> "chips"
            }
            persistence.setAllergyPhase(phase)
        }
    }

    // After showing the summary screen for a short time, transition smoothly to the chat intro.
    LaunchedEffect(showSummaryScreen) {
        if (showSummaryScreen) {
            delay(3000) // Show summary screen for 3 seconds
            showSummaryScreen = false
            showChatBotIntro = true
        }
    }

    // When we arrive at the AI summary screen, load the backend "Summarized with AI" text.
    LaunchedEffect(showPreferenceSummary) {
        if (showPreferenceSummary) {
            if (BuildConfig.DEBUG) {
                Log.d(
                    "OnboardingAllergies",
                    "[AI_SUMMARY] showPreferenceSummary=true, requesting FoodNotesSummary from backend"
                )
            }
            authViewModel.loadFoodNotesSummary()
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
        OnboardingStep.ADD_FAMILY_AVATAR_GENERATING,
        OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE,
        OnboardingStep.ADD_FAMILY_EDIT_MEMBER -> 4

        OnboardingStep.FALLING_CAPSULES -> 5
        OnboardingStep.ADD_FAMILY_ALLERGIES -> 6
        else -> 0
    }

    // Keep addFamilyGeneratedAvatarUrl in sync with memoji Success so it can be restored.
    LaunchedEffect(emojiState) {
        if (emojiState is MemojiGenState.Success) {
            vm.addFamilyGeneratedAvatarUrl = (emojiState as MemojiGenState.Success).imageUrl
            vm.memojiGenerationCompleted = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val isFamilyFlow = vm.familyOverviewMembers.isNotEmpty()

        // Single Shell: background shows allergy/summary in one place; sheet content
        // branches for flow vs preference summary (success or edit).
        OnboardingShell(
            onDismissRequest = onExitOnboarding,
            horizontalPaddingEnabled = step != OnboardingStep.ADD_FAMILY_AVATAR_PICKER && step != OnboardingStep.ADD_FAMILY_ALLERGIES,
            showFocusedShadow = step == OnboardingStep.SIGN_IN_INITIAL ||
                    step == OnboardingStep.SIGN_IN_SOCIAL_LOGIN,
            baseBottomPaddingOverride = if (step == OnboardingStep.ADD_FAMILY_ALLERGIES) 8.dp else null,
            onSheetHeightChanged = { sheetHeight = it },
            backgroundContent = {
                AnimatedContent(
                    targetState = backgroundKey,
                    label = "onboardingBackground",
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(
                                300,
                                easing = FastOutSlowInEasing
                            )
                        ) togetherWith
                                fadeOut(
                                    animationSpec = tween(
                                        300,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                    }
                ) { k ->
                    when (k) {
                        1 -> {
                            SignInBackground(
                                imageRes = R.drawable.iphone_app_img,
                                showLogo = true
                            )
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
                            val members = vm.familyOverviewMembers.toList()

                            if (members.isNotEmpty()) {
                                FamilyOverviewBackground(
                                    members = members,
                                    bottomSheetHeight = sheetHeight,
                                    // In onboarding flow, do NOT allow immediate "Leave Family"
                                    // so onLeaveFamily is left null here.
                                    onInvite = { member ->
                                        memberToInvite = member
                                    },
                                    onEditMember = { member ->
                                        Log.d(
                                            "OnboardingHost",
                                            "Edit member tapped id=${member.id}, name=${member.name}"
                                        )
                                        vm.editingMemberId = member.id
                                        vm.addFamilyName = member.name
                                        vm.addFamilyAvatarId = member.avatarId
                                        vm.addFamilyGeneratedAvatarUrl =
                                            member.generatedAvatarUrl
                                        val selections = buildMap<Int, String> {
                                            if (member.avatarId.isNotBlank()) {
                                                put(0, member.avatarId)
                                            }
                                            if (member.backgroundColorId.isNotBlank()) {
                                                put(5, member.backgroundColorId)
                                            }
                                        }
                                        if (selections.isNotEmpty()) {
                                            vm.addFamilyAvatarSelections = selections
                                        }
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_EDIT_MEMBER)
                                    }
                                )
                            } else {
                                SignInBackground(
                                    imageRes = R.drawable.family_img_add_family,
                                    showLogo = false,
                                    title = "Getting Started!",
                                    subtitle = "Add profiles so IngredientCheck can personalize results for each person.",
                                    aspectRatio = 1f
                                )
                            }
                        }

                        5 -> {
                            FallingCapsulesScreen(
                                modifier = Modifier.fillMaxSize().background(Color.White),
                                bottomInset = sheetHeight
                            )
                        }

                        6 -> {
                            OnboardingAllergyBackground(
                                dynamicStepsLoaded = dynamicStepsLoaded,
                                allergySteps = allergySteps,
                                showPreferenceSummary = showPreferenceSummary,
                                allergyStepIndex = allergyStepIndex,
                                onAllergyStepIndexChange = { allergyStepIndex = it },
                                selectedAllergies = selectedAllergies,
                                selectedAllergyMemberId = selectedAllergyMemberIdState.value,
                                showSummaryScreen = showSummaryScreen,
                                showFineTuneDecision = showFineTuneDecision,
                                onShowFineTuneDecisionChange = { showFineTuneDecision = it },
                                selectedAllergiesByMember = selectedAllergiesByMember.mapValues { it.value.toSet() },
                                familyOverviewMembers = vm.familyOverviewMembers.toList(),
                                summarySelectedMemberId = summarySelectedMemberId,
                                onSummaryMemberSelected = { summarySelectedMemberId = it },
                                onEditSection = { stepId ->
                                    val idx = allergySteps.indexOfFirst { it.id == stepId }
                                    if (idx >= 0) {
                                        // When editing from the summary screen, mirror the current
                                        // member filter in the bottom sheet so it opens with the
                                        // same person (or Everyone) selected, matching iOS.
                                        selectedAllergyMemberIdState.value =
                                            summarySelectedMemberId ?: ""
                                        editingSummaryStepIndex = idx
                                    }
                                },
                                aiSummaryText = foodNotesSummary,
                                bottomInset = sheetHeight
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
                            OnboardingStep.FALLING_CAPSULES -> {
                                AddFamilyLetsGoSheet(
                                    onLetsGo = {
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_ALLERGIES)
                                    }
                                )
                            }

                            OnboardingStep.ADD_FAMILY_ALLERGIES -> {
                                if (!dynamicStepsLoaded || allergySteps.isEmpty()) {
                                    // Don't show sheet content until steps are loaded so chips and question are visible (e.g. after restart).
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                } else if (showPreferenceSummary) {
                                    // Preference summary: success sheet or edit-section sheet (single Shell, same background).
                                    val editIndex = editingSummaryStepIndex
                                    if (editIndex == null) {
                                        PreferenceSummarySheetContent(
                                            isFamilyFlow = isFamilyFlow,
                                            onContinue = {
                                                showPreferenceSummary = false
                                                onExitOnboarding()
                                            }
                                        )
                                    } else {
                                        val activeMemberKey =
                                            if (selectedAllergyMemberIdState.value.isBlank()) EVERYONE_MEMBER_ID else selectedAllergyMemberIdState.value
                                        val activeSelectionsForMember =
                                            selectedAllergiesByMember[activeMemberKey]?.toSet() ?: emptySet()
                                        AddAllergiesSheet(
                                            members = vm.familyOverviewMembers.toList(),
                                            selectedMemberId = selectedAllergyMemberIdState.value,
                                            selectedAllergies = activeSelectionsForMember,
                                            onMemberSelected = {
                                                selectedAllergyMemberIdState.value = it
                                                editingSummaryStepIndex = editIndex
                                            },
                                            onToggleAllergy = { allergyId ->
                                                val memberKey =
                                                    if (selectedAllergyMemberIdState.value.isBlank()) EVERYONE_MEMBER_ID else selectedAllergyMemberIdState.value
                                                val chipsForMember =
                                                    (selectedAllergiesByMember[memberKey]?.toMutableSet() ?: mutableSetOf())
                                                if (chipsForMember.contains(allergyId)) {
                                                    chipsForMember.remove(allergyId)
                                                    if (chipsForMember.isEmpty()) {
                                                        selectedAllergiesByMember.remove(memberKey)
                                                    } else {
                                                        selectedAllergiesByMember[memberKey] = chipsForMember
                                                    }
                                                } else {
                                                    chipsForMember.add(allergyId)
                                                    selectedAllergiesByMember[memberKey] = chipsForMember
                                                }
                                                selectedAllergies.clear()
                                                selectedAllergies.addAll(
                                                    selectedAllergiesByMember.values.flatMap { it }.toSet()
                                                )
                                            },
                                            onNext = { },
                                            onSkipPreferences = { },
                                            showFineTuneDecision = false,
                                            showSummaryScreen = false,
                                            hasOtherSelection = selectedAllergies.any { it.contains("other", ignoreCase = true) },
                                            showChatBotIntro = false,
                                            showChatConversation = false,
                                            onChatBotLetsGo = { },
                                            onChatSkip = { },
                                            questionStepIndex = editIndex,
                                            isEditMode = true,
                                            onEditDone = {
                                                // User finished editing from the summary screen.
                                                // Sync updated food notes to backend and refresh the
                                                // AI summary so the "Summarized with AI" text reflects
                                                // the latest preferences (matches iOS behavior).
                                                if (BuildConfig.DEBUG) {
                                                    val snapshot = selectedAllergiesByMember
                                                        .mapValues { it.value.toSet() }
                                                    Log.d(
                                                        "OnboardingAllergies",
                                                        "[SUMMARY_EDIT] onEditDone stepIndex=$editIndex " +
                                                                "memberFilter=${summarySelectedMemberId ?: "ALL"} " +
                                                                "keys=${snapshot.keys} sizes=${snapshot.values.map { it.size }}"
                                                    )
                                                }
                                                authViewModel.syncFoodNotesFromOnboarding(
                                                    selectedAllergiesByMember.mapValues { it.value.toSet() }
                                                )
                                                // Persist the edited selections immediately so that if the
                                                // user kills the app right after tapping Done, the latest
                                                // preferences (including life stage changes) are restored.
                                                coroutineScope.launch {
                                                    val snapshot = selectedAllergiesByMember
                                                        .mapValues { it.value.toSet() }
                                                    persistence.setAllergySelectionsState(
                                                        selectedAllergiesByMember = snapshot,
                                                        selectedAllergyMemberId = selectedAllergyMemberIdState.value,
                                                        allergyStepIndex = allergyStepIndex
                                                    )
                                                }
                                                editingSummaryStepIndex = null
                                            }
                                        )
                                    }
                                } else {
                                    // Compute per-member selections for the bottom sheet:
                                    // the sheet should reflect ONLY what the currently selected member
                                    // (or Everyone) has chosen, not the union across all members.
                                    val activeMemberId = selectedAllergyMemberIdState.value
                                    val activeMemberKey =
                                        if (activeMemberId.isBlank()) EVERYONE_MEMBER_ID else activeMemberId

                                    // Sync activeMemberSelections whenever activeMemberKey or revision changes
                                    LaunchedEffect(activeMemberKey, allergySelectionRevision) {
                                        val latest =
                                            selectedAllergiesByMember[activeMemberKey]?.toSet()
                                                ?: emptySet()
                                        if (activeMemberSelections != latest) {
                                            activeMemberSelections = latest
                                            Log.d(
                                                "OnboardingAllergies",
                                                "[SYNC] activeMemberSelections updated to=$latest for memberKey=$activeMemberKey revision=$allergySelectionRevision"
                                            )
                                        }
                                    }

                                    Log.d(
                                        "OnboardingAllergies",
                                        "[SHEET RECOMPOSE] memberKey=$activeMemberKey " +
                                                "activeMemberSelections=$activeMemberSelections " +
                                                "revision=$allergySelectionRevision " +
                                                "selectedAllergiesByMember.keys=${selectedAllergiesByMember.keys}"
                                    )

                                    // Key only by member so switching member resets the sheet; do NOT key by
                                    // selections so that chip toggles do not recreate the sheet (preserves
                                    // stacked card order when user selects chips on 2nd/3rd card).
                                    key(activeMemberKey) {
                                        val hasOtherSelection =
                                            selectedAllergies.any {
                                                it.contains(
                                                    "other",
                                                    ignoreCase = true
                                                )
                                            }
                                        AddAllergiesSheet(
                                            members = vm.familyOverviewMembers.toList(),
                                            selectedMemberId = selectedAllergyMemberIdState.value,
                                            selectedAllergies = activeMemberSelections,
                                            onMemberSelected = {
                                                val oldMemberKey =
                                                    if (selectedAllergyMemberIdState.value.isBlank()) EVERYONE_MEMBER_ID else selectedAllergyMemberIdState.value
                                                val newMemberKey =
                                                    if (it.isBlank()) EVERYONE_MEMBER_ID else it
                                                Log.d(
                                                    "OnboardingAllergies",
                                                    "[MEMBER SWITCH] from=$oldMemberKey to=$newMemberKey " +
                                                            "selectionsForNewMember=${selectedAllergiesByMember[newMemberKey]?.toSet()}"
                                                )
                                                selectedAllergyMemberIdState.value = it
                                                // Update activeMemberSelections immediately when switching members
                                                activeMemberSelections =
                                                    selectedAllergiesByMember[newMemberKey]?.toSet()
                                                        ?: emptySet()
                                            },
                                            onToggleAllergy = { allergyId ->
                                                val activeMemberId =
                                                    selectedAllergyMemberIdState.value
                                                val memberKey =
                                                    if (activeMemberId.isBlank()) EVERYONE_MEMBER_ID else activeMemberId

                                                Log.d(
                                                    "OnboardingAllergies",
                                                    "[TAP] START chip=$allergyId memberKey=$memberKey " +
                                                            "beforeChips=${selectedAllergiesByMember[memberKey]?.toSet()}"
                                                )

                                                // Copy out, mutate, then write back so SnapshotStateMap sees a change
                                                // and the sheet recomposes. Mutating the inner MutableSet in place
                                                // does not trigger recomposition.
                                                val chipsForMember =
                                                    (selectedAllergiesByMember[memberKey]?.toMutableSet()
                                                        ?: mutableSetOf())
                                                if (chipsForMember.contains(allergyId)) {
                                                    chipsForMember.remove(allergyId)
                                                    if (chipsForMember.isEmpty()) {
                                                        selectedAllergiesByMember.remove(
                                                            memberKey
                                                        )
                                                    } else {
                                                        selectedAllergiesByMember[memberKey] =
                                                            chipsForMember
                                                    }
                                                } else {
                                                    chipsForMember.add(allergyId)
                                                    selectedAllergiesByMember[memberKey] =
                                                        chipsForMember
                                                }

                                                // Rebuild the flat selectedAllergies list as the union of all chips
                                                // selected by any member (used only for background capsules).
                                                selectedAllergies.clear()
                                                selectedAllergies.addAll(
                                                    selectedAllergiesByMember.values
                                                        .flatMap { it }
                                                        .toSet()
                                                )

                                                // Immediately update activeMemberSelections if this is for the active member
                                                // BEFORE incrementing revision so the key block sees the updated value
                                                if (memberKey == activeMemberKey) {
                                                    activeMemberSelections =
                                                        selectedAllergiesByMember[memberKey]?.toSet()
                                                            ?: emptySet()
                                                }

                                                allergySelectionRevision++

                                                Log.d(
                                                    "OnboardingAllergies",
                                                    "[TAP] END chip=$allergyId memberKey=$memberKey " +
                                                            "afterChips=${selectedAllergiesByMember[memberKey]?.toSet()} " +
                                                            "revision=$allergySelectionRevision " +
                                                            "activeMemberSelections=$activeMemberSelections"
                                                )
                                            },
                                            onNext = {
                                                // Between Life Style (index 6) and Nutrition (index 7),
                                                // show a dedicated fine‑tune decision screen that does
                                                // NOT advance progress until the user confirms.
                                                if (allergyStepIndex == 6 && !showFineTuneDecision) {
                                                    showFineTuneDecision = true
                                                } else {
                                                    showFineTuneDecision = false
                                                    if (allergyStepIndex < allergySteps.lastIndex) {
                                                        allergyStepIndex++
                                                    } else {
                                                        // Sync dietary preferences to backend (same as iOS) before showing summary
                                                        val preferenceText =
                                                            buildDietaryPreferenceText(
                                                                selectedAllergiesByMember
                                                            )
                                                        Log.d(
                                                            "OnboardingAllergies",
                                                            "[DietaryPreference] onNext complete: syncing textLength=${preferenceText.length}"
                                                        )
                                                        authViewModel.syncDietaryPreferencesFromOnboarding(
                                                            preferenceText
                                                        )
                                                        authViewModel.syncFoodNotesFromOnboarding(
                                                            selectedAllergiesByMember.mapValues { it.value.toSet() })
                                                        showSummaryScreen = true
                                                    }
                                                }
                                            },
                                            onSkipPreferences = {
                                                // User tapped "All Set!" on the fine‑tune decision screen:
                                                // sync preferences to backend (same as iOS) then show summary screen.
                                                val preferenceText = buildDietaryPreferenceText(
                                                    selectedAllergiesByMember
                                                )
                                                Log.d(
                                                    "OnboardingAllergies",
                                                    "[DietaryPreference] onSkipPreferences: syncing textLength=${preferenceText.length}"
                                                )
                                                authViewModel.syncDietaryPreferencesFromOnboarding(
                                                    preferenceText
                                                )
                                                authViewModel.syncFoodNotesFromOnboarding(
                                                    selectedAllergiesByMember.mapValues { it.value.toSet() })
                                                showFineTuneDecision = false
                                                showSummaryScreen = true
                                            },
                                            showFineTuneDecision = showFineTuneDecision,
                                            showSummaryScreen = showSummaryScreen,
                                            hasOtherSelection = hasOtherSelection,
                                            showChatBotIntro = showChatBotIntro,
                                            showChatConversation = showChatConversation,
                                            onChatBotLetsGo = {
                                                showChatBotIntro = false
                                                showChatConversation = true
                                            },
                                            onChatSkip = {
                                                // Close chat and show AI summary screen.
                                                showChatConversation = false
                                                showChatBotIntro = false
                                                showPreferenceSummary = true
                                            },
                                            questionStepIndex = allergyStepIndex
                                        )
                                    }
                                }
                            }

                            OnboardingStep.SIGN_IN_INITIAL,
                            OnboardingStep.SIGN_IN_SOCIAL_LOGIN,
                            OnboardingStep.SIGN_IN_INVITE_CODE,
                            OnboardingStep.SIGN_IN_ENTER_INVITE_CODE,
                            OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> {
                                OnboardingAuthSheetContent(
                                    step = s,
                                    vm = vm,
                                    authViewModel = authViewModel,
                                    context = context,
                                    onBack = handleBack,
                                    onGoogleClick = {
                                        if (activity != null) {
                                            val client =
                                                GoogleAuthDataSource.getClient(activity)
                                            googleLauncher.launch(client.signInIntent)
                                        }
                                    },
                                    onAppleClick = {
                                        if (activity != null) {
                                            val redirectUri =
                                                "${AuthEnv.OAUTH_REDIRECT_SCHEME}://${AuthEnv.OAUTH_REDIRECT_HOST}"
                                            val authUrl =
                                                Uri.parse(AuthEnv.SUPABASE_URL).buildUpon()
                                                    .appendPath("auth")
                                                    .appendPath("v1")
                                                    .appendPath("authorize")
                                                    .appendQueryParameter("provider", "apple")
                                                    .appendQueryParameter(
                                                        "redirect_to",
                                                        redirectUri
                                                    )
                                                    .appendQueryParameter(
                                                        "flow_type",
                                                        "implicit"
                                                    )
                                                    .build()
                                            val intent = Intent(
                                                activity,
                                                AppleLoginWebViewActivity::class.java
                                            ).apply {
                                                putExtra("auth_url", authUrl.toString())
                                                putExtra("redirect_uri", redirectUri)
                                            }
                                            appleLauncher.launch(intent)
                                        }
                                    },
                                    isJustMeLoading = isCreatingBiteBuddyFamily,
                                    isAuthLoading = isAuthLoading,
                                    buildBiteBuddyFamilyRequest = { buildBiteBuddyFamilyRequest() },
                                    setCreatingBiteBuddyFamily = {
                                        isCreatingBiteBuddyFamily = it
                                    },
                                    onNavigateToAddFamilyWelcome = {
                                        vm.navigateTo(
                                            OnboardingStep.ADD_FAMILY_WELCOME
                                        )
                                    },
                                    onNavigateToFallingCapsules = { vm.navigateTo(OnboardingStep.FALLING_CAPSULES) }
                                )
                            }

                            OnboardingStep.ADD_FAMILY_WELCOME -> {
                                AddFamilyWelcomeSheet(
                                    onBackClick = handleBack,
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
                                    onAddAvatarClick = {
                                        vm.addFamilyAvatarId = ""
                                        vm.addFamilyAvatarSelections = emptyMap()
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_PICKER)
                                    },
                                    onBackClick = handleBack,
                                    onContinue = {
                                        if (isCreatingFamily) {
                                            return@AddFamilyNameSheet
                                        }
                                        focusManager.clearFocus(force = true)
                                        authViewModel.debugLogCurrentSession("Add Family name continue")
                                        val editingId = vm.editingMemberId
                                        if (editingId != null) {
                                            val existing =
                                                vm.familyOverviewMembers.firstOrNull { it.id == editingId }
                                            val draft = vm.currentDraftFamilyMemberOrNull()
                                            if (existing == null || draft == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter a name",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@AddFamilyNameSheet
                                            }

                                            val updated = existing.copy(
                                                name = draft.name,
                                                avatarId = draft.avatarId,
                                                generatedAvatarUrl = draft.generatedAvatarUrl,
                                                backgroundColorId = draft.backgroundColorId
                                            )
                                            vm.updateFamilyOverviewMember(updated)
                                            vm.clearAddFamilyDraft()
                                            vm.editingMemberId = null
                                            vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                            return@AddFamilyNameSheet
                                        }
                                        val isFirstMember = vm.familyOverviewMembers.isEmpty()
                                        if (!isFirstMember) {
                                            val draft = vm.currentDraftFamilyMemberOrNull()
                                            if (draft == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter a name",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@AddFamilyNameSheet
                                            }

                                            // Persist additional member to backend family before inviting.
                                            val memberDto = FamilyMemberDto(
                                                id = draft.id,
                                                name = draft.name,
                                                color = draft.colorHex.ifBlank { "#BAE1FF" },
                                                joined = false,
                                                invitePending = null,
                                                imageFileHash = draft.generatedAvatarUrl.trim()
                                                    .ifBlank { null }
                                            )

                                            isCreatingFamily = true
                                            authViewModel.addFamilyMember(memberDto) { result ->
                                                isCreatingFamily = false
                                                result.fold(
                                                    onSuccess = {
                                                        Log.d(
                                                            "OnboardingHost",
                                                            "addMember persisted for id=${memberDto.id}, name=${memberDto.name}"
                                                        )
                                                        vm.commitDraftFamilyMember()
                                                        vm.clearAddFamilyDraft()
                                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                                    },
                                                    onFailure = { e ->
                                                        Log.e(
                                                            "OnboardingHost",
                                                            "addMember failed for id=${memberDto.id}, name=${memberDto.name}",
                                                            e
                                                        )
                                                        Toast.makeText(
                                                            context,
                                                            e.localizedMessage
                                                                ?: "Failed to add member",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                )
                                            }
                                            return@AddFamilyNameSheet
                                        }

                                        val draft = vm.currentDraftFamilyMemberOrNull()
                                        if (draft == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyNameSheet
                                        }

                                        val req =
                                            buildCreateFamilyRequestFromMembers(listOf(draft))
                                        if (req == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyNameSheet
                                        }

                                        isCreatingFamily = true
                                        authViewModel.createFamily(req) { result ->
                                            isCreatingFamily = false
                                            result.fold(
                                                onSuccess = {
                                                    vm.commitDraftFamilyMember()
                                                    vm.clearAddFamilyDraft()
                                                },
                                                onFailure = { e ->
                                                    Log.e(
                                                        "OnboardingHost",
                                                        "createFamily failed on first member",
                                                        e
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        e.localizedMessage
                                                            ?: "Failed to create family",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    },
                                    isAdditionalMember = vm.familyOverviewMembers.isNotEmpty(),
                                    isLoading = isCreatingFamily,
                                    showBackArrow = vm.familyOverviewMembers.size >= 2,
                                    isEditing = vm.editingMemberId != null
                                )
                            }

                            OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE -> {
                                AddFamilyAllSetOrMoreSheet(
                                    onAllSet = { vm.navigateTo(OnboardingStep.FALLING_CAPSULES) },
                                    onAddMore = { vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME) }
                                )
                            }

                            OnboardingStep.ADD_FAMILY_EDIT_MEMBER -> {
                                val editingMember =
                                    vm.familyOverviewMembers.firstOrNull { it.id == vm.editingMemberId }
                                val draft = vm.currentDraftFamilyMemberOrNull()
                                val editAvatarBg =
                                    if (editingMember != null) avatarBackgroundColorForId(
                                        editingMember.backgroundColorId
                                    )
                                    else avatarBackgroundColorForId(vm.addFamilyAvatarSelections[5].orEmpty())
                                val isSaveEnabled = editingMember != null && draft != null && (
                                        draft.name != editingMember.name ||
                                                draft.avatarId != editingMember.avatarId ||
                                                draft.generatedAvatarUrl != editingMember.generatedAvatarUrl
                                        )
                                EditFamilyMemberSheet(
                                    name = vm.addFamilyName,
                                    selectedAvatarId = vm.addFamilyAvatarId,
                                    generatedAvatarUrl = vm.addFamilyGeneratedAvatarUrl,
                                    avatarBackgroundColor = editAvatarBg,
                                    isSaveEnabled = isSaveEnabled,
                                    onNameChange = { vm.addFamilyName = it },
                                    onAvatarSelect = {
                                        vm.addFamilyAvatarId = it
                                        vm.addFamilyAvatarSelections =
                                            vm.addFamilyAvatarSelections + (0 to it)
                                    },
                                    onAddAvatarClick = {
                                        vm.addFamilyAvatarId = ""
                                        vm.addFamilyAvatarSelections = emptyMap()
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_PICKER)
                                    },
                                    onBackClick = handleBack,
                                    onSave = {
                                        focusManager.clearFocus(force = true)
                                        val editingId = vm.editingMemberId
                                        if (editingId == null) return@EditFamilyMemberSheet
                                        val existing =
                                            vm.familyOverviewMembers.firstOrNull { it.id == editingId }
                                        val draft = vm.currentDraftFamilyMemberOrNull()
                                        if (existing == null || draft == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@EditFamilyMemberSheet
                                        }
                                        val updated = existing.copy(
                                            name = draft.name,
                                            avatarId = draft.avatarId,
                                            generatedAvatarUrl = draft.generatedAvatarUrl,
                                            backgroundColorId = draft.backgroundColorId
                                        )
                                        vm.updateFamilyOverviewMember(updated)
                                        vm.clearAddFamilyDraft()
                                        vm.editingMemberId = null
                                        vm.back()
                                    },
                                    largeAvatarContent = {
                                        val trimmedUrl = vm.addFamilyGeneratedAvatarUrl.trim()
                                        val res =
                                            lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData.avatarResOrNull(
                                                vm.addFamilyAvatarId.trim()
                                            )
                                        val circleBg = editAvatarBg
                                        when {
                                            trimmedUrl.isNotBlank() -> {
                                                SubcomposeAsyncImage(
                                                    model = trimmedUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize()
                                                        .clip(CircleShape)
                                                ) {
                                                    when (painter.state) {
                                                        is coil.compose.AsyncImagePainter.State.Loading -> {
                                                            Box(
                                                                modifier = Modifier.fillMaxSize()
                                                                    .background(circleBg),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(24.dp),
                                                                    strokeWidth = 2.dp,
                                                                    color = Primary800
                                                                )
                                                            }
                                                        }

                                                        else -> SubcomposeAsyncImageContent()
                                                    }
                                                }
                                            }

                                            res != null -> {
                                                Image(
                                                    painter = painterResource(id = res),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize()
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }

                                            else -> {
                                                val bg =
                                                    familyPlaceholderColor(vm.addFamilyName.ifBlank { "?" })
                                                Box(
                                                    modifier = Modifier.fillMaxSize()
                                                        .clip(CircleShape).background(bg),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val initial =
                                                        vm.addFamilyName.trim().firstOrNull()
                                                            ?.uppercase() ?: "?"
                                                    Text(
                                                        text = initial,
                                                        style = TextStyle(
                                                            fontFamily = Manrope,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 36.sp,
                                                            color = Color.White
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            OnboardingStep.ADD_FAMILY_AVATAR_PICKER -> {
                                AddFamilyAvatarPickerSheet(
                                    displayName = vm.addFamilyName,
                                    selections = vm.addFamilyAvatarSelections,
                                    onBackClick = handleBack,
                                    onAvatarSelected = {
                                        vm.addFamilyAvatarSelections = it
                                    },
                                    onGenerateClick = {
                                        vm.memojiGenerationCompleted = false
                                        authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections)
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_GENERATING)
                                    }
                                )
                            }

                            OnboardingStep.ADD_FAMILY_AVATAR_GENERATING -> {
                                AddFamilyAvatarGeneratingSheet(
                                    state = emojiState,
                                    selections = vm.addFamilyAvatarSelections,
                                    onBackClick = handleBack,
                                    onRetry = { authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections) },
                                    onRegenerate = { vm.back() },
                                    onAssign = { imageUrl ->
                                        vm.addFamilyGeneratedAvatarUrl = imageUrl
                                        if (vm.editingMemberId != null) {
                                            vm.navigateTo(OnboardingStep.ADD_FAMILY_EDIT_MEMBER)
                                            return@AddFamilyAvatarGeneratingSheet
                                        }
                                        if (isCreatingFamily) return@AddFamilyAvatarGeneratingSheet
                                        val isFirstMember = vm.familyOverviewMembers.isEmpty()
                                        if (!isFirstMember) {
                                            vm.commitDraftFamilyMember()
                                            vm.clearAddFamilyDraft()
                                            vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                            return@AddFamilyAvatarGeneratingSheet
                                        }
                                        val draft = vm.currentDraftFamilyMemberOrNull()
                                        if (draft == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyAvatarGeneratingSheet
                                        }
                                        val req =
                                            buildCreateFamilyRequestFromMembers(listOf(draft))
                                        if (req == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyAvatarGeneratingSheet
                                        }
                                        isCreatingFamily = true
                                        authViewModel.createFamily(req) { result ->
                                            isCreatingFamily = false
                                            result.fold(
                                                onSuccess = {
                                                    vm.commitDraftFamilyMember()
                                                    vm.clearAddFamilyDraft()
                                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME)
                                                },
                                                onFailure = { e ->
                                                    Log.e(
                                                        "OnboardingHost",
                                                        "createFamily failed on first member (Assign)",
                                                        e
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        e.localizedMessage
                                                            ?: "Failed to create family",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
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
        if (memberToInvite != null) {
            InviteMemberOverlayScrim(onDismiss = { memberToInvite = null })
        }

        memberToInvite?.let { member ->
            InviteMemberOverlay(
                member = member,
                onDismiss = { memberToInvite = null },
                onMaybeLater = {
                    if (!isInviting) {
                        vm.setInvitePending(member.id, true)
                        memberToInvite = null
                    }
                },
                onInvite = {
                    if (!isInviting) {
                        runInviteFlow(
                            context = context,
                            authViewModel = authViewModel,
                            vm = vm,
                            member = member,
                            currentFamily = currentFamily,
                            getMembers = { vm.familyOverviewMembers.toList() },
                            buildCreateFamilyRequestFromMembers = {
                                buildCreateFamilyRequestFromMembers(
                                    it
                                )
                            },
                            shareInviteCode = { c, code -> shareInviteCode(c, code) },
                            setInviting = { isInviting = it },
                            onDismiss = { memberToInvite = null }
                        )
                    }
                },
                isLoading = isInviting
            )
        }
    }
}
    