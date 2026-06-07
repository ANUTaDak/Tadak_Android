package kr.ac.anu.tadak.data.remote

data class TireAnalysisResponse(
    val success: Boolean,
    val score: Float,
    val status: String,
    val label: String,
    val safe: Boolean,
    val message: String
)