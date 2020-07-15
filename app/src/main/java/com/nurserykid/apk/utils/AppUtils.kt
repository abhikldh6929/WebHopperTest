package com.nurserykid.apk.utils

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.widget.Toast

object AppUtils {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    const val SHARED_PREFERENCE_KEY = "com.nurserykid.apk"

    // Common Keys
    const val TOKEN = "token"
    const val USER_ID = "user_id"
    const val NAME = "name"
    const val IMAGE = "image"
    const val STATE = "state"
    const val CITY = "city"
    const val AREA = "area"
    const val PHONE = "phone"

    fun storeKey(context: Context, key: String, value: String) {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun retrieveKey(context: Context, key: String): String? {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

    fun toaster(context: Context, msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
    }

     fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, projection, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

}