package com.vidyasetuai.feature_store.presentation.screen.role_staff.expenses

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import com.vidyasetuai.feature_store.domain.model.BusinessExpenseUiModel
import com.vidyasetuai.feature_store.presentation.screen.role_staff.expenses.components.AddExpenseBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesListScreen(
    isHindi: Boolean = false,
    businessId: String,
    branchId: String? = null,
    businessName: String = "Store",
    onNavigateBack: () -> Unit,
    viewModel: ExpensesViewModel = run {
        val app = LocalContext.current.applicationContext as android.app.Application
        remember(app, businessId) {
            ExpensesViewModel(app).apply { initialize(businessId, branchId) }
        }
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(businessId, branchId) {
        viewModel.initialize(businessId, branchId)
    }

    // Mandatory BackHandler
    BackHandler(enabled = true) {
        if (uiState.isAddSheetOpen) {
            viewModel.closeAddSheet()
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "दुकान खर्चे व पेट्टी कैश" else "Expenses & Petty Cash",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "दैनिक खर्चे, वाउचर व गल्ला कटौती" else "Store expenses, cash drawer outflows & vouchers",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddSheet() },
                containerColor = Color(0xFF10B981),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
                text = { Text(if (isHindi) "+ खर्च दर्ज करें" else "+ Record Expense", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. KPI Metrics Bar (4 Cards)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Month Total
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "इस महीने का खर्च" else "Month Total",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${String.format("%.2f", uiState.totalExpenseMonth)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 2: Today Cash from Drawer
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "आज का नकद (गल्ला)" else "Today Cash Out",
                            fontSize = 10.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "₹${String.format("%.2f", uiState.todayCashExpense)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Card 3: Today Online
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isHindi) "आज UPI / बैंक" else "Today UPI/Bank",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${String.format("%.2f", uiState.todayOnlineExpense)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 2. Search & Category Filters
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(if (isHindi) "खर्च विवरण या वाउचर खोजें..." else "Search expense title or voucher...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filter Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryFilter == null,
                        onClick = { viewModel.setCategoryFilter(null) },
                        label = { Text(if (isHindi) "सभी (All)" else "All") }
                    )
                }
                items(uiState.categories, key = { it.id }) { cat ->
                    val isSelected = uiState.selectedCategoryFilter == cat.id
                    val label = if (isHindi && !cat.nameHi.isNullOrBlank()) cat.nameHi else cat.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategoryFilter(if (isSelected) null else cat.id) },
                        label = { Text(label) }
                    )
                }
            }

            // 3. Virtualized Expense List (0ms Room Reads)
            val filteredExpenses = uiState.expenses.filter { exp ->
                (uiState.selectedCategoryFilter == null || exp.expenseTypeId == uiState.selectedCategoryFilter) &&
                (uiState.selectedPaymentModeFilter == null || exp.paymentMode == uiState.selectedPaymentModeFilter) &&
                (uiState.searchQuery.isBlank() || exp.title.contains(uiState.searchQuery, ignoreCase = true) || exp.voucherNumber.contains(uiState.searchQuery, ignoreCase = true))
            }

            if (filteredExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHindi) "कोई खर्च नहीं मिला" else "No Expenses Recorded",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "दुकान का नया खर्च दर्ज करने के लिए नीचे बटन दबाएं" else "Tap the '+ Record Expense' button below to add your first expense.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            isHindi = isHindi,
                            onShareWhatsApp = {
                                val msg = "नमस्कार,\n\n*${businessName}* - खर्च वाउचर\nवाउचर सं: ${expense.voucherNumber}\nविवरण: ${expense.title}\nराशि: ₹${String.format("%.2f", expense.amount)}\nमाध्यम: ${expense.paymentMode}\nतारीख: ${expense.expenseDate}\n\nधन्यवाद!"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/?text=${Uri.encode(msg)}")
                                }
                                context.startActivity(intent)
                            },
                            onDelete = {
                                viewModel.deleteExpense(expense.id)
                            }
                        )
                    }
                }
            }
        }

        // Add Expense BottomSheet
        if (uiState.isAddSheetOpen) {
            AddExpenseBottomSheet(
                isHindi = isHindi,
                categories = uiState.categories,
                branchesList = uiState.branchesList,
                staffList = uiState.staffList,
                partiesList = uiState.partiesList,
                itemsList = uiState.itemsList,
                isSubmitting = uiState.isSubmitting,
                onSaveExpense = { expenseTypeId, branchId, title, amount, paymentMode, expenseDate, referenceType, referenceId, paidTo, notes ->
                    viewModel.recordExpense(
                        expenseTypeId = expenseTypeId,
                        branchId = branchId,
                        title = title,
                        amount = amount,
                        paymentMode = paymentMode,
                        expenseDate = expenseDate,
                        referenceType = referenceType,
                        referenceId = referenceId,
                        paidTo = paidTo,
                        notes = notes,
                        onSuccess = {
                            viewModel.closeAddSheet()
                        }
                    )
                },
                onDismiss = { viewModel.closeAddSheet() }
            )
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: BusinessExpenseUiModel,
    isHindi: Boolean,
    onShareWhatsApp: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = expense.voucherNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = expense.categoryName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = expense.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "${expense.expenseDate} • ${expense.paymentMode} ${if (!expense.paidTo.isNullOrBlank()) "• Paid to: ${expense.paidTo}" else ""}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "₹${String.format("%.2f", expense.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShareWhatsApp, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
