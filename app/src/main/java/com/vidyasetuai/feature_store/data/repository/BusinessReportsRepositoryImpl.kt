package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.*
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.*
import com.vidyasetuai.feature_store.domain.repository.BusinessReportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.time.Instant

class BusinessReportsRepositoryImpl(
    context: Context,
    private val database: StoreDatabase = StoreDatabase.getDatabase(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : BusinessReportsRepository {

    private val invoiceDao = database.invoiceDao()
    private val invoiceItemDao = database.invoiceItemDao()
    private val itemDao = database.itemDao()
    private val paymentDao = database.paymentDao()
    private val orderDao = database.orderDao()
    private val purchaseInvoiceDao = database.purchaseInvoiceDao()

    @Suppress("UNCHECKED_CAST")
    override fun getLocalReportsFlow(
        businessId: String,
        startDate: String,
        endDate: String
    ): Flow<BusinessReportsPayloadUiModel> {
        val invoicesFlow = invoiceDao.getAllInvoicesFlow()
        val invoiceItemsFlow = invoiceItemDao.getAllInvoiceItemsFlow()
        val itemsFlow = itemDao.getAllItemsFlow()
        val paymentsFlow = paymentDao.getAllPaymentsFlow()
        val ordersFlow = orderDao.getAllOrdersFlow()
        val purchaseInvoicesFlow = purchaseInvoiceDao.getAllPurchaseInvoicesFlow()

        return combine(
            invoicesFlow,
            invoiceItemsFlow,
            itemsFlow,
            paymentsFlow,
            ordersFlow,
            purchaseInvoicesFlow
        ) { flowsArray ->
            val invoices = flowsArray[0] as List<InvoiceEntity>
            val allInvoiceItems = flowsArray[1] as List<InvoiceItemEntity>
            val allCatalogItems = flowsArray[2] as List<ItemEntity>
            val allPayments = flowsArray[3] as List<PaymentEntity>
            val allOrders = flowsArray[4] as List<OrderEntity>
            val allPurchaseInvoices = flowsArray[5] as List<PurchaseInvoiceEntity>

            val catalogMap = allCatalogItems.associateBy { it.id }
            val ordersMap = allOrders.associateBy { it.id }
            val invoiceItemsGrouped = allInvoiceItems.groupBy { it.invoiceId }

            // 1. Filter sales invoices by business and date range
            val filteredInvoices = invoices.filter { inv ->
                val matchBiz = businessId.isBlank() || inv.businessId == businessId
                val rawDate = inv.invoiceDate.ifBlank { inv.createdAt }
                val inRange = isDateInRange(rawDate, startDate, endDate)
                matchBiz && inRange && !inv.isDeleted && !inv.paymentStatus.equals("CANCELLED", ignoreCase = true)
            }

            // 2. Filter payments by business and date range
            val filteredPayments = allPayments.filter { p ->
                val matchBiz = businessId.isBlank() || p.businessId == businessId
                val inRange = isDateInRange(p.createdAt, startDate, endDate)
                matchBiz && inRange && !p.isDeleted
            }

            // 3. Filter purchase invoices for Input Tax Credit (ITC)
            val filteredPurchaseInvoices = allPurchaseInvoices.filter { pi ->
                val matchBiz = businessId.isBlank() || pi.businessId == businessId
                val rawDate = pi.invoiceDate.ifBlank { pi.createdAt }
                val inRange = isDateInRange(rawDate, startDate, endDate)
                matchBiz && inRange && !pi.isDeleted
            }

            // 4. Daybook Calculations
            var cashSales = 0.0
            var upiSales = 0.0
            var khataSales = 0.0
            var totalRevenue = 0.0
            var totalDiscount = 0.0
            var taxableTotal = 0.0
            var cgstTotal = 0.0
            var sgstTotal = 0.0
            var igstTotal = 0.0

            filteredInvoices.forEach { inv ->
                val amount = inv.grandTotal
                totalRevenue += amount
                totalDiscount += inv.discountTotal
                taxableTotal += inv.taxableAmount
                cgstTotal += inv.cgstAmount
                sgstTotal += inv.sgstAmount
                igstTotal += inv.igstAmount

                val linkedOrder = inv.orderId?.let { ordersMap[it] }
                val paymentMethod = linkedOrder?.paymentMethod ?: ""

                when {
                    paymentMethod.equals("UPI_QR", ignoreCase = true) -> upiSales += amount
                    paymentMethod.equals("CREDIT_KHATA", ignoreCase = true) || inv.paymentStatus.equals("UNPAID", ignoreCase = true) -> khataSales += amount
                    else -> cashSales += amount
                }
            }

            // Payments Table Khata Collections
            var paymentsReceivedCash = 0.0
            var paymentsReceivedUpi = 0.0
            val supplierPaidCash = 0.0

            filteredPayments.forEach { p ->
                if (p.amount > 0) {
                    if (p.paymentMode.equals("CASH", ignoreCase = true)) {
                        paymentsReceivedCash += p.amount
                    } else {
                        paymentsReceivedUpi += p.amount
                    }
                }
            }

            // Net Cash in Hand = (Cash Sales + Cash Payments Received) - Cash Supplier Paid
            val netCashDrawer = (cashSales + paymentsReceivedCash) - supplierPaidCash

            // 5. COGS & HSN Summary
            var cogsTotal = 0.0
            val hsnAggregates = mutableMapOf<String, HsnAccumulator>()

            filteredInvoices.forEach { inv ->
                val items = invoiceItemsGrouped[inv.id] ?: emptyList()
                items.forEach { lineItem ->
                    val catalogItem = catalogMap[lineItem.itemId]
                    val purchasePrice = catalogItem?.purchasePrice ?: 0.0
                    cogsTotal += (lineItem.quantity * purchasePrice)

                    val hsnCode = lineItem.hsnSacCode?.ifBlank { "NA" } ?: "NA"
                    val key = "$hsnCode|${lineItem.itemName}"
                    val acc = hsnAggregates.getOrPut(key) {
                        HsnAccumulator(
                            hsnSacCode = hsnCode,
                            itemName = lineItem.itemName
                        )
                    }
                    acc.totalQty += lineItem.quantity
                    acc.taxableVal += lineItem.taxableValue
                    acc.cgstVal += lineItem.cgstAmount
                    acc.sgstVal += lineItem.sgstAmount
                    acc.igstVal += lineItem.igstAmount
                    acc.totalTax += (lineItem.cgstAmount + lineItem.sgstAmount + lineItem.igstAmount)
                }
            }

            val netProfit = (totalRevenue - cogsTotal) - totalDiscount
            val profitMarginPct = if (totalRevenue > 0) ((netProfit / totalRevenue) * 100) else 0.0
            val outwardTaxTotal = cgstTotal + sgstTotal + igstTotal

            val hsnList = hsnAggregates.values.map {
                HsnSummaryItem(
                    hsnSacCode = it.hsnSacCode,
                    itemName = it.itemName,
                    totalQty = it.totalQty,
                    taxableVal = it.taxableVal,
                    cgstVal = it.cgstVal,
                    sgstVal = it.sgstVal,
                    igstVal = it.igstVal,
                    totalTax = it.totalTax
                )
            }.sortedByDescending { it.taxableVal }

            // 6. GSTR-3B Input Tax Credit (ITC) Calculations
            var itcCgst = 0.0
            var itcSgst = 0.0
            var itcIgst = 0.0

            filteredPurchaseInvoices.forEach { pi ->
                itcCgst += pi.cgstAmount
                itcSgst += pi.sgstAmount
                itcIgst += pi.igstAmount
            }
            val itcClaimableTotal = itcCgst + itcSgst + itcIgst
            val netGstPayable = maxOf(0.0, outwardTaxTotal - itcClaimableTotal)

            BusinessReportsPayloadUiModel(
                startDate = startDate,
                endDate = endDate,
                daybook = DaybookUiModel(
                    cashSales = cashSales,
                    upiSales = upiSales,
                    khataSales = khataSales,
                    paymentsReceivedCash = paymentsReceivedCash,
                    paymentsReceivedUpi = paymentsReceivedUpi,
                    supplierPaidCash = supplierPaidCash,
                    netCashDrawer = netCashDrawer
                ),
                profitLoss = ProfitLossUiModel(
                    totalRevenue = totalRevenue,
                    cogsTotal = cogsTotal,
                    discountTotal = totalDiscount,
                    netProfit = netProfit,
                    profitMarginPct = profitMarginPct
                ),
                gstr1 = Gstr1UiModel(
                    taxableTotal = taxableTotal,
                    cgstTotal = cgstTotal,
                    sgstTotal = sgstTotal,
                    igstTotal = igstTotal,
                    outwardTaxTotal = outwardTaxTotal,
                    hsnSummary = hsnList
                ),
                gstr3b = Gstr3bUiModel(
                    outwardTaxPayable = outwardTaxTotal,
                    itcCgst = itcCgst,
                    itcSgst = itcSgst,
                    itcClaimableTotal = itcClaimableTotal,
                    netGstPayable = netGstPayable
                ),
                generatedAt = Instant.now().toString()
            )
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun fetchRemoteReports(
        businessId: String,
        startDate: String,
        endDate: String
    ): Result<BusinessReportsPayloadUiModel> = withContext(Dispatchers.IO) {
        try {
            val json = remoteDataSource.getBusinessFinancialReports(businessId, startDate, endDate)
                ?: return@withContext Result.failure(Exception("No data returned from reports RPC."))

            val daybookObj = json["daybook"]?.jsonObject
            val profitLossObj = json["profit_loss"]?.jsonObject
            val gstr1Obj = json["gstr1"]?.jsonObject
            val gstr3bObj = json["gstr3b"]?.jsonObject

            val daybook = DaybookUiModel(
                cashSales = daybookObj?.get("cash_sales")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                upiSales = daybookObj?.get("upi_sales")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                khataSales = daybookObj?.get("khata_sales")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                paymentsReceivedCash = daybookObj?.get("payments_received_cash")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                paymentsReceivedUpi = daybookObj?.get("payments_received_upi")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                supplierPaidCash = daybookObj?.get("supplier_paid_cash")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                expensesPaidCash = daybookObj?.get("expenses_paid_cash")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                salaryPaidCash = daybookObj?.get("salary_paid_cash")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                netCashDrawer = daybookObj?.get("net_cash_drawer")?.jsonPrimitive?.doubleOrNull ?: 0.0
            )

            val profitLoss = ProfitLossUiModel(
                totalRevenue = profitLossObj?.get("total_revenue")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                cogsTotal = profitLossObj?.get("cogs_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                discountTotal = profitLossObj?.get("discount_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                expensesTotal = profitLossObj?.get("expenses_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                netProfit = profitLossObj?.get("net_profit")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                profitMarginPct = profitLossObj?.get("profit_margin_pct")?.jsonPrimitive?.doubleOrNull ?: 0.0
            )

            val hsnArray = gstr1Obj?.get("hsn_summary")?.jsonArray
            val hsnList = hsnArray?.map { el ->
                val obj = el.jsonObject
                HsnSummaryItem(
                    hsnSacCode = obj["hsn_sac_code"]?.jsonPrimitive?.contentOrNull ?: "NA",
                    itemName = obj["item_name"]?.jsonPrimitive?.contentOrNull ?: "",
                    totalQty = obj["total_qty"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                    taxableVal = obj["taxable_val"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                    cgstVal = obj["cgst_val"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                    sgstVal = obj["sgst_val"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                    igstVal = obj["igst_val"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                    totalTax = obj["total_tax"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                )
            } ?: emptyList()

            val gstr1 = Gstr1UiModel(
                taxableTotal = gstr1Obj?.get("taxable_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                cgstTotal = gstr1Obj?.get("cgst_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                sgstTotal = gstr1Obj?.get("sgst_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                igstTotal = gstr1Obj?.get("igst_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                outwardTaxTotal = gstr1Obj?.get("outward_tax_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                hsnSummary = hsnList
            )

            val gstr3b = Gstr3bUiModel(
                outwardTaxPayable = gstr3bObj?.get("outward_tax_payable")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                itcCgst = gstr3bObj?.get("itc_cgst")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                itcSgst = gstr3bObj?.get("itc_sgst")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                itcClaimableTotal = gstr3bObj?.get("itc_claimable_total")?.jsonPrimitive?.doubleOrNull ?: 0.0,
                netGstPayable = gstr3bObj?.get("net_gst_payable")?.jsonPrimitive?.doubleOrNull ?: 0.0
            )

            Result.success(
                BusinessReportsPayloadUiModel(
                    startDate = startDate,
                    endDate = endDate,
                    daybook = daybook,
                    profitLoss = profitLoss,
                    gstr1 = gstr1,
                    gstr3b = gstr3b,
                    generatedAt = json["generated_at"]?.jsonPrimitive?.contentOrNull ?: Instant.now().toString()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isDateInRange(dateStr: String, startDate: String, endDate: String): Boolean {
        if (dateStr.isBlank()) return true
        return try {
            val d = dateStr.take(10)
            val s = startDate.take(10)
            val e = endDate.take(10)
            d in s..e
        } catch (_: Exception) {
            true
        }
    }

    private class HsnAccumulator(
        val hsnSacCode: String,
        val itemName: String,
        var totalQty: Double = 0.0,
        var taxableVal: Double = 0.0,
        var cgstVal: Double = 0.0,
        var sgstVal: Double = 0.0,
        var igstVal: Double = 0.0,
        var totalTax: Double = 0.0
    )
}
