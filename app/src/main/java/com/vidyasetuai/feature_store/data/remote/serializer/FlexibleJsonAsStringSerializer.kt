package com.vidyasetuai.feature_store.data.remote.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * Flexible serializer that safely handles JSON fields coming from PostgreSQL/Supabase.
 * Whether Supabase returns a JSON string `"..."`, a raw JSON object `{...}`, or a JSON array `[...]`,
 * this serializer converts it safely into a String? without throwing JsonDecodingException.
 */
object FlexibleJsonAsStringSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleJsonAsStringSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value)
        }
    }

    override fun deserialize(decoder: Decoder): String? {
        val jsonDecoder = decoder as? JsonDecoder ?: return decoder.decodeString()
        val element = jsonDecoder.decodeJsonElement()
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> if (element.isString) element.content else element.toString()
            else -> element.toString()
        }
    }
}
