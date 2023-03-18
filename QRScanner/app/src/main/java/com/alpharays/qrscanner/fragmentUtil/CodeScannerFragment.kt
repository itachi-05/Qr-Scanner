package com.alpharays.qrscanner.fragmentUtil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alpharays.qrscanner.R
import com.alpharays.qrscanner.databinding.FragmentCodeScannerBinding
import com.budiyev.android.codescanner.*
import com.google.android.material.snackbar.Snackbar

class CodeScannerFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var result: String
    private var binding: FragmentCodeScannerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflating the fragment layout
        binding = FragmentCodeScannerBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initiating the codeScannerView class to initiate the QR CODE Scanner using device camera
        val codeScannerView: CodeScannerView = binding!!.codeScannerView
        codeScanner = activity?.let { CodeScanner(it.applicationContext, codeScannerView) }!!
        codeScanner.startPreview()
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // qr scanned successfully and navigating to qr details page
        codeScanner.decodeCallback = DecodeCallback {
            this.activity?.runOnUiThread {
                result = it.text
                val bundle = Bundle()
                bundle.putString("scannedTxt", result)
                findNavController().navigate(R.id.action_codeScannerFragment_to_scannedQrFragment, bundle)
                Log.i("Scan_Result", it.text)
            }
        }

        // handling device camera errors
        codeScanner.errorCallback = ErrorCallback {
            this.activity?.runOnUiThread {
                Log.i("Camera_Error", "Camera_Error")
                Snackbar.make(binding!!.root, "Camera Error", Toast.LENGTH_LONG).show()
            }
        }

        // if scanner shuts down, then starting the scanner using OnClickListener
        codeScannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    // starting the scanner on resume state
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    // pausing the scanner on pause state
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    // to avoid memory leaks
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}