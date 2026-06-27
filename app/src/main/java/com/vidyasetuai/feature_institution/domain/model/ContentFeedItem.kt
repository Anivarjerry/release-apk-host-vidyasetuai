package com.vidyasetuai.feature_institution.domain.model

data class ContentFeedItem(
    val id: String,
    val contentType: String, // "notice" or "gallery"
    val publisherType: String, // "parent" or "child"
    val publisherName: String, // Name of the organization or parent organization
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val createdAt: String
)
