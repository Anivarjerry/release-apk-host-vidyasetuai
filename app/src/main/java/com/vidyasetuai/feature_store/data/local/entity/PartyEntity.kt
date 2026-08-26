package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("party_type")
    @ColumnInfo(name = "party_type")
    val partyType: String,

    @SerialName("name")
    @ColumnInfo(name = "name")
    val name: String,

    @SerialName("phone")
    @ColumnInfo(name = "phone")
    val phone: String,

    @SerialName("email")
    @ColumnInfo(name = "email")
    val email: String? = null,

    @SerialName("gstin")
    @ColumnInfo(name = "gstin")
    val gstin: String? = null,

    @SerialName("pan_number")
    @ColumnInfo(name = "pan_number")
    val panNumber: String? = null,

    @SerialName("state_code")
    @ColumnInfo(name = "state_code")
    val stateCode: String? = null,

    @SerialName("credit_limit")
    @ColumnInfo(name = "credit_limit")
    val creditLimit: Double = 0.0,

    @SerialName("current_balance")
    @ColumnInfo(name = "current_balance")
    val currentBalance: Double = 0.0,

    @SerialName("opening_balance")
    @ColumnInfo(name = "opening_balance")
    val openingBalance: Double = 0.0,

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
