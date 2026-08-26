package com.vidyasetuai.feature_store.presentation.screen.role_staff.parties

import android.content.Intent
import android.net.Uri
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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.BookOpen
import com.composables.icons.lucide.Building2
import com.composables.icons.lucide.IndianRupee
import com.composables.icons.lucide.MessageSquare
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.X
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components.AddEditPartyBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components.PartyPassbookBottomSheet
import com.vidyasetuai.feature_store.presentation.screen.role_staff.parties.components.RecordPaymentBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiesKhataScreen(
    isHindi: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PartiesKhataViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PartiesKhataViewModel(context) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Layered Back Navigation Resilience
    BackHandler(enabled = true) {
        when {
            uiState.isAddEditOpen -> viewModel.closeAddEdit()
            uiState.isPaymentModalOpen -> viewModel.closeRecordPayment()
            uiState.isPassbookOpen -> viewModel.closePassbook()
            else -> onBack()
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
                    Column {
                        Text(
                            text = if (isHindi) "पार्टियां व ग्राहक खाता" else "Parties & Customer Khata",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "उधारी, भुगतान व पासबुक लेज़र" else "Customer Credit, Payments & Ledger",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.openAddParty() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Plus,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "नया खाता" else "New Party",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // 1. 4-Pill KPI Metric Header Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Receivable (उधारी लेना है)
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "कुल लेना (Receivable)" else "To Receive (Due)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF166534)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format("%.2f", uiState.totalReceivable)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF166534)
                        )
                    }
                }

                // Total Payable (सप्लायर देना है)
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "कुल देना (Payable)" else "To Pay (Supplier)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${String.format("%.2f", uiState.totalPayable)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }

                // Customer Count
                Surface(
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "ग्राहक" else "Customers",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.customerCount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Supplier Count
                Surface(
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "सप्लायर" else "Suppliers",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${uiState.supplierCount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Search Bar + Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Input
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(if (isHindi) "पार्टी नाम, फोन या GSTIN खोजें..." else "Search party, phone, GSTIN...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Lucide.X,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(PartyTypeFilter.ALL, if (isHindi) "सभी पार्टियां" else "All"),
                    Pair(PartyTypeFilter.CUSTOMER, if (isHindi) "ग्राहक (Customers)" else "Customers"),
                    Pair(PartyTypeFilter.SUPPLIER, if (isHindi) "सप्लायर (Suppliers)" else "Suppliers")
                ).forEach { (typeFilter, label) ->
                    val isSelected = uiState.selectedType == typeFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedType(typeFilter) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF10B981)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Virtualized Parties List (Rule 7: LazyColumn with stable keys)
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF10B981),
                        strokeWidth = 2.5.dp
                    )
                }
            } else if (uiState.filteredParties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Lucide.Users,
                            contentDescription = "No Parties",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHindi) "कोई पार्टी या ग्राहक नहीं मिला" else "No Parties Found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "'+ नया खाता' बटन दबाकर ग्राहक या सप्लायर जोड़ें" else "Tap '+ New Party' above to add a customer or supplier.",
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
                    items(uiState.filteredParties, key = { it.id }) { party ->
                        PartyCardItem(
                            party = party,
                            isHindi = isHindi,
                            onRecordPayment = { viewModel.openRecordPayment(party) },
                            onOpenPassbook = { viewModel.openPassbook(party) },
                            onEditParty = { viewModel.openEditParty(party) },
                            onSendWhatsAppReminder = {
                                val waUrl = viewModel.getWhatsAppStatementUrl(party, "Sharma Sweets & Fast Food")
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal 1: Add / Edit Party Bottom Sheet
    if (uiState.isAddEditOpen) {
        AddEditPartyBottomSheet(
            isHindi = isHindi,
            editingParty = uiState.editingParty,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeAddEdit() },
            onSave = { pType, name, phone, email, gstin, pan, limit, opening, isRec ->
                viewModel.saveParty(pType, name, phone, email, gstin, pan, limit, opening, isRec)
            }
        )
    }

    // Modal 2: Record Payment Settlement Bottom Sheet
    if (uiState.isPaymentModalOpen && uiState.paymentTargetParty != null) {
        RecordPaymentBottomSheet(
            isHindi = isHindi,
            party = uiState.paymentTargetParty!!,
            isSubmitting = uiState.isSubmitting,
            errorMessage = uiState.errorMessage,
            onDismiss = { viewModel.closeRecordPayment() },
            onRecord = { pId, tType, amt, mode, ref, notes ->
                viewModel.recordPayment(pId, tType, amt, mode, ref, notes)
            }
        )
    }

    // Modal 3: Passbook & Transaction Ledger History Bottom Sheet
    if (uiState.isPassbookOpen && uiState.selectedPartyForPassbook != null) {
        PartyPassbookBottomSheet(
            isHindi = isHindi,
            party = uiState.selectedPartyForPassbook!!,
            ledgerEntries = uiState.ledgerEntries,
            isLoading = uiState.isLedgerLoading,
            storeName = "Sharma Sweets & Fast Food",
            onGetWhatsAppUrl = { p, store -> viewModel.getWhatsAppStatementUrl(p, store) },
            onDismiss = { viewModel.closePassbook() }
        )
    }
}

@Composable
private fun PartyCardItem(
    party: PartyEntity,
    isHindi: Boolean,
    onRecordPayment: () -> Unit,
    onOpenPassbook: () -> Unit,
    onEditParty: () -> Unit,
    onSendWhatsAppReminder: () -> Unit
) {
    val isCustomer = party.partyType == "CUSTOMER"
    val bal = party.currentBalance

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Name, Party Type Badge, Balance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = party.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Type Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCustomer) Color(0xFFDCFCE7) else Color(0xFFDBEAFE)
                    ) {
                        Text(
                            text = if (isCustomer) "Customer" else "Supplier",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomer) Color(0xFF166534) else Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Balance Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        bal > 0 -> Color(0xFFFEE2E2)
                        bal < 0 -> Color(0xFFDCFCE7)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }
                ) {
                    Text(
                        text = when {
                            bal > 0 -> "${if (isHindi) "लेना:" else "Due:"} ₹${String.format("%.2f", bal)}"
                            bal < 0 -> "${if (isHindi) "जमा:" else "Adv:"} ₹${String.format("%.2f", Math.abs(bal))}"
                            else -> if (isHindi) "चुकता ₹0" else "Settled ₹0"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            bal > 0 -> Color(0xFFDC2626)
                            bal < 0 -> Color(0xFF166534)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Phone & GSTIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Phone,
                        contentDescription = "Phone",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = party.phone,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!party.gstin.isNullOrBlank()) {
                    Text(
                        text = "GSTIN: ${party.gstin}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Record Payment CTA (💵)
                Button(
                    onClick = onRecordPayment,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(
                        imageVector = Lucide.IndianRupee,
                        contentDescription = "Pay",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "हिसाब दर्ज करें" else "Payment",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // 2. Passbook CTA (📖)
                OutlinedButton(
                    onClick = onOpenPassbook,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Lucide.BookOpen,
                        contentDescription = "Passbook",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "पासबुक" else "Passbook",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 3. WhatsApp Reminder CTA (📲)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSendWhatsAppReminder() }
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.MessageSquare,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 4. Edit Party CTA (✏️)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEditParty() }
                ) {
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Pencil,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
