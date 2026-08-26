package com.vidyasetuai.feature_store.domain.model

enum class DatePreset {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    CUSTOM
}

enum class ReportTab {
    DAYBOOK,
    PROFIT_LOSS,
    GSTR1,
    GSTR3B
}

data class DaybookUiModel(
    val cashSales: Double = 0.0,
    val upiSales: Double = 0.0,
    val khataSales: Double = 0.0,
    val paymentsReceivedCash: Double = 0.0,
    val paymentsReceivedUpi: Double = 0.0,
    val supplierPaidCash: Double = 0.0,
    val expensesPaidCash: Double = 0.0,
    val salaryPaidCash: Double = 0.0,
    val netCashDrawer: Double = 0.0
) {
    val totalCashInflow: Double get() = cashSales + paymentsReceivedCash
    val totalDigitalInflow: Double get() = upiSales + paymentsReceivedUpi
    val totalSalesRevenue: Double get() = cashSales + upiSales + khataSales
}

data class ProfitLossUiModel(
    val totalRevenue: Double = 0.0,
    val cogsTotal: Double = 0.0,
    val discountTotal: Double = 0.0,
    val expensesTotal: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMarginPct: Double = 0.0
) {
    val isProfitable: Boolean get() = netProfit >= 0
    val grossProfit: Double get() = totalRevenue - cogsTotal
}

data class HsnSummaryItem(
    val hsnSacCode: String = "",
    val itemName: String = "",
    val totalQty: Double = 0.0,
    val taxableVal: Double = 0.0,
    val cgstVal: Double = 0.0,
    val sgstVal: Double = 0.0,
    val igstVal: Double = 0.0,
    val totalTax: Double = 0.0
)

data class Gstr1UiModel(
    val taxableTotal: Double = 0.0,
    val cgstTotal: Double = 0.0,
    val sgstTotal: Double = 0.0,
    val igstTotal: Double = 0.0,
    val outwardTaxTotal: Double = 0.0,
    val hsnSummary: List<HsnSummaryItem> = emptyList()
)

data class Gstr3bUiModel(
    val outwardTaxPayable: Double = 0.0,
    val itcCgst: Double = 0.0,
    val itcSgst: Double = 0.0,
    val itcClaimableTotal: Double = 0.0,
    val netGstPayable: Double = 0.0
)

data class BusinessReportsPayloadUiModel(
    val startDate: String,
    val endDate: String,
    val daybook: DaybookUiModel = DaybookUiModel(),
    val profitLoss: ProfitLossUiModel = ProfitLossUiModel(),
    val gstr1: Gstr1UiModel = Gstr1UiModel(),
    val gstr3b: Gstr3bUiModel = Gstr3bUiModel(),
    val generatedAt: String = ""
)
