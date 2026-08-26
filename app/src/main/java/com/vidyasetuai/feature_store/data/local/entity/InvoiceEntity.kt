package com.vidyasetuai.feature_store.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey
    @SerialName("id")
    @ColumnInfo(name = "id")
    val id: String,

    @SerialName("business_id")
    @ColumnInfo(name = "business_id")
    val businessId: String,

    @SerialName("branch_id")
    @ColumnInfo(name = "branch_id")
    val branchId: String,

    @SerialName("order_id")
    @ColumnInfo(name = "order_id")
    val orderId: String? = null,

    @SerialName("party_id")
    @ColumnInfo(name = "party_id")
    val partyId: String? = null,

    @SerialName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @SerialName("invoice_type")
    @ColumnInfo(name = "invoice_type")
    val invoiceType: String = "TAX_INVOICE",

    @SerialName("invoice_number")
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,

    @SerialName("invoice_date")
    @ColumnInfo(name = "invoice_date")
    val invoiceDate: String,

    @SerialName("due_date")
    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,

    @SerialName("place_of_supply")
    @ColumnInfo(name = "place_of_supply")
    val placeOfSupply: String = "08",

    @SerialName("is_interstate")
    @ColumnInfo(name = "is_interstate")
    val isInterstate: Boolean = false,

    @SerialName("eway_bill_number")
    @ColumnInfo(name = "eway_bill_number")
    val ewayBillNumber: String? = null,

    @SerialName("eway_bill_date")
    @ColumnInfo(name = "eway_bill_date")
    val ewayBillDate: String? = null,

    @SerialName("eway_bill_valid_until")
    @ColumnInfo(name = "eway_bill_valid_until")
    val ewayBillValidUntil: String? = null,

    @SerialName("vehicle_number")
    @ColumnInfo(name = "vehicle_number")
    val vehicleNumber: String? = null,

    @SerialName("transport_name")
    @ColumnInfo(name = "transport_name")
    val transportName: String? = null,

    @SerialName("transporter_id")
    @ColumnInfo(name = "transporter_id")
    val transporterId: String? = null,

    @SerialName("distance_km")
    @ColumnInfo(name = "distance_km")
    val distanceKm: Int = 0,

    @SerialName("eway_status")
    @ColumnInfo(name = "eway_status")
    val ewayStatus: String = "NOT_GENERATED",

    @SerialName("taxable_amount")
    @ColumnInfo(name = "taxable_amount")
    val taxableAmount: Double = 0.0,

    @SerialName("cgst_amount")
    @ColumnInfo(name = "cgst_amount")
    val cgstAmount: Double = 0.0,

    @SerialName("sgst_amount")
    @ColumnInfo(name = "sgst_amount")
    val sgstAmount: Double = 0.0,

    @SerialName("igst_amount")
    @ColumnInfo(name = "igst_amount")
    val igstAmount: Double = 0.0,

    @SerialName("cess_amount")
    @ColumnInfo(name = "cess_amount")
    val cessAmount: Double = 0.0,

    @SerialName("delivery_charge")
    @ColumnInfo(name = "delivery_charge")
    val deliveryCharge: Double = 0.0,

    @SerialName("packing_charge")
    @ColumnInfo(name = "packing_charge")
    val packingCharge: Double = 0.0,

    @SerialName("discount_total")
    @ColumnInfo(name = "discount_total")
    val discountTotal: Double = 0.0,

    @SerialName("round_off")
    @ColumnInfo(name = "round_off")
    val roundOff: Double = 0.0,

    @SerialName("grand_total")
    @ColumnInfo(name = "grand_total")
    val grandTotal: Double = 0.0,

    @SerialName("paid_amount")
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Double = 0.0,

    @SerialName("due_amount")
    @ColumnInfo(name = "due_amount")
    val dueAmount: Double = 0.0,

    @SerialName("payment_status")
    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "UNPAID",

    @SerialName("notes")
    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @SerialName("terms_conditions")
    @ColumnInfo(name = "terms_conditions")
    val termsConditions: String? = null,

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
