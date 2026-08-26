package com.vidyasetuai.feature_store.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicProductDto(
    @SerialName("item_id")
    val itemId: String,

    @SerialName("business_id")
    val businessId: String,

    @SerialName("business_slug")
    val businessSlug: String? = null,

    @SerialName("category_id")
    val categoryId: String? = null,

    @SerialName("item_name")
    val itemName: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("unit")
    val unit: String? = "Pcs",

    @SerialName("selling_price")
    val sellingPrice: Double,

    @SerialName("mrp")
    val mrp: Double? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("merchant_name")
    val merchantName: String,

    @SerialName("merchant_logo")
    val merchantLogo: String? = null,

    @SerialName("branch_id")
    val branchId: String? = null,

    @SerialName("branch_name")
    val branchName: String? = null,

    @SerialName("city")
    val city: String? = null,

    @SerialName("lat")
    val lat: Double? = null,

    @SerialName("lng")
    val lng: Double? = null,

    @SerialName("category_name")
    val categoryName: String? = null
)
