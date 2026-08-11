package com.vidyasetuai.feature_institution.presentation.screen.subscreens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_institution.data.local.entity.LocalParentStaffEntity
import com.vidyasetuai.feature_institution.presentation.state.InstitutionUiState
import com.vidyasetuai.feature_institution.presentation.viewmodel.InstitutionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDirectorySubScreen(
    state: InstitutionUiState,
    isHindi: Boolean,
    isDark: Boolean,
    viewModel: InstitutionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("ALL") } // ALL, TEACHER, DRIVER, ADMIN, SUPPORT

    LaunchedEffect(Unit) {
        val parentOrgId = state.activeWorkspace?.parentOrgId
        if (!parentOrgId.isNullOrEmpty()) {
            viewModel.loadOfflineStaff(parentOrgId)
        }
    }

    val staffList = state.offlineStaff

    val filteredStaff = remember(searchQuery, selectedRoleFilter, staffList) {
        staffList.filter { staff ->
            val matchesQuery = searchQuery.isBlank() ||
                    staff.name.contains(searchQuery, ignoreCase = true) ||
                    staff.mobileNumber.contains(searchQuery, ignoreCase = true) ||
                    (staff.roleName ?: "").contains(searchQuery, ignoreCase = true) ||
                    (staff.email ?: "").contains(searchQuery, ignoreCase = true)

            val roleLower = (staff.roleName ?: "").lowercase()
            val matchesFilter = when (selectedRoleFilter) {
                "TEACHER" -> roleLower.contains("teacher") || roleLower.contains("शिक्षक")
                "DRIVER" -> roleLower.contains("driver") || roleLower.contains("चालक")
                "ADMIN" -> roleLower.contains("admin") || roleLower.contains("principal") || roleLower.contains("director") || roleLower.contains("owner")
                "SUPPORT" -> !roleLower.contains("teacher") && !roleLower.contains("driver") && !roleLower.contains("admin") && !roleLower.contains("principal")
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    // Counts for summary cards
    val totalCount = staffList.size
    val teacherCount = staffList.count { (it.roleName ?: "").lowercase().contains("teacher") || (it.roleName ?: "").lowercase().contains("शिक्षक") }
    val driverCount = staffList.count { (it.roleName ?: "").lowercase().contains("driver") || (it.roleName ?: "").lowercase().contains("चालक") }

    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBgColor = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val primaryColor = AppColors.EmeraldGreen
    val borderClr = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(bgColor)) {
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
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = textColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "कर्मचारी निर्देशिका (Staff Directory)" else "Staff Directory",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(borderClr)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // --- SECTION 1: SUMMARY METRICS CARDS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StaffSummaryMiniCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "कुल कर्मचारी" else "Total Staff",
                        count = totalCount,
                        icon = Lucide.Users,
                        iconTint = primaryColor,
                        bgColor = cardBgColor,
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        borderColor = borderClr
                    )
                    StaffSummaryMiniCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "शिक्षक" else "Teachers",
                        count = teacherCount,
                        icon = Lucide.GraduationCap,
                        iconTint = Color(0xFF3B82F6),
                        bgColor = cardBgColor,
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        borderColor = borderClr
                    )
                    StaffSummaryMiniCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "ड्राइवर्स" else "Drivers",
                        count = driverCount,
                        icon = Lucide.Bus,
                        iconTint = Color(0xFFF59E0B),
                        bgColor = cardBgColor,
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        borderColor = borderClr
                    )
                }
            }

            // --- SECTION 2: SEARCH BAR ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isHindi) "नाम, रोल या मोबाइल से खोजें..." else "Search staff by name, role or mobile...",
                            fontSize = 13.sp,
                            color = subtitleColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = subtitleColor,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Clear",
                                    tint = subtitleColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBgColor,
                        unfocusedContainerColor = cardBgColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = borderClr
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- SECTION 3: ROLE FILTER CHIPS ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "ALL" to (if (isHindi) "सभी स्टाफ" else "All Staff"),
                        "TEACHER" to (if (isHindi) "शिक्षक" else "Teachers"),
                        "DRIVER" to (if (isHindi) "ड्राइवर्स" else "Drivers"),
                        "ADMIN" to (if (isHindi) "प्रशासनिक" else "Admin"),
                        "SUPPORT" to (if (isHindi) "अन्य सहायक" else "Support")
                    )

                    filterOptions.forEach { (key, label) ->
                        val isSelected = selectedRoleFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) primaryColor else cardBgColor)
                                .clickable { selectedRoleFilter = key }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else textColor
                            )
                        }
                    }
                }
            }

            // --- SECTION 4: STAFF CARDS LIST ---
            if (filteredStaff.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Users,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isHindi) "कोई कर्मचारी रिकॉर्ड नहीं मिला" else "No staff record found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = subtitleColor
                            )
                        }
                    }
                }
            } else {
                items(filteredStaff, key = { it.id }) { staff ->
                    StaffProfileCardItem(
                        staff = staff,
                        isHindi = isHindi,
                        context = context,
                        cardBgColor = cardBgColor,
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        primaryColor = primaryColor,
                        borderColor = borderClr
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffSummaryMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    borderColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun StaffProfileCardItem(
    staff: LocalParentStaffEntity,
    isHindi: Boolean,
    context: Context,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color,
    primaryColor: Color,
    borderColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Avatar, Name, Designation & Active Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = staff.name.take(1).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }

                // Name & Role
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = staff.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = staff.roleName ?: (if (isHindi) "कर्मचारी" else "Staff"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subtitleColor
                    )
                }

                // Status Badge
                Surface(
                    color = if (staff.isActive) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFEF4444).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (staff.isActive) (if (isHindi) "सक्रिय" else "ACTIVE") else (if (isHindi) "निष्क्रिय" else "INACTIVE"),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (staff.isActive) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = borderColor, thickness = 0.5.dp)

            // Contact Info Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Phone,
                        contentDescription = null,
                        tint = subtitleColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = staff.mobileNumber,
                        fontSize = 12.sp,
                        color = textColor
                    )
                }

                if (!staff.email.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Mail,
                            contentDescription = null,
                            tint = subtitleColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = staff.email,
                            fontSize = 12.sp,
                            color = textColor
                        )
                    }
                }

                if (!staff.dateOfJoining.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Calendar,
                            contentDescription = null,
                            tint = subtitleColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${if (isHindi) "जॉइनिंग तिथि: " else "Joined: "}${staff.dateOfJoining}",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                    }
                }
            }

            // Quick Actions Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call Button
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.mobileNumber}"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not launch phone dialer", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.PhoneCall,
                            contentDescription = "Call",
                            tint = primaryColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isHindi) "कॉल करें" else "Call",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                // WhatsApp Button
                Button(
                    onClick = {
                        try {
                            val cleanNumber = staff.mobileNumber.replace("+", "").replace(" ", "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366).copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquare,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "WhatsApp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366)
                        )
                    }
                }
            }
        }
    }
}
