package com.vidyasetuai.feature_store.presentation.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.R
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole

data class StoreWorkspaceItem(
    val id: String,
    val businessName: String,
    val roleName: String,
    val roleEnum: StoreRole,
    val staffRole: String? = null,
    val staffBranchId: String? = null,
    val branchName: String? = null,
    val isSelected: Boolean = false
)

@Composable
fun StoreDrawerContent(
    isHindi: Boolean = false,
    onOpenSettings: () -> Unit = {},
    onOpenBranches: () -> Unit = {},
    onOpenStaff: () -> Unit = {},
    onOpenSyncCenter: () -> Unit = {},
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToActiveOrders: () -> Unit = {},
    onNavigateToPastOrders: () -> Unit = {},
    onSelectRole: (StoreRole, String?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }
    var isWorkspaceExpanded by remember { mutableStateOf(false) }

    // --- Live Room Database Observers ---
    val activeBusinessState by storeDb.businessDao().getAnyActiveBusinessFlow().collectAsState(initial = null)
    val businessId = activeBusinessState?.id ?: ""

    val staffList by storeDb.businessStaffDao().getStaffFlow(businessId).collectAsState(initial = emptyList())

    // --- Dynamic 3-Role Workspace Items Construction ---
    val workspaceList = remember(activeBusinessState, staffList) {
        val list = mutableListOf<StoreWorkspaceItem>()

        if (activeBusinessState != null) {
            val tradeName = activeBusinessState?.tradeName ?: "Store"

            // 1. Master Store Owner Role Profile
            list.add(
                StoreWorkspaceItem(
                    id = "owner_${activeBusinessState?.id}",
                    businessName = tradeName,
                    roleName = "STORE OWNER",
                    roleEnum = StoreRole.BUSINESS_OWNER,
                    staffRole = "OWNER",
                    branchName = "All Branches",
                    isSelected = true
                )
            )

            // 2. Store Staff Member Role Profiles (from business_staff_members table)
            staffList.forEach { staff ->
                list.add(
                    StoreWorkspaceItem(
                        id = "staff_${staff.id}",
                        businessName = tradeName,
                        roleName = "STAFF (${staff.role.uppercase()})",
                        roleEnum = StoreRole.STORE_STAFF,
                        staffRole = staff.role.uppercase(),
                        staffBranchId = staff.branchId,
                        branchName = staff.name,
                        isSelected = false
                    )
                )
            }
        }

        // 3. Fallback / Switchable Public Shopper Role Profile
        list.add(
            StoreWorkspaceItem(
                id = "public_shopper",
                businessName = if (isHindi) "विद्यासेतु स्टोर मार्केटप्लेस" else "VidyaSetu Store Marketplace",
                roleName = if (isHindi) "ग्राहक" else "PUBLIC SHOPPER",
                roleEnum = StoreRole.PUBLIC_CUSTOMER,
                staffRole = "PUBLIC_CUSTOMER",
                isSelected = (activeBusinessState == null)
            )
        )
        list
    }

    var selectedWorkspaceId by remember(workspaceList) {
        mutableStateOf(workspaceList.firstOrNull { it.isSelected }?.id ?: "")
    }

    val selectedWorkspace = remember(selectedWorkspaceId, workspaceList) {
        workspaceList.firstOrNull { it.id == selectedWorkspaceId } ?: workspaceList.firstOrNull()
    }

    val activeWorkspaceName = selectedWorkspace?.businessName ?: (if (isHindi) "मार्केटप्लेस मोड" else "Public Store Mode")
    val activeRoleTitle = selectedWorkspace?.roleName ?: (if (isHindi) "ग्राहक" else "PUBLIC SHOPPER")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // =========================================================================
        // 📌 1. FIXED TOP HEADER: Brand Logo & Profile Accordion Card (Non-Scrollable)
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Brand Logo & Title Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = AppColors.EmeraldGreen.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bridge_logo),
                            contentDescription = "Logo",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "VidyaSetu Commerce",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isHindi) "स्मार्ट व्यापार मंच" else "Smart Merchant & POS Platform",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Active Workspace Accordion Card (Fixed Top)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.EmeraldGreen.copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = AppColors.EmeraldGreen.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    // Header Row (Click to toggle accordion)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWorkspaceExpanded = !isWorkspaceExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "सक्रिय वर्कस्पेस" else "Active Workspace",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeWorkspaceName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = activeRoleTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (isWorkspaceExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                            contentDescription = "Toggle Workspaces",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Accordion Dropdown List of Switchable Workspaces
                    AnimatedVisibility(visible = isWorkspaceExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "वर्कस्पेस बदलें (Switch Workspace)" else "Switch Workspace",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            workspaceList.forEach { item ->
                                val isItemSelected = (item.id == selectedWorkspaceId)
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedWorkspaceId = item.id
                                            isWorkspaceExpanded = false
                                            onSelectRole(item.roleEnum, item.staffRole)
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isItemSelected) AppColors.EmeraldGreen.copy(alpha = 0.18f) else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = item.businessName,
                                                fontSize = 13.sp,
                                                fontWeight = if (isItemSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isItemSelected) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${item.roleName}${if (item.branchName != null) " • ${item.branchName}" else ""}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (isItemSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(AppColors.EmeraldGreen)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        // =========================================================================
        // 📜 2. MIDDLE SCROLLABLE MODULES LIST (Scrolls smoothly as modules grow)
        // =========================================================================
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedWorkspace?.roleEnum == StoreRole.PUBLIC_CUSTOMER) {
                // =========================================================================
                // 🛒 PUBLIC CUSTOMER SERVICES LIST (Strictly 2 clean customer items)
                // =========================================================================
                Text(
                    text = if (isHindi) "मेरे ऑर्डर्स (My Orders)" else "Customer Orders",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )

                // Customer Item 1: Live Active Orders & OTP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToActiveOrders() }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Lucide.PackageCheck,
                        contentDescription = "Live Order OTP",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isHindi) "लाइव ऑर्डर व OTP" else "Active Orders & Delivery OTP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Customer Item 2: Order History
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPastOrders() }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Lucide.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isHindi) "पुराने ऑर्डर्स (Order History)" else "Past Order History",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                val currentStaffRole = when {
                    selectedWorkspace?.roleEnum == StoreRole.PUBLIC_CUSTOMER -> "PUBLIC_CUSTOMER"
                    selectedWorkspace?.roleEnum == StoreRole.BUSINESS_OWNER -> "OWNER"
                    else -> selectedWorkspace?.staffRole?.uppercase() ?: "CASHIER"
                }

                val canAccessDeliveryOrders = currentStaffRole in listOf("DELIVERY_RIDER", "RIDER", "DRIVER", "OWNER", "MANAGER", "BRANCH_MANAGER")
                val canAccessPos = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "CASHIER", "WAITER", "SERVER")
                val canAccessKds = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "KITCHEN_STAFF", "CHEF", "COOK", "WAITER", "SERVER")
                val canAccessCatalog = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER")
                val canAccessInventory = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER")
                val canAccessKhata = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "CASHIER")
                val canAccessInvoices = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "CASHIER")
                val canAccessPurchases = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK")
                val canAccessExpenses = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER", "CASHIER")
                val canAccessReports = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER")
                val canAccessBranches = currentStaffRole == "OWNER"
                val canAccessStaff = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER")
                val canAccessSettings = currentStaffRole == "OWNER"

                // =========================================================================
                // 🏪 MERCHANT / STAFF SERVICES LIST
                // =========================================================================
                Text(
                    text = if (isHindi) "स्टोर सेवाएं (Store Services)" else "Store Services",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.EmeraldGreen
                )

                // Service 0 (Special for Delivery Rider): Active Delivery Orders & OTP
                if (canAccessDeliveryOrders && currentStaffRole in listOf("DELIVERY_RIDER", "RIDER", "DRIVER")) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToActiveOrders() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.PackageCheck,
                            contentDescription = "Delivery Orders",
                            tint = AppColors.EmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "डिलीवरी ऑर्डर्स व OTP" else "Active Delivery Orders & OTP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldGreen
                        )
                    }
                }

                // Service 1: POS Counter
                if (canAccessPos) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPos() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ShoppingCart,
                            contentDescription = "POS",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "पीओएस बिलिंग काउंटर" else "POS Billing Counter",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 2: Live KDS Kitchen
                if (canAccessKds) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToKds() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.UtensilsCrossed,
                            contentDescription = "KDS",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "रसोई व KDS स्क्रीन" else "Kitchen & KDS Screen",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 3: Product Catalog & Menu
                if (canAccessCatalog) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToCatalog() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Package,
                            contentDescription = "Catalog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "उत्पाद व मेनू कैटलॉग" else "Products & Menu Catalog",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 4: Inventory & Stock Control
                if (canAccessInventory) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToInventory() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Boxes,
                            contentDescription = "Inventory",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "इन्वेंटरी व स्टॉक कंट्रोल" else "Inventory & Stock Control",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 5: Customer Khata & Parties
                if (canAccessKhata) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToParties() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = "Khata",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "पार्टियां व ग्राहक खाता" else "Parties & Customer Khata",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 6: Invoices & Bills History
                if (canAccessInvoices) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToInvoices() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.FileText,
                            contentDescription = "Invoices",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "बिक्री इनवॉइस व बिल" else "Sales Invoices & Bills",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 7: Purchase Bills
                if (canAccessPurchases) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPurchases() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ShoppingBag,
                            contentDescription = "Purchases",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "सप्लायर खरीद बिल" else "Purchase Bills",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 7B: Store Expenses & Petty Cash
                if (canAccessExpenses) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToExpenses() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Receipt,
                            contentDescription = "Expenses",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "दुकान खर्चे व पेट्टी कैश" else "Expenses & Petty Cash",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 8: Business Reports & Analytics
                if (canAccessReports) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToReports() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.FileText,
                            contentDescription = "Analytics",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "व्यापार रिपोर्ट व लाभ" else "Business Reports & P&L",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 9: Branches Management (Owner Only)
                if (canAccessBranches) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenBranches() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.GitBranch,
                            contentDescription = "Branches",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "शाखाएं व आउटलेट्स" else "Branches & Outlets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 10: Staff & Roles (Owner / Manager)
                if (canAccessStaff) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenStaff() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.UserCheck,
                            contentDescription = "Staff",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "स्टाफ व अनुमतियां" else "Staff Members & Permissions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Service 11: Store & Billing Settings (Owner Only)
                if (canAccessSettings) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenSettings() }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isHindi) "सेटिंग्स व टैक्स कॉन्फिग" else "Settings & Tax Config",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
