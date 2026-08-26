package com.vidyasetuai.feature_store.presentation.screen.role_owner.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBankUpiBottomSheet(
    isHindi: Boolean = false,
    initialBusiness: BusinessEntity?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        upiId: String?,
        bankName: String?,
        accountNo: String?,
        ifsc: String?,
        branch: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var upiId by remember { mutableStateOf(initialBusiness?.upiId ?: "") }
    var bankName by remember { mutableStateOf(initialBusiness?.bankName ?: "") }
    var accountNo by remember { mutableStateOf(initialBusiness?.bankAccountNo ?: "") }
    var ifsc by remember { mutableStateOf(initialBusiness?.bankIfsc ?: "") }
    var branch by remember { mutableStateOf(initialBusiness?.bankBranch ?: "") }

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
                .fillMaxHeight(0.85f)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.QrCode,
                            contentDescription = "UPI",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isHindi) "पेमेंट UPI QR व बैंक खाता" else "Payment UPI QR & Bank Account",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "ग्राहक रसीदों पर लाइव QR कोड जनरेट करने के लिए" else "Auto-generated payment QR code on customer bills",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Lucide.X, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // UPI VPA Box
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it.trim() },
                    label = { Text(if (isHindi) "UPI ID / VPA (e.g. 9898989898@paytm) *" else "Merchant UPI ID / VPA *") },
                    supportingText = { Text("यह UPI ID बिलों और स्टैंडी पर डायनेमिक QR बनाएगी") },
                    leadingIcon = {
                        Icon(imageVector = Lucide.QrCode, contentDescription = null, tint = Color(0xFF10B981))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                Text(
                    text = if (isHindi) "बैंक विवरण (वैकल्पिक / Optional)" else "Bank Account Details (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text(if (isHindi) "बैंक का नाम (Bank Name)" else "Bank Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountNo,
                    onValueChange = { accountNo = it.trim() },
                    label = { Text(if (isHindi) "बैंक खाता संख्या (A/C Number)" else "Bank Account Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ifsc,
                        onValueChange = { ifsc = it.uppercase().trim() },
                        label = { Text(if (isHindi) "IFSC कोड" else "IFSC Code") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = branch,
                        onValueChange = { branch = it },
                        label = { Text(if (isHindi) "बैंक शाखा" else "Bank Branch") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onSave(
                            upiId.ifBlank { null },
                            bankName.ifBlank { null },
                            accountNo.ifBlank { null },
                            ifsc.ifBlank { null },
                            branch.ifBlank { null }
                        )
                    },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isHindi) "बैंक व UPI सुरक्षित करें" else "Save UPI & Bank Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
