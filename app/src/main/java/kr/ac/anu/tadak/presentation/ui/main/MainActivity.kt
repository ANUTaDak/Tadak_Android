package kr.ac.anu.tadak.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.databinding.ActivityMainBinding
import kr.ac.anu.tadak.presentation.ui.check.CheckActivity
import kr.ac.anu.tadak.presentation.viewmodel.main.MainViewModel
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

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
        observeViewModel()

        val username = viewModel.getUsername()
        binding.tvName.text = "${username}님"
    }

    private fun setupListeners() {
        binding.btnCheck.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.latestResult.collect { cachedData ->
                    if (cachedData != null) {
                        val result = cachedData.response
                        val dateText = cachedData.dateString

                        binding.tvDate.text = "마지막 진단 $dateText"

                        val scoreInt = (result.score * 100).toInt()
                        val statusText = when(result.status) {
                            "normal", "정상" -> "양호"
                            "warning", "주의" -> "주의"
                            "danger", "bad", "위험" -> "위험"
                            else -> "위험"
                        }

                        binding.tvStateText.text = statusText
                        binding.tvStateNum.text = scoreInt.toString()

                        val colorStr = when(statusText) {
                            "양호" -> "#22C55E"
                            "주의" -> "#FF9500"
                            else -> "#E74C3C"
                        }
                        binding.layoutCircle.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorStr))

                        binding.tvState.text = statusText
                        binding.tvState.setTextColor(Color.parseColor(colorStr))
                    }
                }
            }
        }
    }

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
