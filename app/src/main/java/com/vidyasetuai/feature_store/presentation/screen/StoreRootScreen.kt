package com.vidyasetuai.feature_store.presentation.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.presentation.component.StoreTopAppBar
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole
import com.vidyasetuai.feature_store.presentation.navigation.StoreRoleRouter
import com.vidyasetuai.feature_store.presentation.sheet.StoreProfileHubSheet

/**
 * 100% Autonomous Root Screen for the `feature_store` micro-module.
 * Modernized with a single Unified Apple HIG Store & Profile Hub Bottom Sheet
 * with 1:1 Parallel Synchronized Background Gaussian Blur.
 */
@Composable
fun StoreRootScreen(
    isHindi: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSubScreenChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }
    val activeBusinessState by storeDb.businessDao().getAnyActiveBusinessFlow().collectAsState(initial = null)

    var isProfileHubOpen by remember { mutableStateOf(false) }
    var isSheetBlurActive by remember { mutableStateOf(false) }

    LaunchedEffect(isProfileHubOpen) {
        if (isProfileHubOpen) {
            isSheetBlurActive = true
        }
    }

    val animatedBlurRadius by animateDpAsState(
        targetValue = if (isSheetBlurActive && isProfileHubOpen) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "store_bg_blur"
    )

    var userSelectedStoreRole by remember { mutableStateOf<StoreRole?>(null) }
    var userSelectedStaffRole by remember { mutableStateOf<String?>(null) }
    var isStoreSearchActive by remember { mutableStateOf(false) }
    var isStoreSubScreenActive by remember { mutableStateOf(false) }
    var requestedStoreSubScreen by remember { mutableStateOf<String?>(null) }

    val activeRole = userSelectedStoreRole ?: when {
        activeBusinessState != null -> StoreRole.BUSINESS_OWNER
        else -> StoreRole.PUBLIC_CUSTOMER
    }

    val businessInitial = remember(activeBusinessState) {
        val name = activeBusinessState?.tradeName?.trim() ?: "Store"
        val words = name.split(" ").filter { it.isNotBlank() }
        when {
            words.size >= 2 -> "${words[0].first()}${words[1].first()}".uppercase()
            words.isNotEmpty() && words[0].length >= 2 -> words[0].take(2).uppercase()
            words.isNotEmpty() -> words[0].take(1).uppercase()
            else -> "S"
        }
    }

    // Pillar 5: Layered Back Navigation Resilience (Zero Trap Backstacks)
    BackHandler(enabled = true) {
        when {
            isProfileHubOpen -> {
                isSheetBlurActive = false
                isProfileHubOpen = false
            }
            isStoreSearchActive -> {
                isStoreSearchActive = false
            }
            isStoreSubScreenActive -> {
                requestedStoreSubScreen = null
            }
            else -> {
                onNavigateBack()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .blur(animatedBlurRadius)
    ) {
        StoreRoleRouter(
            isHindi = isHindi,
            isSearchActive = isStoreSearchActive,
            onCloseSearch = { isStoreSearchActive = false },
            selectedRoleOverride = userSelectedStoreRole,
            selectedStaffRoleOverride = userSelectedStaffRole,
            requestedPublicSubScreen = requestedStoreSubScreen,
            onClearRequestedSubScreen = { requestedStoreSubScreen = null },
            onSubScreenStateChange = { active ->
                isStoreSubScreenActive = active
                onSubScreenChange(active)
            },
            topAppBar = {
                StoreTopAppBar(
                    isHindi = isHindi,
                    isPublicCustomer = (activeRole == StoreRole.PUBLIC_CUSTOMER),
                    businessInitial = businessInitial,
                    onSearchClick = { isStoreSearchActive = true },
                    onProfileClick = { isProfileHubOpen = true }
                )
            }
        )
    }

    // 100% Unified Flagship Store & Profile Hub Floating Island Bottom Sheet
    StoreProfileHubSheet(
        visible = isProfileHubOpen,
        onDismiss = {
            isSheetBlurActive = false
            isProfileHubOpen = false
        },
        onStartDismiss = {
            isSheetBlurActive = false
        },
        isHindi = isHindi,
        selectedRoleOverride = userSelectedStoreRole,
        selectedStaffRoleOverride = userSelectedStaffRole,
        onSelectRole = { role, staffRole ->
            userSelectedStoreRole = role
            userSelectedStaffRole = staffRole
        },
        onRegisterNewBusiness = {
            Toast.makeText(
                context,
                if (isHindi) "नया स्टोर जोड़ने के लिए सेटिंग्स में जाएं" else "Go to Store Settings to register a new branch",
                Toast.LENGTH_SHORT
            ).show()
        },
        onNavigateToPos = { requestedStoreSubScreen = "pos_counter" },
        onNavigateToKds = { requestedStoreSubScreen = "kitchen_kds" },
        onNavigateToCatalog = { requestedStoreSubScreen = "products_catalog" },
        onNavigateToInventory = { requestedStoreSubScreen = "inventory_stock_control" },
        onNavigateToInvoices = { requestedStoreSubScreen = "sales_invoices" },
        onNavigateToPurchases = { requestedStoreSubScreen = "purchase_bills" },
        onNavigateToExpenses = { requestedStoreSubScreen = "expenses" },
        onNavigateToReports = { requestedStoreSubScreen = "business_reports" },
        onNavigateToParties = { requestedStoreSubScreen = "parties_khata" },
        onNavigateToStaff = { requestedStoreSubScreen = "staff_fleet" },
        onNavigateToSettings = { requestedStoreSubScreen = "store_settings" },
        onNavigateToCart = { requestedStoreSubScreen = "checkout" },
        onNavigateToActiveOrders = { requestedStoreSubScreen = "orders_active" },
        onNavigateToPastOrders = { requestedStoreSubScreen = "orders_past" }
    )
}



