package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.repository.StaffFleetRepositoryImpl
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel
import com.vidyasetuai.feature_store.domain.repository.StaffFleetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class StaffFleetViewModel(
    private val context: Context,
    private val repository: StaffFleetRepository = StaffFleetRepositoryImpl(context)
) : ViewModel() {

    private val storeDb = StoreDatabase.getDatabase(context)

    private val _uiState = MutableStateFlow(StaffFleetUiState())
    val uiState: StateFlow<StaffFleetUiState> = _uiState.asStateFlow()

    private var staffJob: Job? = null
    private var branchJob: Job? = null

    init {
        observeActiveBusiness()
    }

    private fun observeActiveBusiness() {
        viewModelScope.launch(Dispatchers.IO) {
            storeDb.businessDao().getAnyActiveBusinessFlow().collect { activeBusiness ->
                val businessId = activeBusiness?.id ?: ""
                _uiState.update { it.copy(businessId = businessId) }

                if (businessId.isNotEmpty()) {
                    observeStaffAndBranches(businessId)
                    repository.syncAllStaffData(businessId)
                }
            }
        }
    }

    private fun observeStaffAndBranches(businessId: String) {
        staffJob?.cancel()
        staffJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            repository.getStaffWithFleetFlow(businessId).collect { staffList ->
                _uiState.update {
                    it.copy(
                        staffList = staffList,
                        isLoading = false
                    )
                }
            }
        }

        branchJob?.cancel()
        branchJob = viewModelScope.launch(Dispatchers.IO) {
            repository.getBranchesFlow(businessId).collect { branchList ->
                _uiState.update { it.copy(branches = branchList) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedRoleFilter(role: String) {
        _uiState.update { it.copy(selectedRoleFilter = role) }
    }

    fun setSelectedBranchFilter(branchId: String) {
        _uiState.update { it.copy(selectedBranchFilter = branchId) }
    }

    fun openAddStaff() {
        _uiState.update {
            it.copy(
                isStaffDrawerOpen = true,
                staffForEdit = null,
                verifiedStaffUser = null,
                isSearchingUser = false,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun openEditStaff(staff: StaffMemberUiModel) {
        _uiState.update {
            it.copy(
                isStaffDrawerOpen = true,
                staffForEdit = staff,
                verifiedStaffUser = if (!staff.staff.userId.isNullOrBlank()) {
                    com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto(
                        id = staff.staff.userId,
                        fullName = staff.name,
                        username = staff.staff.username
                    )
                } else null,
                isSearchingUser = false,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun closeStaffDrawer() {
        _uiState.update {
            it.copy(
                isStaffDrawerOpen = false,
                staffForEdit = null,
                verifiedStaffUser = null,
                isSearchingUser = false,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    private var searchUserJob: Job? = null

    fun searchUsername(query: String) {
        val clean = query.trim().removePrefix("@")
        searchUserJob?.cancel()

        if (clean.length < 2) {
            _uiState.update { it.copy(verifiedStaffUser = null, isSearchingUser = false) }
            return
        }

        searchUserJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSearchingUser = true) }
            kotlinx.coroutines.delay(400)
            val result = repository.searchUserByUsername(clean)
            val user = result.getOrNull()
            _uiState.update {
                it.copy(
                    verifiedStaffUser = user,
                    isSearchingUser = false
                )
            }
        }
    }

    fun openBranchModal() {
        _uiState.update { it.copy(isBranchModalOpen = true, errorMessage = null, successMessage = null) }
    }

    fun closeBranchModal() {
        _uiState.update { it.copy(isBranchModalOpen = false, errorMessage = null, successMessage = null) }
    }

    fun openSettlementModal(staff: StaffMemberUiModel) {
        _uiState.update {
            it.copy(
                isSettlementModalOpen = true,
                riderForSettlement = staff,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun closeSettlementModal() {
        _uiState.update {
            it.copy(
                isSettlementModalOpen = false,
                riderForSettlement = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun saveBranch(
        branchName: String,
        addressLine: String,
        city: String,
        state: String,
        pincode: String,
        lat: Double?,
        lng: Double?,
        isMainBranch: Boolean
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business workspace not active.") }
            return
        }

        if (branchName.isBlank() || addressLine.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Branch name and street address are required.") }
            return
        }

        val nowIso = Instant.now().toString()
        val branchEntity = BusinessBranchEntity(
            id = UUID.randomUUID().toString(),
            businessId = businessId,
            branchName = branchName.trim(),
            addressLine = addressLine.trim(),
            city = city.trim().ifBlank { "Jaipur" },
            state = state.trim().ifBlank { "Rajasthan" },
            pincode = pincode.trim().ifBlank { "302001" },
            lat = lat,
            lng = lng,
            isMainBranch = isMainBranch,
            isActive = true,
            isDeleted = false,
            createdAt = nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.saveBranch(branchEntity)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Branch outlet added successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to save branch."
                    )
                }
            }
        }
    }

    fun saveStaffMember(
        name: String,
        phone: String,
        email: String?,
        branchId: String,
        role: String,
        username: String?,
        userId: String?,
        vehicleType: String?,
        vehicleNumber: String?
    ) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business workspace not active.") }
            return
        }

        if (name.isBlank() || phone.isBlank() || phone.length < 10) {
            _uiState.update { it.copy(errorMessage = "Valid staff name and 10-digit phone are required.") }
            return
        }

        val editing = _uiState.value.staffForEdit
        val nowIso = Instant.now().toString()

        val staffEntity = BusinessStaffEntity(
            id = editing?.id ?: UUID.randomUUID().toString(),
            businessId = businessId,
            branchId = branchId.ifBlank { _uiState.value.branches.firstOrNull()?.id ?: "" },
            userId = userId,
            username = username?.trim()?.removePrefix("@"),
            name = name.trim(),
            phone = phone.trim(),
            email = email?.trim()?.ifBlank { null },
            role = role,
            isActive = true,
            isDeleted = false,
            createdAt = editing?.staff?.createdAt ?: nowIso,
            updatedAt = nowIso
        )

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.saveStaffMember(staffEntity, vehicleType, vehicleNumber)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isStaffDrawerOpen = false,
                        staffForEdit = null,
                        verifiedStaffUser = null,
                        successMessage = "Staff member saved successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to save staff member."
                    )
                }
            }
        }
    }

    fun deleteStaff(staffId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.deleteStaffMember(staffId)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        successMessage = "Staff member removed."
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to remove staff."
                    )
                }
            }
        }
    }

    fun toggleRiderDuty(riderId: String, currentOnline: Boolean) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank() || riderId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleRiderDuty(businessId, riderId, !currentOnline)
        }
    }

    fun settleRiderCash(riderId: String) {
        val businessId = _uiState.value.businessId
        if (businessId.isBlank() || riderId.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = repository.settleRiderCash(businessId, riderId)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSettlementModalOpen = false,
                        riderForSettlement = null,
                        successMessage = "Cash deposited to shop drawer successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = err.message ?: "Failed to settle cash."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
