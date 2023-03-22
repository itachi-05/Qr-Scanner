package com.alpharays.qrscanner.repository

import androidx.lifecycle.LiveData
import com.alpharays.qrscanner.data.entities.QrDao
import com.alpharays.qrscanner.data.entities.QrHistory

class QrRepository(private val qrDao: QrDao) {
    val qrImages: LiveData<List<QrHistory>> = qrDao.getAllQrImageData()

    suspend fun insertQr(qrHistory: QrHistory) {
        qrDao.insertQr(qrHistory)
    }

    suspend fun deleteQr(qrHistory: QrHistory){
        qrDao.deleteQr(qrHistory)
    }
}