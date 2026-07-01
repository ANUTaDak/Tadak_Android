package kr.ac.anu.tadak.data.remote

data class RegisterRequest(
    val id: String,
    val pw: String,
    val name: String
)
