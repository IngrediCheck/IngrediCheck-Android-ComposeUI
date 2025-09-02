    package lc.fungee.IngrediCheck.ui.screens.home

    import androidx.compose.animation.AnimatedContent
    import androidx.compose.foundation.background
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.KeyboardActions
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.foundation.verticalScroll

    import androidx.compose.material.ExperimentalMaterialApi

    import androidx.compose.material.Icon
    import androidx.compose.material.IconButton

    import androidx.compose.material.OutlinedTextField

    import androidx.compose.material.Tab
    import androidx.compose.material.TextFieldDefaults
    import androidx.compose.material.pullrefresh.PullRefreshIndicator

    import androidx.compose.material.pullrefresh.pullRefresh
    import androidx.compose.material.pullrefresh.rememberPullRefreshState


    import androidx.compose.material3.CircularProgressIndicator

    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.ModalBottomSheet

    import androidx.compose.material3.Scaffold

    import androidx.compose.material3.TabRow
    import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
    import androidx.compose.material3.Text
    import androidx.compose.material3.rememberModalBottomSheetState

    import androidx.compose.runtime.*
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.alpha
    import androidx.compose.ui.draw.clip
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
    import kotlinx.coroutines.delay

    import lc.fungee.IngrediCheck.R

    import lc.fungee.IngrediCheck.data.model.ValidationState
    import lc.fungee.IngrediCheck.data.repository.PreferenceViewModel
    import lc.fungee.IngrediCheck.ui.component.PreferenceEmptyState
    import lc.fungee.IngrediCheck.ui.component.BottomBar
    import lc.fungee.IngrediCheck.ui.theme.Greyscale50
    import lc.fungee.IngrediCheck.ui.theme.Greyscale700
    import lc.fungee.IngrediCheck.ui.theme.PrimaryGreen100
    import lc.fungee.IngrediCheck.ui.theme.Statusfail
    import lc.fungee.IngrediCheck.ui.theme.White

    import lc.fungee.IngrediCheck.ui.screens.check.CameraPreview
    import lc.fungee.IngrediCheck.ui.screens.check.CheckBottomSheet

    import lc.fungee.IngrediCheck.ui.theme.LabelsPrimary




    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(
        navController: NavController,
        preferenceViewModel: PreferenceViewModel,
        supabaseClient: io.github.jan.supabase.SupabaseClient,
        functionsBaseUrl: String,
        anonKey: String
    ) {
        var showSheet by remember { mutableStateOf(false) }
//        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        // ✅ use the instance (lowercase), not the class name
        val isRefreshing by preferenceViewModel.isRefreshing.collectAsState()

        var isFocused by remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }



        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { preferenceViewModel.refreshPreferences() }
        )

        Scaffold(
            bottomBar = { BottomBar(navController = navController, onCheckClick = { showSheet = true }) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
//                if (!isOnline) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(Color.Red)
//                            .padding(8.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "No Internet Connection",
//                            color = Color.White,
//                            style = MaterialTheme.typography.h4
//                        )
//                    }
//                }



               Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // ✅ enables swipe
                        .background(White)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .pullRefresh(pullRefreshState)
                ) {


                    Text(
                        text = "Your dietary preference",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 16.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    )


                    // Input Field
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .then(
                                if (isFocused) Modifier.shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(8.dp),
                                    ambientColor = Color(0xFFCBEB6E),
                                    spotColor = PrimaryGreen100
                                ) else Modifier
                            )
                    ) {
                        val isValidating = preferenceViewModel.validationState is ValidationState.Validating
                        val isError = preferenceViewModel.validationState is ValidationState.Failure
                        OutlinedTextField(
                            value = preferenceViewModel.newPreferenceText,
                            onValueChange = { newText ->
                                if (!isValidating) { // ⛔ prevent editing during "Thinking"
                                    preferenceViewModel.newPreferenceText = newText
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Enter dietary preference here",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier.alpha(0.5f)
                                )
                            },
                            textStyle = TextStyle(color = Greyscale700, fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (preferenceViewModel.newPreferenceText.isNotBlank()) {
                                    preferenceViewModel.inputComplete()
                                }
                            }),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isValidating,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = if (isError) Statusfail else PrimaryGreen100,
                                unfocusedBorderColor = if (isError) Statusfail else Color.Transparent,
                                backgroundColor = Greyscale50,
                                cursorColor = PrimaryGreen100
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
                        PullRefreshIndicator(
                            refreshing = isRefreshing,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter) // ✅ position correctly
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }


//                    Spacer(modifier = Modifier.height(16.dp))

                    // Validation feedback
                    when (val state = preferenceViewModel.validationState) {
                        is ValidationState.Validating -> {
                            Row() {
                                Text("Thinking  ", color = PrimaryGreen100, fontSize = 14.sp)
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(14.dp).align(Alignment.CenterVertically),
                                    color = PrimaryGreen100

                                )
                            }
                        }

                        is ValidationState.Failure -> {
                            Text(state.message, color = Statusfail, fontSize = 14.sp)

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
                                    color = PrimaryGreen100,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // List / Demo transition
                    AnimatedContent(
                        targetState = preferenceViewModel.preferences.isEmpty(),
                        label = "preferencesTransition"
                    ) { isEmpty ->
                        if (isEmpty) {
                            PreferenceEmptyState()

                        } else {
    //
                            PreferencesList(preferenceViewModel, onEdit = { pref ->
                                preferenceViewModel.startEditPreference(pref)
                            })
                        }
                    }
                }
            }
        }

        // 👇 BottomSheet over Home
        if (showSheet) {
            CheckBottomSheet(
                onDismiss = { showSheet = false },
                supabaseClient = supabaseClient,
                functionsBaseUrl = functionsBaseUrl,
                anonKey = anonKey
            )
        }
        }



//


