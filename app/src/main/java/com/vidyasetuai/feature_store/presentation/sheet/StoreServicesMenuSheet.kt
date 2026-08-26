package com.vidyasetuai.feature_store.presentation.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.presentation.navigation.StoreRole

/**
 * 100% Autonomous 3-Dots Store Services & Actions Bottom Sheet.
 * Encapsulates POS, KDS, Catalog, Inventory, Invoices, Purchases, Expenses, Reports, Parties, and Settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreServicesMenuSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    activeRole: StoreRole = StoreRole.BUSINESS_OWNER,
    staffRole: String? = null,
    isHindi: Boolean = false,
    onNavigateToPos: () -> Unit = {},
    onNavigateToKds: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToActiveOrders: () -> Unit = {},
    onNavigateToPastOrders: () -> Unit = {}
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentStaffRole = when (activeRole) {
        StoreRole.PUBLIC_CUSTOMER -> "PUBLIC_CUSTOMER"
        StoreRole.BUSINESS_OWNER -> "OWNER"
        StoreRole.STORE_STAFF -> staffRole?.uppercase() ?: "CASHIER"
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
    val canAccessStaff = currentStaffRole in listOf("OWNER", "MANAGER", "BRANCH_MANAGER")
    val canAccessSettings = currentStaffRole == "OWNER"

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
                        text = if (isHindi) "स्टोर सेवाएं व टूल्स" else "Store Services & Tools",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "सभी टूल्स और प्रबंधन विकल्पों तक त्वरित पहुंच" else "Quick access to all store operations",
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

            // Services List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (activeRole == StoreRole.PUBLIC_CUSTOMER) {
                    // Customer Options
                    StoreServiceItemRow(
                        icon = Lucide.PackageCheck,
                        title = if (isHindi) "लाइव ऑर्डर व डिलीवरी OTP" else "Active Orders & OTP",
                        subtitle = if (isHindi) "अपने चल रहे ऑर्डर्स ट्रैक करें" else "Track your running orders and delivery OTP",
                        onClick = {
                            onDismiss()
                            onNavigateToActiveOrders()
                        }
                    )
                    StoreServiceItemRow(
                        icon = Lucide.History,
                        title = if (isHindi) "ऑर्डर हिस्ट्री (Past Orders)" else "Past Orders History",
                        subtitle = if (isHindi) "अपने पुराने ऑर्डर्स और बिल देखें" else "View completed orders and bills",
                        onClick = {
                            onDismiss()
                            onNavigateToPastOrders()
                        }
                    )
                } else {
                    // Merchant / Staff Services
                    if (canAccessDeliveryOrders && currentStaffRole in listOf("DELIVERY_RIDER", "RIDER", "DRIVER")) {
                        StoreServiceItemRow(
                            icon = Lucide.PackageCheck,
                            title = if (isHindi) "डिलीवरी ऑर्डर्स व OTP" else "Active Delivery Orders & OTP",
                            subtitle = if (isHindi) "आवंटित डिलीवरी ऑर्डर्स और OTP सत्यापन" else "Assigned orders and customer OTP verification",
                            isPrimary = true,
                            onClick = {
                                onDismiss()
                                onNavigateToActiveOrders()
                            }
                        )
                    }

                    if (canAccessPos) {
                        StoreServiceItemRow(
                            icon = Lucide.ShoppingCart,
                            title = if (isHindi) "पीओएस बिलिंग काउंटर" else "POS Billing Counter",
                            subtitle = if (isHindi) "फास्ट काउंटर बिलिंग, बारकोड व रसीद" else "Fast checkout, barcode scan & thermal receipts",
                            isPrimary = true,
                            onClick = {
                                onDismiss()
                                onNavigateToPos()
                            }
                        )
                    }

                    if (canAccessKds) {
                        StoreServiceItemRow(
                            icon = Lucide.UtensilsCrossed,
                            title = if (isHindi) "रसोई व KDS स्क्रीन" else "Kitchen & KDS Screen",
                            subtitle = if (isHindi) "लाइव किचन ऑर्डर कतार व कुकिंग स्टेटस" else "Live kitchen queue & food prep status",
                            onClick = {
                                onDismiss()
                                onNavigateToKds()
                            }
                        )
                    }

                    if (canAccessCatalog) {
                        StoreServiceItemRow(
                            icon = Lucide.Package,
                            title = if (isHindi) "उत्पाद व मेनू कैटलॉग" else "Products & Menu Catalog",
                            subtitle = if (isHindi) "आइटम लिस्टिंग, GST दरें व वेरिएंट्स" else "Manage items, prices, GST slabs & variants",
                            onClick = {
                                onDismiss()
                                onNavigateToCatalog()
                            }
                        )
                    }

                    if (canAccessInventory) {
                        StoreServiceItemRow(
                            icon = Lucide.Boxes,
                            title = if (isHindi) "इन्वेंट्री व स्टॉक नियंत्रण" else "Inventory & Stock Control",
                            subtitle = if (isHindi) "स्टॉक मात्रा, कम स्टॉक अलर्ट व समायोजन" else "Real-time stock, low stock alerts & adjustments",
                            onClick = {
                                onDismiss()
                                onNavigateToInventory()
                            }
                        )
                    }

                    if (canAccessInvoices) {
                        StoreServiceItemRow(
                            icon = Lucide.FileText,
                            title = if (isHindi) "बिक्री इनवॉइस व बिल" else "Sales Invoices & Bills",
                            subtitle = if (isHindi) "टैक्स इनवॉइस, प्रिंट व रद्दीकरण रिकॉर्ड" else "Tax invoices, payment status & bill history",
                            onClick = {
                                onDismiss()
                                onNavigateToInvoices()
                            }
                        )
                    }

                    if (canAccessPurchases) {
                        StoreServiceItemRow(
                            icon = Lucide.ShoppingBag,
                            title = if (isHindi) "खरीद बिल व प्रविष्टियां" else "Purchase Bills & Inward",
                            subtitle = if (isHindi) "सप्लायर बिल व स्टॉक इनवर्ड रिकॉर्ड्स" else "Vendor procurement bills & inward stock",
                            onClick = {
                                onDismiss()
                                onNavigateToPurchases()
                            }
                        )
                    }

                    if (canAccessExpenses) {
                        StoreServiceItemRow(
                            icon = Lucide.Receipt,
                            title = if (isHindi) "दुकान खर्च (Expenses)" else "Store Expenses & Petty Cash",
                            subtitle = if (isHindi) "दैनिक खर्चे व पेटी कैश प्रविष्टियां" else "Daily operational expenses & petty cash logs",
                            onClick = {
                                onDismiss()
                                onNavigateToExpenses()
                            }
                        )
                    }

                    if (canAccessKhata) {
                        StoreServiceItemRow(
                            icon = Lucide.Users,
                            title = if (isHindi) "पार्टियां व ग्राहक खाता" else "Parties & Customer Khata",
                            subtitle = if (isHindi) "उधार खाता, लेन-देन लेजर व शेष राशि" else "Customer ledger, credit limits & party balances",
                            onClick = {
                                onDismiss()
                                onNavigateToParties()
                            }
                        )
                    }

                    if (canAccessReports) {
                        StoreServiceItemRow(
                            icon = Lucide.FileSpreadsheet,
                            title = if (isHindi) "व्यापार रिपोर्ट व P&L" else "Business Reports & P&L",
                            subtitle = if (isHindi) "बिक्री ट्रेंड, लाभ-हानि व टैक्स रिपोर्ट" else "Sales analytics, day book & tax summaries",
                            onClick = {
                                onDismiss()
                                onNavigateToReports()
                            }
                        )
                    }

                    if (canAccessStaff) {
                        StoreServiceItemRow(
                            icon = Lucide.UserCheck,
                            title = if (isHindi) "स्टाफ सदस्य व वेतन" else "Staff Members & Payroll",
                            subtitle = if (isHindi) "कर्मचारी अनुमतियां, शाखाएं व वेतन" else "Staff permissions, attendance & salary ledger",
                            onClick = {
                                onDismiss()
                                onNavigateToStaff()
                            }
                        )
                    }

                    if (canAccessSettings) {
                        StoreServiceItemRow(
                            icon = Lucide.Settings,
                            title = if (isHindi) "स्टोर सेटिंग्स व टैक्स" else "Store Settings & Tax Config",
                            subtitle = if (isHindi) "व्यापार प्रोफाइल, थर्मल प्रिंटर व GST" else "Store profile, thermal printer setup & GST details",
                            onClick = {
                                onDismiss()
                                onNavigateToSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreServiceItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isPrimary) AppColors.EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPrimary) 1.5.dp else 1.dp,
            color = if (isPrimary) AppColors.EmeraldGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPrimary) AppColors.EmeraldGreen.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isPrimary) AppColors.EmeraldGreen else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
