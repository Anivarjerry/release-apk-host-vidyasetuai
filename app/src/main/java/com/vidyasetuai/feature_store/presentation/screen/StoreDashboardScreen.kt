package com.vidyasetuai.feature_store.presentation.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.sync.StoreWorkspaceSyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class DashboardTrendDay(
    val date: String,
    val dayLabel: String,
    val amount: Double
)

data class DashboardLowStockItem(
    val id: String,
    val name: String,
    val currentStock: Double,
    val threshold: Double,
    val unit: String
)

data class DashboardDebtor(
    val id: String,
    val name: String,
    val phone: String?,
    val balance: Double
)

/**
 * Flagship Apple Minimalist Flat HIG Store Owner Dashboard.
 * 100% Unified with Profile Tab Aesthetics:
 * - Canvas Background: Pure White (#FFFFFF) in Light Mode / #0B1120 in Dark Mode.
 * - Cards: Soft Off-White/Light Gray (#F8FAFC) with #E2E8F0 Hairline Borders & 18dp Squircles.
 * - Monochromatic Quiet Luxury Action Dock (Slate / White / Subtle Emerald).
 * - Direct Settings Entry & 0ms Reactive Room Flow State Preservation.
 */
@Composable
fun StoreDashboardScreen(
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onNavigateToPos: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToInvoices: () -> Unit = {},
    onNavigateToParties: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var isSyncing by rememberSaveable { mutableStateOf(false) }
    var syncMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isSuccess by rememberSaveable { mutableStateOf(true) }

    val storeDb = remember { StoreDatabase.getDatabase(context) }
    val businessFlow = remember { storeDb.businessDao().getAnyActiveBusinessFlow() }
    val businessState by businessFlow.collectAsState(initial = null)

    // 0ms Reactive Room Flow Aggregations
    val invoicesFlow = remember { storeDb.invoiceDao().getAllInvoicesFlow() }
    val partiesFlow = remember { storeDb.partyDao().getAllPartiesFlow() }
    val itemsFlow = remember { storeDb.itemDao().getAllItemsFlow() }
    val stockFlow = remember { storeDb.inventoryStockDao().getAllStocksFlow() }

    val invoicesList by invoicesFlow.collectAsState(initial = emptyList())
    val partiesList by partiesFlow.collectAsState(initial = emptyList())
    val itemsList by itemsFlow.collectAsState(initial = emptyList())
    val stockList by stockFlow.collectAsState(initial = emptyList())

    // Compute Metrics with Local IST Date Invariant
    val todayStr = remember { LocalDate.now().toString() }
    val currentMonthPrefix = remember { todayStr.substring(0, 7) }

    // 1. Today Sales & Month Invoices
    val todaySales = remember(invoicesList) {
        invoicesList
            .filter { it.isDeleted == false && it.paymentStatus != "CANCELLED" && (it.invoiceDate == todayStr || it.createdAt.startsWith(todayStr)) }
            .sumOf { it.grandTotal }
    }

    val todayOrdersCount = remember(invoicesList) {
        invoicesList.count { it.isDeleted == false && it.paymentStatus != "CANCELLED" && (it.invoiceDate == todayStr || it.createdAt.startsWith(todayStr)) }
    }

    val monthInvoicesCount = remember(invoicesList) {
        invoicesList.count { it.isDeleted == false && (it.invoiceDate.startsWith(currentMonthPrefix) || it.createdAt.startsWith(currentMonthPrefix)) }
    }

    // 2. Customer Udhar & Top Debtors
    val totalCustomerUdhar = remember(partiesList) {
        partiesList.filter { it.isDeleted == false && it.currentBalance > 0 }.sumOf { it.currentBalance }
    }

    val topDebtors = remember(partiesList) {
        partiesList
            .filter { it.isDeleted == false && it.currentBalance > 0 }
            .sortedByDescending { it.currentBalance }
            .take(3)
            .map { DashboardDebtor(it.id, it.name, it.phone, it.currentBalance) }
    }

    // 3. Stock Map & Low Stock Items
    val lowStockItems = remember(itemsList, stockList) {
        val itemMap = itemsList.associateBy { it.id }
        stockList
            .filter { it.isDeleted == false && it.isActive && it.currentStock <= it.lowStockThreshold }
            .mapNotNull { stock ->
                val item = itemMap[stock.itemId] ?: return@mapNotNull null
                DashboardLowStockItem(
                    id = item.id,
                    name = item.name,
                    currentStock = stock.currentStock,
                    threshold = stock.lowStockThreshold,
                    unit = item.unit
                )
            }
            .sortedBy { it.currentStock }
            .take(3)
    }

    // 4. 7-Day Trend Bar Chart Data
    val past7Days = remember {
        (6 downTo 0).map { offset ->
            val date = LocalDate.now().minusDays(offset.toLong())
            val label = if (offset == 0) "Today" else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            DashboardTrendDay(date.toString(), label, 0.0)
        }
    }

    val trendDays = remember(invoicesList, past7Days) {
        past7Days.map { day ->
            val totalForDay = invoicesList
                .filter { it.isDeleted == false && it.paymentStatus != "CANCELLED" && (it.invoiceDate == day.date || it.createdAt.startsWith(day.date)) }
                .sumOf { it.grandTotal }
            day.copy(amount = totalForDay)
        }
    }

    val maxTrendAmount = remember(trendDays) {
        trendDays.maxOfOrNull { it.amount }?.coerceAtLeast(100.0) ?: 100.0
    }

    // 100% Profile-Aligned Quiet Luxury Design Tokens
    val containerBg = if (isDark) Color(0xFF0B1120) else Color.White
    val cardSurface = if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF8FAFC)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
    val innerSurface = if (isDark) Color(0xFF1E293B) else Color.White
    val innerBorder = if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val emerald = Color(0xFF10B981)

    val syncRotation by animateFloatAsState(
        targetValue = if (isSyncing) 360f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "sync_spin"
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(containerBg),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Apple Hero Merchant Card (Profile Style + Settings Entry) ---
        item(key = "merchant_hero") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // Store Avatar Squircle
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDark) emerald.copy(alpha = 0.18f) else innerSurface,
                            border = BorderStroke(1.dp, if (isDark) emerald.copy(alpha = 0.35f) else innerBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val tradeName = businessState?.tradeName ?: "Store"
                                Text(
                                    text = tradeName.take(1).uppercase(),
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = emerald
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = businessState?.tradeName ?: if (isHindi) "स्टोर डैशबोर्ड" else "Store Commerce Hub",
                                    fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(emerald)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Owner: ${businessState?.ownerName ?: "Merchant"} • GST: ${businessState?.gstin ?: "N/A"}",
                                fontSize = 11.sp,
                                color = textSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Store Settings Action Button
                        Surface(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onNavigateToSettings() },
                            shape = RoundedCornerShape(18.dp),
                            color = innerSurface,
                            border = BorderStroke(1.dp, innerBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.Settings,
                                    contentDescription = "Store Settings",
                                    tint = textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Sleek Primary POS Button (Pill Capsule like Profile Edit/Share)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onNavigateToPos() },
                            shape = RoundedCornerShape(20.dp),
                            color = emerald,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ShoppingCart,
                                    contentDescription = "POS",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isHindi) "POS काउंटर" else "POS Counter",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. iOS 2x2 Glanceable Metric Cards (Profile Stats Style) ---
        item(key = "kpi_metrics_grid") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // KPI 1: Today's Sales
                    ProfileStyleKpiCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "आज की बिक्री" else "Today's Sales",
                        value = "₹${String.format(Locale.ENGLISH, "%.2f", todaySales)}",
                        subtitle = "$todayOrdersCount ${if (todayOrdersCount == 1) "order" else "orders"}",
                        icon = Lucide.TrendingUp,
                        tintColor = emerald,
                        cardSurface = cardSurface,
                        cardBorder = cardBorder,
                        innerSurface = innerSurface,
                        innerBorder = innerBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClick = onNavigateToInvoices
                    )

                    // KPI 2: Customer Udhar (Khata)
                    ProfileStyleKpiCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "ग्राहक खाता उधारी" else "Customer Udhar",
                        value = "₹${String.format(Locale.ENGLISH, "%.2f", totalCustomerUdhar)}",
                        subtitle = "${topDebtors.size} pending accounts",
                        icon = Lucide.Users,
                        tintColor = Color(0xFFE11D48),
                        cardSurface = cardSurface,
                        cardBorder = cardBorder,
                        innerSurface = innerSurface,
                        innerBorder = innerBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClick = onNavigateToParties
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // KPI 3: Low Stock Alerts
                    ProfileStyleKpiCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "कम स्टॉक अलर्ट" else "Low Stock Alerts",
                        value = "${lowStockItems.size}",
                        subtitle = if (lowStockItems.isNotEmpty()) "Items to reorder" else "All in good stock",
                        icon = Lucide.TriangleAlert,
                        tintColor = if (lowStockItems.isNotEmpty()) Color(0xFFD97706) else emerald,
                        cardSurface = cardSurface,
                        cardBorder = cardBorder,
                        innerSurface = innerSurface,
                        innerBorder = innerBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClick = onNavigateToInventory
                    )

                    // KPI 4: Month Invoices
                    ProfileStyleKpiCard(
                        modifier = Modifier.weight(1f),
                        title = if (isHindi) "महीने के इनवॉइस" else "Month Invoices",
                        value = "$monthInvoicesCount",
                        subtitle = "Generated this month",
                        icon = Lucide.ReceiptText,
                        tintColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155),
                        cardSurface = cardSurface,
                        cardBorder = cardBorder,
                        innerSurface = innerSurface,
                        innerBorder = innerBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onClick = onNavigateToInvoices
                    )
                }
            }
        }

        // --- 3. Apple iOS Monochromatic Control Center Action Dock (2x4 Grid) ---
        item(key = "quick_actions_dock") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "क्विक टूल्स व मैनेजमेंट" else "Quick Actions Dock",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "8 Tools",
                            fontSize = 11.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Row 1: POS, Expense, Khata, Purchases (Quiet Luxury Monochromatic Icons)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileDockItem(
                            label = if (isHindi) "+ POS सेल" else "+ POS Sale",
                            icon = Lucide.ShoppingCart,
                            tint = emerald,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToPos,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "+ खर्च" else "+ Expense",
                            icon = Lucide.Receipt,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToExpenses,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "+ खाता" else "+ Khata",
                            icon = Lucide.Users,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToParties,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "+ खरीद" else "+ Purchase",
                            icon = Lucide.Package,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToPurchases,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Catalog, Stock, Fleet, Reports
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileDockItem(
                            label = if (isHindi) "कैटलॉग" else "Catalog",
                            icon = Lucide.Layers,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToCatalog,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "स्टॉक" else "Inventory",
                            icon = Lucide.Boxes,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToInventory,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "स्टाफ" else "Staff Fleet",
                            icon = Lucide.Truck,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToStaff,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileDockItem(
                            label = if (isHindi) "डेबुक" else "Reports",
                            icon = Lucide.FileSpreadsheet,
                            tint = textPrimary,
                            innerSurface = innerSurface,
                            innerBorder = innerBorder,
                            isDark = isDark,
                            onClick = onNavigateToReports,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- 4. 7-Day Sales Trend Bar Chart (Profile Card Style) ---
        item(key = "sales_trend_chart") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "7-दिवसीय बिक्री ट्रेंड" else "7-Day Sales Trend",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = if (isHindi) "दैनिक बिक्री का लाइव ग्राफ" else "Daily sales volume comparison",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                        TextButton(onClick = onNavigateToInvoices) {
                            Text(
                                text = if (isHindi) "सभी इनवॉइस >" else "All Invoices >",
                                fontSize = 12.sp,
                                color = emerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 7-Day Visual Bar Graph with Fixed Baseline
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        trendDays.forEachIndexed { idx, day ->
                            val isToday = idx == trendDays.size - 1
                            val heightFraction = if (day.amount <= 0) 0.04f else ((day.amount / maxTrendAmount).toFloat()).coerceIn(0.08f, 1f)
                            val formattedAmount = when {
                                day.amount <= 0 -> ""
                                day.amount >= 10000000 -> "₹${String.format(Locale.ENGLISH, "%.1fCr", day.amount / 10000000)}"
                                day.amount >= 100000 -> "₹${String.format(Locale.ENGLISH, "%.1fL", day.amount / 100000)}"
                                day.amount >= 1000 -> "₹${String.format(Locale.ENGLISH, "%.1fk", day.amount / 1000)}"
                                else -> "₹${day.amount.toInt()}"
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Amount Header Label
                                Box(
                                    modifier = Modifier.height(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (formattedAmount.isNotEmpty()) {
                                        Text(
                                            text = formattedAmount,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) emerald else textSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                // Fixed-Height Bar Container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    // Background Track
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(innerSurface)
                                            .border(1.dp, innerBorder, RoundedCornerShape(6.dp))
                                    )
                                    // Active Bar
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                            .background(if (isToday) emerald else emerald.copy(alpha = 0.55f))
                                    )
                                }

                                // Day Label
                                Text(
                                    text = day.dayLabel,
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isToday) emerald else textSecondary,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 5. Low Stock Actionable Alerts (Profile Card Style) ---
        if (lowStockItems.isNotEmpty()) {
            item(key = "low_stock_alerts") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(1.dp, cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.TriangleAlert,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) "कम स्टॉक वाले सामान" else "Low Stock Warnings",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                            TextButton(onClick = onNavigateToInventory) {
                                Text(
                                    text = if (isHindi) "स्टॉक देखें >" else "Manage Stock >",
                                    fontSize = 11.sp,
                                    color = emerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        lowStockItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(innerSurface)
                                    .border(1.dp, innerBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "Only ${item.currentStock.toInt()} ${item.unit} left (Threshold: ${item.threshold.toInt()})",
                                        fontSize = 11.sp,
                                        color = Color(0xFFD97706)
                                    )
                                }
                                Surface(
                                    modifier = Modifier.clickable { onNavigateToPurchases() },
                                    shape = RoundedCornerShape(16.dp),
                                    color = emerald.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, emerald.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "+ Reorder",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = emerald,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. Top Khata Debtors with 1-Tap Direct WhatsApp Reminder ---
        if (topDebtors.isNotEmpty()) {
            item(key = "top_debtors_khata") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(1.dp, cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Users,
                                    contentDescription = null,
                                    tint = Color(0xFFE11D48),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) "बकाया ग्राहक उधारी (Top Khata Dues)" else "Top Khata Receivables",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE11D48)
                                )
                            }
                            TextButton(onClick = onNavigateToParties) {
                                Text(
                                    text = if (isHindi) "बही-खाता देखें >" else "View Ledger >",
                                    fontSize = 11.sp,
                                    color = emerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        topDebtors.forEach { debtor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(innerSurface)
                                    .border(1.dp, innerBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = debtor.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "₹${String.format(Locale.ENGLISH, "%.2f", debtor.balance)} Due",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE11D48)
                                    )
                                }
                                Surface(
                                    modifier = Modifier.clickable {
                                        val cleanPhone = debtor.phone?.replace(Regex("[^0-9]"), "") ?: ""
                                        val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
                                        val storeName = businessState?.tradeName ?: "VidyaSetu Store"
                                        val msg = "नमस्कार ${debtor.name} जी,\n\n*${storeName}* से आपका कुल बकाया: ₹${String.format(Locale.ENGLISH, "%.2f", debtor.balance)}\nकृपया शीघ्र भुगतान करें। धन्यवाद!"
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://wa.me/$formattedPhone?text=${Uri.encode(msg)}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = emerald.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, emerald.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.MessageCircle,
                                            contentDescription = "WA",
                                            tint = emerald,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "WhatsApp",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = emerald
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 7. Cloud 2-Way Workspace Re-Sync Card ---
        item(key = "workspace_sync") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSyncing) return@clickable
                            isSyncing = true
                            syncMessage = null
                            scope.launch(Dispatchers.IO) {
                                val engine = StoreWorkspaceSyncEngine(context)
                                val success = engine.triggerFullNetworkSync()
                                withContext(Dispatchers.Main) {
                                    isSyncing = false
                                    isSuccess = success
                                    syncMessage = if (success) "Success: 24 Tables Synced!" else "Sync Error: Check Connection"
                                    Toast.makeText(context, syncMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(11.dp),
                            color = if (isDark) emerald.copy(alpha = 0.18f) else innerSurface,
                            border = BorderStroke(1.dp, if (isDark) emerald.copy(alpha = 0.35f) else innerBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.RefreshCw,
                                    contentDescription = "Sync",
                                    tint = emerald,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(syncRotation)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isHindi) "24 टेबल्स लाइव सिंक" else "2-Way Workspace Sync",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = if (isSyncing) "Syncing with cloud..." else "Room Database & Supabase in lockstep",
                                fontSize = 10.sp,
                                color = textSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = innerSurface,
                        border = BorderStroke(1.dp, innerBorder)
                    ) {
                        Text(
                            text = if (isSyncing) "Syncing..." else "Sync Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSyncing) emerald else textPrimary,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Apple iOS 18 Glanceable KPI Metric Card (Profile Stats Style).
 */
@Composable
private fun ProfileStyleKpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tintColor: Color,
    cardSurface: Color,
    cardBorder: Color,
    innerSurface: Color,
    innerBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary,
                    maxLines = 1
                )
                Surface(
                    modifier = Modifier.size(26.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = innerSurface,
                    border = BorderStroke(1.dp, innerBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (tintColor == Color(0xFFE11D48)) tintColor else textPrimary,
                letterSpacing = (-0.3).sp,
                maxLines = 1
            )

            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = textSecondary,
                maxLines = 1
            )
        }
    }
}

/**
 * Apple iOS Monochromatic Control Dock Squircle Tile.
 */
@Composable
private fun ProfileDockItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    tint: Color,
    innerSurface: Color,
    innerBorder: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = innerSurface,
            border = BorderStroke(1.dp, innerBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
            maxLines = 1
        )
    }
}
