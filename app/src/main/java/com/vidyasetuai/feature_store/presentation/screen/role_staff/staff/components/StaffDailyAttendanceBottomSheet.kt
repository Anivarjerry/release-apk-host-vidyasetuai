package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

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
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDailyAttendanceBottomSheet(
    isHindi: Boolean = false,
    date: String,
    staffList: List<BusinessStaffEntity>,
    attendanceMap: Map<String, String>,
    isSaving: Boolean,
    onSetStatus: (staffId: String, status: String) -> Unit,
    onSaveAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                        text = if (isHindi) "आज की दैनिक हाजिरी (Daily Attendance)" else "Daily Staff Attendance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "📅 $date • Total Staff: ${staffList.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.EmeraldGreen
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Quick Action: Mark All Present
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        staffList.forEach { staff ->
                            onSetStatus(staff.id, "PRESENT")
                        }
                    }
                ) {
                    Icon(imageVector = Lucide.CheckCheck, contentDescription = null, tint = AppColors.EmeraldGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHindi) "सभी को उपस्थित मार्क करें" else "Mark All Present", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.EmeraldGreen)
                }
            }

            // Staff List with Quick Toggle Buttons
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(staffList, key = { it.id }) { staff ->
                    val currentStatus = attendanceMap[staff.id] ?: "PRESENT"

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = staff.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = staff.role,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (currentStatus) {
                                        "PRESENT" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                        "HALF_DAY" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                        "PAID_LEAVE" -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                        else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = currentStatus,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (currentStatus) {
                                            "PRESENT" -> Color(0xFF047857)
                                            "HALF_DAY" -> Color(0xFFB45309)
                                            "PAID_LEAVE" -> Color(0xFF1D4ED8)
                                            else -> Color(0xFFB91C1C)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // 4 Toggle Option Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val options = listOf(
                                    Triple("PRESENT", "Present", Color(0xFF10B981)),
                                    Triple("HALF_DAY", "Half-Day", Color(0xFFF59E0B)),
                                    Triple("PAID_LEAVE", "Leave", Color(0xFF3B82F6)),
                                    Triple("ABSENT", "Absent", Color(0xFFEF4444))
                                )

                                options.forEach { (st, label, col) ->
                                    val isSelected = currentStatus == st
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) col else MaterialTheme.colorScheme.surface,
                                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onSetStatus(staff.id, st) }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Save All Daily Attendance CTA Button
            Button(
                onClick = onSaveAll,
                enabled = !isSaving && staffList.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isHindi) "आज की हाजिरी सुरक्षित करें" else "Save Daily Attendance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
