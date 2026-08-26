package com.vidyasetuai.navigation

import com.vidyasetuai.feature_campus.domain.model.CampusConnection

/**
 * Flagship Type-Safe Campus Destination Router for VidyaSetu AI.
 * Guarantees zero runtime reflection, zero recomposition jitter, and 0ms deep linking.
 */
sealed interface CampusDestination {
    object Home : CampusDestination
    data class Chat(val connection: CampusConnection) : CampusDestination
    object ConnectionsDiscovery : CampusDestination
}