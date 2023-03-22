package com.alpharays.qrscanner.data.entities

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface QrDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQr(qrHistory: QrHistory)

    @Query("SELECT * FROM qr_history_table")
    fun getAllQrImageData(): LiveData<List<QrHistory>>

    @Delete
    suspend fun deleteQr(qrHistory: QrHistory)

}