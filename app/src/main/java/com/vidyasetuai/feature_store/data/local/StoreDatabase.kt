package com.vidyasetuai.feature_store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vidyasetuai.feature_store.data.local.dao.BusinessDao
import com.vidyasetuai.feature_store.data.local.dao.BusinessSettingsDao
import com.vidyasetuai.feature_store.data.local.dao.BusinessBranchDao
import com.vidyasetuai.feature_store.data.local.dao.BusinessStaffDao
import com.vidyasetuai.feature_store.data.local.dao.BusinessStaffSalaryDao
import com.vidyasetuai.feature_store.data.local.dao.PartyDao
import com.vidyasetuai.feature_store.data.local.dao.PartyAddressDao
import com.vidyasetuai.feature_store.data.local.dao.ItemCategoryDao
import com.vidyasetuai.feature_store.data.local.dao.ItemDao
import com.vidyasetuai.feature_store.data.local.dao.ItemVariantDao
import com.vidyasetuai.feature_store.data.local.dao.InventoryStockDao
import com.vidyasetuai.feature_store.data.local.dao.InventoryTransactionDao
import com.vidyasetuai.feature_store.data.local.dao.OrderDao
import com.vidyasetuai.feature_store.data.local.dao.OrderItemDao
import com.vidyasetuai.feature_store.data.local.dao.DeliveryRiderDao
import com.vidyasetuai.feature_store.data.local.dao.OrderDeliveryDao
import com.vidyasetuai.feature_store.data.local.dao.InvoiceDao
import com.vidyasetuai.feature_store.data.local.dao.InvoiceItemDao
import com.vidyasetuai.feature_store.data.local.dao.PurchaseInvoiceDao
import com.vidyasetuai.feature_store.data.local.dao.PaymentDao
import com.vidyasetuai.feature_store.data.local.dao.PartyLedgerDao

import com.vidyasetuai.feature_store.data.local.entity.BusinessEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessSettingsEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessBranchEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyAddressEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemCategoryEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemEntity
import com.vidyasetuai.feature_store.data.local.entity.ItemVariantEntity
import com.vidyasetuai.feature_store.data.local.entity.InventoryStockEntity
import com.vidyasetuai.feature_store.data.local.entity.InventoryTransactionEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderItemEntity
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderDeliveryEntity
import com.vidyasetuai.feature_store.data.local.entity.InvoiceEntity
import com.vidyasetuai.feature_store.data.local.entity.InvoiceItemEntity
import com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceEntity
import com.vidyasetuai.feature_store.data.local.entity.PaymentEntity
import com.vidyasetuai.feature_store.data.local.entity.PartyLedgerEntryEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryProfileEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffAttendanceEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryPayoutEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessStaffSalaryPaymentEntity

import com.vidyasetuai.feature_store.data.local.dao.BusinessExpenseDao
import com.vidyasetuai.feature_store.data.local.entity.BusinessExpenseEntity
import com.vidyasetuai.feature_store.data.local.entity.BusinessExpenseTypeEntity
import com.vidyasetuai.feature_store.data.local.entity.PurchaseInvoiceItemEntity

@Database(
    entities = [
        BusinessEntity::class,
        BusinessSettingsEntity::class,
        BusinessBranchEntity::class,
        BusinessStaffEntity::class,
        PartyEntity::class,
        PartyAddressEntity::class,
        ItemCategoryEntity::class,
        ItemEntity::class,
        ItemVariantEntity::class,
        InventoryStockEntity::class,
        InventoryTransactionEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        DeliveryRiderEntity::class,
        OrderDeliveryEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        PurchaseInvoiceEntity::class,
        PurchaseInvoiceItemEntity::class,
        PaymentEntity::class,
        PartyLedgerEntryEntity::class,
        BusinessStaffSalaryProfileEntity::class,
        BusinessStaffAttendanceEntity::class,
        BusinessStaffSalaryPayoutEntity::class,
        BusinessStaffSalaryPaymentEntity::class,
        BusinessExpenseEntity::class,
        BusinessExpenseTypeEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun businessSettingsDao(): BusinessSettingsDao
    abstract fun businessBranchDao(): BusinessBranchDao
    abstract fun businessStaffDao(): BusinessStaffDao
    abstract fun businessStaffSalaryDao(): BusinessStaffSalaryDao
    abstract fun partyDao(): PartyDao
    abstract fun partyAddressDao(): PartyAddressDao
    abstract fun itemCategoryDao(): ItemCategoryDao
    abstract fun itemDao(): ItemDao
    abstract fun itemVariantDao(): ItemVariantDao
    abstract fun inventoryStockDao(): InventoryStockDao
    abstract fun inventoryTransactionDao(): InventoryTransactionDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun deliveryRiderDao(): DeliveryRiderDao
    abstract fun orderDeliveryDao(): OrderDeliveryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun purchaseInvoiceDao(): PurchaseInvoiceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun partyLedgerDao(): PartyLedgerDao
    abstract fun businessExpenseDao(): BusinessExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: StoreDatabase? = null

        fun getDatabase(context: Context): StoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StoreDatabase::class.java,
                    "store_database.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
