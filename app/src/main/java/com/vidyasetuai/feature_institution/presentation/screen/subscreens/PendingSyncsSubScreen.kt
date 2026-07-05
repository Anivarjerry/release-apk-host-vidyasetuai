package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.database.AppDatabase
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.entity.LocalOrganizationLeaveEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalOrganizationRemarkEntity
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentBusTripAttendanceLogEntity
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@Composable
fun PendingSyncsSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.institutionDao()

    var pendingLeaves by remember { mutableStateOf<List<LocalOrganizationLeaveEntity>>(emptyList()) }
    var pendingRemarks by remember { mutableStateOf<List<LocalOrganizationRemarkEntity>>(emptyList()) }
    var pendingAttendance by remember { mutableStateOf<List<LocalParentBusTripAttendanceLogEntity>>(emptyList()) }
    var isLoadingData by remember { mutableStateOf(true) }

    // Fetch lists from database whenever sync counts or syncing status changes
    LaunchedEffect(state.totalUnsyncedCount, state.isSyncingLogs) {
        pendingLeaves = dao.getUnsyncedLeaves()
        pendingRemarks = dao.getUnsyncedRemarks()
        pendingAttendance = dao.getUnsyncedBusTripAttendanceLogs()
        isLoadingData = false
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val subtitleColor = if (isDark) Color(0xFFA3A3A3) else Color(0xFF737373)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isHindi) "सिंक केंद्र (Sync Center)" else "Sync Center",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(dividerColor)
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        if (state.totalUnsyncedCount == 0 && !isLoadingData) {
            // Everything is Synced Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = null,
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = if (isHindi) "सभी डेटा सिंक है!" else "All Synced!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) 
                            "सारे ऑफ़लाइन लीव्स, रिमार्क्स और अटेंडेंस सर्वर पर अपडेट हो चुके हैं।" 
                        else 
                            "All leaves, remarks, and attendance are successfully updated on the server.",
                        fontSize = 14.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Unsynced Items List Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sync Now Banner Button
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) 
                                "आपके पास ${state.totalUnsyncedCount} पेंडिंग सिंक आइटम हैं।" 
                            else 
                                "You have ${state.totalUnsyncedCount} pending items to sync.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.onEvent(InstitutionEvent.SyncAllPending(userId))
                            },
                            enabled = !state.isSyncingLogs,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (state.isSyncingLogs) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (isHindi) "सिंकिंग हो रही है..." else "Syncing...")
                            } else {
                                Icon(imageVector = Lucide.CloudLightning, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(if (isHindi) "अभी सिंक करें (Sync Now)" else "Sync Now")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Scrollable Unsynced Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // A. Leaves Section
                    if (pendingLeaves.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isHindi) "पेंडिंग छुट्टियाँ (Leaves) - ${pendingLeaves.size}" else "Pending Leaves - ${pendingLeaves.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(pendingLeaves) { leave ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().border(1.dp, cardBorderColor, RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Lucide.Calendar, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = leave.leaveType,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "${leave.startDate} to ${leave.endDate}",
                                            fontSize = 13.sp,
                                            color = subtitleColor
                                        )
                                        if (!leave.reason.isNullOrBlank()) {
                                            Text(
                                                text = leave.reason ?: "",
                                                fontSize = 13.sp,
                                                color = textColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "पेंडिंग" else "Pending",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // B. Remarks Section
                    if (pendingRemarks.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isHindi) "पेंडिंग टिप्पणियाँ (Remarks) - ${pendingRemarks.size}" else "Pending Remarks - ${pendingRemarks.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(pendingRemarks) { remark ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().border(1.dp, cardBorderColor, RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Lucide.MessageCircle, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Category: ${remark.category} • ${remark.priority}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = subtitleColor
                                        )
                                        Text(
                                            text = remark.content,
                                            fontSize = 14.sp,
                                            color = textColor,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "पेंडिंग" else "Pending",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // C. Attendance Logs Section
                    if (pendingAttendance.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isHindi) "पेंडिंग अटेंडेंस (Attendance) - ${pendingAttendance.size}" else "Pending Attendance - ${pendingAttendance.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(pendingAttendance) { log ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().border(1.dp, cardBorderColor, RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Lucide.UserCheck, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Student ID: ${log.studentId.take(8)}...",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "Status: ${log.status} • Scanned: ${log.scannedAt}",
                                            fontSize = 13.sp,
                                            color = subtitleColor
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "पेंडिंग" else "Pending",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
