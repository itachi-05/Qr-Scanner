package com.alpharays.qrscanner.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.alpharays.qrscanner.data.db.QrDatabase
import com.alpharays.qrscanner.data.entities.QrHistory
import com.alpharays.qrscanner.repository.QrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: QrRepository
    val allQrImages: LiveData<List<QrHistory>>

    init {
        val dao = QrDatabase.getDatabase(application).qrDao()
        repo = QrRepository(dao)
        allQrImages = repo.qrImages
    }

    fun insertQr(qrHistory: QrHistory) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertQr(qrHistory)
    }

    fun deleteQr(qrHistory: QrHistory) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteQr(qrHistory)
    }
}