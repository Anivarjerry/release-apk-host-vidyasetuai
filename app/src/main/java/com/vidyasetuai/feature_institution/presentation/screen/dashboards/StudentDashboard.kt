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
import com.vidyasetuai.feature_institution.presentation.component.DashboardBusTrackingCard
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentDashboard(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onNavigateToLeave: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToRemarks: () -> Unit,
    onNavigateToSelfProfile: () -> Unit,
    onNavigateToTransport: () -> Unit,
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
            userName = activeWs?.roleDisplayName ?: (if (isHindi) "विद्यार्थी" else "Student"),
            roleDisplayName = activeWs?.role ?: (if (isHindi) "विद्यार्थी" else "Student"),
            profilePictureUrl = activeWs?.roleImageUrl,
            profilePictureLocalPath = activeWs?.roleImageLocalPath,
            todayEventTitle = null,
            todayEventType = null,
            isSchoolClosed = false,
            isHindi = isHindi,
            onProfileClick = onNavigateToSelfProfile
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DashboardTodayLogsCard(
            role = activeWs?.role ?: "Student",
            isHindi = isHindi,
            isDark = isDark,
            onViewAllClick = onNavigateToAllLogs
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DashboardBusTrackingCard(
            role = activeWs?.role ?: "Student",
            studentBuses = state.studentBuses,
            allBuses = state.allBuses,
            activeBusRoutes = state.activeBusRoutes,
            activeBusLocation = state.activeBusLocation,
            isHindi = isHindi,
            isDark = isDark,
            onViewAllClick = onNavigateToTransport
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isHindi) "छात्र डैशबोर्ड" else "Student Dashboard")
        }
    }
}
