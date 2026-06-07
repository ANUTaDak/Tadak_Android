package kr.ac.anu.tadak.data.repository

import kr.ac.anu.tadak.data.local.TokenManager
import kr.ac.anu.tadak.data.remote.AuthApi
import kr.ac.anu.tadak.data.remote.LoginRequest
import kr.ac.anu.tadak.data.remote.LoginResponse
import kr.ac.anu.tadak.data.remote.RegisterRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    // ViewModel에서 호출할 로그인 함수
    suspend fun login(id: String, pw: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(id = id, pw = pw)
            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val loginData = response.body()!!

                tokenManager.saveToken(loginData.token)

                Result.success(loginData)
            } else {
                Result.failure(Exception("로그인 실패: 에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(id: String, pw: String, name: String): Result<Boolean> {
        return try {
            val request = RegisterRequest(id, pw, name)
            val response = authApi.register(request)

            // 서버 응답이 200번대(성공)이면 true 반환
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("회원가입 실패: 에러 코드 ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}