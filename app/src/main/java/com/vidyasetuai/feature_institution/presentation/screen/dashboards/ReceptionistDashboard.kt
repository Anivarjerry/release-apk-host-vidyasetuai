package com.vidyasetuai.feature_institution.presentation.screen.dashboards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vidyasetuai.feature_institution.presentation.component.DashboardWelcomeCard
import com.vidyasetuai.feature_institution.presentation.component.DashboardTodayLogsCard
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReceptionistDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onNavigateToAllLogs: () -> Unit = {}
) {
    val activeWs = state.activeWorkspace
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardWelcomeCard(
            userName = activeWs?.roleDisplayName ?: (if (isHindi) "रिसेप्शनिस्ट" else "Receptionist"),
            roleDisplayName = activeWs?.workspaceSubRole ?: (if (isHindi) "रिसेप्शनिस्ट" else "Receptionist"),
            profilePictureUrl = activeWs?.roleImageUrl,
            profilePictureLocalPath = activeWs?.roleImageLocalPath,
            todayEventTitle = null,
            todayEventType = null,
            isSchoolClosed = false,
            isHindi = isHindi,
            onProfileClick = {}
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DashboardTodayLogsCard(
            role = activeWs?.role ?: "Receptionist",
            isHindi = isHindi,
            isDark = isDark,
            onViewAllClick = onNavigateToAllLogs
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isHindi) "रिसेप्शनिस्ट डैशबोर्ड" else "Receptionist Dashboard")
        }
    }
}
