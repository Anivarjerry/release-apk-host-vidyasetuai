package com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.domain.model.PurchaseFilterStatus
import com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.components.AddPurchaseBillBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.components.PurchaseBillCard
import com.vidyasetuai.feature_store.presentation.screen.role_owner.purchases.components.PurchaseBillDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseBillsScreen(
    businessId: String,
    branchId: String = "",
    isHindi: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val viewModel: PurchaseBillsViewModel = viewModel(
        factory = remember(context) {
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return PurchaseBillsViewModel(context) as T
                }
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Layered Back Navigation
    BackHandler(enabled = true) {
        when {
            uiState.isAddDrawerOpen -> viewModel.closeAddDrawer()
            uiState.selectedPurchaseForDetail != null -> viewModel.closePurchaseDetail()
            else -> onNavigateBack()
        }
    }

    LaunchedEffect(businessId) {
        viewModel.loadData(businessId)
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "सप्लायर खरीद बिल व स्टॉक आवक" else "Supplier Purchase Bills",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "स्टॉक बढ़ोतरी (+Qty) व ITC टैक्स बहीखाता" else "Auto-Stock In (+Qty) & ITC Input Credit",
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
                actions = {
                    IconButton(onClick = { viewModel.loadData(businessId) }) {
                        Icon(imageVector = Lucide.RotateCw, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddDrawer() },
                containerColor = Color(0xFF10B981),
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(imageVector = Lucide.Plus, contentDescription = "Record Bill", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "+ नया खरीद बिल दर्ज करें" else "+ Record Purchase Bill",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Metric Summary Bar (4 Cards)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Spend
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = "Total Spend", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${"%,.2f".format(uiState.summary.totalSpend)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Paid Amount
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = "Paid to Suppliers", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${"%,.2f".format(uiState.summary.totalPaid)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Pending Due
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(text = "Supplier Due", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${"%,.2f".format(uiState.summary.totalDue)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFF59E0B)
                        )
                    }
                }
            }

            // 2. Search & Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("सप्लायर बिल नंबर या नाम खोजें...", fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Lucide.Search, contentDescription = "Search", modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Lucide.X, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )

                // Status Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PurchaseFilterStatus.values().forEach { filter ->
                        val isSelected = uiState.selectedStatusFilter == filter
                        val filterColor = when (filter) {
                            PurchaseFilterStatus.ALL -> MaterialTheme.colorScheme.primary
                            PurchaseFilterStatus.PAID -> Color(0xFF10B981)
                            PurchaseFilterStatus.PARTIAL -> Color(0xFFF59E0B)
                            PurchaseFilterStatus.UNPAID -> Color(0xFFEF4444)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) filterColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) filterColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setStatusFilter(filter) }
                        ) {
                            Text(
                                text = when (filter) {
                                    PurchaseFilterStatus.ALL -> "सभी"
                                    PurchaseFilterStatus.PAID -> "PAID"
                                    PurchaseFilterStatus.PARTIAL -> "PARTIAL"
                                    PurchaseFilterStatus.UNPAID -> "UNPAID"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) filterColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Purchase Bills List (Virtualized LazyColumn for 60 FPS)
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF10B981))
                }
            } else if (uiState.filteredPurchases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Lucide.ShoppingBag, contentDescription = "Empty", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                        }
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "कोई खरीद बिल नहीं मिला" else "कोई खरीद बिल दर्ज नहीं है",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "नया सप्लायर बिल दर्ज करने के लिए नीचे '+ Record Purchase Bill' दबाएं।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.filteredPurchases,
                        key = { it.id }
                    ) { purchase ->
                        PurchaseBillCard(
                            isHindi = isHindi,
                            purchase = purchase,
                            onClick = { viewModel.openPurchaseDetail(purchase) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // 4. Record Purchase Bill Bottom Sheet
    if (uiState.isAddDrawerOpen) {
        AddPurchaseBillBottomSheet(
            isHindi = isHindi,
            suppliers = uiState.suppliers,
            catalogItems = uiState.catalogItems,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeAddDrawer() },
            onSubmit = { payload ->
                viewModel.recordPurchaseBill(businessId, branchId, payload)
            }
        )
    }

    // 5. Purchase Bill Detail Bottom Sheet
    uiState.selectedPurchaseForDetail?.let { selectedPurchase ->
        PurchaseBillDetailBottomSheet(
            isHindi = isHindi,
            purchase = selectedPurchase,
            onDismiss = { viewModel.closePurchaseDetail() }
        )
    }
}
