package com.vidyasetuai.feature_store.presentation.drawer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole

/**
 * Self-contained Feature Store Drawer Wrapper.
 * Styled to match the edge-to-edge standard app drawer appearance.
 */
@Composable
fun StoreFeatureDrawer(
    isHindi: Boolean = false,
    onCloseDrawer: () -> Unit = {},
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToActiveOrders: () -> Unit = {},
    onNavigateToPastOrders: () -> Unit = {},
    onSelectRole: (StoreRole, String?) -> Unit = { _, _ -> }
) {
    ModalDrawerSheet(
        modifier = Modifier.width(285.dp),
        drawerShape = RectangleShape,
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets(0.dp)
    ) {
        StoreDrawerContent(
            isHindi = isHindi,
            onOpenSettings = {
                onCloseDrawer()
                onNavigateToSettings()
            },
            onOpenBranches = {
                onCloseDrawer()
                onNavigateToStaff()
            },
            onOpenStaff = {
                onCloseDrawer()
                onNavigateToStaff()
            },
            onNavigateToPos = {
                onCloseDrawer()
                onNavigateToPos()
            },
            onNavigateToKds = {
                onCloseDrawer()
                onNavigateToKds()
            },
            onNavigateToCatalog = {
                onCloseDrawer()
                onNavigateToCatalog()
            },
            onNavigateToInventory = {
                onCloseDrawer()
                onNavigateToInventory()
            },
            onNavigateToInvoices = {
                onCloseDrawer()
                onNavigateToInvoices()
            },
            onNavigateToPurchases = {
                onCloseDrawer()
                onNavigateToPurchases()
            },
            onNavigateToExpenses = {
                onCloseDrawer()
                onNavigateToExpenses()
            },
            onNavigateToReports = {
                onCloseDrawer()
                onNavigateToReports()
            },
            onNavigateToParties = {
                onCloseDrawer()
                onNavigateToParties()
            },
            onNavigateToCart = {
                onCloseDrawer()
                onNavigateToCart()
            },
            onNavigateToActiveOrders = {
                onCloseDrawer()
                onNavigateToActiveOrders()
            },
            onNavigateToPastOrders = {
                onCloseDrawer()
                onNavigateToPastOrders()
            },
            onSelectRole = { role, staffRole ->
                onSelectRole(role, staffRole)
                onCloseDrawer()
            }
        )
    }
}
