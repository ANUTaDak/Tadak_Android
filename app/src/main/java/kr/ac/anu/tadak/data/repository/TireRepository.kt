package kr.ac.anu.tadak.data.repository

import kr.ac.anu.tadak.data.remote.TireAnalysisResponse
import kr.ac.anu.tadak.data.remote.TireApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class TireRepository @Inject constructor(
    private val tireApi: TireApi
) {
    suspend fun analyzeTire(token: String, imageFile: File): Result<TireAnalysisResponse> {
        return try {
            // 1. File을 RequestBody로 변환
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

            // 2. 서버 파라미터명인 "image"에 맞춰 MultipartBody.Part 생성
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            // 3. API 호출 (토큰 뒤에 Bearer 공백 한 칸 필수)
            val response = tireApi.analyzeTire("Bearer $token", body)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("분석 실패: 에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}