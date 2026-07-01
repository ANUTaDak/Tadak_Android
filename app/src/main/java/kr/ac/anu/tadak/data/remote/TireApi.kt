package kr.ac.anu.tadak.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TireApi {
    @Multipart
    @POST("/predict")
    suspend fun analyzeTire(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Response<TireAnalysisResponse>
}
