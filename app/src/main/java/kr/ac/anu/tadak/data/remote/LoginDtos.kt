package kr.ac.anu.tadak.data.remote

data class LoginRequest(
    val id: String,
    val pw: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val name: String? = null,
    val id: String
)