package kr.ac.anu.tadak.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.anu.tadak.databinding.ActivityMainBinding
import kr.ac.anu.tadak.presentation.ui.check.CheckActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // 💡 뷰모델(DianoseViewModel)은 CheckActivity로 이사 갔으므로 여기서 삭제했습니다!

    // 💡 1. 갤러리(Photo Picker) 세팅
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // 갤러리에서 사진을 골랐을 때
            val file = uriToFile(this, uri)

            if (file != null) {
                val intent = Intent(this@MainActivity, CheckActivity::class.java)
                intent.putExtra("IMAGE_PATH", file.absolutePath)
                startActivity(intent)
            } else {
                Toast.makeText(this, "파일을 변환할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCheck.setOnClickListener {
            // 💡 3. 버튼을 누르면 화면을 바로 넘기지 않고, 일단 갤러리를 띄웁니다!
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    // 💡 4. Uri를 File로 변환해주는 유틸리티 함수 (클래스 내부에 두셔도 되고 외부에 두셔도 됩니다)
    private fun uriToFile(context: Context, uri: Uri): File? {
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
}
