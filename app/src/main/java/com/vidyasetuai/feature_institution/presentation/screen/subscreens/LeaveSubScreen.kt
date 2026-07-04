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

        // 2. Query leaves from local Room DB
        val rawLeaves = dao.getAllLeaves()

        // 3. Filter based on roles (Admin roles see all leaves)
        val adminRoles = listOf("Admin", "System Administrator", "School Administrator", "Org Admin", "Principal", "Director", "Owner")
        val isAdmin = role in adminRoles

        val filteredRaw = when {
            isAdmin -> {
                rawLeaves // Admin sees all leaves
            }
            role == "Guardian" -> {
                val childIds = state.guardianStudents.map { it.id }
                rawLeaves.filter { it.studentId != null && it.studentId in childIds }
            }
            role == "Student" -> {
                val studentId = activeWorkspace?.studentId ?: ""
                rawLeaves.filter { it.studentId != null && it.studentId == studentId }
            }
            else -> {
                // Regular staff/teacher: see their own leaves
                val staffId = activeWorkspace?.id ?: ""
                rawLeaves.filter { it.staffId != null && it.staffId == staffId }
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
                        text = if (isHindi) "छुट्टियाँ" else "Leaves",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            // ── 1. Search Box Bar ──
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isHindi) "छुट्टी प्रकार, नाम या कारण खोजें..." else "Search leave type, name or reason...",
                        fontSize = 13.sp,
                        color = subtitleColor
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = subtitleColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                modifier = Modifier.size(18.dp),
                                tint = subtitleColor
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.EmeraldGreen,
                    unfocusedBorderColor = cardBorderColor,
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg
                ),
                singleLine = true
            )

            // ── 2. Loading State / Content Area ──
            if (isLoadingDbData) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.EmeraldGreen)
                }
            } else if (filteredLeaves.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Lucide.CalendarOff,
                        contentDescription = null,
                        tint = subtitleColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई छुट्टी का रिकॉर्ड नहीं मिला" else "No Leave Records Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi)
                            "आपके फ़िल्टर के अनुसार कोई छुट्टी आवेदन या रिकॉर्ड दर्ज नहीं है।"
                        else
                            "No leave requests or records match your search query.",
                        fontSize = 13.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLeaves) { resolved ->
                        val leave = resolved.entity
                        val isExpanded = expandedLeaveIds.contains(leave.id)

                        // Determine status colors
                        val (statusText, statusBg, statusTextCol) = when (leave.status) {
                            "Approved" -> Triple(
                                if (isHindi) "स्वीकृत" else "Approved",
                                if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9),
                                if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                            )
                            "Rejected" -> Triple(
                                if (isHindi) "अस्वीकृत" else "Rejected",
                                if (isDark) Color(0xFF3E1F1F) else Color(0xFFFFEBEE),
                                if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
                            )
                            else -> Triple(
                                if (isHindi) "लंबित" else "Pending",
                                if (isDark) Color(0xFF3E2D1F) else Color(0xFFFFF3E0),
                                if (isDark) Color(0xFFFFB74D) else Color(0xFFE65100)
                            )
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
                                            role == "Guardian" -> if (isHindi) "बच्चा: " else "Child: "
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

                                        // Action Author
                                        if (!resolved.actionByName.isNullOrBlank()) {
                                            Text(
                                                text = "${if (isHindi) "कार्रवाई कर्ता: " else "Action By: "} ${resolved.actionByName}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = subtitleColor
                                            )
                                        }
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── UI UI Model ──
data class ResolvedLeave(
    val entity: LocalOrganizationLeaveEntity,
    val applicantName: String,
    val classSection: String?,
    val actionByName: String?
)
