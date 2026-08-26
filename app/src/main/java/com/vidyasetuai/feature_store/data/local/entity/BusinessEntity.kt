package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String,

    @SerialName("business_type")
    @ColumnInfo(name = "business_type")
    val businessType: String,

    @SerialName("business_tier")
    @ColumnInfo(name = "business_tier")
    val businessTier: String = "MICRO",

    @SerialName("legal_name")
    @ColumnInfo(name = "legal_name")
    val legalName: String,

    @SerialName("trade_name")
    @ColumnInfo(name = "trade_name")
    val tradeName: String,

    @SerialName("slug")
    @ColumnInfo(name = "slug")
    val slug: String,

    @SerialName("owner_name")
    @ColumnInfo(name = "owner_name")
    val ownerName: String,

    @SerialName("phone")
    @ColumnInfo(name = "phone")
    val phone: String,

    @SerialName("email")
    @ColumnInfo(name = "email")
    val email: String? = null,

    @SerialName("gstin")
    @ColumnInfo(name = "gstin")
    val gstin: String? = null,

    @SerialName("is_composition")
    @ColumnInfo(name = "is_composition")
    val isComposition: Boolean = false,

    @SerialName("pan_number")
    @ColumnInfo(name = "pan_number")
    val panNumber: String? = null,

    @SerialName("currency")
    @ColumnInfo(name = "currency")
    val currency: String = "INR",

    @SerialName("state_code")
    @ColumnInfo(name = "state_code")
    val stateCode: String = "08",

    @SerialName("bank_name")
    @ColumnInfo(name = "bank_name")
    val bankName: String? = null,

    @SerialName("bank_account_no")
    @ColumnInfo(name = "bank_account_no")
    val bankAccountNo: String? = null,

    @SerialName("bank_ifsc")
    @ColumnInfo(name = "bank_ifsc")
    val bankIfsc: String? = null,

    @SerialName("bank_branch")
    @ColumnInfo(name = "bank_branch")
    val bankBranch: String? = null,

    @SerialName("upi_id")
    @ColumnInfo(name = "upi_id")
    val upiId: String? = null,

    @SerialName("logo_url")
    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @SerialName("banner_url")
    @ColumnInfo(name = "banner_url")
    val bannerUrl: String? = null,

    @SerialName("description")
    @ColumnInfo(name = "description")
    val description: String? = null,

    @SerialName("is_active")
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @SerialName("is_deleted")
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @SerialName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @SerialName("updated_by")
    @ColumnInfo(name = "updated_by")
    val updatedBy: String? = null,

    @SerialName("sync_version")
    @ColumnInfo(name = "sync_version")
    val syncVersion: Long = 1,

    @SerialName("sync_status")
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SYNCED"
)
