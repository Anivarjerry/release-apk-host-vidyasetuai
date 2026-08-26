package com.vidyasetuai.feature_store.presentation.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.presentation.drawer.StoreWorkspaceItem
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole

/**
 * 100% Autonomous Apple-Style Workspace Switcher Bottom Sheet.
 * Reads Room Database Flows without modifying any database queries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreWorkspaceSwitcherSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    isHindi: Boolean = false,
    selectedRoleOverride: StoreRole? = null,
    selectedStaffRoleOverride: String? = null,
    onSelectRole: (StoreRole, String?) -> Unit = { _, _ -> },
    onRegisterNewBusiness: () -> Unit = {}
) {
    if (!visible) return

    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- Live Room Database Observers (Zero query change) ---
    val activeBusinessState by storeDb.businessDao().getAnyActiveBusinessFlow().collectAsState(initial = null)
    val businessId = activeBusinessState?.id ?: ""
    val staffList by storeDb.businessStaffDao().getStaffFlow(businessId).collectAsState(initial = emptyList())

    val workspaceList = remember(activeBusinessState, staffList, selectedRoleOverride, selectedStaffRoleOverride) {
        val list = mutableListOf<StoreWorkspaceItem>()

        if (activeBusinessState != null) {
            val tradeName = activeBusinessState?.tradeName ?: "Store"

            // 1. Master Store Owner Role Profile
            val isOwnerSelected = selectedRoleOverride == StoreRole.BUSINESS_OWNER || 
                (selectedRoleOverride == null && activeBusinessState != null)
            list.add(
                StoreWorkspaceItem(
                    id = "owner_${activeBusinessState?.id}",
                    businessName = tradeName,
                    roleName = if (isHindi) "स्टोर मालिक (Store Owner)" else "STORE OWNER",
                    roleEnum = StoreRole.BUSINESS_OWNER,
                    staffRole = "OWNER",
                    branchName = if (isHindi) "सभी शाखाएं (All Branches)" else "All Branches",
                    isSelected = isOwnerSelected
                )
            )

            // 2. Store Staff Member Role Profiles
            staffList.forEach { staff ->
                val isStaffSelected = selectedRoleOverride == StoreRole.STORE_STAFF && 
                    selectedStaffRoleOverride == staff.role.uppercase()
                list.add(
                    StoreWorkspaceItem(
                        id = "staff_${staff.id}",
                        businessName = tradeName,
                        roleName = "STAFF (${staff.role.uppercase()})",
                        roleEnum = StoreRole.STORE_STAFF,
                        staffRole = staff.role.uppercase(),
                        staffBranchId = staff.branchId,
                        branchName = staff.name,
                        isSelected = isStaffSelected
                    )
                )
            }
        }

        // 3. Fallback / Switchable Public Shopper Role Profile
        val isShopperSelected = selectedRoleOverride == StoreRole.PUBLIC_CUSTOMER || 
            (selectedRoleOverride == null && activeBusinessState == null)
        list.add(
            StoreWorkspaceItem(
                id = "public_shopper",
                businessName = if (isHindi) "विद्यासेतु स्टोर मार्केटप्लेस" else "VidyaSetu Store Marketplace",
                roleName = if (isHindi) "ग्राहक (Public Shopper)" else "PUBLIC SHOPPER",
                roleEnum = StoreRole.PUBLIC_CUSTOMER,
                staffRole = "PUBLIC_CUSTOMER",
                isSelected = isShopperSelected
            )
        )
        list
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isHindi) "स्टोर / वर्कस्पेस चुनें" else "Switch Workspace",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "अपना सक्रिय स्टोर या रोल चुनें" else "Select your active store or staff profile",
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Workspaces List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                workspaceList.forEach { item ->
                    val isSelected = item.isSelected
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectRole(item.roleEnum, item.staffRole)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Avatar Icon
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) AppColors.EmeraldGreen.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.roleEnum) {
                                        StoreRole.BUSINESS_OWNER -> Lucide.Store
                                        StoreRole.STORE_STAFF -> Lucide.Users
                                        StoreRole.PUBLIC_CUSTOMER -> Lucide.ShoppingBag
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.businessName,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.roleName}${if (item.branchName != null) " • ${item.branchName}" else ""}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.EmeraldGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action: Register New Business / Branch
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRegisterNewBusiness()
                            onDismiss()
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = AppColors.EmeraldGreen.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = null,
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "+ नया स्टोर या शाखा जोड़ें" else "+ Register New Store / Branch",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}
