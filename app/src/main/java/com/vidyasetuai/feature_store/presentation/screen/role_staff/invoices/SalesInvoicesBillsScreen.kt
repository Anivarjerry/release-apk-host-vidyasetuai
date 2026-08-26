package com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.composables.icons.lucide.*
import com.vidyasetuai.core.ui.colors.AppColors
import com.vidyasetuai.feature_store.domain.model.InvoiceWithDetailsUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.components.EwayBillBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.invoices.components.InvoiceDetailsBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesInvoicesBillsScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SalesInvoicesViewModel = remember(context) {
        SalesInvoicesViewModel(context.applicationContext)
    }
    val uiState by viewModel.uiState.collectAsState()

    // Pillar 5 Mandatory Back Navigation Resilience
    BackHandler {
        if (uiState.isDetailsSheetOpen) {
            viewModel.closeDetails()
        } else if (uiState.isEwaySheetOpen) {
            viewModel.closeEway()
        } else if (uiState.isCancelDialogOpen) {
            viewModel.dismissCancelDialog()
        } else {
            onBack()
        }
    }

    // Feedback Toasts
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "बिक्री इनवॉइस व बिल (GST)" else "Sales Invoices & Bills",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "${uiState.filteredInvoices.size} इनवॉइस मिले" else "${uiState.filteredInvoices.size} Invoices Recorded",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- 1. KPI SUMMARY HEADER CARDS (4-Pill Grid) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InvoiceMetricCard(
                    title = if (isHindi) "कुल बिक्री" else "Total Sales",
                    value = "₹${"%.0f".format(uiState.totalSalesRevenue)}",
                    color = AppColors.EmeraldGreen,
                    modifier = Modifier.weight(1.2f)
                )
                InvoiceMetricCard(
                    title = if (isHindi) "प्राप्त राशि" else "Paid",
                    value = "₹${"%.0f".format(uiState.totalPaidRevenue)}",
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1.1f)
                )
                InvoiceMetricCard(
                    title = if (isHindi) "उधार / बाकी" else "Credit Due",
                    value = "₹${"%.0f".format(uiState.totalCreditDue)}",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1.1f)
                )
                InvoiceMetricCard(
                    title = if (isHindi) "कुल बिल" else "Invoices",
                    value = uiState.totalInvoicesCount.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(0.8f)
                )
            }

            // --- 2. SEARCH INPUT ---
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(if (isHindi) "बिल नंबर, ग्राहक नाम, फ़ोन..." else "Search by invoice #, customer, phone...") },
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // --- 3. STATUS SEGMENTED FILTER BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statusFilters = listOf(
                    Pair(InvoiceStatusFilter.ALL, if (isHindi) "सभी" else "All"),
                    Pair(InvoiceStatusFilter.PAID, if (isHindi) "🟢 चुकाया (Paid)" else "🟢 Paid"),
                    Pair(InvoiceStatusFilter.CREDIT_KHATA, if (isHindi) "🟠 उधार (Credit)" else "🟠 Credit Khata"),
                    Pair(InvoiceStatusFilter.UNPAID, if (isHindi) "🔴 बाकी (Unpaid)" else "🔴 Unpaid"),
                    Pair(InvoiceStatusFilter.CANCELLED, if (isHindi) "⚪ रद्द (Cancelled)" else "⚪ Cancelled")
                )

                statusFilters.forEach { (filter, label) ->
                    val isSelected = uiState.statusFilter == filter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.clickable { viewModel.setStatusFilter(filter) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- 4. INVOICES LIST VIEW ---
            if (uiState.filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.FileSpreadsheet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (isHindi) "कोई इनवॉइस या बिल नहीं मिला" else "No sales invoices found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isHindi) "POS काउंटर बिलिंग से नए इनवॉइस यहाँ तुरंत दिखाई देंगे।" else "Invoices created from POS Billing Counter will appear here.",
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
                    items(uiState.filteredInvoices, key = { it.invoice.id }) { invoiceWithDetails ->
                        InvoiceCardItem(
                            invoiceWithDetails = invoiceWithDetails,
                            isHindi = isHindi,
                            onViewDetails = { viewModel.openDetails(invoiceWithDetails) },
                            onOpenEway = { viewModel.openEway(invoiceWithDetails) },
                            onCancelPrompt = { viewModel.promptCancel(invoiceWithDetails) }
                        )
                    }
                }
            }
        }
    }

    // Modal 1: Invoice Details & Print Preview Bottom Sheet
    if (uiState.isDetailsSheetOpen && uiState.selectedInvoiceForDetails != null) {
        InvoiceDetailsBottomSheet(
            invoiceWithDetails = uiState.selectedInvoiceForDetails!!,
            business = uiState.activeBusiness,
            branch = uiState.activeBranch,
            isHindi = isHindi,
            printMode = uiState.printMode,
            onPrintModeChange = { viewModel.setPrintMode(it) },
            onCancelInvoicePrompt = { viewModel.promptCancel(uiState.selectedInvoiceForDetails!!) },
            onDismiss = { viewModel.closeDetails() }
        )
    }

    // Modal 2: E-Way Bill Bottom Sheet
    if (uiState.isEwaySheetOpen && uiState.selectedInvoiceForEway != null) {
        EwayBillBottomSheet(
            invoiceWithDetails = uiState.selectedInvoiceForEway!!,
            isHindi = isHindi,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeEway() },
            onSaveEwayBill = { ewayNo, vehicleNo, transportName, distanceKm, ewayStatus ->
                viewModel.updateEwayBill(
                    invoiceId = uiState.selectedInvoiceForEway!!.invoice.id,
                    ewayBillNo = ewayNo,
                    vehicleNo = vehicleNo,
                    transportName = transportName,
                    distanceKm = distanceKm,
                    ewayStatus = ewayStatus
                )
            }
        )
    }

    // Modal 3: Destructive Cancel Confirmation Alert Dialog
    if (uiState.isCancelDialogOpen && uiState.selectedInvoiceForCancel != null) {
        val cancelInv = uiState.selectedInvoiceForCancel!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            icon = {
                Icon(
                    imageVector = Lucide.Ban,
                    contentDescription = "Warning",
                    tint = Color(0xFFDC2626)
                )
            },
            title = {
                Text(
                    text = if (isHindi) "क्या आप बिल रद्द करना चाहते हैं?" else "Cancel Invoice?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "इनवॉइस #${cancelInv.invoice.invoiceNumber} (₹${"%.2f".format(cancelInv.invoice.grandTotal)}) को रद्द कर दिया जाएगा। यह बिल राजस्व से हटा दिया जाएगा।"
                    } else {
                        "Are you sure you want to cancel Invoice #${cancelInv.invoice.invoiceNumber} (₹${"%.2f".format(cancelInv.invoice.grandTotal)})? This will mark it as CANCELLED."
                    },
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmCancelInvoice() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(if (isHindi) "हाँ, बिल रद्द करें" else "Confirm Cancel")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissCancelDialog() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isHindi) "वापस जाएं" else "Dismiss")
                }
            }
        )
    }
}

