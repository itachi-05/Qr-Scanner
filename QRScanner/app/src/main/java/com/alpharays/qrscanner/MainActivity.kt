package com.alpharays.qrscanner

import android.Manifest
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpharays.qrscanner.databinding.ActivityMainBinding
import com.alpharays.qrscanner.utils.Constants
import com.alpharays.qrscanner.utils.TrackingUtility
import com.google.android.material.tabs.TabLayoutMediator
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        binding.viewpager.adapter = QrAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, index ->
            tab.text = when (index) {
                0 -> {
                    "QR Generator"
                }
                1 -> {
                    "QR Scanner"
                }
                else -> {
                    throw Resources.NotFoundException("Position not found")
                }
            }
        }.attach()
    }

    // new permission handling:
    private fun requestPermissions() {
        if (TrackingUtility.hasCameraPermissions(this)) {
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept camera permissions to use this app.",
                Constants.REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (TrackingUtility.hasStoragePermissions(this)) {
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept storage permissions to use this app.",
                Constants.REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}