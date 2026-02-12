package lc.fungee.Ingredicheck.onboarding.ui

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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import kotlin.math.absoluteValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.Toast
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.auth.AuthEnv
import lc.fungee.Ingredicheck.auth.AuthState
import lc.fungee.Ingredicheck.auth.MemojiGenState
import lc.fungee.Ingredicheck.auth.GoogleAuthDataSource
import lc.fungee.Ingredicheck.auth.rememberAppleLoginLauncher
import lc.fungee.Ingredicheck.auth.rememberGoogleSignInLauncher
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.family.FamilyDto
import lc.fungee.Ingredicheck.family.FamilyMemberDto
import lc.fungee.Ingredicheck.family.InviteRequest
import lc.fungee.Ingredicheck.memoji.GetStatedScreen
import lc.fungee.Ingredicheck.onboarding.model.OnboardingPersistence
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModelFactory
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Primary700
import lc.fungee.Ingredicheck.ui.theme.Primary800

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

@DrawableRes
private fun familyAvatarResOrNull(avatarId: String): Int? {
    return when (avatarId) {
        "baby_boy" -> R.drawable.family_member_baby
        "baby_girl" -> R.drawable.family_member_baby_girl
        "young_daughter" -> R.drawable.young_daughter_onehand
        "young_son" -> R.drawable.family_member_young_son
        "mom" -> R.drawable.family_member_mom
        "father" -> R.drawable.family_member_father
        "grand_mother" -> R.drawable.family_member_grand_mother
        "grand_father" -> R.drawable.family_member_grand_father
        "dog_avtar" -> R.drawable.avtar_dog
        "cat_avtar" -> R.drawable.avtar_cat
        "litch_avtar" -> R.drawable.avtar_lichi
        "pear_avtar" -> R.drawable.avtar_pear
        "potato_avtar" -> R.drawable.avtar_potatto
        "tomato_avtar" -> R.drawable.avtar_tomato
        else -> null
    }
}

private fun familyPlaceholderColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF9AD0FF),
        Color(0xFFFFB3C1),
        Color(0xFFB9F6CA),
        Color(0xFFFFE29A),
        Color(0xFFD7B9FF),
        Color(0xFFFFC59A)
    )
    val idx = (seed.hashCode().absoluteValue % palette.size)
    return palette[idx]
}

private fun avatarBackgroundColorForId(colorId: String): Color {
    return when (colorId) {
        "color_pastel_blue" -> Color(0xFFA5D8FF)
        "color_warm_pink" -> Color(0xFFFFB3C1)
        "color_soft_green" -> Color(0xFFB9FBC0)
        "color_lavender" -> Color(0xFFE3B8FF)
        "color_orange" -> Color(0xFFFFB74D)
        "color_yellow" -> Color(0xFFFFE082)
        "color_transparent" -> Color.Transparent
        else -> Color.White
    }
}

