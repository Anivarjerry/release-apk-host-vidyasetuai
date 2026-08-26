package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.repository.BusinessReportsRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.DatePreset
import com.vidyasetuai.feature_store.domain.model.ReportTab
import com.vidyasetuai.feature_store.domain.repository.BusinessReportsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BusinessReportsViewModel(
    private val context: Context,
    private val repository: BusinessReportsRepository = BusinessReportsRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessReportsUiState())
    val uiState: StateFlow<BusinessReportsUiState> = _uiState.asStateFlow()

    private var localObservationJob: Job? = null

    init {
        // Default to THIS_MONTH
        applyDatePreset(DatePreset.THIS_MONTH)
    }

    fun setReportTab(tab: ReportTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun applyDatePreset(preset: DatePreset) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val (start, end) = when (preset) {
            DatePreset.TODAY -> Pair(today.format(formatter), today.format(formatter))
            DatePreset.THIS_WEEK -> {
                val dayOfWeek = today.dayOfWeek.value % 7 // Sunday = 0, Monday = 1
                val firstDayOfWeek = today.minusDays(dayOfWeek.toLong())
                Pair(firstDayOfWeek.format(formatter), today.format(formatter))
            }
            DatePreset.THIS_MONTH -> {
                val firstDayOfMonth = today.withDayOfMonth(1)
                Pair(firstDayOfMonth.format(formatter), today.format(formatter))
            }
            DatePreset.CUSTOM -> {
                Pair(_uiState.value.startDate.ifBlank { today.format(formatter) }, _uiState.value.endDate.ifBlank { today.format(formatter) })
            }
        }

        _uiState.update {
            it.copy(
                selectedDatePreset = preset,
                startDate = start,
                endDate = end,
                isCustomDatePickerOpen = preset == DatePreset.CUSTOM
            )
        }

        restartLocalObservation(start, end)
    }

    fun setCustomDateRange(start: String, end: String) {
        _uiState.update {
            it.copy(
                selectedDatePreset = DatePreset.CUSTOM,
                startDate = start,
                endDate = end,
                isCustomDatePickerOpen = false
            )
        }
        restartLocalObservation(start, end)
    }

    fun openCustomDatePicker() {
        _uiState.update { it.copy(isCustomDatePickerOpen = true) }
    }

    fun closeCustomDatePicker() {
        _uiState.update { it.copy(isCustomDatePickerOpen = false) }
    }

    private fun restartLocalObservation(start: String, end: String) {
        localObservationJob?.cancel()
        localObservationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getLocalReportsFlow("", start, end).collect { payload ->
                _uiState.update {
                    it.copy(
                        daybook = payload.daybook,
                        profitLoss = payload.profitLoss,
                        gstr1 = payload.gstr1,
                        gstr3b = payload.gstr3b,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun syncWithCloud(businessId: String) {
        if (businessId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingRemote = true) }
            val result = repository.fetchRemoteReports(
                businessId = businessId,
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate
            )
            result.onSuccess { remotePayload ->
                _uiState.update {
                    it.copy(
                        daybook = remotePayload.daybook,
                        profitLoss = remotePayload.profitLoss,
                        gstr1 = remotePayload.gstr1,
                        gstr3b = remotePayload.gstr3b,
                        isSyncingRemote = false,
                        successMessage = "क्लाउड से ऑडिटेड रिपोर्ट्स सिंक हो गई! (Cloud Verified)"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSyncingRemote = false,
                        errorMessage = "क्लाउड सिंक में समस्या: ${err.message}"
                    )
                }
            }
        }
    }

    fun exportHsnCsv(businessName: String) {
        val hsnList = _uiState.value.gstr1.hsnSummary
        if (hsnList.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "निर्यात करने के लिए कोई HSN डेटा नहीं है।") }
            return
        }

        val csvBuilder = StringBuilder()
        csvBuilder.append("HSN/SAC Code,Item Description,Quantity Sold,Taxable Value (INR),CGST (INR),SGST (INR),IGST (INR),Total Tax (INR)\n")

        hsnList.forEach { hsn ->
            csvBuilder.append("\"${hsn.hsnSacCode}\",")
            csvBuilder.append("\"${hsn.itemName.replace("\"", "\"\"")}\",")
            csvBuilder.append("${hsn.totalQty},")
            csvBuilder.append("${hsn.taxableVal},")
            csvBuilder.append("${hsn.cgstVal},")
            csvBuilder.append("${hsn.sgstVal},")
            csvBuilder.append("${hsn.igstVal},")
            csvBuilder.append("${hsn.totalTax}\n")
        }

        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
                putExtra(Intent.EXTRA_TITLE, "GSTR-1_HSN_Report_${_uiState.value.startDate}_${_uiState.value.endDate}.csv")
                type = "text/csv"
            }
            val shareIntent = Intent.createChooser(sendIntent, "GSTR-1 HSN CSV शेयर करें")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "CSV शेयर करने में त्रुटि: ${e.message}") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
