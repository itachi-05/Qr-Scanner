package com.alpharays.qrscanner

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class CopyDialogFragment(scannedText: String) : DialogFragment() {
    var scannedText: String
    init {
        this.scannedText = scannedText
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(scannedText).setPositiveButton("Copy", DialogInterface.OnClickListener{ _, _ ->
               val clipboard = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val clip = ClipData.newPlainText("", scannedText)
               clipboard.setPrimaryClip(clip)
            })
            builder.create()
        } ?: throw java.lang.IllegalStateException("Activity cannot be null")
    }
}