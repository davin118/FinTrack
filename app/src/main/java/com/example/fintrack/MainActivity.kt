package com.example.fintrack

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Check
import com.example.fintrack.ui.finance.FinanceTransaction
import com.example.fintrack.ui.finance.FinanceUiState
import com.example.fintrack.ui.finance.FinanceViewModel
import com.example.fintrack.ui.finance.TransactionCategory
import com.example.fintrack.ui.finance.TransactionType
import com.example.fintrack.ui.components.BottomBarItemModel
import com.example.fintrack.ui.components.BrandWordmarkText
import com.example.fintrack.ui.components.OrvynBottomBar
import com.example.fintrack.ui.dialogs.BackupPasswordDialog
import com.example.fintrack.ui.dialogs.ContributeSavingGoalDialog
import com.example.fintrack.ui.dialogs.CreateAccountDialog
import com.example.fintrack.ui.dialogs.CreateDebtDialog
import com.example.fintrack.ui.dialogs.CreateRecurringPlanDialog
import com.example.fintrack.ui.dialogs.CreateSavingGoalDialog
import com.example.fintrack.ui.dialogs.CreateSubscriptionDialog
import com.example.fintrack.ui.dialogs.EditProfileDialog
import com.example.fintrack.ui.dialogs.PayDebtDialog
import com.example.fintrack.ui.dialogs.AppPopupDialog
import com.example.fintrack.ui.dialogs.PopupConfirmButton
import com.example.fintrack.ui.dialogs.PopupDismissButton
import com.example.fintrack.ui.dialogs.PopupHeaderIcon
import com.example.fintrack.ui.dialogs.PopupInputField
import com.example.fintrack.ui.dialogs.PopupTitle
import com.example.fintrack.ui.dialogs.TransferDialog
import com.example.fintrack.ui.dialogs.UserSetupDialog
import com.example.fintrack.ui.screens.BudgetsScreen
import com.example.fintrack.ui.screens.SummaryScreen
import com.example.fintrack.ui.screens.TransactionsScreen
import com.example.fintrack.ui.screens.ToolsModuleScreen
import com.example.fintrack.ui.theme.AppFintechTeal
import com.example.fintrack.ui.theme.AppThemeMode
import com.example.fintrack.ui.theme.FinTrackTheme
import kotlinx.coroutines.delay

private val bottomDestinations = listOf(
    BottomBarItemModel(route = "summary", label = "Resumen", icon = Icons.Filled.Wallet),
    BottomBarItemModel(route = "transactions", label = "Movimientos", icon = Icons.AutoMirrored.Filled.ReceiptLong),
    BottomBarItemModel(route = "budgets", label = "Presupuestos", icon = Icons.Filled.Analytics),
    BottomBarItemModel(route = "tools", label = "Herramientas", icon = Icons.Filled.Settings)
)

private val backgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF5F7FF),
        Color(0xFFE8F5FF),
        Color(0xFFF9F5FF)
    )
)

private val routeOrder = mapOf(
    "summary" to 0,
    "transactions" to 1,
    "budgets" to 2,
    "tools" to 3
)

private val topBarColorByRoute = mapOf(
    "summary" to Color(0x66D7E8FF),
    "transactions" to Color(0x66CFE1FF),
    "budgets" to Color(0x66D9F0E5),
    "tools" to Color(0x66E2DCF8)
)

private val topBarDarkColorByRoute = mapOf(
    "summary" to Color(0xCC1B2D44),
    "transactions" to Color(0xCC1A2940),
    "budgets" to Color(0xCC18352D),
    "tools" to Color(0xCC2A2442)
)

private val backgroundStartColorByRoute = mapOf(
    "summary" to Color(0xFFF5F7FF),
    "transactions" to Color(0xFFF2F8FF),
    "budgets" to Color(0xFFF4FBF8),
    "tools" to Color(0xFFF8F5FF)
)
private val backgroundStartDarkColorByRoute = mapOf(
    "summary" to Color(0xFF0E1520),
    "transactions" to Color(0xFF0F1624),
    "budgets" to Color(0xFF0E1A17),
    "tools" to Color(0xFF161220)
)

