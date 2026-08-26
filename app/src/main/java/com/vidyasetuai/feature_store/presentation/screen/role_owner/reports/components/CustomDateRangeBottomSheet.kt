package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangeBottomSheet(
    isHindi: Boolean = false,
    initialStartDate: String,
    initialEndDate: String,
    onDismiss: () -> Unit,
    onApplyRange: (start: String, end: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }

    // Pillar 5: Layered BackHandler
    BackHandler(enabled = true) {
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isHindi) "कस्टम दिनांक सीमा चुनें" else "Select Custom Date Range",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Start Date Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "प्रारंभ तिथि (Start Date: YYYY-MM-DD)" else "Start Date (YYYY-MM-DD)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Lucide.Calendar, contentDescription = "Start", tint = Color(0xFF10B981))
                    }
                )
            }

            // End Date Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isHindi) "अंतिम तिथि (End Date: YYYY-MM-DD)" else "End Date (YYYY-MM-DD)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Lucide.Calendar, contentDescription = "End", tint = Color(0xFF3B82F6))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onApplyRange(startDate.trim(), endDate.trim()) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Lucide.Check, contentDescription = "Apply", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "दिनांक सीमा लागू करें (Apply Date Range)" else "APPLY DATE RANGE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
