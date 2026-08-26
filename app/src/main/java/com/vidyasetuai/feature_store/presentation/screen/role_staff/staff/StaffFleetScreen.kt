package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChefHat
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Power
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Shield
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.User
import com.composables.icons.lucide.UserPlus
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components.AddEditStaffBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components.BranchManagementBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.staff.components.RiderCashSettlementBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffFleetScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit,
    onNavigateToSalary: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: StaffFleetViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StaffFleetViewModel(context) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Delete Confirmation State
    var staffToDelete by remember { mutableStateOf<StaffMemberUiModel?>(null) }

    // Pillar 5: Layered Back Navigation Resilience
    BackHandler(enabled = true) {
        when {
            uiState.isStaffDrawerOpen -> viewModel.closeStaffDrawer()
            uiState.isBranchModalOpen -> viewModel.closeBranchModal()
            uiState.isSettlementModalOpen -> viewModel.closeSettlementModal()
            else -> onBack()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "स्टाफ व डिलीवरी फ्लीट" else "Staff & Delivery Fleet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "${uiState.totalStaff} कर्मचारी • ${uiState.totalBranches} शाखाएं" else "${uiState.totalStaff} Staff • ${uiState.totalBranches} Branches",
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Salary & Attendance Button
                    IconButton(onClick = onNavigateToSalary) {
                        Icon(
                            imageVector = Lucide.Wallet,
                            contentDescription = "Salary & Attendance",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Manage Branches Icon Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.openBranchModal() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Building2,
                                contentDescription = "Branches",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${uiState.totalBranches}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Add Staff Member CTA Button
                    Button(
                        onClick = { viewModel.openAddStaff() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "स्टाफ" else "Staff",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // 1. 4-Pill KPI Metric Header Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Total Staff
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "कुल स्टाफ" else "Total Staff",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Lucide.Users,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.totalStaff}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 2: On-Duty Riders
                Surface(
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "ऑन-ड्यूटी राइडर" else "On-Duty Riders",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF166534)
                            )
                            Icon(
                                imageVector = Lucide.Bike,
                                contentDescription = null,
                                tint = Color(0xFF166534),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.onDutyRidersCount} / ${uiState.deliveryRiders.size}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF166534)
                        )
                    }
                }

                // Card 3: Store Outlets
                Surface(
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "आउटलेट" else "Outlets",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Lucide.Building2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.totalBranches}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 4: Cash with Riders
                Surface(
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "राइडर COD कैश" else "Rider Cash",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF991B1B)
                            )
                            Icon(
                                imageVector = Lucide.Wallet,
                                contentDescription = null,
                                tint = Color(0xFF991B1B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format("%.2f", uiState.totalPendingCash)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Search Bar + Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(if (isHindi) "कर्मचारी नाम, फोन, गाड़ी नंबर खोजें..." else "Search worker, phone, vehicle no...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Role Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val roles = listOf(
                    Pair("ALL", if (isHindi) "सभी पद (${uiState.staffList.size})" else "All Roles (${uiState.staffList.size})"),
                    Pair("DELIVERY_RIDER", if (isHindi) "राइडर्स" else "Riders"),
                    Pair("CASHIER", if (isHindi) "कैशियर" else "Cashiers"),
                    Pair("KITCHEN_STAFF", if (isHindi) "शेफ" else "Chefs"),
                    Pair("WAITER", if (isHindi) "वेटर" else "Waiters"),
                    Pair("MANAGER", if (isHindi) "मैनेजर" else "Managers")
                )

                items(roles) { (rKey, rLabel) ->
                    val isSelected = uiState.selectedRoleFilter == rKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedRoleFilter(rKey) },
                        label = {
                            Text(
                                text = rLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF10B981)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Virtualized Staff List (Rule 7: LazyColumn with stable keys)
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF10B981),
                        strokeWidth = 2.5.dp
                    )
                }
            } else if (uiState.filteredStaff.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = "No Staff",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHindi) "कोई कर्मचारी नहीं मिला" else "No Staff Members Found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "'+ कर्मचारी' बटन दबाकर नया कर्मचारी या राइडर जोड़ें" else "Tap '+ Add Staff' above to add employees and delivery riders.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredStaff, key = { it.id }) { staff ->
                        StaffCardItem(
                            staff = staff,
                            isHindi = isHindi,
                            onEdit = { viewModel.openEditStaff(staff) },
                            onDelete = { staffToDelete = staff },
                            onToggleDuty = {
                                staff.riderId?.let { rId ->
                                    viewModel.toggleRiderDuty(rId, staff.isOnline)
                                }
                            },
                            onOpenSettlement = { viewModel.openSettlementModal(staff) }
                        )
                    }
                }
            }
        }
    }

    // Modal 1: Add / Edit Staff Bottom Sheet
    if (uiState.isStaffDrawerOpen) {
        AddEditStaffBottomSheet(
            isHindi = isHindi,
            staffForEdit = uiState.staffForEdit,
            branches = uiState.branches,
            verifiedUser = uiState.verifiedStaffUser,
            isSearchingUser = uiState.isSearchingUser,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onSearchUsername = { viewModel.searchUsername(it) },
            onDismiss = { viewModel.closeStaffDrawer() },
            onSaveStaff = { name, phone, email, branchId, role, username, userId, vType, vNum ->
                viewModel.saveStaffMember(name, phone, email, branchId, role, username, userId, vType, vNum)
            }
        )
    }

    // Modal 2: Branch & Warehouse Management Bottom Sheet
    if (uiState.isBranchModalOpen) {
        BranchManagementBottomSheet(
            isHindi = isHindi,
            branches = uiState.branches,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeBranchModal() },
            onSaveBranch = { bName, addr, city, state, pin, lat, lng, isMain ->
                viewModel.saveBranch(bName, addr, city, state, pin, lat, lng, isMain)
            }
        )
    }

    // Modal 3: Rider COD Cash Settlement Bottom Sheet
    if (uiState.isSettlementModalOpen && uiState.riderForSettlement != null) {
        RiderCashSettlementBottomSheet(
            isHindi = isHindi,
            staff = uiState.riderForSettlement!!,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeSettlementModal() },
            onConfirmSettlement = { rId -> viewModel.settleRiderCash(rId) }
        )
    }

    // Delete Confirmation Dialog
    if (staffToDelete != null) {
        AlertDialog(
            onDismissRequest = { staffToDelete = null },
            title = {
                Text(
                    text = if (isHindi) "कर्मचारी हटाएं?" else "Remove Staff Member?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi) "क्या आप सचमुच '${staffToDelete?.name}' को हटाना चाहते हैं?"
                    else "Are you sure you want to remove '${staffToDelete?.name}' from your active staff list?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        staffToDelete?.id?.let { viewModel.deleteStaff(it) }
                        staffToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(if (isHindi) "हाँ, हटाएं" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { staffToDelete = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun StaffCardItem(
    staff: StaffMemberUiModel,
    isHindi: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleDuty: () -> Unit,
    onOpenSettlement: () -> Unit
) {
    val isRider = staff.role == "DELIVERY_RIDER"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Name, Role Badge, Assigned Branch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar initial circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = staff.name.firstOrNull()?.uppercase() ?: "S",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = staff.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!staff.staff.username.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Check,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "@${staff.staff.username}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF065F46)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Phone,
                                contentDescription = "Phone",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = staff.phone,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Role Designation Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (staff.role) {
                        "DELIVERY_RIDER" -> Color(0xFFDCFCE7)
                        "CASHIER" -> Color(0xFFDBEAFE)
                        "KITCHEN_STAFF" -> Color(0xFFFEF3C7)
                        "MANAGER" -> Color(0xFFF3E8FF)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (staff.role) {
                                "DELIVERY_RIDER" -> Lucide.Bike
                                "KITCHEN_STAFF" -> Lucide.ChefHat
                                "MANAGER", "ADMIN" -> Lucide.Shield
                                else -> Lucide.User
                            },
                            contentDescription = null,
                            tint = when (staff.role) {
                                "DELIVERY_RIDER" -> Color(0xFF166534)
                                "CASHIER" -> Color(0xFF1E40AF)
                                "KITCHEN_STAFF" -> Color(0xFFB45309)
                                "MANAGER" -> Color(0xFF6B21A8)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = when (staff.role) {
                                "DELIVERY_RIDER" -> if (isHindi) "डिलीवरी राइडर" else "Delivery Rider"
                                "CASHIER" -> if (isHindi) "कैशियर" else "Cashier"
                                "KITCHEN_STAFF" -> if (isHindi) "शेफ" else "Kitchen Chef"
                                "WAITER" -> if (isHindi) "वेटर" else "Waiter"
                                "MANAGER" -> if (isHindi) "मैनेजर" else "Manager"
                                "ADMIN" -> if (isHindi) "एडमिन" else "Admin"
                                else -> staff.role
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (staff.role) {
                                "DELIVERY_RIDER" -> Color(0xFF166534)
                                "CASHIER" -> Color(0xFF1E40AF)
                                "KITCHEN_STAFF" -> Color(0xFFB45309)
                                "MANAGER" -> Color(0xFF6B21A8)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Assigned Branch & Rider Fleet Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Branch Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Building2,
                        contentDescription = "Branch",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = staff.branchName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // If Rider: Duty status & vehicle
                if (isRider) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Vehicle registration badge
                        if (!staff.vehicleNumber.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${staff.vehicleType ?: "BIKE"}: ${staff.vehicleNumber}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Duty Toggle Chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (staff.isOnline) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onToggleDuty() }
                        ) {
                            Text(
                                text = if (staff.isOnline) "🟢 ON-DUTY" else "⚪ OFF-DUTY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (staff.isOnline) Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: COD Cash Settlement & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // COD Cash in Hand
                if (isRider && staff.unsettledCodCash > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "₹${String.format("%.2f", staff.unsettledCodCash)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFDC2626)
                        )

                        Button(
                            onClick = onOpenSettlement,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (isHindi) "जमा करें (Settle)" else "Settle",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Edit & Delete Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEdit() }
                    ) {
                        Box(
                            modifier = Modifier.padding(7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Pencil,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Delete
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDelete() }
                    ) {
                        Box(
                            modifier = Modifier.padding(7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Trash2,
                                contentDescription = "Delete",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