private val backgroundMidColorByRoute = mapOf(
    "summary" to Color(0xFFE8F5FF),
    "transactions" to Color(0xFFE6F1FF),
    "budgets" to Color(0xFFE6F6EF),
    "tools" to Color(0xFFEDE7FB)
)
private val backgroundMidDarkColorByRoute = mapOf(
    "summary" to Color(0xFF142234),
    "transactions" to Color(0xFF132033),
    "budgets" to Color(0xFF113128),
    "tools" to Color(0xFF1F1A33)
)

private val backgroundEndColorByRoute = mapOf(
    "summary" to Color(0xFFF9F5FF),
    "transactions" to Color(0xFFF4F6FF),
    "budgets" to Color(0xFFF2FBF6),
    "tools" to Color(0xFFFDF7FF)
)
private val backgroundEndDarkColorByRoute = mapOf(
    "summary" to Color(0xFF121A28),
    "transactions" to Color(0xFF121927),
    "budgets" to Color(0xFF12231D),
    "tools" to Color(0xFF1A1630)
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val themePrefs = remember {
                context.getSharedPreferences("ui_prefs", MODE_PRIVATE)
            }
            var themeMode by rememberSaveable {
                mutableStateOf(
                    AppThemeMode.fromPref(themePrefs.getString("theme_mode", AppThemeMode.SYSTEM.prefValue))
                )
            }

            FinTrackTheme(themeMode = themeMode) {
                FinanceApp(
                    themeMode = themeMode,
                    onThemeModeChange = { next ->
                        themeMode = next
                        themePrefs.edit().putString("theme_mode", next.prefValue).apply()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceApp(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    financeViewModel: FinanceViewModel = viewModel()
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    var requestedNotificationsPermission by remember { mutableStateOf(false) }
    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !requestedNotificationsPermission) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            requestedNotificationsPermission = true
        }
    }

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(500)
        isLoading = false
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val topBarTargetColor = if (darkTheme) {
        topBarDarkColorByRoute[currentRoute] ?: Color(0xCC1B2D44)
    } else {
        topBarColorByRoute[currentRoute] ?: Color(0x66D7E8FF)
    }
    val topBarContainerColor by animateColorAsState(
        targetValue = topBarTargetColor,
        animationSpec = tween(320),
        label = "topBarContainerColor"
    )
    val bgStartColor by animateColorAsState(
        targetValue = if (darkTheme) {
            backgroundStartDarkColorByRoute[currentRoute] ?: Color(0xFF0E1520)
        } else {
            backgroundStartColorByRoute[currentRoute] ?: Color(0xFFF5F7FF)
        },
        animationSpec = tween(380),
        label = "bgStartColor"
    )
    val bgMidColor by animateColorAsState(
        targetValue = if (darkTheme) {
            backgroundMidDarkColorByRoute[currentRoute] ?: Color(0xFF142234)
        } else {
            backgroundMidColorByRoute[currentRoute] ?: Color(0xFFE8F5FF)
        },
        animationSpec = tween(380),
        label = "bgMidColor"
    )
    val bgEndColor by animateColorAsState(
        targetValue = if (darkTheme) {
            backgroundEndDarkColorByRoute[currentRoute] ?: Color(0xFF121A28)
        } else {
            backgroundEndColorByRoute[currentRoute] ?: Color(0xFFF9F5FF)
        },
        animationSpec = tween(380),
        label = "bgEndColor"
    )
    val animatedBackgroundGradient = Brush.verticalGradient(
        colors = listOf(bgStartColor, bgMidColor, bgEndColor)
    )

    val uiState by financeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastSnackbarMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(uiState.backupStatusMessage, uiState.recurringStatusMessage) {
        val next = uiState.backupStatusMessage ?: uiState.recurringStatusMessage
        if (!next.isNullOrBlank() && next != lastSnackbarMessage) {
            lastSnackbarMessage = next
            snackbarHostState.showSnackbar(next)
        }
    }
    var topBarLogoPulse by remember { mutableStateOf(false) }
    LaunchedEffect(currentRoute) {
        if (currentRoute != null) {
            topBarLogoPulse = true
            delay(180)
            topBarLogoPulse = false
        }
    }
    val topBarLogoScale by animateFloatAsState(
        targetValue = if (topBarLogoPulse) 1.08f else 1f,
        animationSpec = tween(240),
        label = "topBarLogoScale"
    )
    val topBarLogoRotation by animateFloatAsState(
        targetValue = if (topBarLogoPulse) 5f else 0f,
        animationSpec = tween(240),
        label = "topBarLogoRotation"
    )

    var formTarget by remember { mutableStateOf<FinanceTransaction?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<FinanceTransaction?>(null) }
    var showCreateBackupDialog by remember { mutableStateOf(false) }
    var showRestoreBackupDialog by remember { mutableStateOf(false) }
    var showCreateAccountDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var showCreateSubscriptionDialog by remember { mutableStateOf(false) }
    var showCreateSavingGoalDialog by remember { mutableStateOf(false) }
    var selectedSavingGoalIdForContribution by remember { mutableStateOf<Long?>(null) }
    var showCreateDebtDialog by remember { mutableStateOf(false) }
    var selectedDebtIdForPayment by remember { mutableStateOf<Long?>(null) }
    var showCreateRecurringPlanDialog by remember { mutableStateOf(false) }
    var showUserSetupDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.shouldPromptUserCreation) {
        showUserSetupDialog = uiState.shouldPromptUserCreation
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarContainerColor,
                    titleContentColor = Color(0xFF1E2A3D)
                ),
                actions = {
                    val (themeIcon, themeLabel) = when (themeMode) {
                        AppThemeMode.SYSTEM -> Icons.Filled.SettingsBrightness to "Tema sistema"
                        AppThemeMode.LIGHT -> Icons.Filled.Brightness7 to "Tema claro"
                        AppThemeMode.DARK -> Icons.Filled.Brightness4 to "Tema oscuro"
                    }
                    IconButton(
                        onClick = {
                            val next = when (themeMode) {
                                AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
                                AppThemeMode.LIGHT -> AppThemeMode.DARK
                                AppThemeMode.DARK -> AppThemeMode.SYSTEM
                            }
                            onThemeModeChange(next)
                        }
                    ) {
                        Icon(
                            imageVector = themeIcon,
                            contentDescription = themeLabel
                        )
                    }
                },
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Logo Ortvyn",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(2.dp)
                                .graphicsLayer(
                                    scaleX = topBarLogoScale,
                                    scaleY = topBarLogoScale,
                                    rotationZ = topBarLogoRotation
                                )
                        )
                        Column {
                            BrandWordmarkText(text = "Ortvyn", fontSize = 24.sp)
                            val topBarSubtitle = if (currentRoute == "summary") {
                                "Controla tu dinero"
                            } else {
                                uiState.currentUser?.let { "Hola, ${it.name}" } ?: "Controla tu dinero"
                            }
                            Text(
                                topBarSubtitle,
                                color = if (currentRoute == "summary") Color(0xFF143A5B) else Color(0xFF5F6B7A),
                                fontWeight = if (currentRoute == "summary") FontWeight.SemiBold else FontWeight.Medium,
                                fontSize = if (currentRoute == "summary") 15.sp else 13.sp,
                                letterSpacing = if (currentRoute == "summary") 0.25.sp else 0.sp
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            OrvynBottomBar(
                items = bottomDestinations,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBackgroundGradient)
                .padding(innerPadding)
                .imePadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = "summary",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("summary") {
                    SummaryScreen(
                        uiState = uiState,
                        monthLabel = financeViewModel::monthLabel,
                        onAddClick = {
                            formTarget = null
                            showFormDialog = true
                        },
                        onEdit = {
                            formTarget = it
                            showFormDialog = true
                        },
                        onDelete = { deleteTarget = it }
                    )
                }

                composable(
                    route = "transactions",
                    enterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    exitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    },
                    popEnterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    popExitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    }
                ) {
                    TransactionsScreen(
                        uiState = uiState,
                        onSetMonthFilter = financeViewModel::setMonthFilter,
                        onSetAccountFilter = financeViewModel::setAccountFilter,
                        onSetTypeFilter = financeViewModel::setTypeFilter,
                        onSetCategoryFilter = financeViewModel::setCategoryFilter,
                        onSearchQueryChange = financeViewModel::setSearchQuery,
                        monthLabel = financeViewModel::monthLabel,
                        onAddClick = {
                            formTarget = null
                            showFormDialog = true
                        },
                        onEdit = {
                            formTarget = it
                            showFormDialog = true
                        },
                        onDelete = { deleteTarget = it }
                    )
                }

                composable(
                    route = "budgets",
                    enterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    exitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    },
                    popEnterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    popExitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    }
                ) {
                    BudgetsScreen(
                        uiState = uiState,
                        monthLabel = financeViewModel::monthLabel,
                        onSaveBudget = financeViewModel::saveBudget
                    )
                }

                composable(
                    route = "tools",
                    enterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    exitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    },
                    popEnterTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeIn(animationSpec = tween(220)) +
                            slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { if (forward) it / 3 else -it / 3 }
                            )
                    },
                    popExitTransition = {
                        val from = routeOrder[initialState.destination.route] ?: 0
                        val to = routeOrder[targetState.destination.route] ?: 0
                        val forward = to >= from
                        fadeOut(animationSpec = tween(170)) +
                            slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { if (forward) -it / 6 else it / 6 }
                            )
                    }
                ) {
                    ToolsModuleScreen(
                        uiState = uiState,
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange,
                        onCreateBackup = { showCreateBackupDialog = true },
                        onRestoreBackup = { showRestoreBackupDialog = true },
                        onEditProfile = { showEditProfileDialog = true },
                        onCreateSubscription = { showCreateSubscriptionDialog = true },
                        onSetSubscriptionActive = financeViewModel::setSubscriptionActive,
                        onDeleteSubscription = financeViewModel::deleteSubscription,
                        onCreateSavingGoal = { showCreateSavingGoalDialog = true },
                        onContributeSavingGoal = { goalId -> selectedSavingGoalIdForContribution = goalId },
                        onDeleteSavingGoal = financeViewModel::deleteSavingGoal,
                        onCreateDebt = { showCreateDebtDialog = true },
                        onPayDebt = { debtId -> selectedDebtIdForPayment = debtId },
                        onDeleteDebt = financeViewModel::deleteDebt,
                        onCreateRecurringPlan = { showCreateRecurringPlanDialog = true },
                        onSetRecurringPlanActive = financeViewModel::setRecurringPlanActive,
                        onDeleteRecurringPlan = financeViewModel::deleteRecurringPlan,
                        onApplyRecurringPlansNow = financeViewModel::applyRecurringPlansNow,
                        onCreateAccount = { showCreateAccountDialog = true },
                        onTransfer = { showTransferDialog = true }
                    )
                }
            }
        }
    }

    AnimatedDialogHost(visible = showFormDialog) {
        TransactionFormDialog(
            initialTransaction = formTarget,
            accounts = uiState.accounts,
            onDismiss = {
                showFormDialog = false
                formTarget = null
            },
            onSave = { description, amount, type, category, accountId ->
                val editing = formTarget
                if (editing == null) {
                    financeViewModel.addTransaction(description, amount, type, category, accountId)
                } else {
                    financeViewModel.updateTransaction(
                        id = editing.id,
                        description = description,
                        amount = amount,
                        type = type,
                        category = category,
                        accountId = accountId,
                        dateEpochMillis = editing.dateEpochMillis
                    )
                }
                showFormDialog = false
                formTarget = null
            }
        )
    }

    AnimatedDialogHost(visible = deleteTarget != null) {
        AppPopupDialog(
            onDismissRequest = { deleteTarget = null },
            icon = { PopupHeaderIcon(Icons.Filled.Delete) },
            title = { PopupTitle("Eliminar transaccion", "Esta accion no se puede deshacer") },
            text = {
                Text("Se eliminara \"${deleteTarget?.description ?: ""}\". Esta accion no se puede deshacer.")
            },
            confirmButton = {
                PopupConfirmButton(
                    text = "Eliminar",
                    onClick = {
                        deleteTarget?.id?.let { financeViewModel.deleteTransaction(it) }
                        deleteTarget = null
                    }
                )
            },
            dismissButton = {
                PopupDismissButton(onClick = { deleteTarget = null })
            }
        )
    }

    AnimatedDialogHost(visible = showCreateBackupDialog) {
        BackupPasswordDialog(
            title = "Crear backup cifrado",
            confirmText = "Crear",
            onDismiss = { showCreateBackupDialog = false },
            onConfirm = { password ->
                financeViewModel.createEncryptedBackup(password)
                showCreateBackupDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showRestoreBackupDialog) {
        BackupPasswordDialog(
            title = "Restaurar backup cifrado",
            confirmText = "Restaurar",
            onDismiss = { showRestoreBackupDialog = false },
            onConfirm = { password ->
                financeViewModel.restoreEncryptedBackup(password)
                showRestoreBackupDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showCreateAccountDialog) {
        CreateAccountDialog(
            onDismiss = { showCreateAccountDialog = false },
            onConfirm = { name ->
                financeViewModel.addAccount(name)
                showCreateAccountDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showTransferDialog) {
        TransferDialog(
            accounts = uiState.accounts,
            onDismiss = { showTransferDialog = false },
            onConfirm = { fromId, toId, amount, note ->
                financeViewModel.transferBetweenAccounts(
                    fromAccountId = fromId,
                    toAccountId = toId,
                    amount = amount,
                    note = note
                )
                showTransferDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showCreateSubscriptionDialog) {
        CreateSubscriptionDialog(
            onDismiss = { showCreateSubscriptionDialog = false },
            onConfirm = { name, amount, dayOfMonth ->
                financeViewModel.addSubscription(name, amount, dayOfMonth)
                showCreateSubscriptionDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showCreateSavingGoalDialog) {
        CreateSavingGoalDialog(
            onDismiss = { showCreateSavingGoalDialog = false },
            onConfirm = { name, targetAmount ->
                financeViewModel.addSavingGoal(name, targetAmount)
                showCreateSavingGoalDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = selectedSavingGoalIdForContribution != null) {
        selectedSavingGoalIdForContribution?.let { goalId ->
            val selectedGoal = uiState.savingGoals.firstOrNull { it.id == goalId }
            if (selectedGoal != null) {
                ContributeSavingGoalDialog(
                    goalName = selectedGoal.name,
                    onDismiss = { selectedSavingGoalIdForContribution = null },
                    onConfirm = { amount ->
                        financeViewModel.contributeToSavingGoal(goalId, amount)
                        selectedSavingGoalIdForContribution = null
                    }
                )
            } else {
                selectedSavingGoalIdForContribution = null
            }
        }
    }

    AnimatedDialogHost(visible = showCreateDebtDialog) {
        CreateDebtDialog(
            onDismiss = { showCreateDebtDialog = false },
            onConfirm = { name, totalAmount ->
                financeViewModel.addDebt(name, totalAmount)
                showCreateDebtDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = selectedDebtIdForPayment != null) {
        selectedDebtIdForPayment?.let { debtId ->
            val selectedDebt = uiState.debts.firstOrNull { it.id == debtId }
            if (selectedDebt != null) {
                PayDebtDialog(
                    debtName = selectedDebt.name,
                    onDismiss = { selectedDebtIdForPayment = null },
                    onConfirm = { amount ->
                        financeViewModel.payDebt(debtId, amount)
                        selectedDebtIdForPayment = null
                    }
                )
            } else {
                selectedDebtIdForPayment = null
            }
        }
    }

    AnimatedDialogHost(visible = showCreateRecurringPlanDialog) {
        CreateRecurringPlanDialog(
            savingGoals = uiState.savingGoals,
            debts = uiState.debts,
            onDismiss = { showCreateRecurringPlanDialog = false },
            onConfirm = { targetType, targetId, amount, dayOfMonth ->
                financeViewModel.addRecurringPlan(
                    targetType = targetType,
                    targetId = targetId,
                    amount = amount,
                    dayOfMonth = dayOfMonth
                )
                showCreateRecurringPlanDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showEditProfileDialog) {
        EditProfileDialog(
            currentName = uiState.currentUser?.name.orEmpty(),
            currentAvatarUri = uiState.currentUser?.avatarUri,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, avatarUri ->
                financeViewModel.createOrUpdateUser(name, avatarUri)
                showEditProfileDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showUserSetupDialog) {
        UserSetupDialog(
            onConfirm = { name, avatarUri ->
                financeViewModel.createOrUpdateUser(name, avatarUri)
            }
        )
    }
}

@Composable
private fun AnimatedDialogHost(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(120)) +
            slideInVertically(
                initialOffsetY = { it / 8 },
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f)
            ) +
            scaleIn(
                initialScale = 0.88f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f)
            ),
        exit = fadeOut(animationSpec = tween(90)) +
            slideOutVertically(
                targetOffsetY = { it / 10 },
                animationSpec = tween(140)
            ) +
            scaleOut(
                targetScale = 0.94f,
                animationSpec = tween(140)
            )
    ) {
        content()
    }
}

@Composable
private fun BrandLogoMark(size: Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFFFFFFF)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo Ortvyn",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BrandLogoMark(size = 94.dp)
            BrandWordmarkText(text = "Ortvyn", fontSize = 38.sp)
            Text(
                text = "Cargando tu panel financiero...",
                color = Color(0xFF5F6B7A)
            )
            CircularProgressIndicator(
                color = AppFintechTeal,
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
private fun TransactionFormDialog(
    initialTransaction: FinanceTransaction?,
    accounts: List<com.example.fintrack.ui.finance.FinanceAccount>,
    onDismiss: () -> Unit,
    onSave: (
        description: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        accountId: Long
    ) -> Unit
) {
    val formKey = initialTransaction?.id ?: -1L
    var description by remember(formKey) { mutableStateOf(initialTransaction?.description ?: "") }
    var amount by remember(formKey) { mutableStateOf(initialTransaction?.amount?.toString() ?: "") }
    var type by remember(formKey) { mutableStateOf(initialTransaction?.type ?: TransactionType.EXPENSE) }
    var category by remember(formKey) { mutableStateOf(initialTransaction?.category ?: TransactionCategory.OTHER) }
    val defaultAccountId = accounts.firstOrNull()?.id ?: 1L
    var accountId by remember(formKey, defaultAccountId) {
        mutableStateOf(initialTransaction?.accountId ?: defaultAccountId)
    }

    val title = if (initialTransaction == null) "Nueva transaccion" else "Editar transaccion"
    val subtitle = if (initialTransaction == null) {
        "Registra ingresos o gastos con detalle"
    } else {
        "Actualiza la informacion de este movimiento"
    }
    val confirmLabel = if (initialTransaction == null) "Guardar" else "Actualizar"

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = {
            PopupHeaderIcon(
                imageVector = if (initialTransaction == null) Icons.Filled.Add else Icons.Filled.Edit
            )
        },
        title = { PopupTitle(title, subtitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PopupInputField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripcion"
                )
                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto"
                )
                SingleChoiceSegmentedButtonRow {
                    TransactionType.entries.forEachIndexed { index, option ->
                        val selected = option == type
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TransactionType.entries.size
                            ),
                            selected = selected,
                            onClick = { type = option }
                        ) {
                            Icon(
                                imageVector = if (option == TransactionType.INCOME) {
                                    Icons.AutoMirrored.Filled.TrendingUp
                                } else {
                                    Icons.AutoMirrored.Filled.TrendingDown
                                },
                                contentDescription = option.label,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                option.label,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Text("Categoria")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TransactionCategory.entries) { option ->
                        FilterChip(
                            selected = option == category,
                            onClick = { category = option },
                            label = { Text(option.label) },
                            leadingIcon = {
                                if (option == category) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }

                Text("Cuenta")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts, key = { it.id }) { option ->
                        FilterChip(
                            selected = option.id == accountId,
                            onClick = { accountId = option.id },
                            label = { Text(option.name) },
                            leadingIcon = {
                                if (option.id == accountId) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = confirmLabel,
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (description.isNotBlank() && parsedAmount != null && parsedAmount > 0) {
                        onSave(description.trim(), parsedAmount, type, category, accountId)
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}
