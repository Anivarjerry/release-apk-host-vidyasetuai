package com.vidyasetuai.feature_store.presentation.screen.role_staff.kds

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.domain.model.KdsFilterTab
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.kds.components.AssignRiderBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenKdsScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: KitchenKdsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return KitchenKdsViewModel(context) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Layered BackHandler Navigation Resilience
    BackHandler(enabled = true) {
        if (uiState.selectedTicketForRiderAssignment != null) {
            viewModel.closeAssignRiderModal()
        } else {
            onBack()
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "किचन KDS बोर्ड" else "KITCHEN DISPLAY (KDS)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "${uiState.activeTickets.size} KOT",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isHindi) "रसोइयों व शेफ के लिए लाइव ऑर्डर टिकट्स" else "Live Kitchen Order Tickets for Chefs",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    // Sound Toggle Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (uiState.isSoundEnabled) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.isSoundEnabled) Color(0xFF10B981).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.toggleSound() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isSoundEnabled) Lucide.Volume2 else Lucide.VolumeX,
                                contentDescription = "Sound",
                                tint = if (uiState.isSoundEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (uiState.isSoundEnabled) "Audio ON" else "Muted",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isSoundEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    KdsFilterChip(
                        label = if (isHindi) "सभी सक्रिय" else "ALL ACTIVE",
                        count = uiState.activeTickets.size,
                        isSelected = uiState.activeFilterTab == KdsFilterTab.ALL_ACTIVE,
                        activeColor = Color(0xFF0F172A),
                        onClick = { viewModel.setFilterTab(KdsFilterTab.ALL_ACTIVE) }
                    )
                }
                item {
                    KdsFilterChip(
                        label = if (isHindi) "नया 🔴" else "NEW 🔴",
                        count = uiState.newCount,
                        isSelected = uiState.activeFilterTab == KdsFilterTab.NEW,
                        activeColor = Color(0xFFEF4444),
                        onClick = { viewModel.setFilterTab(KdsFilterTab.NEW) }
                    )
                }
                item {
                    KdsFilterChip(
                        label = if (isHindi) "बन रहा 🟡" else "PREPARING 🟡",
                        count = uiState.preparingCount,
                        isSelected = uiState.activeFilterTab == KdsFilterTab.PREPARING,
                        activeColor = Color(0xFFF59E0B),
                        onClick = { viewModel.setFilterTab(KdsFilterTab.PREPARING) }
                    )
                }
                item {
                    KdsFilterChip(
                        label = if (isHindi) "तैयार 🟢" else "READY 🟢",
                        count = uiState.readyCount,
                        isSelected = uiState.activeFilterTab == KdsFilterTab.READY,
                        activeColor = Color(0xFF10B981),
                        onClick = { viewModel.setFilterTab(KdsFilterTab.READY) }
                    )
                }
                item {
                    KdsFilterChip(
                        label = if (isHindi) "सर्व हो चुका ⚪" else "SERVED TODAY",
                        count = uiState.servedCount,
                        isSelected = uiState.activeFilterTab == KdsFilterTab.SERVED_TODAY,
                        activeColor = Color(0xFF64748B),
                        onClick = { viewModel.setFilterTab(KdsFilterTab.SERVED_TODAY) }
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // 2. KDS Tickets Board (Rule 7: Virtualized LazyColumn with unique key)
            val tickets = uiState.displayedTickets

            if (tickets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Utensils,
                                contentDescription = "Clean",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = if (isHindi) "किचन KDS बोर्ड साफ़ है!" else "Kitchen KDS Board is Clean!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (isHindi) "POS काउंटर या ऑनलाइन स्टोर से नया ऑर्डर आते ही यहाँ टिकट दिखेगा।" else "New orders placed at POS counter or online storefront will appear here.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
                ) {
                    items(
                        items = tickets,
                        key = { it.order.id }
                    ) { ticket ->
                        KdsTicketCard(
                            isHindi = isHindi,
                            ticket = ticket,
                            onStartPreparing = { viewModel.startPreparing(ticket.order.id, ticket.order.businessId) },
                            onMarkReady = { viewModel.markReady(ticket.order.id, ticket.order.businessId) },
                            onMarkServed = { viewModel.markServed(ticket.order.id, ticket.order.businessId) },
                            onOpenRiderAssign = { viewModel.openAssignRiderModal(ticket) }
                        )
                    }
                }
            }
        }
    }

    // Modal: Assign Rider & Delivery OTP Bottom Sheet
    uiState.selectedTicketForRiderAssignment?.let { ticket ->
        AssignRiderBottomSheet(
            isHindi = isHindi,
            ticket = ticket,
            availableRiders = uiState.availableRiders,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeAssignRiderModal() },
            onConfirmDispatch = { orderId, newStatus, riderId, otpCode ->
                viewModel.dispatchOrDeliverOrder(
                    orderId = orderId,
                    newStatus = newStatus,
                    riderId = riderId,
                    otpCode = otpCode,
                    businessId = ticket.order.businessId
                )
            }
        )
    }
}

