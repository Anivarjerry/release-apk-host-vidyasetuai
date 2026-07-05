package com.vidyasetuai.feature_institution.presentation.screen.dashboards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TransportManagerDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onNavigateToAllLogs: () -> Unit = {}
) {
    val activeWs = state.activeWorkspace
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardWelcomeCard(
            userName = activeWs?.roleDisplayName ?: (if (isHindi) "परिवहन प्रबंधक" else "Transport Manager"),
            roleDisplayName = activeWs?.workspaceSubRole ?: (if (isHindi) "परिवहन प्रबंधक" else "Transport Manager"),
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
            role = activeWs?.role ?: "Transport Manager",
            isHindi = isHindi,
            isDark = isDark,
            onViewAllClick = onNavigateToAllLogs
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isHindi) "परिवहन प्रबंधक डैशबोर्ड" else "Transport Manager Dashboard")
        }
    }
}
