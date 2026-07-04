package com.vidyasetuai.feature_feed.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.event.InstitutionEvent
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDirectorySubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onStudentClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val role = state.activeWorkspace?.role ?: ""
    val isStudent = role.equals("Student", ignoreCase = true)
    val isGuardian = role.equals("Guardian", ignoreCase = true)

    if (isStudent) {
        val studentId = state.activeWorkspace?.studentId ?: ""
        LaunchedEffect(studentId) {
            if (studentId.isNotEmpty()) {
                viewModel.onEvent(InstitutionEvent.LoadStudentProfileDetails(studentId))
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
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
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isHindi) "मेरी प्रोफ़ाइल" else "My Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                val student = state.selectedStudentDetail
                if (student == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColors.EmeraldGreen
                    )
                } else {
                    StudentDetailsCard(
                        student = student,
                        isHindi = isHindi,
                        isDark = isDark
                    )
                }
            }
        }
    } else {
        var searchQuery by remember { mutableStateOf("") }
        
        val filteredStudents = remember(searchQuery, state.offlineStudents) {
            if (searchQuery.isBlank()) {
                state.offlineStudents
            } else {
                state.offlineStudents.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    (it.srNumber ?: "").contains(searchQuery, ignoreCase = true)
                }
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
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
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isGuardian) {
                                if (isHindi) "मेरे बच्चे" else "My Children"
                            } else {
                                if (isHindi) "छात्र निर्देशिका" else "Student Directory"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (!isGuardian) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = {
                            Text(text = if (isHindi) "नाम या एसआर नंबर से खोजें..." else "Search by name or SR number...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Lucide.Search,
                                contentDescription = "Search"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.EmeraldGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )
                }

                val listData = if (isGuardian) state.guardianStudents else null
                
                if (isGuardian) {
                    if (listData.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "कोई बच्चा नहीं मिला" else "No children found",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listData) { child ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onStudentClick(child.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0xFF1E1E20) else Color(0xFFF2F2F7),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Lucide.User,
                                                contentDescription = child.name,
                                                tint = AppColors.EmeraldGreen,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = child.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (isHindi) "कक्षा: ${child.className ?: "N/A"}" else "Class: ${child.className ?: "N/A"}",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = Lucide.ChevronRight,
                                            contentDescription = "View details",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (filteredStudents.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "कोई छात्र नहीं मिला" else "No students found",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredStudents) { student ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onStudentClick(student.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0xFF1E1E20) else Color(0xFFF2F2F7),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.EmeraldGreen.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Lucide.Users,
                                                contentDescription = student.name,
                                                tint = AppColors.EmeraldGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = student.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${if (isHindi) "कक्षा" else "Class"}: ${student.className ?: "N/A"} | SR: ${student.srNumber}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = Lucide.ChevronRight,
                                            contentDescription = "View",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
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

@Composable
fun StudentDetailsCard(
    student: com.vidyasetuai.feature_institution.data.local.entity.LocalStudentEntity,
    isHindi: Boolean,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFF2F2F7)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AppColors.EmeraldGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.User,
                        contentDescription = student.name,
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = student.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (isHindi) "एसआर नंबर" else "SR Number"}: ${student.srNumber}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            DetailItem(
                label = if (isHindi) "कक्षा और अनुभाग" else "Class & Section",
                value = "${student.className ?: "N/A"} - ${student.sectionName ?: "N/A"}",
                isDark = isDark
            )
            DetailItem(
                label = if (isHindi) "अनुक्रमांक (Roll No.)" else "Roll Number",
                value = student.rollNumber?.toString() ?: "N/A",
                isDark = isDark
            )
            DetailItem(
                label = if (isHindi) "अभिभावक का नाम" else "Guardian Name",
                value = student.guardianName ?: "N/A",
                isDark = isDark
            )
            DetailItem(
                label = if (isHindi) "मोबाइल नंबर" else "Mobile Number",
                value = student.guardianMobile ?: "N/A",
                isDark = isDark
            )
            DetailItem(
                label = if (isHindi) "जन्म तिथि" else "Date of Birth",
                value = student.dateOfBirth.ifEmpty { "N/A" },
                isDark = isDark
            )
            DetailItem(
                label = if (isHindi) "लिंग" else "Gender",
                value = student.gender ?: "N/A",
                isDark = isDark
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
