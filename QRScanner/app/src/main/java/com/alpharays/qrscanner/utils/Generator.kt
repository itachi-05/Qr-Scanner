package com.alpharays.qrscanner.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alpharays.qrscanner.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

class Generator {
    private var _finalFileName: MutableLiveData<String> = MutableLiveData("")
    private var popupWindow: PopupWindow? = null
    private var onWindowCloseListener: OnWindowCloseListener? = null

    // save path for storing qr bitmap image in Downloads folder
    private val savePath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/QRCode/"

    // Bitmap Generator util function
    fun qrBitmapGenerator(text: String): Bitmap {
        val encoder = BarcodeEncoder()
        return encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 800, 800)
    }

    // Popup window to save file image with name util function
    fun qrFileName(
        context: Context,
        activity: Activity,
        view: View,
        ok: Boolean,
        suggestionTxt: String
    ) {
        // inflating the popup window's view with respective layout
        val popupView =
            LayoutInflater.from(context).inflate(R.layout.file_name_layout, null)
        popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            true
        )

        if (ok) {
            val fileName = popupView.findViewById<EditText>(R.id.myQrFileNameTxt)
            fileName.setText(suggestionTxt)
        }

        // setting height and width of window according to the device dimensions
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        popupWindow!!.width = width - 100
        popupWindow!!.height = height - 1020

        // animation to slide popup window
        val padding = 30
        popupWindow!!.animationStyle = R.style.PopupAnimation
        popupView.setPadding(padding, padding, padding, padding)
        popupWindow!!.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // saving the file to device storage
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.saveBtn)
        saveBtn.setOnClickListener {
            var fileName =
                popupView.findViewById<TextInputEditText>(R.id.myQrFileNameTxt).text.toString()
            if (fileName.isEmpty() || fileName == "") {
                Snackbar.make(popupView, "File name can not be empty", Snackbar.LENGTH_SHORT).show()
            } else {
                popupWindow!!.dismiss()
                fileName = fileName.replace(" ", "_")
                _finalFileName.postValue(fileName)
            }
        }

        // cancelling the saving of image
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            popupWindow!!.dismiss()
            Snackbar.make(view, "Cancelled", Snackbar.LENGTH_SHORT).show()
        }

        // listener for popupWindow dismissal
        popupWindow!!.setOnDismissListener {
            onWindowCloseListener?.onWinClose()
        }
    }

    // saving the name of file
    fun qrSaveBitmap(
        viewLifecycleOwner: LifecycleOwner,
        bitmap: Bitmap?,
        view: View,
        lifeCycleScope: LifecycleCoroutineScope
    ) {
        _finalFileName.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty() && it != "") {
                lifeCycleScope.launch {
                    try {
                        // Create a File object with the path and filename
                        savingFile(it, bitmap, view)
                    } catch (e: Exception) {
                        Snackbar.make(view, "Error", Snackbar.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    suspend fun savingFile(it: String, bitmap: Bitmap?, view: View) {
        val removingHttps = it.replace("https://", "")
        val removingHttps2 = removingHttps.replace("/", "_")
        val suggestionName =
            removingHttps2.substring(0, min(removingHttps2.length, 15)) + "..."
        val fileName = "$suggestionName.png"
        val file = File(savePath, fileName)
        val directory = File(savePath)
        if (!directory.exists()) directory.mkdirs()
        val stream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        withContext(Dispatchers.IO) {
            stream.close()
        }
        withContext(Dispatchers.Main) {
            Snackbar.make(
                view,
                "Successfully saved in /Downloads",
                Snackbar.LENGTH_LONG
            ).show()
            _finalFileName.postValue("")
        }
    }

    fun setOnWindowCloseListener(listener: OnWindowCloseListener) {
        onWindowCloseListener = listener
    }

    // sharing the generated / scanned Qr Code
    fun shareQrCode(context: Context, bitmap: Bitmap?, result: String) {
        try {
            val uri = getImageUri(bitmap!!, context)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this QR Code!\n$result")
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share QR Code via..."
                )
            )
        } catch (e: Exception) {
            Log.i("shareQrCodeException", e.message.toString())
        }
    }

    // converting bitmap image into Uri to store to device storage
    private fun getImageUri(bitmapImage: Bitmap, context: Context): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmapImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    // interface for listening changes
    interface OnWindowCloseListener {
        fun onWinClose()
    }

}