@Composable
private fun KdsFilterChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) activeColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun KdsTicketCard(
    isHindi: Boolean,
    ticket: KitchenKdsUiModel,
    onStartPreparing: () -> Unit,
    onMarkReady: () -> Unit,
    onMarkServed: () -> Unit,
    onOpenRiderAssign: () -> Unit
) {
    val order = ticket.order
    val isNew = ticket.isNew
    val isPreparing = ticket.isPreparing
    val isReady = ticket.isReady
    val isOutForDelivery = ticket.isOutForDelivery
    val isServed = ticket.isServed
    val isDeliveryOrder = ticket.isDeliveryOrder

    // Color Scheme based on status
    val borderColor = when {
        isNew -> Color(0xFFEF4444)
        isPreparing -> Color(0xFFF59E0B)
        isReady -> Color(0xFF10B981)
        isOutForDelivery -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val headerBgColor = when {
        isNew -> Color(0xFFEF4444)
        isPreparing -> Color(0xFFF59E0B).copy(alpha = 0.15f)
        isReady -> Color(0xFF10B981).copy(alpha = 0.15f)
        isOutForDelivery -> Color(0xFF3B82F6).copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val headerTextColor = when {
        isNew -> Color.White
        isPreparing -> Color(0xFFD97706)
        isReady -> Color(0xFF059669)
        isOutForDelivery -> Color(0xFF2563EB)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isNew) 4.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            if (isNew) 2.dp else 1.dp,
            borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBgColor)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = (order.tableOrTokenNo ?: order.orderType).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = headerTextColor
                    )
                    Text(
                        text = ticket.formattedOrderNumber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = headerTextColor.copy(alpha = 0.9f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = "Timer",
                            tint = headerTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${ticket.elapsedMinutes}m ago",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = headerTextColor
                        )
                    }

                    Text(
                        text = order.orderType.replace("_", " ").uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = headerTextColor.copy(alpha = 0.85f)
                    )
                }
            }

            // Items Checklist Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ticket.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = "${item.quantity.toInt()}x",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = item.itemName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Chef Special Notes
                if (!order.orderNotes.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Info,
                                contentDescription = "Note",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Note: ${order.orderNotes}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // Card Action Footer Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                when {
                    isNew -> {
                        Button(
                            onClick = onStartPreparing,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Lucide.ChefHat, contentDescription = "Cook", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "पकाना शुरू करें (Start Preparing)" else "START PREPARING (COOKING)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    isPreparing -> {
                        Button(
                            onClick = onMarkReady,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Lucide.Check, contentDescription = "Ready", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "खाना तैयार है (Mark Food Ready)" else "MARK FOOD READY FOR PICKUP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    isReady && isDeliveryOrder -> {
                        // Online / Delivery Order: Assign Rider Button
                        Button(
                            onClick = onOpenRiderAssign,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Lucide.Bike, contentDescription = "Rider", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "🛵 राइडर को असाइन करें (Assign to Rider)" else "🛵 ASSIGN TO RIDER / DISPATCH",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    isReady && !isDeliveryOrder -> {
                        // POS / Counter / Dine-In Order: Direct Served Button
                        Button(
                            onClick = onMarkServed,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Lucide.Utensils, contentDescription = "Served", tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "🍽️ काउंटर पर सर्व करें (Mark Served)" else "🍽️ MARK SERVED (COUNTER PICKUP)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                    isOutForDelivery -> {
                        // Out For Delivery: Complete with OTP Button
                        Button(
                            onClick = onOpenRiderAssign,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Lucide.Key, contentDescription = "OTP", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "🔑 OTP दर्ज करके डिलीवर करें" else "🔑 ENTER OTP & DELIVER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ORDER COMPLETED / SERVED 🟢",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
