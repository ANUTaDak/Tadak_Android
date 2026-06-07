package kr.ac.anu.tadak

import android.content.Context
import android.net.Uri
import java.io.File

// 💡 갤러리에서 얻은 Uri를 서버로 보낼 수 있는 File 객체로 변환해 주는 함수
fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("tire_image", ".jpg", context.cacheDir)

        tempFile.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}