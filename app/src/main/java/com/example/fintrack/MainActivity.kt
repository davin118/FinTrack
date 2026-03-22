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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Check
import com.example.fintrack.ui.finance.FinanceTransaction
import com.example.fintrack.ui.finance.FinanceUiState
import com.example.fintrack.ui.finance.FinanceViewModel
import com.example.fintrack.ui.finance.SmartReminderWorker
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
    BottomBarItemModel(route = "budgets", label = "Presupuestos", icon = Icons.Filled.Savings),
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
    "tools" to Color(0x66D7E8FF)
)

private val topBarDarkColorByRoute = mapOf(
    "summary" to Color(0xD9142233),
    "transactions" to Color(0xD9142230),
    "budgets" to Color(0xD9122D26),
    "tools" to Color(0xD9142233)
)

private val backgroundStartColorByRoute = mapOf(
    "summary" to Color(0xFFF5F7FF),
    "transactions" to Color(0xFFF2F8FF),
    "budgets" to Color(0xFFF4FBF8),
    "tools" to Color(0xFFF5F7FF)
)
private val backgroundStartDarkColorByRoute = mapOf(
    "summary" to Color(0xFF0A1018),
    "transactions" to Color(0xFF0A111A),
    "budgets" to Color(0xFF0A1512),
    "tools" to Color(0xFF0A1018)
)

private val backgroundMidColorByRoute = mapOf(
    "summary" to Color(0xFFE8F5FF),
    "transactions" to Color(0xFFE6F1FF),
    "budgets" to Color(0xFFE6F6EF),
    "tools" to Color(0xFFE8F5FF)
)
private val backgroundMidDarkColorByRoute = mapOf(
    "summary" to Color(0xFF0E1B2A),
    "transactions" to Color(0xFF0F1B2B),
    "budgets" to Color(0xFF0D2720),
    "tools" to Color(0xFF0E1B2A)
)

private val backgroundEndColorByRoute = mapOf(
    "summary" to Color(0xFFF9F5FF),
    "transactions" to Color(0xFFF4F6FF),
    "budgets" to Color(0xFFF2FBF6),
    "tools" to Color(0xFFF9F5FF)
)
private val backgroundEndDarkColorByRoute = mapOf(
    "summary" to Color(0xFF0D1623),
    "transactions" to Color(0xFF0D1622),
    "budgets" to Color(0xFF0E1E19),
    "tools" to Color(0xFF0D1623)
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

    LaunchedEffect(Unit) {
        runCatching { SmartReminderWorker.schedule(context) }
    }

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
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

    if (!uiState.authReady) {
        LoadingScreen()
        return
    }
    if (!uiState.isAuthenticated) {
        AuthScreen(
            hasRegisteredUsers = uiState.hasAnyRegisteredUser,
            statusMessage = uiState.authStatusMessage,
            authRunning = uiState.authOperationRunning,
            onLogin = financeViewModel::loginUser,
            onRegister = financeViewModel::registerUser,
            onSendRecoveryPin = financeViewModel::sendRecoveryPin,
            onRecoverPasswordWithPin = financeViewModel::recoverPasswordWithPin,
            onClearStatus = financeViewModel::clearAuthStatusMessage
        )
        return
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
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showRemindersSheet by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarContainerColor,
                    titleContentColor = if (darkTheme) Color(0xFFE3EBF7) else Color(0xFF1E2A3D)
                ),
                actions = {
                    IconButton(
                        onClick = { showRemindersSheet = true }
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.reminders.isNotEmpty()) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Recordatorios"
                            )
                        }
                    }
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
                    IconButton(
                        onClick = { showLogoutConfirmDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesion"
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
                                color = if (darkTheme) {
                                    if (currentRoute == "summary") Color(0xFFB9D5F2) else Color(0xFFAABCD2)
                                } else {
                                    if (currentRoute == "summary") Color(0xFF143A5B) else Color(0xFF5F6B7A)
                                },
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
                        onSendResendTestEmail = financeViewModel::sendResendTestEmail,
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

    if (showRemindersSheet) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showRemindersSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Notificaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (uiState.reminders.isEmpty()) {
                    Text("No hay recordatorios por ahora.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    uiState.reminders.forEach { reminder ->
                        val tone = when (reminder.severity) {
                            com.example.fintrack.ui.finance.ReminderSeverity.INFO -> MaterialTheme.colorScheme.primary
                            com.example.fintrack.ui.finance.ReminderSeverity.WARNING -> Color(0xFFC08A00)
                            com.example.fintrack.ui.finance.ReminderSeverity.CRITICAL -> Color(0xFFE25151)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Canvas(modifier = Modifier.size(8.dp)) {
                                drawCircle(color = tone)
                            }
                            Text(reminder.message)
                        }
                    }
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
            onConfirm = { name, kind ->
                financeViewModel.addAccount(name, kind)
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
            currentEmail = uiState.currentUser?.email.orEmpty(),
            currentAvatarUri = uiState.currentUser?.avatarUri,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, avatarUri, email, newPassword ->
                financeViewModel.updateCurrentUserProfileComplete(
                    name = name,
                    avatarUri = avatarUri,
                    email = email,
                    newPassword = newPassword
                )
                showEditProfileDialog = false
            }
        )
    }

    AnimatedDialogHost(visible = showLogoutConfirmDialog) {
        AppPopupDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { PopupHeaderIcon(Icons.AutoMirrored.Filled.Logout) },
            title = { PopupTitle("Cerrar sesion", "Confirma si deseas salir de tu cuenta") },
            text = {
                Text("Se cerrara tu sesion actual en este dispositivo.")
            },
            confirmButton = {
                PopupConfirmButton(
                    text = "Cerrar sesion",
                    onClick = {
                        financeViewModel.logoutUser()
                        showLogoutConfirmDialog = false
                    }
                )
            },
            dismissButton = {
                PopupDismissButton(onClick = { showLogoutConfirmDialog = false })
            }
        )
    }
}

