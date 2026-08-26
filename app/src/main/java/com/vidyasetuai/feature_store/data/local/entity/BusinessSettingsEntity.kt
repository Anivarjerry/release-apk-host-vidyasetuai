package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "business_settings")
data class BusinessSettingsEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("enable_gst_billing")
    @ColumnInfo(name = "enable_gst_billing")
    val enableGstBilling: Boolean = false,

    @SerialName("enable_kds")
    @ColumnInfo(name = "enable_kds")
    val enableKds: Boolean = false,

    @SerialName("enable_delivery_tracking")
    @ColumnInfo(name = "enable_delivery_tracking")
    val enableDeliveryTracking: Boolean = false,

    @SerialName("enable_inventory_tracking")
    @ColumnInfo(name = "enable_inventory_tracking")
    val enableInventoryTracking: Boolean = true,

    @SerialName("enable_customer_khata")
    @ColumnInfo(name = "enable_customer_khata")
    val enableCustomerKhata: Boolean = true,

    @SerialName("allow_online_orders")
    @ColumnInfo(name = "allow_online_orders")
    val allowOnlineOrders: Boolean = true,

    @SerialName("allow_negative_stock")
    @ColumnInfo(name = "allow_negative_stock")
    val allowNegativeStock: Boolean = true,

    @SerialName("invoice_prefix")
    @ColumnInfo(name = "invoice_prefix")
    val invoicePrefix: String = "INV",

    @SerialName("thermal_printer_size")
    @ColumnInfo(name = "thermal_printer_size")
    val thermalPrinterSize: String = "3_INCH",

    @SerialName("delivery_charge_default")
    @ColumnInfo(name = "delivery_charge_default")
    val deliveryChargeDefault: Double = 0.0,

    @SerialName("packing_charge_default")
    @ColumnInfo(name = "packing_charge_default")
    val packingChargeDefault: Double = 0.0,

    @SerialName("free_delivery_above")
    @ColumnInfo(name = "free_delivery_above")
    val freeDeliveryAbove: Double? = null,

    @SerialName("opening_time")
    @ColumnInfo(name = "opening_time")
    val openingTime: String? = null,

    @SerialName("closing_time")
    @ColumnInfo(name = "closing_time")
    val closingTime: String? = null,

    @SerialName("is_store_open")
    @ColumnInfo(name = "is_store_open")
    val isStoreOpen: Boolean = true,

    @SerialName("gst_api_username")
    @ColumnInfo(name = "gst_api_username")
    val gstApiUsername: String? = null,

    @SerialName("gst_api_password")
    @ColumnInfo(name = "gst_api_password")
    val gstApiPassword: String? = null,

    @SerialName("gsp_provider_name")
    @ColumnInfo(name = "gsp_provider_name")
    val gspProviderName: String = "CLEARTAX",

    @SerialName("enable_auto_eway_bill")
    @ColumnInfo(name = "enable_auto_eway_bill")
    val enableAutoEwayBill: Boolean = false,

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
