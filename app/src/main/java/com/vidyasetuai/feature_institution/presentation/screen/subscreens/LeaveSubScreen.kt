package com.vidyasetuai.feature_feed.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel
import com.vidyasetuai.feature_institution.data.local.entity.LocalOrganizationLeaveEntity

@Composable
fun LeaveSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    userId: String,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val db = remember { com.vidyasetuai.core.database.AppDatabase.getDatabase(context) }
    val activeWorkspace = state.activeWorkspace
    val role = activeWorkspace?.role ?: ""

    // Trigger LoadLeaves when screen opens
    LaunchedEffect(userId, role) {
        if (role.isNotEmpty()) {
            viewModel.onEvent(InstitutionEvent.LoadLeaves(userId, role))
        }
    }

    // ── Local Resolved States ──
    var searchQuery by remember { mutableStateOf("") }
    var allResolvedLeaves by remember { mutableStateOf<List<ResolvedLeave>>(emptyList()) }
    var expandedLeaveIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoadingDbData by remember { mutableStateOf(true) }

    // Approval / Rejection Action Dialog State
    var selectedLeaveForAction by remember { mutableStateOf<LocalOrganizationLeaveEntity?>(null) }
    var actionTypeTarget by remember { mutableStateOf<String?>(null) } // "Approved" or "Rejected"
    var actionRemarksInput by remember { mutableStateOf("") }

    // Case-insensitive Admin Check
    val adminRoles = remember { listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner") }
    val isAdmin = remember(role) { adminRoles.any { it.equals(role, ignoreCase = true) } }

    // Query database to resolve leave details (with names, comments, etc.)
    LaunchedEffect(state.leaves, activeWorkspace, role) {
        isLoadingDbData = true
        val dao = db.institutionDao()

        // 1. Fetch students & staff to build mapping cache
        val students = dao.searchStudentsOffline("")
        val staffList = dao.getStaffProfiles(activeWorkspace?.parentOrgId ?: "")

        val studentNameMap = students.associate { it.id to it.name }
        val studentClassMap = students.associate { it.id to "${it.className ?: ""} - ${it.sectionName ?: ""}" }
        val staffNameMap = staffList.associate { it.id to it.name }

        // 2. Query leaves from local Room DB and filter out past leaves older than today
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val rawLeaves = dao.getAllLeaves().filter { it.endDate >= todayStr || it.startDate >= todayStr }

        // 3. Filter based on roles (Admin roles see all leaves)
        val filteredRaw = when {
            isAdmin -> {
                rawLeaves // Admin / Principal sees ALL staff & student leaves
            }
            role.equals("Guardian", ignoreCase = true) -> {
                val childIds = state.guardianStudents.map { it.id }
                rawLeaves.filter { (it.studentId != null && it.studentId in childIds) || (it.actionBy != null && it.actionBy == userId) }
            }
            role.equals("Student", ignoreCase = true) -> {
                val studentId = activeWorkspace?.studentId ?: ""
                rawLeaves.filter { (it.studentId != null && it.studentId == studentId) || (it.actionBy != null && it.actionBy == userId) }
            }
            else -> {
                // Regular staff / teacher: see their own leaves
                val staffId = activeWorkspace?.staffId ?: activeWorkspace?.id ?: ""
                rawLeaves.filter { 
                    (it.staffId != null && (it.staffId == staffId || it.staffId == activeWorkspace?.id)) || 
                    (it.actionBy != null && it.actionBy == userId) 
                }
            }
        }

        // 4. Map to UI Resolved Model
        allResolvedLeaves = filteredRaw.map { leave ->
            val applicantName = if (leave.applicantType.equals("student", ignoreCase = true)) {
                studentNameMap[leave.studentId ?: ""] ?: leave.studentName ?: (if (isHindi) "छात्र" else "Student")
            } else {
                staffNameMap[leave.staffId ?: ""] ?: leave.staffName ?: (if (isHindi) "स्टाफ" else "Staff")
            }

            val classSection = if (leave.applicantType.equals("student", ignoreCase = true)) {
                studentClassMap[leave.studentId ?: ""] ?: "${leave.className ?: ""} - ${leave.sectionName ?: ""}"
            } else {
                leave.staffRoleName ?: ""
            }

            val actionByName = staffNameMap[leave.actionBy ?: ""] ?: leave.actionByName ?: leave.actionBy

            ResolvedLeave(
                entity = leave,
                applicantName = applicantName,
                classSection = classSection,
                actionByName = actionByName
            )
        }
        isLoadingDbData = false
    }

    // ── Filter Leaves by Search Box Query ──
    val filteredLeaves = remember(searchQuery, allResolvedLeaves) {
        if (searchQuery.isBlank()) {
            allResolvedLeaves
        } else {
            val q = searchQuery.trim()
            allResolvedLeaves.filter { r ->
                r.entity.leaveType.contains(q, ignoreCase = true) ||
                r.applicantName.contains(q, ignoreCase = true) ||
                r.entity.reason?.contains(q, ignoreCase = true) == true ||
                r.entity.status.contains(q, ignoreCase = true) ||
                (r.classSection != null && r.classSection.contains(q, ignoreCase = true))
            }
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color.White
    val subtitleColor = if (isDark) Color(0xFFA0A0A0) else Color(0xFF666666)
    val cardBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E7EB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    tint = textColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "छुट्टियाँ (Leaves)" else "Leaves",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            placeholder = {
                Text(
                    text = if (isHindi) "छुट्टी का प्रकार, नाम या कारण खोजें..." else "Search leave type, name or reason...",
                    fontSize = 14.sp,
                    color = subtitleColor
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Lucide.Search,
                    contentDescription = "Search",
                    tint = subtitleColor,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Clear",
                            tint = subtitleColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.EmeraldGreen,
                unfocusedBorderColor = cardBorderColor,
                focusedContainerColor = cardBg,
                unfocusedContainerColor = cardBg
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        if (isLoadingDbData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.EmeraldGreen)
            }
        } else if (filteredLeaves.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.CalendarOff,
                        contentDescription = null,
                        tint = subtitleColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई लीव रिकॉर्ड नहीं मिला" else "No Leave Records Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHindi) "आपकी खोज या भूमिका के अनुसार कोई लीव उपलब्ध नहीं है।" else "No leave requests or records match your search query.",
                        fontSize = 13.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLeaves, key = { it.entity.id }) { resolved ->
                    val leave = resolved.entity
                    val isExpanded = expandedLeaveIds.contains(leave.id)

                    val statusBg = when (leave.status) {
                        "Approved" -> Color(0xFFE8F5E9)
                        "Rejected" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF3E0)
                    }
                    val statusTextCol = when (leave.status) {
                        "Approved" -> Color(0xFF2E7D32)
                        "Rejected" -> Color(0xFFC62828)
                        else -> Color(0xFFE65100)
                    }
                    val statusText = when (leave.status) {
                        "Approved" -> if (isHindi) "स्वीकृत (Approved)" else "Approved"
                        "Rejected" -> if (isHindi) "अस्वीकृत (Rejected)" else "Rejected"
                        else -> if (isHindi) "पेंडिंग (Pending)" else "Pending"
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedLeaveIds = if (isExpanded) {
                                        expandedLeaveIds - leave.id
                                    } else {
                                        expandedLeaveIds + leave.id
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            // ── Short Header Row ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = leave.leaveType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = textColor
                                    )
                                    
                                    // Show applicant details
                                    val displayApplicantLabel = when {
                                        role.equals("Guardian", ignoreCase = true) -> if (isHindi) "बच्चा: " else "Child: "
                                        leave.applicantType.equals("student", ignoreCase = true) -> if (isHindi) "छात्र: " else "Student: "
                                        else -> if (isHindi) "स्टाफ: " else "Staff: "
                                    }
                                    Text(
                                        text = "$displayApplicantLabel${resolved.applicantName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    if (!resolved.classSection.isNullOrBlank()) {
                                        Text(
                                            text = resolved.classSection,
                                            fontSize = 11.sp,
                                            color = subtitleColor
                                        )
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = statusBg
                                    ) {
                                        Text(
                                            text = statusText,
                                            color = statusTextCol,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                                        contentDescription = "Expand details",
                                        tint = subtitleColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = cardBorderColor.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Date duration
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.Calendar,
                                    contentDescription = null,
                                    tint = subtitleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${leave.startDate} ${if (isHindi) "से" else "to"} ${leave.endDate}",
                                    fontSize = 13.sp,
                                    color = textColor
                                )
                                if (leave.isHalfDay) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = cardBorderColor,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "हाफ डे" else "Half Day",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // ── Expanded Accordion Details ──
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Applied Reason
                                if (!leave.reason.isNullOrBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Info,
                                            contentDescription = null,
                                            tint = subtitleColor,
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${if (isHindi) "कारण: " else "Reason: "} ${leave.reason}",
                                            fontSize = 13.sp,
                                            color = textColor
                                        )
                                    }
                                }

                                // Half day period description
                                if (leave.isHalfDay && !leave.halfDayPeriod.isNullOrBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Clock,
                                            contentDescription = null,
                                            tint = subtitleColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${if (isHindi) "अवधि: " else "Period: "} ${leave.halfDayPeriod}",
                                            fontSize = 13.sp,
                                            color = textColor
                                        )
                                    }
                                }

                                // Action Details (Approved/Rejected Info)
                                if (leave.status != "Pending") {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Divider(color = cardBorderColor.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Action Date
                                    if (!leave.actionAt.isNullOrBlank()) {
                                        val cleanDate = leave.actionAt.substringBefore("T")
                                        Text(
                                            text = "${if (isHindi) "कार्रवाई तिथि: " else "Action Date: "} $cleanDate",
                                            fontSize = 12.sp,
                                            color = subtitleColor,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    // Rejection Reason / Remarks
                                    if (!leave.actionRemarks.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = statusBg.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${if (isHindi) "टिप्पणी: " else "Remarks: "} ${leave.actionRemarks}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = statusTextCol,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }

                                // ── Admin Action Buttons (Approve / Reject) for Pending Leaves ──
                                if (isAdmin && leave.status == "Pending") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = cardBorderColor.copy(alpha = 0.4f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedLeaveForAction = leave
                                                actionTypeTarget = "Approved"
                                                actionRemarksInput = ""
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Check,
                                                contentDescription = "Approve",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "स्वीकृत करें" else "Approve",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                selectedLeaveForAction = leave
                                                actionTypeTarget = "Rejected"
                                                actionRemarksInput = ""
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Lucide.X,
                                                contentDescription = "Reject",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "अस्वीकृत करें" else "Reject",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Approve / Reject Confirmation Dialog ──
    if (selectedLeaveForAction != null && actionTypeTarget != null) {
        val target = actionTypeTarget!!
        val isApprove = target == "Approved"
        val dialogTitle = if (isApprove) {
            if (isHindi) "छुट्टी स्वीकृत करें" else "Approve Leave Request"
        } else {
            if (isHindi) "छुट्टी अस्वीकृत करें" else "Reject Leave Request"
        }

        AlertDialog(
            onDismissRequest = {
                selectedLeaveForAction = null
                actionTypeTarget = null
            },
            title = {
                Text(
                    text = dialogTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isHindi) "क्या आप इस लीव आवेदन की स्थिति बदलना चाहते हैं? (ऐच्छिक टिप्पणी लिखें):"
                               else "Are you sure you want to update the leave status? (Optional Remarks):",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = actionRemarksInput,
                        onValueChange = { actionRemarksInput = it },
                        placeholder = {
                            Text(
                                text = if (isHindi) "टिप्पणी यहाँ लिखें (जैसे: स्वीकृत/अस्वीकृत)..." else "Write remarks here...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetLeave = selectedLeaveForAction
                        if (targetLeave != null) {
                            viewModel.onEvent(
                                InstitutionEvent.UpdateLeaveStatus(
                                    leaveId = targetLeave.id,
                                    status = target,
                                    remarks = actionRemarksInput.trim(),
                                    actionBy = userId
                                )
                            )
                        }
                        selectedLeaveForAction = null
                        actionTypeTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApprove) AppColors.EmeraldGreen else Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = if (isHindi) "पुष्टि करें" else "Confirm",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedLeaveForAction = null
                        actionTypeTarget = null
                    }
                ) {
                    Text(text = if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

// ── UI Resolved Model ──
data class ResolvedLeave(
    val entity: LocalOrganizationLeaveEntity,
    val applicantName: String,
    val classSection: String?,
    val actionByName: String?
)
