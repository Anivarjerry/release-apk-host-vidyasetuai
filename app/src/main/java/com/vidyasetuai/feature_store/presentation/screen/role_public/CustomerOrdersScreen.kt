package com.vidyasetuai.feature_store.presentation.screen.role_public

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.data.remote.dto.CustomerOrderDto
import kotlinx.coroutines.launch

enum class OrdersTab {
    ACTIVE,
    PAST
}

/**
 * Flagship Apple Minimalist Flat HIG Orders & Live Delivery Tracking Screen.
 * Features:
 * 1. Apple iOS 18 Segmented Pill Controls (Active Orders vs Past Orders).
 * 2. 4-Step Minimalist Order Status Stepper (Placed -> Kitchen -> Out for Delivery -> Handed Over).
 * 3. Frosted Slate Luxury Delivery OTP Card with 1-tap Copy.
 * 4. Vector Fulfillment Badges (Lucide.Truck, Lucide.Utensils, Lucide.Package).
 * 5. 0ms Virtualized Lazy Layouts with Zero Lag.
 */
@Composable
fun CustomerOrdersScreen(
    initialTab: OrdersTab = OrdersTab.ACTIVE,
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onBack: () -> Unit = {}
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val remoteDataSource = remember { StoreRemoteDataSource() }

    var selectedTab by remember { mutableStateOf(initialTab) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val ordersList = remember { mutableStateListOf<CustomerOrderDto>() }

    val fetchOrders: (Boolean) -> Unit = { isManualRefresh ->
        if (isManualRefresh) isRefreshing = true else isLoading = true
        scope.launch {
            try {
                val orders = remoteDataSource.fetchCustomerOrders()
                ordersList.clear()
                ordersList.addAll(orders)
            } catch (e: Exception) {
                // Fail-safe
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchOrders(false)
    }

    // Filter Active vs Past Orders
    val activeOrders = remember(ordersList.toList()) {
        ordersList.filter {
            it.orderStatus.uppercase() in listOf("NEW", "PLACED", "PREPARING", "READY", "OUT_FOR_DELIVERY")
        }
    }
    val pastOrders = remember(ordersList.toList()) {
        ordersList.filter {
            it.orderStatus.uppercase() in listOf("DELIVERED", "COMPLETED", "CANCELLED")
        }
    }

    val displayedOrders = if (selectedTab == OrdersTab.ACTIVE) activeOrders else pastOrders

    // Design Tokens
    val containerBg = if (isDark) Color(0xFF0B0F17) else Color(0xFFF8FAFC)
    val topBarSurface = if (isDark) Color(0xFF0F172A) else Color.White
    val cardSurface = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155).copy(alpha = 0.55f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val emerald = Color(0xFF10B981)
    val pillTrackBg = if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f) else Color(0xFFF1F5F9)

    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "refresh_spin"
    )

    Scaffold(
        containerColor = containerBg,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = topBarSurface,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // Top Bar Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { onBack() },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, cardBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.ArrowLeft,
                                        contentDescription = "Back",
                                        tint = textPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isHindi) "मेरे ऑर्डर्स व ट्रैकिंग" else "My Orders & Tracking",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    text = if (isHindi) {
                                        "सक्रिय (${activeOrders.size}) • पुराना इतिहास (${pastOrders.size})"
                                    } else {
                                        "Active (${activeOrders.size}) • Past History (${pastOrders.size})"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textSecondary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { fetchOrders(true) },
                            shape = RoundedCornerShape(12.dp),
                            color = emerald.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, emerald.copy(alpha = 0.25f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Lucide.RefreshCw,
                                    contentDescription = "Refresh",
                                    tint = emerald,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(refreshRotation)
                                )
                            }
                        }
                    }

                    // Apple iOS 18 Segmented Pill Controls
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(pillTrackBg)
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // 1. Active Orders Segment
                            val isActive = (selectedTab == OrdersTab.ACTIVE)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedTab = OrdersTab.ACTIVE },
                                color = if (isActive) (if (isDark) Color(0xFF334155) else Color(0xFF0F172A)) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (isActive) 2.dp else 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.Clock,
                                        contentDescription = null,
                                        tint = if (isActive) Color.White else textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "सक्रिय ऑर्डर" else "Active Orders",
                                        fontSize = 12.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) Color.White else textSecondary
                                    )
                                    if (activeOrders.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isActive) emerald else emerald.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "${activeOrders.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isActive) Color.White else emerald,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Past Orders Segment
                            val isPast = (selectedTab == OrdersTab.PAST)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedTab = OrdersTab.PAST },
                                color = if (isPast) (if (isDark) Color(0xFF334155) else Color(0xFF0F172A)) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (isPast) 2.dp else 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.History,
                                        contentDescription = null,
                                        tint = if (isPast) Color.White else textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "पुराने ऑर्डर्स" else "Past Orders",
                                        fontSize = 12.sp,
                                        fontWeight = if (isPast) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isPast) Color.White else textSecondary
                                    )
                                    if (pastOrders.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isPast) Color.White.copy(alpha = 0.25f) else Color.Gray.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${pastOrders.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPast) Color.White else textSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Divider(
                        color = cardBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(containerBg)
                .navigationBarsPadding()
        ) {
            if (isLoading && ordersList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = emerald,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.5.dp
                    )
                }
            } else if (displayedOrders.isEmpty()) {
                // Luxury Apple Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (selectedTab == OrdersTab.ACTIVE) Lucide.PackageOpen else Lucide.ReceiptText,
                                contentDescription = "Empty",
                                tint = emerald,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == OrdersTab.ACTIVE) {
                            if (isHindi) "कोई सक्रिय ऑर्डर नहीं है" else "No Active Orders"
                        } else {
                            if (isHindi) "कोई पुराना ऑर्डर इतिहास नहीं है" else "No Past Orders Yet"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.2).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedTab == OrdersTab.ACTIVE) {
                            if (isHindi) "जब आप स्टोर से ऑर्डर करेंगे, तब उनका लाइव स्टेटस और डिलीवरी OTP यहाँ दिखेगा।" else "When you place orders from the store, their live status and handover OTPs will appear here."
                        } else {
                            if (isHindi) "आपके पूरे हो चुके ऑर्डर्स का डिजिटल बिल और रिकॉर्ड यहाँ हमेशा सुरक्षित रहेगा।" else "Your completed order receipts and payment records will be safely archived here."
                        },
                        fontSize = 12.sp,
                        color = textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    if (selectedTab == OrdersTab.ACTIVE) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            modifier = Modifier.clickable { onBack() },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFF0F172A),
                            border = BorderStroke(1.dp, cardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Store,
                                    contentDescription = null,
                                    tint = emerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) "स्टोर मेन्यू देखें" else "Browse Store Menu",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedOrders, key = { it.orderId }) { order ->
                        FlagshipCustomerOrderCard(
                            order = order,
                            isHindi = isHindi,
                            isDark = isDark,
                            onCopyOtp = { otp ->
                                clipboardManager.setText(AnnotatedString(otp))
                                Toast.makeText(
                                    context,
                                    if (isHindi) "OTP कॉपी किया गया: $otp" else "Delivery OTP Copied: $otp",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Flagship Apple Minimalist Flat HIG Customer Order Card.
 */
@Composable
fun FlagshipCustomerOrderCard(
    order: CustomerOrderDto,
    isHindi: Boolean = false,
    isDark: Boolean = false,
    onCopyOtp: (String) -> Unit = {}
) {
    val statusUpper = order.orderStatus.uppercase()
    val isLive = statusUpper in listOf("NEW", "PLACED", "PREPARING", "READY", "OUT_FOR_DELIVERY")

    // Theme Colors
    val cardSurface = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155).copy(alpha = 0.55f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val emerald = Color(0xFF10B981)

    val (statusBg, statusBorder, statusTextColor, statusDotColor, statusLabel) = when (statusUpper) {
        "NEW", "PLACED" -> StatusBadgeConfig(
            bg = Color(0xFF3B82F6).copy(alpha = 0.12f),
            border = Color(0xFF3B82F6).copy(alpha = 0.28f),
            text = Color(0xFF3B82F6),
            dot = Color(0xFF3B82F6),
            label = if (isHindi) "ऑर्डर प्राप्त हुआ" else "Order Placed"
        )
        "PREPARING" -> StatusBadgeConfig(
            bg = Color(0xFFF59E0B).copy(alpha = 0.12f),
            border = Color(0xFFF59E0B).copy(alpha = 0.28f),
            text = Color(0xFFF59E0B),
            dot = Color(0xFFF59E0B),
            label = if (isHindi) "किचन में बन रहा है" else "Preparing in Kitchen"
        )
        "READY" -> StatusBadgeConfig(
            bg = emerald.copy(alpha = 0.12f),
            border = emerald.copy(alpha = 0.28f),
            text = emerald,
            dot = emerald,
            label = if (isHindi) "पिकअप के लिए तैयार" else "Ready for Pickup"
        )
        "OUT_FOR_DELIVERY" -> StatusBadgeConfig(
            bg = Color(0xFF8B5CF6).copy(alpha = 0.12f),
            border = Color(0xFF8B5CF6).copy(alpha = 0.28f),
            text = Color(0xFF8B5CF6),
            dot = Color(0xFF8B5CF6),
            label = if (isHindi) "रास्ते में है" else "Out for Delivery"
        )
        "DELIVERED", "COMPLETED" -> StatusBadgeConfig(
            bg = emerald.copy(alpha = 0.12f),
            border = emerald.copy(alpha = 0.28f),
            text = emerald,
            dot = emerald,
            label = if (isHindi) "डिलीवर हो गया" else "Delivered"
        )
        "CANCELLED" -> StatusBadgeConfig(
            bg = Color(0xFFEF4444).copy(alpha = 0.12f),
            border = Color(0xFFEF4444).copy(alpha = 0.28f),
            text = Color(0xFFEF4444),
            dot = Color(0xFFEF4444),
            label = if (isHindi) "रद्द हुआ" else "Cancelled"
        )
        else -> StatusBadgeConfig(
            bg = Color.Gray.copy(alpha = 0.12f),
            border = Color.Gray.copy(alpha = 0.28f),
            text = Color.Gray,
            dot = Color.Gray,
            label = order.orderStatus
        )
    }

    val (fulfillmentIcon, fulfillmentLabel) = when (order.orderType.uppercase()) {
        "DINE_IN" -> Pair(
            Lucide.Utensils,
            if (isHindi) "डाइन-इन ${order.tableOrTokenNo?.let { "• टेबल $it" } ?: ""}" else "Dine-In ${order.tableOrTokenNo?.let { "• Table $it" } ?: ""}"
        )
        "TAKEAWAY" -> Pair(
            Lucide.Package,
            if (isHindi) "काउंटर टेकअवे" else "Counter Takeaway"
        )
        else -> Pair(
            Lucide.Truck,
            if (isHindi) "होम डिलीवरी" else "Home Delivery"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
            // 1. Header: Store Name, Order Number, Fulfillment Capsule & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Store Avatar Squircle
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(11.dp),
                        color = emerald.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, emerald.copy(alpha = 0.25f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initial = (order.merchantName?.firstOrNull() ?: 'S').uppercaseChar().toString()
                            Text(
                                text = initial,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = emerald
                            )
                        }
                    }

                    Column {
                        Text(
                            text = order.merchantName ?: "VidyaSetu Store",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = order.orderNumber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(text = "•", fontSize = 10.sp, color = textSecondary)
                            Icon(
                                imageVector = fulfillmentIcon,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = fulfillmentLabel,
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusDotColor)
                        )
                        Text(
                            text = statusLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor
                        )
                    }
                }
            }

            // 2. Active Order 4-Step Stepper Progress Bar (Shown on live active orders)
            if (isLive) {
                ActiveOrderStepperBar(
                    currentStatus = statusUpper,
                    isHindi = isHindi,
                    isDark = isDark
                )
            }

            // 3. Highlighted Live Delivery OTP Banner (Shown on live orders)
            if (isLive && order.deliveryOtp.isNotBlank() && order.deliveryOtp != "0000") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF0F172A) else Color(0xFF0F172A),
                    border = BorderStroke(1.dp, emerald.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCopyOtp(order.deliveryOtp) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = emerald.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.ShieldCheck,
                                        contentDescription = "Security OTP",
                                        tint = emerald,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (isHindi) "डिलीवरी हैंडओवर OTP" else "Delivery Handover OTP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = if (isHindi) "डिलीवरी बॉय को यह कोड बताएँ" else "Share this code with the rider",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = emerald.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, emerald.copy(alpha = 0.50f))
                            ) {
                                Text(
                                    text = order.deliveryOtp,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = emerald,
                                    letterSpacing = 2.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.Copy,
                                        contentDescription = "Copy OTP",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Itemized Products Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.6f) else Color(0xFFF8FAFC))
                    .border(1.dp, cardBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                            ) {
                                Text(
                                    text = "${item.quantity.toInt()}×",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = item.itemName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = textPrimary,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "₹${item.totalPrice.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    }
                }

                if (!order.orderNotes.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquareQuote,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = order.orderNotes,
                            fontSize = 10.sp,
                            color = textSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            // 5. Bill Summary & Payment Footer
            Divider(color = cardBorder, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Payment Method Pill
                val paymentMethodLabel = when (order.paymentMethod?.uppercase()) {
                    "ONLINE", "UPI" -> if (isHindi) "⚡ UPI / ऑनलाइन" else "⚡ UPI Paid"
                    else -> if (isHindi) "💵 कैश ऑन डिलीवरी" else "💵 Cash on Delivery"
                }
                Text(
                    text = paymentMethodLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )

                // Grand Total
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isHindi) "कुल:" else "Total:",
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                    Text(
                        text = "₹${order.grandTotal.toInt()}.00",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = emerald,
                        letterSpacing = (-0.2).sp
                    )
                }
            }
        }
    }
}

