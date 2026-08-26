package com.vidyasetuai.feature_store.presentation.screen.role_staff.staff

import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.domain.model.StaffMemberUiModel

data class StaffFleetUiState(
    val businessId: String = "",
    val staffList: List<StaffMemberUiModel> = emptyList(),
    val branches: List<BusinessBranchEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedRoleFilter: String = "ALL",
    val selectedBranchFilter: String = "ALL",
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Modal & Sheet States
    val isStaffDrawerOpen: Boolean = false,
    val staffForEdit: StaffMemberUiModel? = null,

    val isBranchModalOpen: Boolean = false,
    val isSettlementModalOpen: Boolean = false,
    val riderForSettlement: StaffMemberUiModel? = null,

    // Linked App Account Search
    val verifiedStaffUser: com.vidyasetuai.feature_store.data.remote.dto.StaffLinkedUserDto? = null,
    val isSearchingUser: Boolean = false
) {
    val filteredStaff: List<StaffMemberUiModel>
        get() {
            return staffList.filter { staff ->
                val q = searchQuery.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                        staff.name.lowercase().contains(q) ||
                        staff.phone.contains(q) ||
                        (staff.vehicleNumber?.lowercase()?.contains(q) == true)

                val matchesRole = selectedRoleFilter == "ALL" || staff.role == selectedRoleFilter
                val matchesBranch = selectedBranchFilter == "ALL" || staff.staff.branchId == selectedBranchFilter

                matchesQuery && matchesRole && matchesBranch
            }
        }

    val totalStaff: Int get() = staffList.size
    val deliveryRiders: List<StaffMemberUiModel> get() = staffList.filter { it.role == "DELIVERY_RIDER" }
    val onDutyRidersCount: Int get() = deliveryRiders.count { it.isOnline }
    val totalBranches: Int get() = branches.size
    val totalPendingCash: Double get() = deliveryRiders.sumOf { it.unsettledCodCash }
}
