package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffPayrollGenerationBottomSheet(
    isHindi: Boolean = false,
    uiState: StaffSalaryUiState,
    onGenerate: (lockMonth: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var lockMonth by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
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
                        text = if (isHindi) "मासिक पेरोल जेनरेशन" else "Generate Monthly Payroll",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${if (isHindi) uiState.monthNameHindi else uiState.monthName} ${uiState.selectedYear}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.EmeraldGreen
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.EmeraldGreen.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isHindi) "कुल कर्मचारी (Staff Count):" else "Total Employees:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${uiState.staffRows.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isHindi) "अनुमानित कुल पेरोल (Projected Payroll):" else "Projected Payroll:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${uiState.totalPayroll}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.EmeraldGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isHindi) "कुल चालू अग्रिम (Advances):" else "Running Advances:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹${uiState.totalRunningAdvances}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                }
            }

            // Lock Month Option Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "महीने का पेरोल लॉक करें (Lock Month)" else "Lock Monthly Payroll",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "लॉक करने के बाद इस महीने की हाजिरी में बदलाव नहीं किया जा सकेगा।" else "Prevents further attendance alterations once finalized.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = lockMonth,
                        onCheckedChange = { lockMonth = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AppColors.EmeraldGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CTA Button
            Button(
                onClick = { onGenerate(lockMonth) },
                enabled = !uiState.isSaving && uiState.staffRows.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.EmeraldGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (lockMonth) {
                            if (isHindi) "पेरोल लॉक व जेनरेट करें" else "Lock & Generate Payroll"
                        } else {
                            if (isHindi) "मासिक स्लिप जेनरेट करें" else "Generate Payroll Slips"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
