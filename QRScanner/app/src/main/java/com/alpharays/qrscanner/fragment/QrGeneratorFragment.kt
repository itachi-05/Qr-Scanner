package com.alpharays.qrscanner.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alpharays.qrscanner.databinding.FragmentQrGeneratorBinding
import com.alpharays.qrscanner.utils.Generator
import com.google.android.material.snackbar.Snackbar


class QrGeneratorFragment : Fragment(), Generator.OnWindowCloseListener {
    private var binding: FragmentQrGeneratorBinding? = null
    private var bitmap: Bitmap? = null
    private var myGenerator = Generator()
    private var result = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrGeneratorBinding.inflate(layoutInflater, container, false)
        myGenerator.setOnWindowCloseListener(this)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.myMainLayout.foreground.alpha = 0

        binding!!.generateQrBtn.setOnClickListener {
            if (binding!!.qrCodeMessage.text.toString().isEmpty()) {
                Snackbar.make(binding!!.root, "Empty message", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            result = binding!!.qrCodeMessage.text.toString()
            bitmap = myGenerator.qrBitmapGenerator(binding!!.qrCodeMessage.text.toString())
            binding!!.imageQrCode.setImageBitmap(bitmap)
            binding!!.saveQrCode.visibility = View.VISIBLE
            binding!!.shareQrCode.visibility = View.VISIBLE
            binding!!.clearQrCode.visibility = View.VISIBLE
        }

        binding!!.saveQrCode.setOnClickListener {
            if (bitmap != null && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
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

        binding!!.shareQrCode.setOnClickListener {
            try {
                myGenerator.shareQrCode(requireContext(), bitmap, result)
            } catch (e: Exception) {
                Log.i("shareQrCodeException", e.message.toString())
            }
        }

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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onWinClose() {
        Log.i("checkingWinClose", "Working")
        binding!!.myMainLayout.foreground.alpha = 0
    }
}