package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components

import androidx.compose.foundation.background
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
import com.composables.icons.lucide.AtSign
import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChefHat
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.User
import com.composables.icons.lucide.UserPlus
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStaffBottomSheet(
    isHindi: Boolean = false,
    staffForEdit: StaffMemberUiModel?,
    branches: List<BusinessBranchEntity>,
    verifiedUser: StaffLinkedUserDto?,
    isSearchingUser: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSearchUsername: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveStaff: (
        name: String,
        phone: String,
        email: String?,
        branchId: String,
        role: String,
        username: String?,
        userId: String?,
        vehicleType: String?,
        vehicleNumber: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember(staffForEdit) { mutableStateOf(staffForEdit?.name ?: "") }
    var phone by remember(staffForEdit) { mutableStateOf(staffForEdit?.phone ?: "") }
    var email by remember(staffForEdit) { mutableStateOf(staffForEdit?.staff?.email ?: "") }
    var usernameInput by remember(staffForEdit) {
        mutableStateOf(staffForEdit?.staff?.username ?: "")
    }
    var selectedBranchId by remember(staffForEdit, branches) {
        mutableStateOf(staffForEdit?.staff?.branchId ?: branches.firstOrNull()?.id ?: "")
    }
    var selectedRole by remember(staffForEdit) {
        mutableStateOf(staffForEdit?.role ?: "CASHIER")
    }
    var vehicleType by remember(staffForEdit) {
        mutableStateOf(staffForEdit?.vehicleType ?: "BIKE")
    }
    var vehicleNumber by remember(staffForEdit) {
        mutableStateOf(staffForEdit?.vehicleNumber ?: "")
    }

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
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (staffForEdit == null) {
                            if (isHindi) "नया कर्मचारी / राइडर जोड़ें" else "Add New Staff Member"
                        } else {
                            if (isHindi) "कर्मचारी विवरण संपादित करें" else "Edit Staff Details"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "कैशियर, शेफ, डिलीवरी राइडर एवं पद प्रबंधन" else "Cashiers, chefs, delivery riders & role permissions",
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

            // 1. App Account Username Linking (@username)
            OutlinedTextField(
                value = usernameInput,
                onValueChange = {
                    usernameInput = it
                    onSearchUsername(it)
                },
                label = { Text(if (isHindi) "VidyaSetu ऐप यूजरनेम (@username)" else "VidyaSetu App Username (@username)") },
                placeholder = { Text("e.g. @rahul_sharma") },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.AtSign,
                        contentDescription = "Username",
                        modifier = Modifier.size(18.dp),
                        tint = if (verifiedUser != null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (isSearchingUser) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else if (verifiedUser != null) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Verified",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // User Verification Feedback Card
            if (verifiedUser != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = "Verified",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "वेरिफाइड खाता: @${verifiedUser.username ?: ""} (${verifiedUser.fullName ?: "App User"})" else "Verified App Account: @${verifiedUser.username ?: ""} (${verifiedUser.fullName ?: "App User"})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF065F46)
                        )
                    }
                }
            } else if (usernameInput.isNotBlank() && !isSearchingUser && usernameInput.length >= 3) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) "⚠️ कोई ऐप खाता नहीं मिला। यह कर्मचारी बिना लिंक हुए सेव होगा।" else "⚠️ No app user found with this @username. Staff will be saved without linked login.",
                    fontSize = 11.sp,
                    color = Color(0xFFD97706),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Name & Mobile Number
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isHindi) "कर्मचारी का नाम *" else "Full Name *") },
                placeholder = { Text("e.g. Lokesh Kumar") },
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional)") },
                placeholder = { Text("e.g. worker@example.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Branch Assignment Selector
            Text(
                text = if (isHindi) "शाखा असाइन करें (Assigned Branch)" else "Assigned Branch Outlet",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            branches.forEach { branch ->
                val isSelected = selectedBranchId == branch.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { selectedBranchId = branch.id },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.Building2,
                                contentDescription = "Branch",
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = branch.branchName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${branch.city}, ${branch.state}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Role Selection (CASHIER, CHEF, DELIVERY_RIDER, MANAGER, INVENTORY_CLERK)
            Text(
                text = if (isHindi) "पद व भूमिका (Role & Permission)" else "Designation & Role",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            val roles = listOf(
                Triple("CASHIER", if (isHindi) "कैशियर (POS बिलिंग)" else "Cashier (POS Counter)", Lucide.User),
                Triple("CHEF", if (isHindi) "शेफ / किचन (KDS व्यू)" else "Chef / Kitchen (KDS)", Lucide.ChefHat),
                Triple("DELIVERY_RIDER", if (isHindi) "डिलीवरी राइडर (Fleet)" else "Delivery Rider (Fleet)", Lucide.Bike),
                Triple("INVENTORY_CLERK", if (isHindi) "स्टॉक क्लर्क (Inventory)" else "Stock Clerk", Lucide.Building2),
                Triple("BRANCH_MANAGER", if (isHindi) "शाखा प्रबंधक (Manager)" else "Branch Manager", Lucide.Shield)
            )

            roles.forEach { (roleKey, roleLabel, iconVector) ->
                val isSelected = selectedRole == roleKey
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { selectedRole = roleKey },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = roleLabel,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = roleLabel,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Lucide.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 5. If DELIVERY_RIDER, show Vehicle Details Section
            if (selectedRole == "DELIVERY_RIDER") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isHindi) "राइडर वाहन विवरण (Vehicle Details)" else "Rider Vehicle Information",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("BIKE", "SCOOTER", "EV_CYCLE", "VAN").forEach { type ->
                        val isSelected = vehicleType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { vehicleType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it.uppercase() },
                    label = { Text(if (isHindi) "गाड़ी नंबर (Vehicle Number)" else "Vehicle Registration No.") },
                    placeholder = { Text("e.g. PB09AH9457") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Staff CTA Button
            Button(
                onClick = {
                    onSaveStaff(
                        name,
                        phone,
                        email,
                        selectedBranchId,
                        selectedRole,
                        usernameInput.ifBlank { null },
                        verifiedUser?.id,
                        if (selectedRole == "DELIVERY_RIDER") vehicleType else null,
                        if (selectedRole == "DELIVERY_RIDER") vehicleNumber else null
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
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (staffForEdit == null) {
                            if (isHindi) "कर्मचारी सुरक्षित करें (Save Staff)" else "Save Staff Member"
                        } else {
                            if (isHindi) "परिवर्तन सहेजें (Update Staff)" else "Update Staff Member"
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
