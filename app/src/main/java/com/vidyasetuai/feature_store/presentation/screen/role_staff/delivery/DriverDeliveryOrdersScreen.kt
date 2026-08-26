package com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.DriverDeliveryTab
import com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery.components.DriverDeliveryCard
import com.vidyasetuai.feature_store.presentation.screen.role_staff.delivery.components.VerifyDeliveryOtpBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDeliveryOrdersScreen(
    businessId: String = "",
    isHindi: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val viewModel: DriverDeliveryViewModel = viewModel(
        factory = remember(context, businessId) {
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return DriverDeliveryViewModel(context, businessId) as T
                }
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Layered Back Navigation
    BackHandler(enabled = true) {
        when {
            uiState.selectedOrderForOtp != null -> viewModel.closeOtpSheet()
            else -> onNavigateBack()
        }
    }

    // Snackbar notifications
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "डिलीवरी फ्लीट व OTP" else "Delivery Fleet & OTP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "लाइव ऑर्डर्स व Google Maps नेविगेशन" else "Live Orders & GPS Navigation",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // =========================================================================
            // 📌 1. TOP METRIC SUMMARY CARDS (Active, Delivered, Cash in Hand)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Active Assigned
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.EmeraldGreen.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AppColors.EmeraldGreen)
                            )
                            Text(
                                text = if (isHindi) "एक्टिव" else "Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.EmeraldGreen
                            )
                        }
                        Text(
                            text = "${uiState.activeOrders.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 2: Delivered Today
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "डिलीवर हो चुके" else "Delivered",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.completedOrders.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 3: COD Cash in Hand
                Card(
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF59E0B).copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "हाथ में कैश (जमा करना है)" else "Cash in Hand",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706),
                            maxLines = 1
                        )
                        Text(
                            text = "₹${"%.0f".format(uiState.cashInHandTotal)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            // =========================================================================
            // 📌 2. TAB FILTER CHIPS (Active Deliveries vs Delivered History)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedTab == DriverDeliveryTab.ACTIVE,
                    onClick = { viewModel.selectTab(DriverDeliveryTab.ACTIVE) },
                    label = {
                        Text(
                            text = if (isHindi) "एक्टिव डिलीवरी (${uiState.activeOrders.size})" else "Active Deliveries (${uiState.activeOrders.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.EmeraldGreen,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = uiState.selectedTab == DriverDeliveryTab.DELIVERED,
                    onClick = { viewModel.selectTab(DriverDeliveryTab.DELIVERED) },
                    label = {
                        Text(
                            text = if (isHindi) "इतिहास (${uiState.completedOrders.size})" else "History (${uiState.completedOrders.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.EmeraldGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // =========================================================================
            // 📌 3. VIRTUALIZED ORDERS LIST (Rule 7: 60 FPS Smooth Scrolling)
            // =========================================================================
            val displayList = if (uiState.selectedTab == DriverDeliveryTab.ACTIVE) uiState.activeOrders else uiState.completedOrders

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.selectedTab == DriverDeliveryTab.ACTIVE) Lucide.PackageCheck else Lucide.History,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (uiState.selectedTab == DriverDeliveryTab.ACTIVE) {
                                if (isHindi) "कोई पेंडिंग डिलीवरी नहीं है" else "No pending deliveries right now"
                            } else {
                                if (isHindi) "कोई पूरा हुआ ऑर्डर नहीं है" else "No delivered orders yet"
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = displayList,
                        key = { it.id }
                    ) { order ->
                        DriverDeliveryCard(
                            order = order,
                            isHindi = isHindi,
                            onOpenOtpSheet = { viewModel.openOtpSheet(it) }
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 📌 4. OTP VERIFICATION BOTTOM SHEET
    // =========================================================================
    uiState.selectedOrderForOtp?.let { targetOrder ->
        VerifyDeliveryOtpBottomSheet(
            order = targetOrder,
            isHindi = isHindi,
            isVerifying = uiState.isVerifyingOtp,
            onDismiss = { viewModel.closeOtpSheet() },
            onVerifyOtp = { orderId, otpCode ->
                viewModel.verifyAndCompleteDelivery(orderId, otpCode, isHindi = isHindi)
            }
        )
    }
}
