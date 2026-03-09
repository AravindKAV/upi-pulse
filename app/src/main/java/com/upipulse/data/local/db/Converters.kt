package com.upipulse.data.local.db

import androidx.room.TypeConverter
import com.upipulse.domain.model.TransactionDirection
import com.upipulse.domain.model.TransactionSource
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromDirection(value: TransactionDirection?): String? = value?.name

    @TypeConverter
    fun toDirection(value: String?): TransactionDirection? =
        value?.let { TransactionDirection.valueOf(it) }

    @TypeConverter
    fun fromSource(value: TransactionSource?): String? = value?.name

    @TypeConverter
    fun toSource(value: String?): TransactionSource? =
        value?.let { TransactionSource.valueOf(it) }

    @TypeConverter
    fun fromMetadata(value: Map<String, String>?): String? =
        value?.entries?.joinToString(separator = "||") { (key, v) ->
            key.replace("||", "\\|\\|") + "==" + v.replace("||", "\\|\\|")
        }

    @TypeConverter
    fun toMetadata(value: String?): Map<String, String> = value
        ?.takeIf { it.isNotEmpty() }
        ?.split("||")
        ?.mapNotNull { pair ->
            val (key, v) = pair.split("==", limit = 2).let {
                if (it.size == 2) it[0] to it[1] else return@mapNotNull null
            }
            key.replace("\\|\\|", "||") to v.replace("\\|\\|", "||")
        }
        ?.toMap()
        ?: emptyMap()
}