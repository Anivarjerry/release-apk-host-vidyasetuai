package com.vidyasetuai.feature_store.presentation.screen.role_staff.kds

import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.domain.model.KdsFilterTab
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel

/**
 * UI State for Kitchen Display System (KDS) Screen.
 */
data class KitchenKdsUiState(
    val activeTickets: List<KitchenKdsUiModel> = emptyList(),
    val servedTickets: List<KitchenKdsUiModel> = emptyList(),
    val availableRiders: List<DeliveryRiderEntity> = emptyList(),
    val selectedTicketForRiderAssignment: KitchenKdsUiModel? = null,
    val activeFilterTab: KdsFilterTab = KdsFilterTab.ALL_ACTIVE,
    val searchQuery: String = "",
    val isSoundEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val displayedTickets: List<KitchenKdsUiModel>
        get() {
            val list = when (activeFilterTab) {
                KdsFilterTab.ALL_ACTIVE -> activeTickets
                KdsFilterTab.NEW -> activeTickets.filter { it.isNew }
                KdsFilterTab.PREPARING -> activeTickets.filter { it.isPreparing }
                KdsFilterTab.READY -> activeTickets.filter { it.isReady }
                KdsFilterTab.SERVED_TODAY -> servedTickets
            }

            if (searchQuery.isBlank()) return list
            return list.filter { ticket ->
                ticket.order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                (ticket.order.tableOrTokenNo?.contains(searchQuery, ignoreCase = true) == true) ||
                ticket.order.customerName.contains(searchQuery, ignoreCase = true) ||
                ticket.items.any { it.itemName.contains(searchQuery, ignoreCase = true) }
            }
        }

    val newCount: Int get() = activeTickets.count { it.isNew }
    val preparingCount: Int get() = activeTickets.count { it.isPreparing }
    val readyCount: Int get() = activeTickets.count { it.isReady }
    val servedCount: Int get() = servedTickets.size
}
