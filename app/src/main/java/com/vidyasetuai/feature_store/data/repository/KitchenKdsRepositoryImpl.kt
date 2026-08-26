package com.vidyasetuai.feature_store.data.repository

import android.content.Context
import com.vidyasetuai.feature_store.data.local.StoreDatabase
import com.vidyasetuai.feature_store.data.local.entity.DeliveryRiderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderEntity
import com.vidyasetuai.feature_store.data.local.entity.OrderItemEntity
import com.vidyasetuai.feature_store.data.remote.datasource.StoreRemoteDataSource
import com.vidyasetuai.feature_store.domain.model.KitchenKdsUiModel
import com.vidyasetuai.feature_store.domain.repository.KitchenKdsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

class KitchenKdsRepositoryImpl(
    context: Context,
    private val database: StoreDatabase = StoreDatabase.getDatabase(context),
    private val remoteDataSource: StoreRemoteDataSource = StoreRemoteDataSource()
) : KitchenKdsRepository {

    private val orderDao = database.orderDao()
    private val orderItemDao = database.orderItemDao()
    private val deliveryRiderDao = database.deliveryRiderDao()

    override fun getActiveKdsTicketsFlow(businessId: String): Flow<List<KitchenKdsUiModel>> {
        val ordersFlow = orderDao.getAllLiveKdsOrdersFlow(businessId)
        val itemsFlow = orderItemDao.getAllOrderItemsFlow()

        return combine(ordersFlow, itemsFlow) { orders, allItems ->
            val itemsGrouped = allItems.groupBy { it.orderId }
            val nowMillis = System.currentTimeMillis()

            orders.map { order ->
                val elapsedMinutes = calculateElapsedMinutes(order.createdAt, nowMillis)
                val items = itemsGrouped[order.id] ?: emptyList()
                val isUrgent = elapsedMinutes >= 15 && (order.orderStatus.equals("NEW", true) || order.orderStatus.equals("PLACED", true))

                KitchenKdsUiModel(
                    order = order,
                    items = items,
                    elapsedMinutes = elapsedMinutes,
                    isUrgent = isUrgent
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override fun getServedTicketsFlow(businessId: String): Flow<List<KitchenKdsUiModel>> {
        val servedOrdersFlow = orderDao.getServedOrdersFlow(businessId)
        val itemsFlow = orderItemDao.getAllOrderItemsFlow()

        return combine(servedOrdersFlow, itemsFlow) { orders, allItems ->
            val itemsGrouped = allItems.groupBy { it.orderId }
            val nowMillis = System.currentTimeMillis()

            orders.map { order ->
                val elapsedMinutes = calculateElapsedMinutes(order.createdAt, nowMillis)
                val items = itemsGrouped[order.id] ?: emptyList()

                KitchenKdsUiModel(
                    order = order,
                    items = items,
                    elapsedMinutes = elapsedMinutes,
                    isUrgent = false
                )
            }
        }.flowOn(Dispatchers.Default)
    }

    override fun getDeliveryRidersFlow(businessId: String): Flow<List<DeliveryRiderEntity>> {
        return if (businessId.isNotBlank()) {
            deliveryRiderDao.getRidersFlow(businessId)
        } else {
            deliveryRiderDao.getAllRidersFlow()
        }
    }

    override suspend fun updateOrderStatus(
        businessId: String,
        orderId: String,
        newStatus: String,
        riderId: String?,
        otpCode: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existingOrder = orderDao.getOrderById(orderId)
            val effectiveBizId = if (businessId.isNotBlank()) businessId else (existingOrder?.businessId ?: "")

            // 1. Direct Supabase RPC Call first
            if (effectiveBizId.isNotBlank()) {
                remoteDataSource.updateKdsOrderStatus(
                    businessId = effectiveBizId,
                    orderId = orderId,
                    newStatus = newStatus,
                    riderId = riderId,
                    otpCode = otpCode
                )
            }

            // 2. Local Room DB Update on remote success
            if (existingOrder != null) {
                val nowIso = Instant.now().toString()
                val updatedOrder = existingOrder.copy(
                    orderStatus = newStatus,
                    updatedAt = nowIso
                )
                orderDao.upsertOrder(updatedOrder)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateElapsedMinutes(createdAtStr: String?, nowMillis: Long): Int {
        if (createdAtStr.isNullOrBlank()) return 0

        val epochMillis = parseEpochMillis(createdAtStr) ?: return 0
        val diffMinutes = (nowMillis - epochMillis) / (60 * 1000)
        return diffMinutes.toInt().coerceAtLeast(0)
    }

    private fun parseEpochMillis(dateStr: String): Long? {
        val trimmed = dateStr.trim()

        // 1. Direct Long epoch check
        trimmed.toLongOrNull()?.let { return it }

        // 2. Instant parse (e.g. 2026-08-17T18:14:22Z)
        try {
            return Instant.parse(trimmed).toEpochMilli()
        } catch (_: Exception) {}

        // 3. OffsetDateTime parse (e.g. 2026-08-17T18:14:22.123456+05:30)
        try {
            return OffsetDateTime.parse(trimmed).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 4. ZonedDateTime parse
        try {
            return ZonedDateTime.parse(trimmed).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 5. LocalDateTime parse (e.g. 2026-08-17 18:14:22 or 2026-08-17T18:14:22)
        try {
            val normalized = trimmed.replace(" ", "T")
            val cleanStr = normalized.substringBefore("+").substringBefore("Z")
            val ldt = LocalDateTime.parse(cleanStr)
            return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 6. SimpleDateFormat fallbacks
        val patterns = listOf(
            "yyyy-MM-dd HH:mm:ss.SSSSSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(trimmed)
                if (date != null) return date.time
            } catch (_: Exception) {}
        }

        return null
    }
}
