package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vidyasetuai.feature_institution.domain.model.ContentFeedItem

// Supabase table: organization_contents
@Entity(tableName = "local_content_feed_items")
data class LocalContentFeedItemEntity(
    @PrimaryKey val id: String,
    val organizationId: String?,
    val parentOrganizationId: String?,
    val sessionId: String = "",
    val contentType: String,
    val publisherType: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val localImagePath: String? = null,      // Downloaded image local path
    val targetScope: String = "",
    val targetRoles: String = "",           // Stored as JSON string (ARRAY)
    val status: String = "active",
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdBy: String? = null,
    val updatedBy: String? = null,
    val createdAt: String,
    val updatedAt: String = "",
    val publisherName: String? = null       // Denormalized for display
) {
    fun toDomain(): ContentFeedItem = ContentFeedItem(
        id = id,
        contentType = contentType,
        publisherType = publisherType,
        publisherName = publisherName ?: "",
        title = title,
        description = description,
        imageUrl = imageUrl,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(domain: ContentFeedItem, orgId: String, sessionId: String): LocalContentFeedItemEntity =
            LocalContentFeedItemEntity(
                id = domain.id,
                organizationId = orgId,
                parentOrganizationId = null,
                sessionId = sessionId,
                contentType = domain.contentType,
                publisherType = domain.publisherType,
                publisherName = domain.publisherName,
                title = domain.title,
                description = domain.description,
                imageUrl = domain.imageUrl,
                createdAt = domain.createdAt
            )
    }
}

