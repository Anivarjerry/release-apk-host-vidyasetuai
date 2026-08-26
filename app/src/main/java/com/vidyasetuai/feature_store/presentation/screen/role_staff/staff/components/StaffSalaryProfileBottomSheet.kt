package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.StaffSalaryRowUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSalaryProfileBottomSheet(
    isHindi: Boolean = false,
    staffRow: StaffSalaryRowUiModel,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        salaryType: String,
        baseSalary: Double,
        workingDays: Int,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        holderName: String?,
        upiId: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val profile = staffRow.profile

    var salaryType by remember(profile) { mutableStateOf(profile?.salaryType ?: "MONTHLY") }
    var baseSalaryText by remember(profile) { mutableStateOf(if (profile != null && profile.baseSalary > 0) profile.baseSalary.toString() else "") }
    var workingDaysText by remember(profile) { mutableStateOf((profile?.workingDaysPerMonth ?: 30).toString()) }

    var bankName by remember(profile) { mutableStateOf(profile?.bankName ?: "") }
    var accountNo by remember(profile) { mutableStateOf(profile?.accountNumber ?: "") }
    var ifscCode by remember(profile) { mutableStateOf(profile?.ifscCode ?: "") }
    var holderName by remember(profile) { mutableStateOf(profile?.accountHolderName ?: staffRow.staff.name) }
    var upiId by remember(profile) { mutableStateOf(profile?.upiId ?: "") }

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
                        text = if (isHindi) "सैलरी व बैंक खाता सेटिंग्स" else "Salary & Bank Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${staffRow.staff.name} • ${staffRow.staff.role}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 1. Salary Type Chips
            Text(
                text = if (isHindi) "वेतन का प्रकार (Salary Type)" else "Salary Type",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("MONTHLY" to "Monthly (मासिक)", "DAILY" to "Daily (दैनिक)").forEach { (type, label) ->
                    val isSelected = salaryType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { salaryType = type },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            // 2. Base Salary Amount
            OutlinedTextField(
                value = baseSalaryText,
                onValueChange = { baseSalaryText = it },
                label = { Text(if (isHindi) "मूल वेतन (Base Salary ₹) *" else "Base Salary Amount (₹) *") },
                placeholder = { Text("e.g. 18000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Working Days Per Month
            OutlinedTextField(
                value = workingDaysText,
                onValueChange = { workingDaysText = it },
                label = { Text(if (isHindi) "महीने के कार्य दिवस (Working Days)" else "Standard Working Days / Month") },
                placeholder = { Text("30") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 4. UPI ID
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text(if (isHindi) "कर्मचारी का UPI ID (वैकल्पिक)" else "Staff UPI ID (Optional)") },
                placeholder = { Text("worker@upi / 9876543210@paytm") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Bank Account Details
            Text(
                text = if (isHindi) "बैंक विवरण (Bank Account Details):" else "Bank Account Details (Optional):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it },
                label = { Text("Account Holder Name") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name (e.g. SBI, HDFC)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = accountNo,
                    onValueChange = { accountNo = it },
                    label = { Text("Account Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f)
                )
                OutlinedTextField(
                    value = ifscCode,
                    onValueChange = { ifscCode = it.uppercase() },
                    label = { Text("IFSC Code") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save CTA Button
            Button(
                onClick = {
                    val base = baseSalaryText.toDoubleOrNull() ?: 0.0
                    val days = workingDaysText.toIntOrNull() ?: 30
                    onSave(salaryType, base, days, bankName, accountNo, ifscCode, holderName, upiId)
                },
                enabled = !isSaving && (baseSalaryText.toDoubleOrNull() ?: 0.0) > 0.0,
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
                        text = if (isHindi) "सैलरी प्रोफाइल सुरक्षित करें" else "Save Salary Profile",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
