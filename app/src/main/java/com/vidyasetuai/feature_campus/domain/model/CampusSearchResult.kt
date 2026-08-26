package com.vidyasetuai.feature_campus.domain.model

enum class CampusConnectionStatus {
    NONE,
    INSPIRED,
    INSPIRES_YOU,
    MUTUAL
}

/**
 * Domain model for Campus User Search Results with bidirectional connection state.
 */
data class CampusSearchResult(
    val userId: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String?,
    val bio: String?,
    val isVerified: Boolean = false,
    val status: CampusConnectionStatus = CampusConnectionStatus.NONE,
    val isActionInProgress: Boolean = false
) {
    val isMutual: Boolean get() = status == CampusConnectionStatus.MUTUAL
    val hasInspired: Boolean get() = status == CampusConnectionStatus.INSPIRED || status == CampusConnectionStatus.MUTUAL
    val canInspireBack: Boolean get() = status == CampusConnectionStatus.INSPIRES_YOU
}
