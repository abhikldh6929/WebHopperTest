package com.nurserykid.apk.utils

import android.content.Context
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.nurserykid.apk.R


class DevProgress(var context: Context) {

    private var alert: AlertDialog? = null

    fun showDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_dialog)
        alert = builder.create()
        alert!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alert!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alert!!.show()

    }

    fun hideDialog() {
        if (alert?.isShowing!!) {
            alert?.dismiss()
        }
    }

}