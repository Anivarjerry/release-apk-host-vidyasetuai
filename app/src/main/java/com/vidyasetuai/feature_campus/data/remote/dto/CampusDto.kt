package com.vidyasetuai.feature_campus.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class RoomDto(
    val id: String,
    val name: String,
    val category: String? = null,
    val description: String? = null,
    @SerialName("message_cooldown_seconds")
    val messageCooldownSeconds: Int = 5,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("room_id")
    val roomId: String,
    @SerialName("user_id")
    val userId: String,
    val content: String,
    @SerialName("is_hidden")
    val isHidden: Boolean = false,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

object BlockedKeywordsSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BlockedKeywords", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val jsonDecoder = decoder as? JsonDecoder ?: return emptyList()
        val element = jsonDecoder.decodeJsonElement()
        return try {
            if (element is JsonPrimitive) {
                val jsonStr = element.jsonPrimitive.content
                Json.decodeFromString<List<String>>(jsonStr)
            } else if (element is JsonArray) {
                element.jsonArray.map { it.jsonPrimitive.content }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        encoder.encodeSerializableValue(JsonArray.serializer(), JsonArray(value.map { JsonPrimitive(it) }))
    }
}

@Serializable
data class ModerationSettingsDto(
    val id: Int,
    @SerialName("blocked_keywords")
    @Serializable(with = BlockedKeywordsSerializer::class)
    val blockedKeywords: List<String>,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class ReportDto(
    val id: String? = null,
    @SerialName("message_id")
    val messageId: String,
    @SerialName("reporter_user_id")
    val reporterUserId: String,
    val reason: String,
    val status: String = "pending"
)