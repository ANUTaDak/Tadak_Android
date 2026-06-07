package kr.ac.anu.tadak.presentation.ui.check

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.databinding.ActivityCheckBinding
import kr.ac.anu.tadak.presentation.ui.result.ResultActivity
import kr.ac.anu.tadak.presentation.viewmodel.main.DiagnoseUiState
import kr.ac.anu.tadak.presentation.viewmodel.main.DiagnoseViewModel
import java.io.File

@AndroidEntryPoint // 🚨 뷰모델 주입을 위해 무조건 필수!
class CheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckBinding
    private val viewModel: DiagnoseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 💡 1. 뷰모델 상태 관찰 시작 (성공 시 넘어갈 준비)
        observeViewModel()

        // 💡 2. MainActivity에서 보낸 이미지 경로 받기
        val imagePath = intent.getStringExtra("IMAGE_PATH")

        if (imagePath != null) {
            // 경로가 있다면 File로 만들고, 뷰모델에 타이어 분석 요청! (서버 통신 시작)
            val imageFile = File(imagePath)
            viewModel.analyzeTire(imageFile)
        } else {
            // 경로를 못 받았다면 에러 메시지 띄우고 다시 메인으로 돌려보냄
            Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.diagnoseState.collect { state ->
                    when (state) {
                        is DiagnoseUiState.Idle -> {}

                        is DiagnoseUiState.Loading -> {

                        }

                        is DiagnoseUiState.Success -> {
                            // 💡 progressBar 지움! 바로 결과 화면으로 이동
                            val intent =
                                Intent(this@CheckActivity, ResultActivity::class.java).apply {
                                    putExtra("SCORE", state.result.score)
                                    putExtra("STATUS", state.result.status)
                                }
                            startActivity(intent)
                            finish()
                        }

                        is DiagnoseUiState.Error -> {
                            // 💡 progressBar 지움! 에러 띄우고 메인으로 복귀
                            Toast.makeText(this@CheckActivity, state.message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
            }

        }
    }
}