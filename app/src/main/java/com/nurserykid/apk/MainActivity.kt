package com.nurserykid.apk

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nurserykid.apk.Retrofit.RetrofitResponse
import com.nurserykid.apk.Retrofit.RetrofitService
import com.nurserykid.apk.utils.AppUtils
import com.nurserykid.apk.utils.AppUtils.getRealPathFromUri
import com.nurserykid.apk.utils.PermissionHelper
import com.nurserykid.apk.utils.WebUrls
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity(), RetrofitResponse, View.OnClickListener {

    private var file: File? = null
    private var TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChooseImg.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

    }

    private fun openGallery() {

        Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            startActivityForResult(this, 111)
        }
    }

    private fun openIntent() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            if (PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                PermissionHelper.requestPermission(this)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            111 -> {

                if (resultCode == Activity.RESULT_OK && data != null) {

                    val uri = data.data

                    file = File(getRealPathFromUri(this, uri)!!)

                    tvFileName.text = file?.name
                    Log.d(TAG, "Fileis -> $file")

                }

            }

        }

    }

    private fun submitData() {

        val reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file!!)
        val part = MultipartBody.Part.createFormData("image", file?.name, reqFile)

        HashMap<String, RequestBody>().apply {

            put(
                "name",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etName.text.trim().toString()
                )
            )
            put(
                "area",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etArea.text.trim().toString()
                )
            )
            put(
                "phone",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etPhone.text.trim().toString()
                )
            )
            put(
                "password",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etPass.text.trim().toString()
                )
            )
            put(
                "state",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etState.text.trim().toString()
                )
            )
            put(
                "city",
                RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    etCity.text.trim().toString()
                )
            )

            RetrofitService(
                this@MainActivity,
                this@MainActivity,
                WebUrls.SIGN_UP,
                WebUrls.SIGN_UP_CODE,
                1,
                this,
                part
            ).callService(true, "")
        }


    }

    override fun onResponse(reqCode: Int, response: String) {

        try {

            when (reqCode) {

                WebUrls.SIGN_UP_CODE -> {

                    JSONObject(response).apply {

                        if (this.getBoolean("success")) {
                            btnLogin.visibility = View.VISIBLE
                            AppUtils.toaster(this@MainActivity, "Sign up Successfully,Please login")
                        }

                        Log.d(TAG, "SignUp-> $this")

                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun valid(): Boolean {

        return when {
            etName.text.trim().isEmpty() -> {
                AppUtils.toaster(this, getString(R.string.enter_name))
                false
            }
            etArea.text.trim().isEmpty() -> {
                AppUtils.toaster(this, getString(R.string.enter_area))
                false
            }
            etPhone.text.trim().isEmpty() || etPhone.text.trim().length < 10 -> {
                AppUtils.toaster(this, getString(R.string.enter_phone))
                false
            }
            etPass.text.trim().isEmpty() -> {
                AppUtils.toaster(this, getString(R.string.enter_password))
                false
            }
            etState.text.trim().isEmpty() -> {
                AppUtils.toaster(this, getString(R.string.enter_state))
                false
            }
            etCity.text.trim().isEmpty() -> {
                AppUtils.toaster(this, getString(R.string.enter_city))
                false
            }
            file == null -> {
                AppUtils.toaster(this, getString(R.string.select_image))
                false
            }
            else -> true
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnSubmit -> {
                if (valid()) {
                    submitData()
                }
            }
            R.id.btnChooseImg -> {
                if (PermissionHelper.hasPermission(this)) {
                    openGallery()
                }
            }
            R.id.btnLogin -> {
                openIntent()
            }

        }

    }


}