@Composable
private fun FamilyOverviewBackground(
    members: List<OnboardingViewModel.FamilyOverviewMember>,
    modifier: Modifier = Modifier,
    bottomSheetHeight: Dp = 0.dp,
    onLeaveFamily: (() -> Unit)? = null,
    onInvite: ((OnboardingViewModel.FamilyOverviewMember) -> Unit)? = null,
    onEditMember: ((OnboardingViewModel.FamilyOverviewMember) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 64.dp, start = 20.dp, end = 20.dp)
    ) {

        Text(
            text = "Your Family Overview",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Greyscale150
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = bottomSheetHeight + 24.dp)
        ) {
            members.forEachIndexed { index, member ->
                if (index > 0) Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, Greyscale110.copy(alpha = 0.28f), RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Box(

                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val hasGenerated = member.generatedAvatarUrl.trim().isNotBlank()
                            val circleBackground = if (hasGenerated) {
                                avatarBackgroundColorForId(member.backgroundColorId)
                            } else {
                                Color.White
                            }

                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                // Avatar circle
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(CircleShape)
                                        .border(2.dp, Primary800.copy(alpha = 0.18f), CircleShape)
                                        .background(circleBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val trimmedUrl = member.generatedAvatarUrl.trim()
                                    val res = familyAvatarResOrNull(member.avatarId.trim())
                                    when {
                                        trimmedUrl.isNotBlank() -> {
                                            SubcomposeAsyncImage(
                                                model = trimmedUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                            ) {
                                                when (painter.state) {
                                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(circleBackground),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                strokeWidth = 2.dp,
                                                                color = Primary800
                                                            )
                                                        }
                                                    }
                                                    else -> {
                                                        SubcomposeAsyncImageContent()
                                                    }
                                                }
                                            }
                                        }
                                        res != null -> {
                                            Image(
                                                painter = androidx.compose.ui.res.painterResource(id = res),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                            )
                                        }
                                        else -> {
                                            val bg = familyPlaceholderColor(member.name)
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .background(bg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val initial = member.name.trim().firstOrNull()?.uppercase() ?: "?"
                                                Text(
                                                    text = initial,
                                                    style = TextStyle(
                                                        fontFamily = Manrope,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                // Edit (pen) icon overlay
                                if (onEditMember != null) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 2.dp, y = 2.dp)
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(color = Greyscale40)
                                            .clickable { onEditMember(member) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.pen_line_icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.name,
                                    style = TextStyle(
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Greyscale150
                                    )
                                )

                                if (member.invitePending) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.50.dp))
                                            .background(Color(0xFFFFF9ED)) // soft yellow pending pill
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.exclamation_circle),
                                                contentDescription = null,
                                                tint = Color(0xFFFAB222),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Pending",
                                                style = TextStyle(
                                                    fontFamily = Nunito,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFFAB222)
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (member.joined) "(You)" else "Not joined yet !",
                                        style = TextStyle(
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Greyscale110
                                        )
                                    )
                                }
                            }

                            val isFirst = index == 0
                            val isPending = member.invitePending
                            val actionText = if (isFirst) {
                                "Leave Family"
                            } else if (isPending) {
                                "Re-invite"
                            } else {
                                "Invite"
                            }
                            val leaveFamilyColor = Color(0xFFFF3F31)
                            val inviteColor = Color(0xFF75990E)
                            val actionColor = if (isFirst) leaveFamilyColor else inviteColor
                            val borderColor = if (isFirst) leaveFamilyColor else Greyscale40

                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(18.dp) ,)
                                    .clickable {
                                        if (isFirst) {
                                            onLeaveFamily?.invoke()
                                        } else {
                                            onInvite?.invoke(member)
                                        }
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!isFirst) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.share_icon),
                                            contentDescription = null,
                                            tint = actionColor,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(end = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = actionText,
                                        style = TextStyle(
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = actionColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingHost(
    authViewModel: AuthViewModel,
    onExitOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val savedStateOwner = LocalSavedStateRegistryOwner.current
    val persistence = remember(context) { OnboardingPersistence(context.applicationContext) }
    val factory = remember(savedStateOwner, persistence) {
        OnboardingViewModelFactory(owner = savedStateOwner, persistence = persistence)
    }

    val vm: OnboardingViewModel = viewModel(factory = factory)
    val step = vm.currentStep
    var isCreatingFamily by remember { mutableStateOf(false) }
    var isInviting by remember { mutableStateOf(false) }
    val isRestored = vm.isRestored
    val authState by authViewModel.state.collectAsState()
    val memojiState by authViewModel.memojiState.collectAsState()
    val isAuthLoading = authState is AuthState.Loading
    val currentFamily by authViewModel.currentFamily.collectAsState()

    var sheetHeight by remember { mutableStateOf(0.dp) }
    var memberToInvite by remember { mutableStateOf<OnboardingViewModel.FamilyOverviewMember?>(null) }

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
            step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING ||
            step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE ||
            step == OnboardingStep.ADD_FAMILY_EDIT_MEMBER
        ) {
            authViewModel.debugLogCurrentSession("Entered $step (before ensureAnonymousSession)")
            authViewModel.ensureAnonymousSession()
        }

        // Restore memoji generation UI state after process death:
        // - If we are on the generating screen and have a saved image URL with
        //   memojiGenerationCompleted == true, restore Success state.
        if (step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING) {
            val currentMemoji = authViewModel.memojiState.value
            if (currentMemoji is MemojiGenState.Idle && vm.memojiGenerationCompleted) {
                val url = vm.addFamilyGeneratedAvatarUrl.trim()
                if (url.isNotBlank()) {
                    authViewModel.restoreMemojiSuccess(url)
                }
            }
        }
    }

    val activity = LocalContext.current.findActivity()
    val googleLauncher = rememberGoogleSignInLauncher(activity = activity, authViewModel = authViewModel)
    val appleLauncher = rememberAppleLoginLauncher(activity = activity, authViewModel = authViewModel)

    // Debounce back to prevent double-tap from going 2 screens back
    var lastBackTime by remember { mutableStateOf(0L) }
    val vmRef = rememberUpdatedState(vm)
    val onExitRef = rememberUpdatedState(onExitOnboarding)
    val handleBack: () -> Unit = handleBack@ {
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

    val isAddMoreMemberNoBack = step == OnboardingStep.ADD_FAMILY_NAME && vm.familyOverviewMembers.size == 1
    val isAllSetOrMoreScreen = step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE
    val isFallingCapsulesScreen = step == OnboardingStep.ADD_FAMILY_FALLING_CAPSULES
    BackHandler(enabled = true) {
        if (!isAddMoreMemberNoBack && !isAllSetOrMoreScreen && !isFallingCapsulesScreen) {
            handleBack()
        }
    }

    val selectedAllergyMemberIdState = remember(vm.familyOverviewMembers.size) {
        mutableStateOf(vm.familyOverviewMembers.firstOrNull()?.id.orEmpty())
    }
    val selectedAllergies = remember { mutableStateListOf<String>() }

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
        OnboardingStep.ADD_FAMILY_FALLING_CAPSULES -> 5
        OnboardingStep.ADD_FAMILY_ALLERGIES -> 6
        else -> 0
    }

    // Keep addFamilyGeneratedAvatarUrl in sync with memoji Success so it can be restored.
    LaunchedEffect(memojiState) {
        if (memojiState is MemojiGenState.Success) {
            vm.addFamilyGeneratedAvatarUrl = (memojiState as MemojiGenState.Success).imageUrl
            vm.memojiGenerationCompleted = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingShell(
            onDismissRequest = onExitOnboarding,
            horizontalPaddingEnabled = step != OnboardingStep.ADD_FAMILY_AVATAR_PICKER,
            showFocusedShadow = step == OnboardingStep.SIGN_IN_INITIAL ||
                step == OnboardingStep.SIGN_IN_SOCIAL_LOGIN,
            onSheetHeightChanged = { sheetHeight = it },
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
                                    vm.addFamilyGeneratedAvatarUrl = member.generatedAvatarUrl
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
                        FallingCapsulesScreen(modifier = Modifier.fillMaxSize())
                    }
                    6 -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF2F2F7))
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Spacer(modifier = Modifier.height(48.dp))
                                AnimatedProgressLine(progress = 0.1f)
                                Spacer(modifier = Modifier.height(10.dp))
                                CapsuleStepperRow(
                                    steps = listOf(
                                        CapsuleStep("allergies", "Allergies", R.drawable.ic_step_allergies),
                                        CapsuleStep(
                                            "intolerances",
                                            "Intolerances",
                                            R.drawable.ic_step_intolerances
                                        ),
                                        CapsuleStep(
                                            "health_conditions",
                                            "Health Conditions",
                                            R.drawable.ic_step_health_conditions
                                        ),
                                        CapsuleStep("life_stage", "Life Stage", R.drawable.ic_step_life_style),
                                        CapsuleStep("region", "Region", R.drawable.ic_step_region),
                                        CapsuleStep("avoid", "Avoid", R.drawable.ic_step_avoid_cross),
                                        CapsuleStep(
                                            "life_style",
                                            "Life Style",
                                            R.drawable.ic_step_diet_preferences
                                        ),
                                        CapsuleStep("nutrition", "Nutrition", R.drawable.ic_step_meals),
                                        CapsuleStep("ethical", "Ethical", R.drawable.ic_step_ethical),
                                        CapsuleStep("taste", "Taste", R.drawable.iconoir_chocolate)
                                    ),
                                    activeIndex = 0
                                )
                            }
                        }
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
                        OnboardingStep.ADD_FAMILY_FALLING_CAPSULES -> {
                            AddFamilyLetsGoSheet(
                                onLetsGo = {
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_ALLERGIES)
                                }
                            )
                        }
                        OnboardingStep.ADD_FAMILY_ALLERGIES -> {
                            AddFamilyAllergiesSheet(
                                members = vm.familyOverviewMembers.toList(),
                                selectedMemberId = selectedAllergyMemberIdState.value,
                                selectedAllergies = selectedAllergies.toSet(),
                                onMemberSelected = { selectedAllergyMemberIdState.value = it },
                                onToggleAllergy = { allergyId ->
                                    if (selectedAllergies.contains(allergyId)) {
                                        selectedAllergies.remove(allergyId)
                                    } else {
                                        selectedAllergies.add(allergyId)
                                    }
                                },
                                onNext = {
                                    onExitOnboarding()
                                }
                            )
                        }
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
                                onBackClick = handleBack,
                                onGoogleClick = {
                                    if (activity != null) {
                                        val client = GoogleAuthDataSource.getClient(activity)
                                        googleLauncher.launch(client.signInIntent)
                                    }
                                },
                                onAppleClick = {
                                    if (activity != null) {
                                        val redirectUri =
                                            "${AuthEnv.OAUTH_REDIRECT_SCHEME}://${AuthEnv.OAUTH_REDIRECT_HOST}"
                                        val authUrl = Uri.parse(AuthEnv.SUPABASE_URL).buildUpon()
                                            .appendPath("auth")
                                            .appendPath("v1")
                                            .appendPath("authorize")
                                            .appendQueryParameter("provider", "apple")
                                            .appendQueryParameter("redirect_to", redirectUri)
                                            .appendQueryParameter("flow_type", "implicit")
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
                                }
                            )
                        }

                        OnboardingStep.SIGN_IN_INVITE_CODE -> {
                            SignInInviteCodeSheet(
                                onBackClick = handleBack,
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
                                onBackClick = handleBack,
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
                                onBackClick = handleBack,
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
                                            imageFileHash = draft.generatedAvatarUrl.trim().ifBlank { null }
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
                                                        e.localizedMessage ?: "Failed to add member",
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

                                    val req = buildCreateFamilyRequestFromMembers(listOf(draft))
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
                                                    e.localizedMessage ?: "Failed to create family",
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
                                onAllSet = { vm.navigateTo(OnboardingStep.ADD_FAMILY_FALLING_CAPSULES) },
                                onAddMore = { vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME) }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_EDIT_MEMBER -> {
                            val editingMember =
                                vm.familyOverviewMembers.firstOrNull { it.id == vm.editingMemberId }
                            val draft = vm.currentDraftFamilyMemberOrNull()
                            val editAvatarBg =
                                if (editingMember != null) avatarBackgroundColorForId(editingMember.backgroundColorId)
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
                                    vm.addFamilyAvatarSelections = vm.addFamilyAvatarSelections + (0 to it)
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
                                    val existing = vm.familyOverviewMembers.firstOrNull { it.id == editingId }
                                    val draft = vm.currentDraftFamilyMemberOrNull()
                                    if (existing == null || draft == null) {
                                        Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
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
                                    val res = familyAvatarResOrNull(vm.addFamilyAvatarId.trim())
                                    val circleBg = editAvatarBg
                                    when {
                                        trimmedUrl.isNotBlank() -> {
                                            SubcomposeAsyncImage(
                                                model = trimmedUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            ) {
                                                when (painter.state) {
                                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize().background(circleBg),
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
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        else -> {
                                            val bg = familyPlaceholderColor(vm.addFamilyName.ifBlank { "?" })
                                            Box(
                                                modifier = Modifier.fillMaxSize().clip(CircleShape).background(bg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val initial = vm.addFamilyName.trim().firstOrNull()?.uppercase() ?: "?"
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
                                state = memojiState,
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
                                    val req = buildCreateFamilyRequestFromMembers(listOf(draft))
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
                                                    e.localizedMessage ?: "Failed to create family",
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


        // Overlay with opacity when invite confirmation is shown
        if (memberToInvite != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
                    .background(Color.Black)
                    .clickable { memberToInvite = null }
            )
        }

        // Invite confirmation bottom sheet
        memberToInvite?.let { member ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                NonDraggableBottomSheet(
                    onDismissRequest = { memberToInvite = null },
                    horizontalPaddingEnabled = true
                ) {
                    InviteConfirmationSheet(
                        memberName = member.name,
                        onMayBeLater = {
                            if (!isInviting) {
                                vm.setInvitePending(member.id, true)
                                memberToInvite = null
                            }
                        },
                        onInvite = {
                            if (isInviting) return@InviteConfirmationSheet
                            isInviting = true
                            Log.d(
                                "OnboardingHost",
                                "Invite confirmed for memberId=${member.id}, name=${member.name}"
                            )
                            // Ensure family exists on backend before inviting.
                            if (currentFamily != null) {
                                authViewModel.inviteFamilyMember(member.id) { result ->
                                    val code = result.getOrNull()
                                    if (code != null) {
                                        vm.setInvitePending(member.id, true)
                                        memberToInvite = null
                                        shareInviteCode(context, code)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to create invite",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    isInviting = false
                                }
                            } else {
                                val req = buildCreateFamilyRequestFromMembers(vm.familyOverviewMembers.toList())
                                if (req == null) {
                                    authViewModel.inviteFamilyMember(member.id) { result ->
                                        val code = result.getOrNull()
                                        if (code != null) {
                                            vm.setInvitePending(member.id, true)
                                            memberToInvite = null
                                            shareInviteCode(context, code)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed to create invite",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isInviting = false
                                    }
                                } else {
                                    authViewModel.createFamily(req) { createResult ->
                                        createResult.fold(
                                            onSuccess = {
                                                authViewModel.inviteFamilyMember(member.id) { result ->
                                                    val code = result.getOrNull()
                                                    if (code != null) {
                                                        vm.setInvitePending(member.id, true)
                                                        memberToInvite = null
                                                        shareInviteCode(context, code)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to create invite",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    isInviting = false
                                                }
                                            },
                                            onFailure = {
                                                val msg = it.localizedMessage.orEmpty()
                                                val isDuplicateMemberId =
                                                    msg.contains("members_pkey", ignoreCase = true) ||
                                                        msg.contains("duplicate key", ignoreCase = true)

                                                if (isDuplicateMemberId) {
                                                    Log.e(
                                                        "OnboardingHost",
                                                        "createFamily failed due to duplicate memberId; regenerating ids + retry",
                                                        it
                                                    )

                                                    val idMap = vm.regenerateFamilyOverviewMemberIds()
                                                    val regeneratedMemberId = idMap[member.id] ?: member.id
                                                    val retryReq = buildCreateFamilyRequestFromMembers(vm.familyOverviewMembers.toList())

                                                    if (retryReq == null) {
                                                        isInviting = false
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to retry createFamily",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        return@createFamily
                                                    }

                                                    authViewModel.createFamily(retryReq) { retryResult ->
                                                        retryResult.fold(
                                                            onSuccess = {
                                                                authViewModel.inviteFamilyMember(regeneratedMemberId) { result ->
                                                                    val code = result.getOrNull()
                                                                    if (code != null) {
                                                                        vm.setInvitePending(regeneratedMemberId, true)
                                                                        memberToInvite = null
                                                                        shareInviteCode(context, code)
                                                                    } else {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Failed to create invite",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    isInviting = false
                                                                }
                                                            },
                                                            onFailure = { retryErr ->
                                                                Log.e(
                                                                    "OnboardingHost",
                                                                    "createFamily retry failed; not inviting memberId=$regeneratedMemberId",
                                                                    retryErr
                                                                )
                                                                isInviting = false
                                                                Toast.makeText(
                                                                    context,
                                                                    retryErr.localizedMessage
                                                                        ?: "Failed to create family",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        )
                                                    }

                                                    return@createFamily
                                                }

                                                isInviting = false
                                                Log.e(
                                                    "OnboardingHost",
                                                    "createFamily failed; not inviting memberId=${member.id}",
                                                    it
                                                )
                                                Toast.makeText(
                                                    context,
                                                    it.localizedMessage ?: "Failed to create family",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        isLoading = isInviting
                    )
                }
            }
        }
    }
}
