package com.vidyasetuai.feature_campus.data.remote.datasource

import com.vidyasetuai.core.network.SupabaseClient
import com.vidyasetuai.feature_campus.data.remote.dto.MessageDto
import com.vidyasetuai.feature_campus.data.remote.dto.ModerationSettingsDto
import com.vidyasetuai.feature_campus.data.remote.dto.RoomDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.presenceDataFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.track
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf

@Serializable
data class PresenceUser(
    @SerialName("user_id")
    val userId: String
)

class CampusRemoteDataSource {

    companion object {
        val lastMessagesStatus = mutableStateOf("INITIAL")
        val lastPresenceStatus = mutableStateOf("INITIAL")
        val lastMessagesError = mutableStateOf<String?>(null)
        val lastPresenceError = mutableStateOf<String?>(null)
        val globalRealtimeStatus = mutableStateOf("INITIAL")
        
        // Live scrollable log list for diagnostic interface
        val realtimeLogs = mutableStateListOf<String>()

        fun addLog(message: String) {
            try {
                val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US)
                val time = sdf.format(java.util.Date())
                realtimeLogs.add("[$time] $message")
            } catch (e: Exception) {
                realtimeLogs.add(message)
            }
            if (realtimeLogs.size > 80) {
                realtimeLogs.removeAt(0)
            }
        }
    }

    init {
        addLog("CampusRemoteDataSource init")
        try {
            val url = SupabaseClient.client.supabaseUrl
            val host = java.net.URI(url).host ?: "unknown"
            addLog("Supabase Host: $host")
        } catch (e: Exception) {
            addLog("Host parse error: ${e.message}")
        }
        
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            try {
                SupabaseClient.client.realtime.status.collect { status ->
                    globalRealtimeStatus.value = status.name
                    addLog("Global WebSocket status changed to: ${status.name}")
                }
            } catch (e: Exception) {
                addLog("Global status collector failed: ${e.message}")
            }
        }
    }

    suspend fun getRooms(): List<RoomDto> {
        return SupabaseClient.client.from("rooms")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("is_active", true)
                    eq("is_deleted", false)
                }
            }.decodeList()
    }

    suspend fun getMessages(roomId: String): List<MessageDto> {
        return SupabaseClient.client.from("messages")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("room_id", roomId)
                    eq("is_deleted", false)
                    eq("is_hidden", false)
                }
                order("created_at", order = io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }.decodeList()
    }

    suspend fun sendMessage(roomId: String, userId: String, content: String): MessageDto {
        return SupabaseClient.client.from("messages").insert(
            mapOf(
                "room_id" to roomId,
                "user_id" to userId,
                "content" to content
            )
        ) {
            select()
        }.decodeSingle()
    }

    suspend fun getModerationSettings(): ModerationSettingsDto {
        return SupabaseClient.client.from("moderation_settings")
            .select(columns = Columns.raw("*")) {
                filter {
                    eq("id", 1)
                }
            }.decodeSingle()
    }

    suspend fun reportMessage(messageId: String, reporterUserId: String, reason: String) {
        SupabaseClient.client.from("reports").insert(
            mapOf(
                "message_id" to messageId,
                "reporter_user_id" to reporterUserId,
                "reason" to reason,
                "status" to "pending"
            )
        )
    }

    fun subscribeToMessages(roomId: String): Flow<MessageDto> = callbackFlow {
        addLog("subscribeToMessages initiated for room: $roomId")
        val channel = SupabaseClient.client.realtime.channel("room_messages_$roomId")
        addLog("Channel created: 'room_messages_$roomId'")
        
        val job = launch {
            try {
                addLog("Collecting postgresChangeFlow events on 'messages' table...")
                channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "messages"
                }.collect { action ->
                    addLog("Realtime Event: Received PostgresAction ${action::class.java.simpleName}")
                    if (action is PostgresAction.Insert) {
                        try {
                            val recordJson = action.record.toString()
                            val dto = Json.decodeFromString<MessageDto>(recordJson)
                            addLog("Realtime Event Details -> Msg ID: ${dto.id}, Room: ${dto.roomId}, Content preview: ${dto.content.take(15)}")
                            if (dto.roomId == roomId && !dto.isDeleted && !dto.isHidden) {
                                trySend(dto)
                            } else {
                                addLog("Realtime Event filtered out (mismatched room or deleted/hidden)")
                            }
                        } catch (e: Exception) {
                            addLog("Error parsing realtime insert record: ${e.message}")
                            android.util.Log.e("CampusRemoteDS", "Error parsing realtime message", e)
                        }
                    } else {
                        addLog("PostgresAction collected but is not an Insert: ${action::class.java.simpleName}")
                    }
                }
            } catch (e: Exception) {
                addLog("postgresChangeFlow collection failed: ${e.message}")
            }
        }
        
        val statusJob = launch {
            channel.status.collect { status ->
                lastMessagesStatus.value = status.name
                addLog("Messages channel status event: ${status.name}")
            }
        }

        launch {
            try {
                lastMessagesError.value = null
                addLog("Invoking channel.subscribe() for messages...")
                channel.subscribe()
                addLog("channel.subscribe() completed for messages")
            } catch (e: Exception) {
                lastMessagesError.value = "Sub failed: ${e.message}"
                addLog("Subscription call error: ${e.message}")
                android.util.Log.e("CampusRemoteDS", "Realtime channel subscription failed", e)
            }
        }
        
        awaitClose {
            addLog("subscribeToMessages Flow collector is closed. Clean cleanup starting...")
            job.cancel()
            statusJob.cancel()
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    SupabaseClient.client.realtime.removeChannel(channel)
                    addLog("Successfully removed and unsubscribed messages channel")
                } catch (e: Exception) {
                    addLog("Messages remove channel failed: ${e.message}")
                }
            }
        }
    }

    fun subscribeToPresenceCount(roomId: String, userId: String): Flow<Int> = callbackFlow {
        addLog("subscribeToPresenceCount initiated for room: $roomId, user: $userId")
        val channel = SupabaseClient.client.realtime.channel("room_presence_$roomId")
        addLog("Presence channel created: 'room_presence_$roomId'")
        
        val job = launch {
            try {
                addLog("Collecting presenceDataFlow events...")
                channel.presenceDataFlow<PresenceUser>().collect { list ->
                    val uniqueUsersCount = list.map { it.userId }.distinct().size
                    addLog("Presence update received: ${list.size} sessions, $uniqueUsersCount unique users")
                    trySend(uniqueUsersCount)
                }
            } catch (e: Exception) {
                addLog("presenceDataFlow collection failed: ${e.message}")
            }
        }
        
        val statusJob = launch {
            channel.status.collect { status ->
                lastPresenceStatus.value = status.name
                addLog("Presence channel status event: ${status.name}")
                if (status.name == "SUBSCRIBED") {
                    try {
                        lastPresenceError.value = null
                        addLog("Channel subscribed. Performing channel.track(userId: $userId)...")
                        channel.track(PresenceUser(userId))
                        addLog("channel.track() successfully registered for $userId")
                    } catch (e: Exception) {
                        lastPresenceError.value = "Track failed: ${e.message}"
                        addLog("Track call exception: ${e.message}")
                        android.util.Log.e("CampusRemoteDS", "Presence tracking failed", e)
                    }
                }
            }
        }

        launch {
            try {
                lastPresenceError.value = null
                addLog("Invoking channel.subscribe() for presence...")
                channel.subscribe()
                addLog("channel.subscribe() completed for presence")
            } catch (e: Exception) {
                lastPresenceError.value = "Sub failed: ${e.message}"
                addLog("Presence subscription call error: ${e.message}")
                android.util.Log.e("CampusRemoteDS", "Presence channel subscription failed", e)
            }
        }
        
        awaitClose {
            addLog("subscribeToPresenceCount Flow collector is closed. Clean cleanup starting...")
            job.cancel()
            statusJob.cancel()
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    SupabaseClient.client.realtime.removeChannel(channel)
                    addLog("Successfully removed and unsubscribed presence channel")
                } catch (e: Exception) {
                    addLog("Presence remove channel failed: ${e.message}")
                }
            }
        }
    }
}