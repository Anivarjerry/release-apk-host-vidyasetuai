package com.vidyasetuai.feature_store.presentation.screen.role_owner

import androidx.compose.runtime.Composable
import com.vidyasetuai.feature_store.presentation.screen.StoreDashboardScreen

/**
 * ROLE 1: Master Store Owner Workspace.
 * Full access across all branches, financial analytics, staff permissions, and inventory.
 */
@Composable
fun OwnerDashboardScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onNavigateToPos: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    StoreDashboardScreen(
        isHindi = isHindi,
        isDark = isDark,
        onNavigateToPos = onNavigateToPos,
        onNavigateToCatalog = onNavigateToCatalog,
        onNavigateToInventory = onNavigateToInventory,
        onNavigateToInvoices = onNavigateToInvoices,
        onNavigateToPurchases = onNavigateToPurchases,
        onNavigateToExpenses = onNavigateToExpenses,
        onNavigateToReports = onNavigateToReports,
        onNavigateToParties = onNavigateToParties,
        onNavigateToStaff = onNavigateToStaff,
        onNavigateToSettings = onNavigateToSettings
    )
}
