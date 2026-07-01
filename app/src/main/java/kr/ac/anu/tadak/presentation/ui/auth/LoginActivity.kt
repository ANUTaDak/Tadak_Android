package kr.ac.anu.tadak.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.R
import kr.ac.anu.tadak.databinding.ActivityLoginBinding
import kr.ac.anu.tadak.presentation.ui.main.MainActivity
import kr.ac.anu.tadak.presentation.viewmodel.auth.LoginUiState
import kr.ac.anu.tadak.presentation.viewmodel.auth.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewSets()
        setupListeners()
        observeViewModel()
    }

    private fun viewSets() {
        val originText = binding.tvAppTitle.text.toString()
        val spannable = SpannableStringBuilder(originText)

        val targetWord = "TaDak"
        val start = originText.indexOf(targetWord)
        val end = start + targetWord.length

        if (start != -1) {
            val color = ContextCompat.getColor(this, R.color.tadak_blue)

            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvAppTitle.text = spannable
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPw.text.toString()
            viewModel.login(id, password)
        }

        binding.btnJoin.setOnClickListener {
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginUiState.Idle -> {}
                        is LoginUiState.Loading -> {}
                        is LoginUiState.Success -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }

                        is LoginUiState.Error -> {
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
