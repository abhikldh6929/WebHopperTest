package com.nurserykid.apk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nurserykid.apk.utils.AppUtils.getRealPathFromUri
import com.nurserykid.apk.Retrofit.RetrofitResponse
import com.nurserykid.apk.Retrofit.RetrofitService
import com.nurserykid.apk.utils.WebUrls
import com.nurserykid.apk.utils.AppUtils
import kotlinx.android.synthetic.main.activity_update_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class UpdateProfile : AppCompatActivity(), View.OnClickListener, RetrofitResponse {

    private var file: File? = null
    private var TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        updateUI()
        btnChooseImg.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    private fun updateUI() {
        etName.setText(AppUtils.retrieveKey(this, AppUtils.NAME).toString())
        etState.setText(AppUtils.retrieveKey(this, AppUtils.STATE).toString())
        etCity.setText(AppUtils.retrieveKey(this, AppUtils.CITY).toString())
        etArea.setText(AppUtils.retrieveKey(this, AppUtils.AREA).toString())
        etPhone.setText(AppUtils.retrieveKey(this, AppUtils.PHONE).toString())

    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            startActivityForResult(this, 111)
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

        if (file != null) {

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
                    this@UpdateProfile,
                    this@UpdateProfile,
                    WebUrls.UPDATE_USER,
                    WebUrls.UPDATE_USER_CODE,
                    4,
                    this,
                    part
                ).callService(true, AppUtils.retrieveKey(this@UpdateProfile, AppUtils.TOKEN)!!)


            }

        } else {

            JSONObject().apply {

                if (etName.text.trim().toString().isNotEmpty()) {
                    put("name", etName.text.trim().toString())
                }

                if (etState.text.trim().toString().isNotEmpty()) {
                    put("state", etState.text.trim().toString())
                }

                if (etCity.text.trim().toString().isNotEmpty()) {
                    put("city", etCity.text.trim().toString())
                }

                if (etArea.text.trim().toString().isNotEmpty()) {
                    put("area", etArea.text.trim().toString())
                }

                if (etPhone.text.trim().toString().isNotEmpty()) {
                    put("phone", etPhone.text.trim().toString())
                }


                RetrofitService(
                    this@UpdateProfile,
                    this@UpdateProfile,
                    WebUrls.UPDATE_USER,
                    WebUrls.UPDATE_USER_CODE,
                    3,
                    this
                ).callService(true, AppUtils.retrieveKey(this@UpdateProfile, AppUtils.TOKEN)!!)

            }

        }

    }

    override fun onResponse(reqCode: Int, response: String) {

        try {

            when (reqCode) {

                WebUrls.UPDATE_USER_CODE -> {

                    JSONObject(response).apply {

                        if (this.getBoolean("success")) {
                            AppUtils.toaster(
                                this@UpdateProfile,
                                "Profile successfully updated"
                            )
                        }

                        Log.d(TAG, "UPDATE_USER_CODE-> $this")

                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnSubmit -> {
                submitData()
            }
            R.id.btnChooseImg -> {
                openGallery()
            }

        }

    }

}