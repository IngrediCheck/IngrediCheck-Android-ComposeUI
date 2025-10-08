package lc.fungee.IngrediCheck.ui.view.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.delay
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.viewmodel.AppleAuthViewModel
import lc.fungee.IngrediCheck.model.dto.ValidationState
import lc.fungee.IngrediCheck.viewmodel.PreferenceViewModel
import lc.fungee.IngrediCheck.ui.view.component.BottomBar
import lc.fungee.IngrediCheck.ui.theme.AppColors
import lc.fungee.IngrediCheck.ui.theme.BrandGlow
import lc.fungee.IngrediCheck.ui.theme.Greyscale700
import lc.fungee.IngrediCheck.ui.theme.Greyscale50
import lc.fungee.IngrediCheck.ui.theme.Statusfail
import lc.fungee.IngrediCheck.ui.theme.White
import lc.fungee.IngrediCheck.model.utils.AutoScanGate
import lc.fungee.IngrediCheck.ui.view.screens.check.CheckBottomSheet
import lc.fungee.IngrediCheck.ui.view.screens.setting.SettingScreen
import lc.fungee.IngrediCheck.analytics.Analytics
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    preferenceViewModel: PreferenceViewModel,
    supabaseClient: SupabaseClient,
    functionsBaseUrl: String,
    anonKey: String,
    viewModel: AppleAuthViewModel,
    googleSignInClient: GoogleSignInClient
)
{
    var showSheet by remember { mutableStateOf(false) }
//        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // use the instance (lowercase), not the class name
    val isRefreshing by preferenceViewModel.isRefreshing.collectAsState()
    var showSheetSetting by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }


    // Analytics: Home view appeared (iOS parity)
    LaunchedEffect(Unit) {
        Analytics.trackHomeViewAppeared()
    }

    // One-shot auto-open on app start using pending flag
    var didAutoOpen by rememberSaveable { mutableStateOf(false) }
    val autoScanEnabled by preferenceViewModel.autoScanFlow.collectAsState(initial = false)
    LaunchedEffect(autoScanEnabled) {
        if (autoScanEnabled && !AutoScanGate.openedOnceInProcess) {
            AutoScanGate.openedOnceInProcess = true
            showSheet = true
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { preferenceViewModel.refreshPreferences() }
    )

    Scaffold(
        bottomBar = { BottomBar(navController = navController, onCheckClick = { showSheet = true }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(White)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            // Settings BottomSheet
            if (showSheetSetting) {
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                ModalBottomSheet(
                    onDismissRequest = { showSheetSetting = false },
                    sheetState = sheetState,
                    containerColor = AppColors.SurfaceMuted,
                    dragHandle = null,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.94f)
                            .fillMaxWidth()
                    ) {
                        SettingScreen(
                            preferenceViewModel = preferenceViewModel,
                            onDismiss = { showSheetSetting = false },
                            supabaseClient = supabaseClient,
                            onRequireReauth = {
                                showSheetSetting = false
                                navController.navigate("welcome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            viewModel = viewModel,
                            googleSignInClient = googleSignInClient
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(White)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "Your dietary preferences",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = {
                            Analytics.trackButtonTapped("Settings")
                            showSheetSetting = true
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.settingicon),
                            contentDescription = "Setting Icon",
                            modifier = Modifier.size(25.dp),
                            tint = AppColors.Brand
                        )
                    }
                }

                Box(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .then(
                            if (isFocused) {
                                Modifier.Companion.shadow(
                                    elevation = 16.dp,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                    ambientColor = BrandGlow,
                                    spotColor = AppColors.Brand
                                )
                            } else {
                                Modifier.Companion
                            }
                        )
                ) {
                    val isValidating =
                        preferenceViewModel.validationState is ValidationState.Validating
                    val isError = preferenceViewModel.validationState is ValidationState.Failure
                    OutlinedTextField(
                        value = preferenceViewModel.newPreferenceText,
                        onValueChange = { newText ->
                            if (!isValidating) { // prevent editing during "Thinking"
                                preferenceViewModel.newPreferenceText = newText
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Enter dietary preference here",
                                style = TextStyle(fontSize = 20.sp),
                               modifier = Modifier.Companion.alpha(0.4f)
                            )
                        },
                        textStyle = TextStyle(color = Greyscale700, fontSize = 20.sp),
                        modifier = Modifier.Companion.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Companion.Default.copy(imeAction = ImeAction.Companion.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (preferenceViewModel.newPreferenceText.isNotBlank()) {
                                preferenceViewModel.inputComplete()
                            }
                        }),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        enabled = !isValidating,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (isError) Statusfail else AppColors.Brand,
                            unfocusedBorderColor = if (isError) Statusfail else Color.Companion.Transparent,
                            backgroundColor = Greyscale50,
                            cursorColor = AppColors.Brand
                        ),
                        maxLines = 3,
                        interactionSource = interactionSource,
                        trailingIcon = {
                            if (preferenceViewModel.newPreferenceText.isNotEmpty()) {
                                IconButton(onClick = {
                                    preferenceViewModel.newPreferenceText = ""
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.trailingicon),
                                        contentDescription = "Clear text"
                                    )
                                }
                            }
                        }
                    )
//
                    Spacer(modifier = Modifier.Companion.height(16.dp))
                }


//                    Spacer(modifier = Modifier.height(16.dp))

                // Validation feedback
                when (val state = preferenceViewModel.validationState) {
                    is ValidationState.Validating -> {
                        Text(
                            "Thinking...",
                            color = AppColors.Brand,
                            fontSize = 18.sp,
                            modifier = Modifier.Companion.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    is ValidationState.Failure -> {
                        Text(
                            state.message,
                            color = Statusfail,
                            fontSize = 18.sp,
                            modifier = Modifier.Companion.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                    }

                    is ValidationState.Success -> {
                        var showSuccess by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            showSuccess = true
//                    Text("Preference added successfully!", color = PrimaryGreen100, fontSize = 14.sp)
                            delay(500)
                            showSuccess = false
                        }
                        if (showSuccess) {
                            Text(
                                "Preference added successfully...",
                                color = AppColors.Brand,
                                fontSize = 18.sp,
                                modifier = Modifier.Companion.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.Companion.height(24.dp))

                // List / Demo transition
                AnimatedContent(
                    targetState = Pair(isRefreshing, preferenceViewModel.preferences.isEmpty()),
                    label = "preferencesTransition"
                ) { (loading, isEmpty) ->
                    when {
                        loading -> {
                            // While loading, show nothing to avoid flashing the empty state
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                        isEmpty -> {
                            // Only show empty state after loading completes and list is still empty
                            PreferenceEmptyState()
                        }
                        else -> {
                            PreferencesList(preferenceViewModel, onEdit = { pref ->
                                preferenceViewModel.startEditPreference(pref)
                            })
                        }
                    }
                }
            }
            // Place the indicator at the very top of the content so it overlays correctly
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.Companion.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding())
                    .zIndex(1f)
            )
        }

    }

    // BottomSheet over Home
    if (showSheet) {
        CheckBottomSheet(
            onDismiss = { showSheet = false },
            supabaseClient = supabaseClient,
            functionsBaseUrl = functionsBaseUrl,
            anonKey = anonKey
        )
    }
}