@Composable
private fun InvoiceMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun InvoiceCardItem(
    invoiceWithDetails: InvoiceWithDetailsUiModel,
    isHindi: Boolean = false,
    onViewDetails: () -> Unit,
    onOpenEway: () -> Unit,
    onCancelPrompt: () -> Unit
) {
    val inv = invoiceWithDetails.invoice
    val isCancelled = invoiceWithDetails.isCancelled

    val statusBadgeColor = when {
        isCancelled -> Color(0xFF9CA3AF)
        invoiceWithDetails.isPaid -> Color(0xFF16A34A)
        invoiceWithDetails.isCreditKhata -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Invoice Number, Date, Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "#${inv.invoiceNumber}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = inv.invoiceDate.take(16).replace("T", " • "),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBadgeColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusBadgeColor)
                        )
                        Text(
                            text = inv.paymentStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusBadgeColor
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // Row 2: Customer & Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoiceWithDetails.displayCustomerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    invoiceWithDetails.customerPhone?.let { phone ->
                        Text(
                            text = phone,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${"%.2f".format(inv.grandTotal)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isCancelled) Color(0xFF9CA3AF) else MaterialTheme.colorScheme.onSurface
                    )
                    if (inv.dueAmount > 0) {
                        Text(
                            text = "Due: ₹${"%.2f".format(inv.dueAmount)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            // Row 3: Action CTAs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(34.dp)
                ) {
                    Icon(imageVector = Lucide.FileText, contentDescription = "View", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHindi) "विवरण / प्रिंट" else "View & Print", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onOpenEway,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                ) {
                    Icon(imageVector = Lucide.Truck, contentDescription = "E-Way", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHindi) "ई-वे बिल" else "E-Way Bill", fontSize = 11.sp)
                }

                if (!isCancelled) {
                    OutlinedButton(
                        onClick = onCancelPrompt,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(34.dp)
                    ) {
                        Icon(imageVector = Lucide.Ban, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "रद्द करें" else "Cancel", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
