package com.nurserykid.apk.Retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitAPI {

    @POST
    suspend fun callPost(@Url url: String, @Body body: RequestBody): Response<ResponseBody>

    @PATCH
    suspend fun callPatch(@Url url: String, @Body body: RequestBody): Response<ResponseBody>

    @Multipart
    @PATCH
    suspend fun callPatchMultipart(
        @Url url: String, @PartMap map: HashMap<String, RequestBody>,
        @Part part: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @PUT
    suspend fun callPutMultipart(
        @Url url: String,
        @PartMap map: HashMap<String, RequestBody>,
        @Part part: MultipartBody.Part
    ): Response<ResponseBody>

}