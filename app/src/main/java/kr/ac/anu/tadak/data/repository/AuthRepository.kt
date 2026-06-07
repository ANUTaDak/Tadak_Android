package kr.ac.anu.tadak.data.repository

import kr.ac.anu.tadak.data.remote.AuthApi
import kr.ac.anu.tadak.data.remote.LoginRequest
import kr.ac.anu.tadak.data.remote.LoginResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {

    // ViewModel에서 호출할 로그인 함수
    suspend fun login(id: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(id = id, password = password)
            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("로그인 실패: 에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}