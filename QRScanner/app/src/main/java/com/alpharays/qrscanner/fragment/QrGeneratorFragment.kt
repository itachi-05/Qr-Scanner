package com.alpharays.qrscanner.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alpharays.qrscanner.data.entities.QrHistory
import com.alpharays.qrscanner.databinding.FragmentQrGeneratorBinding
import com.alpharays.qrscanner.utils.Generator
import com.alpharays.qrscanner.viewmodels.QrHistoryViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class QrGeneratorFragment : Fragment(), Generator.OnWindowCloseListener {
    private var binding: FragmentQrGeneratorBinding? = null
    private var bitmap: Bitmap? = null
    private var myGenerator = Generator()
    private var result = ""
    private lateinit var qrHistoryViewModel: QrHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflating the fragment layout
        binding = FragmentQrGeneratorBinding.inflate(layoutInflater, container, false)

        // calling interface to listen changes when popup window is dismissed
        myGenerator.setOnWindowCloseListener(this)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.myMainLayout.foreground.alpha = 0

        // generating Qr Code and setting the image as a Bitmap
        binding!!.generateQrBtn.setOnClickListener {
            if (binding!!.qrCodeMessage.text.toString().isEmpty()) {
                Snackbar.make(binding!!.root, "Empty message", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            result = binding!!.qrCodeMessage.text.toString()
            bitmap = myGenerator.qrBitmapGenerator(binding!!.qrCodeMessage.text.toString())
            savingBitmapToDb(bitmap!!, result)
            binding!!.imageQrCode.setImageBitmap(bitmap)
            binding!!.saveQrCode.visibility = View.VISIBLE
            binding!!.shareQrCode.visibility = View.VISIBLE
            binding!!.clearQrCode.visibility = View.VISIBLE
        }

        // saving the image to device storage in Downloads folder
        binding!!.saveQrCode.setOnClickListener {
            // checking bitmap null safety and Write permissions
            if (bitmap != null && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // calling qrFileName to open a popup window to save the bitmap image with file name in png format
                myGenerator.qrFileName(
                    requireContext(),
                    requireActivity(),
                    binding!!.root,
                    false,
                    ""
                )
                binding!!.myMainLayout.foreground.alpha = 180
                myGenerator.qrSaveBitmap(this, bitmap, binding!!.root, lifecycleScope)
            } else {
                // permission was not granted or was later explicitly denied
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(requireContext(), "Permission not allowed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        // sharing the qr code bitmap via other application using implicit intent
        binding!!.shareQrCode.setOnClickListener {
            try {
                myGenerator.shareQrCode(requireContext(), bitmap, result)
            } catch (e: Exception) {
                Log.i("shareQrCodeException", e.message.toString())
            }
        }


        // clearing the generated qr code to generate a new qr code
        binding!!.clearQrCode.setOnClickListener {
            try {
                binding!!.qrCodeMessage.setText("")
                binding!!.imageQrCode.setImageResource(0)
                binding!!.saveQrCode.visibility = View.GONE
                binding!!.shareQrCode.visibility = View.GONE
                binding!!.clearQrCode.visibility = View.GONE
            } catch (e: Exception) {
                Log.i("clearQrCodeException", e.message.toString())
            }
        }
    }

    private fun savingBitmapToDb(bitmap: Bitmap, result: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = stream.toByteArray()
                qrHistoryViewModel =
                    ViewModelProvider(this@QrGeneratorFragment)[QrHistoryViewModel::class.java]
                val qrHistory = QrHistory(about = result, imageData = imageData)
                qrHistoryViewModel.insertQr(qrHistory)
            } catch (e: Exception) {
                Log.i("checkingException", e.message.toString())
            }
        }
    }

    // to avoid memory leaks
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    // interface method for popup window dismissal
    override fun onWinClose() {
        Log.i("checkingWinClose", "Working")
        binding!!.myMainLayout.foreground.alpha = 0
    }
}