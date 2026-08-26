package com.vidyasetuai.feature_store.presentation.screen.role_branch_manager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * ROLE 2: Branch Manager / Sub-Branch Owner Workspace.
 * Restricted to specific branch operation, stock audit, local staff, and counter sales.
 */
@Composable
fun BranchDashboardScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (isHindi) "शाखा प्रबंधक डैशबोर्ड (Branch Manager)" else "Branch Manager Dashboard")
    }
}
