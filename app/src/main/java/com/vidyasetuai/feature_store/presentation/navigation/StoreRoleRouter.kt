package com.vidyasetuai.feature_store.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.presentation.screen.role_owner.OwnerDashboardScreen
import com.vidyasetuai.feature_store.presentation.screen.role_public.*
import com.vidyasetuai.feature_store.presentation.screen.role_staff.PosCashierScreen

/**
 * 3 Core Authority Roles for Store Feature:
 * 1. BUSINESS_OWNER -> Full Master Store Owner Workspace (all branches)
 * 2. STORE_STAFF -> Branch Staff Workspace (Manager, Cashier, Rider, Clerk based on staff.role)
 * 3. PUBLIC_CUSTOMER -> Public Store Marketplace (Shopping & Order OTP)
 */
enum class StoreRole {
    BUSINESS_OWNER,
    STORE_STAFF,
    PUBLIC_CUSTOMER
}

/**
 * Smart Role Router for Store Feature.
 * Apple HIG Layered Architecture:
 * - Layer 1: Rock-Solid Fixed Main Screen (StoreTopAppBar + Feed/Dashboard) -> Never moves, zero shift.
 * - Layer 2: Full Edge-to-Edge Sub-Screens with iOS Bubble Origin Expansion:
 *   * Search blooms from Top-Right search icon (0.82f, 0.04f)
 *   * Cart Checkout blooms from Bottom Cart pill (0.5f, 0.94f)
 *   * POS blooms from Hero POS pill (0.85f, 0.12f)
 *   * Settings blooms from Hero Settings pill (0.65f, 0.12f)
 * - 0ms instant saved state restoration on dismissal.
 */
