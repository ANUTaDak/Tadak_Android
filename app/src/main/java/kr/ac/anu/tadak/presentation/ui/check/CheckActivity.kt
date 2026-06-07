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
                        is DiagnoseUiState.Idle -> { }

                        is DiagnoseUiState.Loading -> {
                            // 💡 분석 중: 프로그레스 바(로딩 바) 띄우기
                            // (XML에 id가 progressBar인 로딩 UI가 있다고 가정합니다)
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is DiagnoseUiState.Success -> {
                            binding.progressBar.visibility = View.GONE

                            // 💡 분석 완료! 서버에서 받은 점수와 상태를 들고 ResultActivity로 이동
                            val intent = Intent(this@CheckActivity, ResultActivity::class.java).apply {
                                putExtra("SCORE", state.result.score)
                                putExtra("STATUS", state.result.status)
                            }
                            startActivity(intent)

                            // 🚨 이동 후 현재 '분석 중' 화면은 백스택에서 지워줍니다.
                            // 그래야 결과 화면에서 뒤로가기를 눌렀을 때 엉뚱하게 이 화면이 다시 나오지 않습니다.
                            finish()
                        }

                        is DiagnoseUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@CheckActivity, state.message, Toast.LENGTH_SHORT).show()

                            // 에러가 났으니 이 화면을 닫고 메인으로 돌아갑니다.
                            finish()
                        }
                    }
                }
            }
        }
    }
}