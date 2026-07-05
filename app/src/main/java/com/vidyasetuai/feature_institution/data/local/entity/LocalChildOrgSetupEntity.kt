package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_child_org_setups")
data class LocalChildOrgSetupEntity(
    @PrimaryKey
    @ColumnInfo(name = "organization_id")
    val organizationId: String,

    @ColumnInfo(name = "organization_name")
    val organizationName: String?,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "mobile_number")
    val mobileNumber: String?,

    @ColumnInfo(name = "alternate_mobile_number")
    val alternateMobileNumber: String?,

    @ColumnInfo(name = "address_line1")
    val addressLine1: String?,

    @ColumnInfo(name = "address_line2")
    val addressLine2: String?,

    @ColumnInfo(name = "city")
    val city: String?,

    @ColumnInfo(name = "state")
    val state: String?,

    @ColumnInfo(name = "pincode")
    val pincode: String?,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "session_name")
    val sessionName: String,

    @ColumnInfo(name = "boards_json")
    val boardsJson: String,

    @ColumnInfo(name = "mediums_json")
    val mediumsJson: String,

    @ColumnInfo(name = "languages_json")
    val languagesJson: String,

    @ColumnInfo(name = "class_structure_json")
    val classStructureJson: String,

    @ColumnInfo(name = "periods_json")
    val periodsJson: String,

    @ColumnInfo(name = "fees_structure_json")
    val feesStructureJson: String,

    @ColumnInfo(name = "is_setup_complete")
    val isSetupComplete: Boolean,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,

    @ColumnInfo(name = "sync_state")
    val syncState: String // SYNCED, PENDING_UPDATE
)