@Composable
fun StoreRoleRouter(
    isHindi: Boolean = false,
    isDark: Boolean = isSystemInDarkTheme(),
    isSearchActive: Boolean = false,
    onCloseSearch: () -> Unit = {},
    selectedRoleOverride: StoreRole? = null,
    selectedStaffRoleOverride: String? = null,
    requestedPublicSubScreen: String? = null,
    onClearRequestedSubScreen: () -> Unit = {},
    onSubScreenStateChange: (Boolean) -> Unit = {},
    topAppBar: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }

    val businessFlow = remember { storeDb.businessDao().getAnyActiveBusinessFlow() }
    val businessState by businessFlow.collectAsState(initial = null)

    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "pos_counter", "checkout", "otp_tracking", "orders_active", "orders_past"
    var activeOrderNumber by remember { mutableStateOf<String?>(null) }
    var activeDeliveryOtp by remember { mutableStateOf<String?>(null) }

    // Instant 0ms synchronous subscreen state setter (removes 1-frame lag)
    val setSubScreen: (String?) -> Unit = { screen ->
        activeSubScreen = screen
        onSubScreenStateChange(screen != null || isSearchActive)
    }

    // External SubScreen Request Handler (e.g. from Navigation Drawer)
    LaunchedEffect(requestedPublicSubScreen) {
        if (requestedPublicSubScreen != null) {
            setSubScreen(requestedPublicSubScreen)
            onClearRequestedSubScreen()
        }
    }

    // Notify parent StoreRootScreen whether a subscreen is active
    LaunchedEffect(activeSubScreen, isSearchActive) {
        onSubScreenStateChange(activeSubScreen != null || isSearchActive)
    }

    val activeRole = selectedRoleOverride ?: when {
        businessState != null -> StoreRole.BUSINESS_OWNER
        else -> StoreRole.PUBLIC_CUSTOMER
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // =========================================================================
        // 🏛️ LAYER 1: 100% FIXED BASE SCREEN (TopAppBar + Feed / Dashboard)
        // Never shifts, never recalculates height, always alive in RAM.
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            topAppBar()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeRole) {
                    StoreRole.BUSINESS_OWNER -> {
                        OwnerDashboardScreen(
                            isHindi = isHindi,
                            isDark = isDark,
                            onNavigateToPos = { setSubScreen("pos_counter") },
                            onNavigateToCatalog = { setSubScreen("products_catalog") },
                            onNavigateToInventory = { setSubScreen("inventory_stock_control") },
                            onNavigateToInvoices = { setSubScreen("sales_invoices") },
                            onNavigateToPurchases = { setSubScreen("purchase_bills") },
                            onNavigateToExpenses = { setSubScreen("expenses") },
                            onNavigateToReports = { setSubScreen("business_reports") },
                            onNavigateToParties = { setSubScreen("parties_khata") },
                            onNavigateToStaff = { setSubScreen("staff_fleet") },
                            onNavigateToSettings = { setSubScreen("store_settings") }
                        )
                    }
                    StoreRole.STORE_STAFF -> {
                        com.vidyasetuai.feature_store.presentation.screen.role_staff.StaffDashboardScreen(
                            isHindi = isHindi,
                            isDark = isDark,
                            staffRole = selectedStaffRoleOverride ?: "CASHIER",
                            onNavigateToPos = { setSubScreen("pos_counter") },
                            onNavigateToKds = { setSubScreen("kitchen_kds") },
                            onNavigateToCatalog = { setSubScreen("products_catalog") },
                            onNavigateToInventory = { setSubScreen("inventory_stock_control") },
                            onNavigateToInvoices = { setSubScreen("sales_invoices") },
                            onNavigateToPurchases = { setSubScreen("purchase_bills") },
                            onNavigateToReports = { setSubScreen("business_reports") },
                            onNavigateToParties = { setSubScreen("parties_khata") },
                            onNavigateToDeliveryOrders = { setSubScreen("orders_active") },
                            onNavigateToMarketplace = { setSubScreen(null) }
                        )
                    }
                    StoreRole.PUBLIC_CUSTOMER -> {
                        StoreMarketplaceScreen(
                            isHindi = isHindi,
                            isDark = isDark,
                            activeOrderOtp = activeDeliveryOtp,
                            activeOrderNumber = activeOrderNumber,
                            onOpenCartCheckout = { setSubScreen("checkout") },
                            onOpenLiveOtpOrder = { setSubScreen("orders_active") }
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 🚀 LAYER 2: FULL EDGE-TO-EDGE OVERLAYS WITH iOS BUBBLE ORIGIN EXPANSION
        // Covers 0,0 to bottom edge-to-edge (eliminates double headers!).
        // =========================================================================

        // 1. Search Screen (iOS Bubble Origin: Top-Right Search Icon 0.82f, 0.04f)
        AnimatedVisibility(
            visible = isSearchActive,
            enter = scaleIn(
                initialScale = 0.05f,
                transformOrigin = TransformOrigin(0.82f, 0.04f),
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.05f,
                transformOrigin = TransformOrigin(0.82f, 0.04f),
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            PublicStoreSearchScreen(
                isHindi = isHindi,
                onBack = onCloseSearch,
                onOpenCartCheckout = {
                    onCloseSearch()
                    setSubScreen("checkout")
                }
            )
        }

        // 2. Cart Checkout Screen (iOS Bubble Origin: Bottom Cart Pill 0.5f, 0.94f)
        AnimatedVisibility(
            visible = activeSubScreen == "checkout",
            enter = scaleIn(
                initialScale = 0.15f,
                transformOrigin = TransformOrigin(0.5f, 0.94f),
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.15f,
                transformOrigin = TransformOrigin(0.5f, 0.94f),
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + slideOutVertically(
                targetOffsetY = { it / 6 },
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            PublicCartCheckoutScreen(
                isHindi = isHindi,
                isDark = isDark,
                onBack = { setSubScreen(null) },
                onOrderPlacedSuccess = { ordNum, otp ->
                    activeOrderNumber = ordNum
                    activeDeliveryOtp = otp
                    setSubScreen(null)
                }
            )
        }

        // 3. Customer Orders Screen (Active / Past Tab)
        AnimatedVisibility(
            visible = activeSubScreen == "orders_active" || activeSubScreen == "orders_past",
            enter = scaleIn(
                initialScale = 0.88f,
                transformOrigin = TransformOrigin(0.5f, 0.25f),
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.88f,
                transformOrigin = TransformOrigin(0.5f, 0.25f),
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            val tab = if (activeSubScreen == "orders_past") OrdersTab.PAST else OrdersTab.ACTIVE
            CustomerOrdersScreen(
                initialTab = tab,
                isHindi = isHindi,
                isDark = isDark,
                onBack = { setSubScreen(null) }
            )
        }

        // 4. OTP Live Tracking Screen
        AnimatedVisibility(
            visible = activeSubScreen == "otp_tracking",
            enter = scaleIn(
                initialScale = 0.85f,
                transformOrigin = TransformOrigin(0.5f, 0.2f),
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.85f,
                transformOrigin = TransformOrigin(0.5f, 0.2f),
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            OrderTrackingOtpScreen(
                orderNumber = activeOrderNumber ?: "ORD-984210",
                deliveryOtp = activeDeliveryOtp ?: "4892",
                isHindi = isHindi,
                onBack = { setSubScreen(null) }
            )
        }

        // 5. Owner POS Counter (iOS Bubble Origin: Hero POS Button 0.85f, 0.12f)
        AnimatedVisibility(
            visible = activeSubScreen == "pos_counter",
            enter = scaleIn(
                initialScale = 0.15f,
                transformOrigin = TransformOrigin(0.85f, 0.12f),
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.15f,
                transformOrigin = TransformOrigin(0.85f, 0.12f),
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            com.vidyasetuai.feature_store.presentation.screen.role_staff.PosCounterBillingScreen(
                onNavigateBack = { setSubScreen(null) }
            )
        }

        // 6. Owner Store Settings (iOS Bubble Origin: Hero Settings Button 0.65f, 0.12f)
        AnimatedVisibility(
            visible = activeSubScreen == "store_settings" || activeSubScreen == "settings",
            enter = scaleIn(
                initialScale = 0.15f,
                transformOrigin = TransformOrigin(0.65f, 0.12f),
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.15f,
                transformOrigin = TransformOrigin(0.65f, 0.12f),
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.StoreSettingsScreen(
                isHindi = isHindi,
                onBack = { setSubScreen(null) }
            )
        }

        // 7. Other Merchant & Staff Sub-Screens (Catalog, Inventory, Invoices, Purchases, Reports, Khata, Staff, Expenses, Delivery)
        val isGenericSubScreen = activeSubScreen != null && activeSubScreen !in listOf(
            "checkout", "pos_counter", "store_settings", "settings", "orders_active", "orders_past", "otp_tracking"
        )
        AnimatedVisibility(
            visible = isGenericSubScreen,
            enter = scaleIn(
                initialScale = 0.85f,
                transformOrigin = TransformOrigin(0.5f, 0.35f),
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(180)),
            exit = scaleOut(
                targetScale = 0.85f,
                transformOrigin = TransformOrigin(0.5f, 0.35f),
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(180))
        ) {
            when (activeSubScreen) {
                "products_catalog" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.catalog.ProductsCatalogScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "inventory_stock_control" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.inventory.InventoryStockControlScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "sales_invoices" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.SalesInvoicesBillsScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "purchase_bills", "purchases", "purchase_invoices" -> com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.PurchaseBillsScreen(
                    businessId = businessState?.id ?: "",
                    isHindi = isHindi,
                    onNavigateBack = { setSubScreen(null) }
                )
                "business_reports", "reports" -> com.vidyasetuai.feature_store.presentation.screen.role_owner.reports.BusinessReportsScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "parties_khata" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.PartiesKhataScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "staff_fleet" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffFleetScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) },
                    onNavigateToSalary = { setSubScreen("staff_salary") }
                )
                "staff_salary", "salary", "payroll" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryScreen(
                    isHindi = isHindi,
                    onNavigateBack = { setSubScreen(null) }
                )
                "kitchen_kds", "kds" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.kds.KitchenKdsScreen(
                    isHindi = isHindi,
                    onBack = { setSubScreen(null) }
                )
                "expenses", "business_expenses" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.expenses.ExpensesListScreen(
                    isHindi = isHindi,
                    businessId = businessState?.id ?: "",
                    branchId = null,
                    businessName = businessState?.tradeName ?: "Store",
                    onNavigateBack = { setSubScreen(null) }
                )
                "orders_active", "delivery_orders" -> com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery.DriverDeliveryOrdersScreen(
                    businessId = businessState?.id ?: "",
                    isHindi = isHindi,
                    onNavigateBack = { setSubScreen(null) }
                )
                else -> Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