/**
 * 4-Step Minimalist Stepper Bar for Live Orders.
 */
@Composable
private fun ActiveOrderStepperBar(
    currentStatus: String,
    isHindi: Boolean,
    isDark: Boolean
) {
    val activeStep = when (currentStatus) {
        "NEW", "PLACED" -> 1
        "PREPARING" -> 2
        "OUT_FOR_DELIVERY", "READY" -> 3
        "DELIVERED", "COMPLETED" -> 4
        else -> 1
    }

    val emerald = Color(0xFF10B981)
    val inactiveLine = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val textMuted = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    val steps = listOf(
        Pair(1, if (isHindi) "ऑर्डर प्राप्त" else "Placed"),
        Pair(2, if (isHindi) "किचन में" else "Kitchen"),
        Pair(3, if (isHindi) "रास्ते में" else "On the Way"),
        Pair(4, if (isHindi) "डिलीवर" else "Delivered")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Step Indicator Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, (stepNum, _) ->
                val isCompleted = stepNum <= activeStep

                // Step Circle Dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) emerald else inactiveLine),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                // Connecting Line (between dots)
                if (index < steps.size - 1) {
                    val linePassed = (stepNum < activeStep)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .background(if (linePassed) emerald else inactiveLine)
                    )
                }
            }
        }

        // Step Text Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { (stepNum, label) ->
                val isCompleted = stepNum <= activeStep
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) emerald else textMuted
                )
            }
        }
    }
}

private data class StatusBadgeConfig(
    val bg: Color,
    val border: Color,
    val text: Color,
    val dot: Color,
    val label: String
)
