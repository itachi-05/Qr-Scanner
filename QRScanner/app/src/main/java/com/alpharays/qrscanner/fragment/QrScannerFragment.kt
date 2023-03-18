package com.alpharays.qrscanner.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alpharays.qrscanner.databinding.FragmentQrScannerBinding

class QrScannerFragment : Fragment() {
    private var binding: FragmentQrScannerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflating the fragment layout
        binding = FragmentQrScannerBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    // to avoid memory leaks
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}