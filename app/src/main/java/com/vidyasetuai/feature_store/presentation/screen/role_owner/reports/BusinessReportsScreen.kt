package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.domain.model.DatePreset
import com.vidyasetuai.feature_store.domain.model.ReportTab
import com.vidyasetuai.feature_store.presentation.screen.role_owner.reports.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessReportsScreen(
    isHindi: Boolean = false,
    businessId: String = "",
    businessName: String = "My Business",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: BusinessReportsViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BusinessReportsViewModel(context) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pillar 5: Layered BackHandler Navigation Resilience
    BackHandler(enabled = true) {
        if (uiState.isCustomDatePickerOpen) {
            viewModel.closeCustomDatePicker()
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
                    Column {
                        Text(
                            text = if (isHindi) "व्यापार रिपोर्ट्स व P&L" else "FINANCIAL REPORTS & P&L",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "दैनिक रोकड़, शुद्ध मुनाफ़ा व GST फाइलिंग" else "Daybook, Net Profit Margin & GST Filing",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Lucide.ArrowLeft, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    // Cloud Sync Button
                    IconButton(
                        onClick = { viewModel.syncWithCloud(businessId) },
                        enabled = !uiState.isSyncingRemote
                    ) {
                        if (uiState.isSyncingRemote) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF10B981))
                        } else {
                            Icon(imageVector = Lucide.RefreshCw, contentDescription = "Sync", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Share / Export CSV Button
                    IconButton(onClick = { viewModel.exportHsnCsv(businessName) }) {
                        Icon(imageVector = Lucide.Share2, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface)
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
            // 1. Date Range Preset Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            DatePresetChip(
                                label = if (isHindi) "आज (Today)" else "Today",
                                isSelected = uiState.selectedDatePreset == DatePreset.TODAY,
                                onClick = { viewModel.applyDatePreset(DatePreset.TODAY) }
                            )
                        }
                        item {
                            DatePresetChip(
                                label = if (isHindi) "इस सप्ताह (Week)" else "This Week",
                                isSelected = uiState.selectedDatePreset == DatePreset.THIS_WEEK,
                                onClick = { viewModel.applyDatePreset(DatePreset.THIS_WEEK) }
                            )
                        }
                        item {
                            DatePresetChip(
                                label = if (isHindi) "इस महीने (Month)" else "This Month",
                                isSelected = uiState.selectedDatePreset == DatePreset.THIS_MONTH,
                                onClick = { viewModel.applyDatePreset(DatePreset.THIS_MONTH) }
                            )
                        }
                        item {
                            DatePresetChip(
                                label = if (isHindi) "कस्टम दिनांक 📅" else "Custom 📅",
                                isSelected = uiState.selectedDatePreset == DatePreset.CUSTOM,
                                onClick = { viewModel.openCustomDatePicker() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Lucide.Calendar, contentDescription = "Date", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(
                            text = "${uiState.startDate}  ➔  ${uiState.endDate}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // 2. Financial Tabs Selector (Scrollable TabRow)
            ScrollableTabRow(
                selectedTabIndex = uiState.activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 14.dp,
                divider = {}
            ) {
                Tab(
                    selected = uiState.activeTab == ReportTab.DAYBOOK,
                    onClick = { viewModel.setReportTab(ReportTab.DAYBOOK) },
                    text = {
                        Text(
                            text = if (isHindi) "दैनिक रोकड़ (Daybook)" else "Daily Daybook",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.activeTab == ReportTab.DAYBOOK) FontWeight.Black else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == ReportTab.PROFIT_LOSS,
                    onClick = { viewModel.setReportTab(ReportTab.PROFIT_LOSS) },
                    text = {
                        Text(
                            text = if (isHindi) "लाभ व हानि (P&L)" else "Profit & Loss",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.activeTab == ReportTab.PROFIT_LOSS) FontWeight.Black else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == ReportTab.GSTR1,
                    onClick = { viewModel.setReportTab(ReportTab.GSTR1) },
                    text = {
                        Text(
                            text = if (isHindi) "GSTR-1 सेल्स" else "GSTR-1 Outward",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.activeTab == ReportTab.GSTR1) FontWeight.Black else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == ReportTab.GSTR3B,
                    onClick = { viewModel.setReportTab(ReportTab.GSTR3B) },
                    text = {
                        Text(
                            text = if (isHindi) "GSTR-3B इनपुट" else "GSTR-3B ITC",
                            fontSize = 12.sp,
                            fontWeight = if (uiState.activeTab == ReportTab.GSTR3B) FontWeight.Black else FontWeight.Medium
                        )
                    }
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // 3. Virtualized LazyColumn (Rule 7) for Active Tab View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 40.dp)
            ) {
                item {
                    when (uiState.activeTab) {
                        ReportTab.DAYBOOK -> DaybookReportView(isHindi = isHindi, daybook = uiState.daybook)
                        ReportTab.PROFIT_LOSS -> ProfitLossReportView(isHindi = isHindi, profitLoss = uiState.profitLoss)
                        ReportTab.GSTR1 -> Gstr1ReportView(isHindi = isHindi, gstr1 = uiState.gstr1, onExportCsv = { viewModel.exportHsnCsv(businessName) })
                        ReportTab.GSTR3B -> Gstr3bReportView(isHindi = isHindi, gstr3b = uiState.gstr3b)
                    }
                }
            }
        }
    }

    // Modal: Custom Date Picker Bottom Sheet
    if (uiState.isCustomDatePickerOpen) {
        CustomDateRangeBottomSheet(
            isHindi = isHindi,
            initialStartDate = uiState.startDate,
            initialEndDate = uiState.endDate,
            onDismiss = { viewModel.closeCustomDatePicker() },
            onApplyRange = { start, end -> viewModel.setCustomDateRange(start, end) }
        )
    }
}

@Composable
private fun DatePresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
