package com.alpharays.qrscanner.fragmentScanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
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
import androidx.navigation.fragment.findNavController
import com.alpharays.qrscanner.R
import com.alpharays.qrscanner.data.entities.QrHistory
import com.alpharays.qrscanner.databinding.FragmentScannedQrBinding
import com.alpharays.qrscanner.utils.Generator
import com.alpharays.qrscanner.viewmodels.QrHistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.min

class ScannedQrFragment : Fragment(), Generator.OnWindowCloseListener {
    private var binding: FragmentScannedQrBinding? = null
    private var bitmap: Bitmap? = null
    private var myGenerator = Generator()
    private lateinit var qrHistoryViewModel: QrHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflating the fragment layout
        binding = FragmentScannedQrBinding.inflate(layoutInflater, container, false)

        // calling interface to listen changes when popup window is dismissed
        myGenerator.setOnWindowCloseListener(this)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.myMainLayout2.foreground.alpha = 0

        val result = arguments?.getString("scannedTxt")
        val ss = SpannableString(result)

        // checking if the result contains URL
        if (result!!.contains("https://")) {
            binding!!.scannedTxt.isClickable = true

            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ss.toString()))
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.qrCodeColor)
                    ds.isUnderlineText = false
                }
            }
            ss.setSpan(clickableSpan, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(StyleSpan(Typeface.BOLD), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding!!.scannedTxt.text = ss
            binding!!.scannedTxt.movementMethod = LinkMovementMethod.getInstance()
            binding!!.scannedTxt.highlightColor = Color.TRANSPARENT
        } else {
            ss.setSpan(StyleSpan(Typeface.BOLD), 0, ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding!!.scannedTxt.text = ss
            binding!!.scannedTxt.highlightColor = Color.TRANSPARENT
        }


        // generating Qr Code and setting the image as a Bitmap
        bitmap = myGenerator.qrBitmapGenerator(result.toString())
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                bitmap = myGenerator.qrBitmapGenerator(result.toString())
                savingBitmapToDb(bitmap!!, result)
            }
        }
        if (bitmap != null) binding!!.imageQrCode2.setImageBitmap(bitmap)


        // saving the image to device storage in Downloads folder
        binding!!.saveQrCode2.setOnClickListener {
            // checking bitmap null safety and Write permissions
            if (bitmap != null && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val removingHttps = result.replace("https://", "")
                val suggestionName =
                    removingHttps.substring(0, min(removingHttps.length, 15)) + "..."
                // calling qrFileName to open a popup window to save the bitmap image with file name in png format
                myGenerator.qrFileName(
                    requireContext(),
                    requireActivity(),
                    binding!!.root,
                    true,
                    suggestionName
                )
                binding!!.myMainLayout2.foreground.alpha = 180
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
        binding!!.shareQrCode2.setOnClickListener {
            myGenerator.shareQrCode(requireContext(), bitmap, result)
        }

        // on click listener to scan new Qr code
        binding!!.scanNewQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_scannedQrFragment_to_codeScannerFragment)
        }

    }

    private fun savingBitmapToDb(bitmap: Bitmap, result: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = stream.toByteArray()
                qrHistoryViewModel =
                    ViewModelProvider(this@ScannedQrFragment)[QrHistoryViewModel::class.java]
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
        Log.i("checkingWinClose2", "Working2")
        binding!!.myMainLayout2.foreground.alpha = 0
    }

}