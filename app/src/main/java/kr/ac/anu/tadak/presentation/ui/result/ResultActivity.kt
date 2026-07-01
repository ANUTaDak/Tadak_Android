package kr.ac.anu.tadak.presentation.ui.result

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.ac.anu.tadak.databinding.ActivityResultBinding
import kr.ac.anu.tadak.presentation.ui.main.MainActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // 1. CheckActivity에서 넘겨준 데이터 받기
        val score = intent.getFloatExtra("SCORE", 0.0f)
        val status = intent.getStringExtra("STATUS") ?: "알 수 없음"

        // 2. 점수를 퍼센트로 변환 (예: 0.823 -> 82.3%)
        val percentage = String.format("%.1f", score * 100)
        binding.tvScore.text = "AI 안전 스코어 $percentage%"

        when (status) {
            "정상", "normal" -> {
                binding.tvStatus.text = "정상 타이어"
                binding.tvStatus.setTextColor(Color.parseColor("#22C55E")) // 초록색

                binding.tvInsightTitle.text = "안전 등급: 우수"
                binding.tvInsightTitle.setTextColor(Color.parseColor("#22C55E"))
                binding.tvInsightDesc.text = "마모 상태가 양호하며 즉각적인 교체가 필요하지 않습니다. 안전 운전하세요!"
            }
            "주의", "warning" -> {
                binding.tvStatus.text = "점검 요망"
                binding.tvStatus.setTextColor(Color.parseColor("#FF9500")) // 주황색

                binding.tvInsightTitle.text = "마모 진행 중"
                binding.tvInsightTitle.setTextColor(Color.parseColor("#FF9500"))
                binding.tvInsightDesc.text = "편마모나 크랙이 의심됩니다. 조만간 가까운 정비소에 방문하여 점검을 받아보세요."
            }
            "위험", "danger", "bad" -> {
                binding.tvStatus.text = "교체 시급"
                binding.tvStatus.setTextColor(Color.parseColor("#E74C3C")) // 빨간색

                binding.tvInsightTitle.text = "위험 경고!"
                binding.tvInsightTitle.setTextColor(Color.parseColor("#E74C3C"))
                binding.tvInsightDesc.text = "타이어 마모 한계선에 도달했거나 심각한 손상이 있습니다. 주행을 자제하고 즉시 교체하세요."
            }
            else -> {
                binding.tvStatus.text = status
                binding.tvStatus.setTextColor(Color.parseColor("#1C1C1E")) // 기본 검정
                binding.tvInsightDesc.text = "결과 데이터를 불러오는 중 문제가 발생했습니다."
            }
        }
    }

    private fun setupListeners() {
        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 메인 화면으로 돌아가기 버튼 (스택 다 날리고 새로 시작)
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
