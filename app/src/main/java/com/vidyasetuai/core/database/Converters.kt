package com.vidyasetuai.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String): List<String> {
        return try {
            Json.decodeFromString<List<String>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromFloatList(value: List<Float>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    @JvmStatic
    fun toFloatList(value: String): List<Float> {
        return try {
            Json.decodeFromString<List<Float>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}