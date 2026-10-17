package com.example.coopgrid.worker.data.apiservice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response

interface ApiService {

    @Multipart
    @POST("media/upload")
    suspend fun uploadSingleMedia(
        @Part("mediaId") mediaId: RequestBody,
        @Part("fileName") fileName: RequestBody,
        @Part("contentType") contentType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}
@Serializable
data class UploadResponse(
    val success: Boolean,
    @SerialName("media_id")
    val mediaId: String,
    val message: String
)