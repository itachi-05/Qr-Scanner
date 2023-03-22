package com.alpharays.qrscanner.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_history_table")
data class QrHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val about: String = "",
    val imageData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QrHistory

        if (id != other.id) return false
        if (!imageData.contentEquals(other.imageData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + imageData.contentHashCode()
        return result
    }
}