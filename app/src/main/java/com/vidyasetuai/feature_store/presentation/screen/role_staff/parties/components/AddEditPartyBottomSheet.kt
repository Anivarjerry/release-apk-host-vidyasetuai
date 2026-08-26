package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPartyBottomSheet(
    isHindi: Boolean = false,
    editingParty: PartyEntity?,
    isSubmitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (
        partyType: String,
        name: String,
        phone: String,
        email: String?,
        gstin: String?,
        panNumber: String?,
        creditLimit: Double,
        openingBalance: Double,
        isReceivable: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var partyType by remember(editingParty) { mutableStateOf(editingParty?.partyType ?: "CUSTOMER") }
    var name by remember(editingParty) { mutableStateOf(editingParty?.name ?: "") }
    var phone by remember(editingParty) { mutableStateOf(editingParty?.phone ?: "") }
    var email by remember(editingParty) { mutableStateOf(editingParty?.email ?: "") }
    var gstin by remember(editingParty) { mutableStateOf(editingParty?.gstin ?: "") }
    var panNumber by remember(editingParty) { mutableStateOf(editingParty?.panNumber ?: "") }
    var creditLimitStr by remember(editingParty) {
        mutableStateOf(if (editingParty != null && editingParty.creditLimit > 0) editingParty.creditLimit.toString() else "")
    }

    var openingBalanceStr by remember { mutableStateOf("") }
    var isReceivable by remember { mutableStateOf(true) }

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
                .padding(bottom = 32.dp)
        ) {
            // Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (editingParty == null) {
                            if (isHindi) "नया खाता / पार्टी जोड़ें" else "Add New Party / Khata"
                        } else {
                            if (isHindi) "पार्टी विवरण संपादित करें" else "Edit Party Details"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "ग्राहक या सप्लायर का संपर्क व खाता विवरण" else "Customer or supplier contact & credit details",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner if any
            if (!errorMessage.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFB91C1C),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 1. Party Type Selector Toggle
            Text(
                text = if (isHindi) "पार्टी का प्रकार (Party Type)" else "Party Type",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Customer Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { partyType = "CUSTOMER" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (partyType == "CUSTOMER") Color(0xFF10B981) else Color.Transparent,
                    shadowElevation = if (partyType == "CUSTOMER") 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.User,
                            contentDescription = "Customer",
                            tint = if (partyType == "CUSTOMER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "ग्राहक (Customer)" else "Customer",
                            fontSize = 13.sp,
                            fontWeight = if (partyType == "CUSTOMER") FontWeight.Bold else FontWeight.Medium,
                            color = if (partyType == "CUSTOMER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Supplier Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { partyType = "SUPPLIER" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (partyType == "SUPPLIER") Color(0xFF3B82F6) else Color.Transparent,
                    shadowElevation = if (partyType == "SUPPLIER") 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Building2,
                            contentDescription = "Supplier",
                            tint = if (partyType == "SUPPLIER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "सप्लायर (Supplier)" else "Supplier",
                            fontSize = 13.sp,
                            fontWeight = if (partyType == "SUPPLIER") FontWeight.Bold else FontWeight.Medium,
                            color = if (partyType == "SUPPLIER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Party Name & Phone
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isHindi) "पार्टी का नाम *" else "Party Name *") },
                placeholder = { Text("e.g. Ramesh Kumar / ABC Traders") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 15) phone = it },
                label = { Text(if (isHindi) "मोबाइल नंबर *" else "Mobile Number *") },
                placeholder = { Text("10-digit mobile number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Email & GSTIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("optional") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = gstin,
                    onValueChange = { if (it.length <= 15) gstin = it.uppercase() },
                    label = { Text("GSTIN") },
                    placeholder = { Text("15 digits") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. PAN Number & Credit Limit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = panNumber,
                    onValueChange = { if (it.length <= 10) panNumber = it.uppercase() },
                    label = { Text("PAN Number") },
                    placeholder = { Text("10 chars") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = creditLimitStr,
                    onValueChange = { creditLimitStr = it },
                    label = { Text(if (isHindi) "क्रेडिट लिमिट (₹)" else "Credit Limit (₹)") },
                    placeholder = { Text("₹0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // 5. Opening Balance (Only shown when creating a new party)
            if (editingParty == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isHindi) "प्रारंभिक शेष (Opening Balance)" else "Opening Balance (Initial Amount)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = openingBalanceStr,
                        onValueChange = { openingBalanceStr = it },
                        placeholder = { Text("₹0.00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    // Toggle: Receivable (लेना है) vs Payable (देना है)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isReceivable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                            .border(1.dp, if (isReceivable) Color(0xFF10B981) else Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .clickable { isReceivable = !isReceivable }
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isReceivable) {
                                if (isHindi) "लेना है (To Receive)" else "To Receive"
                            } else {
                                if (isHindi) "देना है (To Pay)" else "To Pay"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isReceivable) Color(0xFF166534) else Color(0xFF991B1B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save CTA Button
            Button(
                onClick = {
                    val limit = creditLimitStr.toDoubleOrNull() ?: 0.0
                    val opening = openingBalanceStr.toDoubleOrNull() ?: 0.0
                    onSave(
                        partyType,
                        name,
                        phone,
                        email,
                        gstin,
                        panNumber,
                        limit,
                        opening,
                        isReceivable
                    )
                },
                enabled = !isSubmitting && name.isNotBlank() && phone.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (editingParty == null) {
                            if (isHindi) "खाता सुरक्षित करें (Save Party)" else "Save Party & Khata"
                        } else {
                            if (isHindi) "परिवर्तन सहेजें (Update Party)" else "Update Party"
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
