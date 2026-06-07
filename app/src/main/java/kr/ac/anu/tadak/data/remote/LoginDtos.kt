package kr.ac.anu.tadak.data.remote

data class LoginRequest(
    val id: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val username: String
)