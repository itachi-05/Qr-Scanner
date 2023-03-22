package com.alpharays.qrscanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpharays.qrscanner.data.entities.QrHistory
import com.alpharays.qrscanner.databinding.ActivityHistoryBinding
import com.alpharays.qrscanner.viewmodels.QrHistoryViewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var qrHistoryViewModel: QrHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAndRemoveTask()
        }

        binding.historyRV.layoutManager = LinearLayoutManager(this)

        val adapter = HistoryAdapter(
            context = this@HistoryActivity,
            view = binding.root,
            onDeleteClickListener = object : HistoryAdapter.OnDeleteClickListener {
                override fun onDeleteClicked(qrHistory: QrHistory) {
                    deletingQr(qrHistory)
                }
            },
            scope = lifecycleScope,
        )
        binding.historyRV.adapter = adapter

        qrHistoryViewModel = ViewModelProvider(this)[QrHistoryViewModel::class.java]
        try {
            qrHistoryViewModel.allQrImages.observe(this, Observer { list ->
                list?.let {
                    adapter.updateList(it as ArrayList<QrHistory>)
                }
            })
        } catch (e: Exception) {
            Log.i("checking_History", e.message.toString())
        }
    }

    private fun deletingQr(qrHistory: QrHistory) {
        qrHistoryViewModel = ViewModelProvider(this)[QrHistoryViewModel::class.java]
        qrHistoryViewModel.deleteQr(qrHistory)
    }
}