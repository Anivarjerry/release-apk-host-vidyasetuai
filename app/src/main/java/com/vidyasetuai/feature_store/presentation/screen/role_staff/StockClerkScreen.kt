package com.vidyasetuai.feature_store.presentation.screen.role_staff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * ROLE 3 (Staff): Inventory / Stock Clerk Workspace.
 * Goods inward, stock audit, low stock restocking.
 */
@Composable
fun StockClerkScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (isHindi) "इन्वेंटरी क्लर्क (Stock Manager)" else "Inventory Stock Clerk Workspace")
    }
}
