package com.nurserykid.apk.Retrofit

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nurserykid.apk.utils.AppUtils
import com.nurserykid.apk.utils.DevProgress
import com.nurserykid.apk.utils.WebUrls
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService() {

    private var TAG = this.javaClass.simpleName
    private var mContext: Context? = null
    private var mUrl: String? = null
    lateinit var retrofit: Retrofit
    private var retrofitResponse: RetrofitResponse? = null
    private lateinit var response: Response<ResponseBody>
    private var mRequestcode: Int = 0
    private var mValue: Int = 0
    private lateinit var mPart: MultipartBody.Part
    private var mMap: HashMap<String, RequestBody>? = null
    private var progress: DevProgress? = null
    private lateinit var mJSONObj: JSONObject

    constructor(
        context: Context,
        response: RetrofitResponse,
        url: String,
        requestCode: Int,
        value: Int,
        jsonObject: JSONObject
    ) : this() {
        mContext = context
        retrofitResponse = response
        mUrl = url
        mRequestcode = requestCode
        mValue = value
        mJSONObj = jsonObject
    }

    constructor(
        context: Context,
        response: RetrofitResponse,
        url: String,
        requestCode: Int,
        value: Int,
        map: HashMap<String, RequestBody>,
        part: MultipartBody.Part
    ) : this() {
        mContext = context
        retrofitResponse = response
        mUrl = url
        mRequestcode = requestCode
        mValue = value
        mMap = map
        mPart = part
    }

    fun callService(dialogFlag: Boolean, token: String) {

        if (dialogFlag) {
            progress = DevProgress(mContext!!)
            progress?.showDialog()
            // CustomProgressDialog.showDialog(mContext!!)
        }

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build()

        if (token.isEmpty()) {

            retrofit = Retrofit.Builder()
                .baseUrl(WebUrls.BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()


        } else {

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${token.trim()}")
                        .build()
                chain.proceed(request)
            }

            retrofit = Retrofit.Builder()
                .baseUrl(WebUrls.BaseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        }

        val retrofitApi = retrofit.create(RetrofitAPI::class.java)
        val corExHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        }
        CoroutineScope(Dispatchers.IO + corExHandler).launch {

            when (mValue) {

                // For Put with Multipart
                1 -> {
                    response = mUrl.let {
                        retrofitApi.callPutMultipart(it!!, mMap!!, mPart!!)
                    }
                }

                // For Post
                2 -> {
                    response = mUrl.let {
                        retrofitApi.callPost(
                            it!!,
                            RequestBody.create(
                                MediaType.parse("application/json"),
                                mJSONObj.toString()
                            )
                        )
                    }
                }


                // For Patch
                3 -> {
                    response = mUrl.let {
                        retrofitApi.callPatch(
                            it!!,
                            RequestBody.create(
                                MediaType.parse("application/json"),
                                mJSONObj.toString()
                            )
                        )
                    }
                }

                // For Patch Multipart
                4 -> {

                    response = mUrl.let {
                        retrofitApi.callPatchMultipart(
                            it!!,
                            mMap!!, mPart
                        )
                    }

                }

            }
            CoroutineScope(Dispatchers.Main).launch {
                progress?.hideDialog()
                if (response.isSuccessful) {
                    val res = response.body()!!.string()
                    Log.e(TAG, "onResponseSuccess $res")
                    retrofitResponse?.onResponse(mRequestcode, res)
                } else {
                    val res = response.errorBody()!!.string()
                    Log.e(TAG, "onResponseError $res")
                    val jsonObj = JSONObject(res)
                    AppUtils.toaster(mContext!!, jsonObj.getString("message"))
                }

            }
        }


    }


}