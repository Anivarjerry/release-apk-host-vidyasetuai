package com.vidyasetuai.feature_store.presentation.screen.role_staff

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.data.local.StoreDatabase

/**
 * 👔 ROLE 2: Staff Workspace Operational Dashboard.
 * Dynamically tailored for Branch Managers, Cashiers, Chefs, Riders, and Stock Clerks.
 * High-performance, 0ms load, with zero administrative Re-Sync clutter.
 */
@Composable
fun StaffDashboardScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    staffRole: String = "CASHIER",
    staffName: String? = null,
    branchName: String? = null,
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToDeliveryOrders: () -> Unit = {},
    onNavigateToMarketplace: () -> Unit = {}
) {
    val context = LocalContext.current
    val storeDb = remember { StoreDatabase.getDatabase(context) }
    val businessFlow = remember { storeDb.businessDao().getAnyActiveBusinessFlow() }
    val businessState by businessFlow.collectAsState(initial = null)

    val normalizedRole = staffRole.uppercase().trim()

    // Permission flags for dynamic quick action cards
    val canAccessDeliveryOrders = normalizedRole in listOf("DELIVERY_RIDER", "RIDER", "DRIVER", "MANAGER", "BRANCH_MANAGER")
    val canAccessPos = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "CASHIER", "WAITER", "SERVER")
    val canAccessKds = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "KITCHEN_STAFF", "CHEF", "COOK", "WAITER", "SERVER")
    val canAccessCatalog = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER", "KITCHEN_STAFF", "CHEF")
    val canAccessInventory = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER")
    val canAccessKhata = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "CASHIER")
    val canAccessInvoices = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "CASHIER")
    val canAccessPurchases = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER", "INVENTORY_CLERK", "CLERK")
    val canAccessReports = normalizedRole in listOf("MANAGER", "BRANCH_MANAGER")

    // Role display badges
    val roleBadgeTitle = when (normalizedRole) {
        "MANAGER", "BRANCH_MANAGER" -> if (isHindi) "शाखा प्रबंधक (Manager)" else "Branch Manager"
        "CASHIER" -> if (isHindi) "कैशियर (Billing Counter)" else "Cashier Desk"
        "KITCHEN_STAFF", "CHEF", "COOK" -> if (isHindi) "रसोई शेफ (Kitchen Chef)" else "Kitchen Chef"
        "DELIVERY_RIDER", "RIDER", "DRIVER" -> if (isHindi) "डिलीवरी राइडर (Delivery Fleet)" else "Delivery Fleet"
        "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER" -> if (isHindi) "स्टॉक क्लर्क (Inventory)" else "Stock Clerk"
        else -> if (isHindi) "स्टाफ सदस्य" else "Staff Member"
    }

    val roleBadgeIcon = when (normalizedRole) {
        "MANAGER", "BRANCH_MANAGER" -> Lucide.UserCheck
        "CASHIER" -> Lucide.ShoppingCart
        "KITCHEN_STAFF", "CHEF", "COOK" -> Lucide.UtensilsCrossed
        "DELIVERY_RIDER", "RIDER", "DRIVER" -> Lucide.PackageCheck
        "INVENTORY_CLERK", "CLERK", "STOCK_MANAGER" -> Lucide.Boxes
        else -> Lucide.User
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================================
        // 📌 1. TOP STAFF PROFILE & OUTLET HERO BANNER
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.EmeraldGreen.copy(alpha = 0.10f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = businessState?.tradeName ?: if (isHindi) "स्टोर स्टाफ वर्कस्पेस" else "Staff Workspace",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = staffName?.let { "👤 $it" } ?: (if (isHindi) "स्टोर कर्मचारी" else "Store Staff"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Live Status Pill Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AppColors.EmeraldGreen
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = if (isHindi) "सक्रिय" else "LIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Divider(color = AppColors.EmeraldGreen.copy(alpha = 0.2f))

                // Bottom Meta: Role Pill Badge & Branch Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AppColors.EmeraldGreen.copy(alpha = 0.18f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = roleBadgeIcon,
                                contentDescription = "Role",
                                tint = AppColors.EmeraldGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = roleBadgeTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                        }
                    }

                    Text(
                        text = "📍 ${branchName ?: if (isHindi) "मुख्य शाखा" else "Main Branch"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // =========================================================================
        // 📌 2. QUICK OPERATIONAL ACTIONS GRID
        // =========================================================================
        Text(
            text = if (isHindi) "आपकी सेवाएं (Quick Actions)" else "Operational Actions",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Special Priority 1: Delivery Orders & OTP (For Riders / Drivers)
        if (normalizedRole in listOf("DELIVERY_RIDER", "RIDER", "DRIVER")) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clickable { onNavigateToDeliveryOrders() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.PackageCheck,
                                contentDescription = "Deliveries",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "डिलीवरी ऑर्डर्स व OTP" else "Active Delivery Orders & OTP",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHindi) "लाइव ऑर्डर्स देखें और OTP वेरिफाई करें" else "View assigned orders & verify delivery OTP",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "Go",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Special Priority 2: Live Kitchen KDS (For Kitchen Chefs / Cooks)
        if (normalizedRole in listOf("KITCHEN_STAFF", "CHEF", "COOK")) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clickable { onNavigateToKds() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.EmeraldGreen.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.EmeraldGreen,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Lucide.UtensilsCrossed,
                                contentDescription = "KDS",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "लाइव KDS किचन स्क्रीन" else "Live Kitchen KDS Screen",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHindi) "आने वाले ऑर्डर्स तैयार करें और मार्क करें" else "Live order tickets & preparation timer",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "Go",
                        tint = AppColors.EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Standard 2-Column Action Cards Grid
        if (canAccessPos || canAccessCatalog) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canAccessPos) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "POS काउंटर बिलिंग" else "POS Counter",
                        subtitle = if (isHindi) "नया बिल बनाएं" else "Fast Billing Desk",
                        icon = Lucide.ShoppingCart,
                        onClick = onNavigateToPos
                    )
                }
                if (canAccessCatalog) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "उत्पाद व कैटलॉग" else "Items & Menu",
                        subtitle = if (isHindi) "कीमत व मेनू देखें" else "Menu & Pricing",
                        icon = Lucide.Package,
                        onClick = onNavigateToCatalog
                    )
                }
            }
        }

        if (canAccessInvoices || canAccessKhata) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canAccessInvoices) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "बिक्री इनवॉइस व बिल" else "Sales Bills",
                        subtitle = if (isHindi) "कटे हुए बिल देखें" else "Invoice History",
                        icon = Lucide.FileText,
                        onClick = onNavigateToInvoices
                    )
                }
                if (canAccessKhata) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "ग्राहक बही-खाता" else "Customer Khata",
                        subtitle = if (isHindi) "उधारी व भुगतान" else "Ledger & Collect",
                        icon = Lucide.BookOpen,
                        onClick = onNavigateToParties
                    )
                }
            }
        }

        if (canAccessInventory || canAccessPurchases) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canAccessInventory) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "इन्वेंटरी व स्टॉक" else "Inventory Stock",
                        subtitle = if (isHindi) "स्टॉक बैलेंस देखें" else "Real-Time Stock",
                        icon = Lucide.Boxes,
                        onClick = onNavigateToInventory
                    )
                }
                if (canAccessPurchases) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "सप्लायर खरीद बिल" else "Purchase Bills",
                        subtitle = if (isHindi) "माल इनवर्ड करें" else "Stock Inward",
                        icon = Lucide.ShoppingBag,
                        onClick = onNavigateToPurchases
                    )
                }
            }
        }

        if (canAccessReports || canAccessKds) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canAccessReports) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "व्यापार रिपोर्ट व लाभ" else "Branch Reports",
                        subtitle = if (isHindi) "डेबुक व P&L" else "Daybook & Analytics",
                        icon = Lucide.FileText,
                        onClick = onNavigateToReports
                    )
                }
                if (canAccessKds && normalizedRole !in listOf("KITCHEN_STAFF", "CHEF", "COOK")) {
                    StaffActionCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "KDS किचन स्क्रीन" else "Kitchen KDS",
                        subtitle = if (isHindi) "लाइव ऑर्डर्स" else "Live Kitchen Display",
                        icon = Lucide.UtensilsCrossed,
                        onClick = onNavigateToKds
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(108.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AppColors.EmeraldGreen,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