@Composable
private fun AuthScreen(
    hasRegisteredUsers: Boolean,
    statusMessage: String?,
    authRunning: Boolean,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit,
    onSendRecoveryPin: (email: String) -> Unit,
    onRecoverPasswordWithPin: (email: String, pin: String, newPassword: String) -> Unit,
    onClearStatus: () -> Unit
) {
    var registerMode by rememberSaveable { mutableStateOf(!hasRegisteredUsers) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showRecoverDialog by rememberSaveable { mutableStateOf(false) }
    var recoverEmail by rememberSaveable { mutableStateOf("") }
    var recoverPin by rememberSaveable { mutableStateOf("") }
    var recoverPassword by rememberSaveable { mutableStateOf("") }
    var recoverPasswordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(hasRegisteredUsers) {
        if (!hasRegisteredUsers) registerMode = true
    }

    val authHeroGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2E5B88),
            Color(0xFF1E7AA8),
            Color(0xFF15A5A1)
        ),
        start = Offset.Zero,
        end = Offset(800f, 380f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(authHeroGradient)
                            .padding(horizontal = 14.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            BrandLogoMark(size = 54.dp)
                            Column {
                                BrandWordmarkText(text = "Ortvyn", fontSize = 30.sp)
                                Text(
                                    "Acceso seguro a tus finanzas",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (registerMode) "Crea tu cuenta" else "Bienvenido de nuevo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (registerMode) {
                        "Registra tu usuario para empezar a gestionar tu dinero."
                    } else {
                        "Inicia sesion para entrar a tu panel financiero."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (hasRegisteredUsers) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            selected = !registerMode,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = Color.White,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                registerMode = false
                                onClearStatus()
                            }
                        ) {
                            Text(
                                "Iniciar",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            selected = registerMode,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                activeContentColor = Color.White,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                registerMode = true
                                onClearStatus()
                            }
                        ) {
                            Text(
                                "Registrar",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                if (registerMode) {
                    PopupInputField(
                        value = name,
                        onValueChange = {
                            name = it
                            onClearStatus()
                        },
                        label = "Nombre"
                    )
                }
                PopupInputField(
                    value = email,
                    onValueChange = {
                        email = it
                        onClearStatus()
                    },
                    label = "Correo"
                )
                PopupInputField(
                    value = password,
                    onValueChange = {
                        password = it
                        onClearStatus()
                    },
                    label = "Contrasena",
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = if (passwordVisible) {
                                    "Ocultar contrasena"
                                } else {
                                    "Mostrar contrasena"
                                }
                            )
                        }
                    }
                )

                if (!statusMessage.isNullOrBlank()) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9E9))
                    ) {
                        Text(
                            statusMessage,
                            color = Color(0xFFB3261E),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (registerMode) {
                            onRegister(name, email, password)
                        } else {
                            onLogin(email, password)
                        }
                    },
                    enabled = !authRunning,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (authRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procesando...")
                    } else {
                        Icon(
                            imageVector = if (registerMode) Icons.Filled.PersonAdd else Icons.Filled.Lock,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (registerMode) "Crear cuenta" else "Entrar")
                    }
                }

                if (!registerMode && hasRegisteredUsers) {
                    TextButton(
                        onClick = {
                            recoverEmail = email
                            recoverPin = ""
                            recoverPassword = ""
                            recoverPasswordVisible = false
                            showRecoverDialog = true
                            onClearStatus()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Olvide mi contrasena")
                    }
                }

                if (!hasRegisteredUsers) {
                    Text(
                        "Primera vez: crea tu cuenta para activar la app.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showRecoverDialog) {
        AppPopupDialog(
            onDismissRequest = { showRecoverDialog = false },
            icon = { PopupHeaderIcon(Icons.Filled.Lock) },
            title = { PopupTitle("Recuperar contraseña", "Define una nueva contraseña para tu cuenta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PopupInputField(
                        value = recoverEmail,
                        onValueChange = { recoverEmail = it },
                        label = "Correo"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PopupInputField(
                            value = recoverPin,
                            onValueChange = { recoverPin = it },
                            label = "PIN",
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { onSendRecoveryPin(recoverEmail) },
                            enabled = !authRunning
                        ) {
                            Text("Enviar PIN")
                        }
                    }
                    PopupInputField(
                        value = recoverPassword,
                        onValueChange = { recoverPassword = it },
                        label = "Nueva contrasena",
                        visualTransformation = if (recoverPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { recoverPasswordVisible = !recoverPasswordVisible }) {
                                Icon(
                                    imageVector = if (recoverPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (recoverPasswordVisible) "Ocultar contrasena" else "Mostrar contrasena"
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                PopupConfirmButton(
                    text = "Restablecer",
                    onClick = {
                        onRecoverPasswordWithPin(recoverEmail, recoverPin, recoverPassword)
                        showRecoverDialog = false
                    },
                    enabled = !authRunning
                )
            },
            dismissButton = {
                PopupDismissButton(onClick = { showRecoverDialog = false })
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
    val darkTheme = isSystemInDarkTheme()
    val loadingBackground = if (darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0A1018),
                Color(0xFF0F1B2A),
                Color(0xFF141327)
            )
        )
    } else {
        backgroundGradient
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(loadingBackground),
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
                color = if (isSystemInDarkTheme()) Color(0xFFAABCD2) else Color(0xFF5F6B7A)
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
