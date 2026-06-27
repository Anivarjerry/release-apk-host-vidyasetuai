package com.vidyasetuai.feature_feed.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AttendanceSubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val cardBg = if (isDark) Color(0xFF1C1C1E) else Color.White
    val borderVal = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    val currentYear = java.time.LocalDate.now().year
    val currentMonthVal = java.time.LocalDate.now().monthValue

    var selectedYear by remember { mutableIntStateOf(currentYear) }
    var selectedMonth by remember { mutableIntStateOf(currentMonthVal) }

    var showYearDialog by remember { mutableStateOf(false) }
    var showMonthDialog by remember { mutableStateOf(false) }

    val monthsEn = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val monthsHi = listOf("जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")

    val years = listOf(2026, 2025)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom App Bar


        // Year / Month Selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Month Selector Button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                modifier = Modifier
                    .weight(1f)
                    .clickable { showMonthDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) monthsHi[selectedMonth - 1] else monthsEn[selectedMonth - 1],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(imageVector = Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Year Selector Button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                modifier = Modifier
                    .weight(1f)
                    .clickable { showYearDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedYear.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(imageVector = Lucide.ChevronDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.guardianStudents.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderVal),
                    color = cardBg
                ) {
                    Text(
                        text = if (isHindi) "कोई बच्चा लिंक नहीं है" else "No children linked.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                state.guardianStudents.forEach { student ->
                    // Filter attendance for this student and this month/year
                    val studentAttendanceList = state.studentAttendance.filter {
                        it.studentId == student.id &&
                        it.attendanceDate.startsWith(String.format(java.util.Locale.US, "%04d-%02d", selectedYear, selectedMonth))
                    }

                    // Compute stats
                    val presentCount = studentAttendanceList.count { it.status == "Present" }
                    val absentCount = studentAttendanceList.count { it.status == "Absent" }
                    val lateCount = studentAttendanceList.count { it.status == "Late" || it.status == "Half Day" }
                    val leaveCount = studentAttendanceList.count { it.status == "On Leave" || it.status == "Leave" }
                    
                    val presentRatio = if (studentAttendanceList.isNotEmpty()) {
                        ((presentCount + lateCount).toFloat() / studentAttendanceList.size * 100).toInt()
                    } else 0

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, borderVal),
                        color = cardBg
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Student Name and Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = student.className ?: "",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "$presentRatio%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Today's Status badge
                            val todayStr = java.time.LocalDate.now().toString()
                            val todayAttendance = state.studentAttendance.find { it.studentId == student.id && it.attendanceDate == todayStr }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isHindi) "आज की उपस्थिति:" else "Today's Status:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val todayStatus = todayAttendance?.status ?: "No Record"
                                val badgeColor = when (todayStatus) {
                                    "Present" -> AppColors.EmeraldGreen
                                    "Absent" -> Color(0xFFEF4444)
                                    "Late", "Half Day" -> Color(0xFFF59E0B)
                                    "On Leave", "Leave" -> Color(0xFF8E8E93)
                                    else -> Color(0xFF8E8E93)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = badgeColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = if (isHindi) {
                                            when (todayStatus) {
                                                "Present" -> "उपस्थित"
                                                "Absent" -> "अनुपस्थित"
                                                "Late" -> "देरी से"
                                                "Half Day" -> "हाफ-डे"
                                                "On Leave", "Leave" -> "छुट्टी पर"
                                                else -> "जानकारी नहीं"
                                            }
                                        } else todayStatus,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(MaterialTheme.colorScheme.outlineVariant))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Calendar grid
                            Text(
                                text = if (isHindi) "दैनिक कैलेंडर दृश्य:" else "Daily Calendar Grid:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Generate days grid
                            val firstDayOfMonth = java.time.LocalDate.of(selectedYear, selectedMonth, 1)
                            val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value - 1
                            val totalDays = firstDayOfMonth.lengthOfMonth()

                            val totalGridCells = dayOfWeekOffset + totalDays
                            val rows = (totalGridCells + 6) / 7

                            // Weekday headers
                            val weekdaysEn = listOf("M", "T", "W", "T", "F", "S", "S")
                            val weekdaysHi = listOf("सो", "मं", "बु", "गु", "शु", "श", "र")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (i in 0 until 7) {
                                    Text(
                                        text = if (isHindi) weekdaysHi[i] else weekdaysEn[i],
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (r in 0 until rows) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        for (c in 0 until 7) {
                                            val cellIdx = r * 7 + c
                                            val dayNumber = cellIdx - dayOfWeekOffset + 1
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (dayNumber in 1..totalDays) {
                                                    val dateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, dayNumber)
                                                    val record = studentAttendanceList.find { it.attendanceDate == dateStr }
                                                    val statusColor = when (record?.status) {
                                                        "Present" -> AppColors.EmeraldGreen
                                                        "Absent" -> Color(0xFFEF4444)
                                                        "Late", "Half Day" -> Color(0xFFF59E0B)
                                                        "On Leave", "Leave" -> Color(0xFF8E8E93)
                                                        else -> if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                                    }
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = statusColor.copy(alpha = if (record != null) 0.15f else 1f),
                                                        border = if (record != null) androidx.compose.foundation.BorderStroke(1.dp, statusColor) else null,
                                                        modifier = Modifier.fillMaxSize()
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = dayNumber.toString(),
                                                                fontSize = 11.sp,
                                                                fontWeight = if (record != null) FontWeight.Bold else FontWeight.Normal,
                                                                color = if (record != null) statusColor else MaterialTheme.colorScheme.onBackground
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(MaterialTheme.colorScheme.outlineVariant))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Legend and Statistics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LegendItem(color = AppColors.EmeraldGreen, label = if (isHindi) "उपस्थित: $presentCount" else "Present: $presentCount")
                                LegendItem(color = Color(0xFFEF4444), label = if (isHindi) "अनुपस्थित: $absentCount" else "Absent: $absentCount")
                                LegendItem(color = Color(0xFFF59E0B), label = if (isHindi) "लेट/हाफ-डे: $lateCount" else "Late/Half: $lateCount")
                                LegendItem(color = Color(0xFF8E8E93), label = if (isHindi) "छुट्टी: $leaveCount" else "Leave: $leaveCount")
                            }
                        }
                    }
                }
            }
        }
    }

    // Month Selector Dialog
    if (showMonthDialog) {
        Dialog(onDismissRequest = { showMonthDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "महीना चुनें" else "Select Month",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.height(280.dp)
                    ) {
                        items(12) { idx ->
                            val monthNum = idx + 1
                            val isSelected = monthNum == selectedMonth
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable {
                                        selectedMonth = monthNum
                                        showMonthDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val monthText = if (isHindi) monthsHi[idx] else monthsEn[idx]
                                Text(
                                    text = monthText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                                )
                                if (isSelected) {
                                    Icon(imageVector = Lucide.Check, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Year Selector Dialog
    if (showYearDialog) {
        Dialog(onDismissRequest = { showYearDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1C1C1E) else Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "वर्ष चुनें" else "Select Year",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        years.forEach { yr ->
                            val isSelected = yr == selectedYear
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.1f) else Color.Transparent)
                                    .clickable {
                                        selectedYear = yr
                                        showYearDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = yr.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onBackground
                                )
                                if (isSelected) {
                                    Icon(imageVector = Lucide.Check, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
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
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
