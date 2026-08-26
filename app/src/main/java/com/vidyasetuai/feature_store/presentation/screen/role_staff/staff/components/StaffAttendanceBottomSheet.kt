package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffAttendanceEntity
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryRowUiModel
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAttendanceBottomSheet(
    isHindi: Boolean = false,
    staffRow: StaffSalaryRowUiModel,
    month: Int,
    year: Int,
    attendanceRecords: List<BusinessStaffAttendanceEntity>,
    onToggleDay: (date: String, status: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val yearMonth = remember(month, year) { YearMonth.of(year, month) }
    val daysInMonth = yearMonth.lengthOfMonth()

    val attendanceMap = remember(attendanceRecords) {
        attendanceRecords.associate { it.attendanceDate to it.status }
    }

    var selectedDateForPicker by remember { mutableStateOf<String?>(null) }

    val presentCount = attendanceRecords.count { it.status == "PRESENT" }
    val halfDayCount = attendanceRecords.count { it.status == "HALF_DAY" }
    val leaveCount = attendanceRecords.count { it.status == "PAID_LEAVE" || it.status == "HOLIDAY" }
    val absentCount = attendanceRecords.count { it.status == "ABSENT" }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHindi) "मासिक हाजिरी कैलेंडर" else "Monthly Attendance Calendar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${staffRow.staff.name} • ${yearMonth.month.name} $year",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.EmeraldGreen
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Summary Metric Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttendanceSummaryPill(
                    label = if (isHindi) "उपस्थित" else "Present",
                    count = presentCount,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                AttendanceSummaryPill(
                    label = if (isHindi) "हाफ-डे" else "Half Day",
                    count = halfDayCount,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                AttendanceSummaryPill(
                    label = if (isHindi) "सवैतनिक छुट्टी" else "Leave",
                    count = leaveCount,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                AttendanceSummaryPill(
                    label = if (isHindi) "अनुपस्थित" else "Absent",
                    count = absentCount,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (isHindi) "तारीख पर टैप करके हाजिरी बदलें:" else "Tap any date to change attendance status:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Monthly Days Grid (7 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items((1..daysInMonth).toList()) { day ->
                    val dateStr = String.format("%04d-%02d-%02d", year, month, day)
                    val status = attendanceMap[dateStr]

                    val (bgColor, textColor, label) = when (status) {
                        "PRESENT" -> Triple(Color(0xFF10B981).copy(alpha = 0.15f), Color(0xFF047857), "P")
                        "HALF_DAY" -> Triple(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFB45309), "HD")
                        "PAID_LEAVE" -> Triple(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF1D4ED8), "PL")
                        "HOLIDAY" -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFF6D28D9), "H")
                        "ABSENT" -> Triple(Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFB91C1C), "A")
                        else -> Triple(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), "—")
                    }

                    val isToday = dateStr == LocalDate.now().toString()

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bgColor,
                        border = if (isToday) androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.EmeraldGreen) else null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                selectedDateForPicker = dateStr
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 12.sp,
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Quick Status Modifier Row for Selected Date
            selectedDateForPicker?.let { targetDate ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Set Status for $targetDate:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { selectedDateForPicker = null }, modifier = Modifier.size(20.dp)) {
                                Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val options = listOf(
                                Triple("PRESENT", "Present (P)", Color(0xFF10B981)),
                                Triple("HALF_DAY", "Half Day (HD)", Color(0xFFF59E0B)),
                                Triple("PAID_LEAVE", "Leave (PL)", Color(0xFF3B82F6)),
                                Triple("ABSENT", "Absent (A)", Color(0xFFEF4444))
                            )

                            options.forEach { (st, lbl, col) ->
                                Button(
                                    onClick = {
                                        onToggleDay(targetDate, st)
                                        selectedDateForPicker = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = col),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(lbl, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
private fun AttendanceSummaryPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}
