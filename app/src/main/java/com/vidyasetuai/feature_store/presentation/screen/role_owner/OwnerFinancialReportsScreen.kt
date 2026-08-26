package com.vidyasetuai.feature_store.presentation.screen.role_owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OwnerFinancialReportsScreen(
    isHindi: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (isHindi) "वित्तीय रिपोर्ट (Owner Reports)" else "Financial Reports & PnL")
    }
}
