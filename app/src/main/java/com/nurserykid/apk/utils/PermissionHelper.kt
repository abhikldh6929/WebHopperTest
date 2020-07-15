package com.nurserykid.apk.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionHelper {

    companion object {

        private val PERMISSION_CODE = 0

        private val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        @JvmStatic
        fun hasPermission(activity: Activity): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission(activity)
                    return false
                }
            }
            return true
        }

        @JvmStatic
        fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_CODE)
        }

        @JvmStatic
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return true
                }
            }
            return false
        }
    }
}