package kr.ac.anu.tadak.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tadak_prefs", Context.MODE_PRIVATE)

    // 토큰 저장하기
    fun saveToken(token: String) {
        prefs.edit().putString("USER_TOKEN", token).apply()
    }

    // 토큰 꺼내오기 (없으면 null 반환)
    fun getToken(): String? {
        return prefs.getString("USER_TOKEN", null)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString("USER_NAME", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("USER_NAME", null)
    }

    // 로그아웃 시 전체 삭제
    fun clearToken() {
        prefs.edit().clear().apply()
    }
}