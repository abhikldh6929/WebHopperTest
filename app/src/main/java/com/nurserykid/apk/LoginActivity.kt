package com.nurserykid.apk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nurserykid.apk.Retrofit.RetrofitResponse
import com.nurserykid.apk.Retrofit.RetrofitService
import com.nurserykid.apk.utils.WebUrls
import com.nurserykid.apk.utils.AppUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), RetrofitResponse {

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSubmit.setOnClickListener {
            login()
        }
    }

    private fun login() {
        JSONObject().apply {

            put("phone", etName.text.trim().toString())
            put("password", etPass.text.trim().toString())

            RetrofitService(
                this@LoginActivity,
                this@LoginActivity,
                WebUrls.LOG_IN,
                WebUrls.LOG_IN_CODE,
                2,
                this
            ).callService(true, "")
        }
    }

    override fun onResponse(reqCode: Int, response: String) {

        try {

            when (reqCode) {

                WebUrls.LOG_IN_CODE -> {

                    JSONObject(response).apply {

                        Log.d(TAG, "LOG_IN_CODE-> $this")

                        if (this.getBoolean("success")) {

                            AppUtils.toaster(this@LoginActivity, "Login Successfully")

                            this.optJSONObject("data")?.let {

                                it.getJSONArray("items").apply {

                                    if (this.length() > 0) {

                                        AppUtils.storeKey(
                                            this@LoginActivity,
                                            AppUtils.TOKEN,
                                            this.getString(0)
                                        )

                                        this.optJSONObject(1)?.let { user ->

                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.USER_ID,
                                                user.getString("id")
                                            )
                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.NAME,
                                                user.getString("name")
                                            )
                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.IMAGE,
                                                user.getString("image")
                                            )

                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.STATE,
                                                user.getString("state")
                                            )
                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.CITY,
                                                user.getString("city")
                                            )
                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.PHONE,
                                                user.getString("phone")
                                            )
                                            AppUtils.storeKey(
                                                this@LoginActivity,
                                                AppUtils.AREA,
                                                user.getString("area")
                                            )
                                            Intent(
                                                this@LoginActivity,
                                                UpdateProfile::class.java
                                            ).apply {
                                                startActivity(this)
                                                finish()
                                            }

                                        }

                                    }

                                }

                            }

                        }

                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}