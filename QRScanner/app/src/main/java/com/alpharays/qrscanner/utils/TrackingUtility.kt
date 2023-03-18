package com.alpharays.qrscanner.utils

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {

    fun hasCameraPermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.CAMERA
        )

    fun hasStoragePermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
}