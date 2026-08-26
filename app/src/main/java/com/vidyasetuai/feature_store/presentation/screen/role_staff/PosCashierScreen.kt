package com.vidyasetuai.feature_store.presentation.screen.role_staff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * ROLE 3 (Staff): Cashier POS Billing Screen.
 * Fast counter billing, barcode scanner, tax receipts.
 */
@Composable
fun PosCashierScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    PosCounterBillingScreen(
        onNavigateBack = onNavigateBack
    )
}
