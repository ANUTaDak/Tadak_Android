package kr.ac.anu.tadak.presentation.ui.auth

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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.R
import kr.ac.anu.tadak.databinding.ActivityJoinBinding
import kr.ac.anu.tadak.presentation.viewmodel.auth.JoinUiState
import kr.ac.anu.tadak.presentation.viewmodel.auth.JoinViewModel

@AndroidEntryPoint
class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding
    private val viewModel: JoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewSets()
        setupListeners()
        setupTextWatchers()
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
        binding.btnJoin.setOnClickListener {
            val id = binding.etId.text.toString()
            val pw = binding.etPw.text.toString()
            val name = binding.etName.text.toString()

            viewModel.register(id, pw, name)
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = { _: CharSequence?, _: Int, _: Int, _: Int ->
            val id = binding.etId.text.toString()
            val pw = binding.etPw.text.toString()
            val pwCheck = binding.etPwCheck.text.toString()
            val name = binding.etName.text.toString()

            viewModel.validateInput(id, pw, pwCheck, name)
        }

        binding.etId.addTextChangedListener(onTextChanged = textWatcher)
        binding.etPw.addTextChangedListener(onTextChanged = textWatcher)
        binding.etPwCheck.addTextChangedListener(onTextChanged = textWatcher)
        binding.etName.addTextChangedListener(onTextChanged = textWatcher)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.isButtonEnabled.collect { isEnabled ->
                        binding.btnJoin.isEnabled = isEnabled
                    }
                }

                launch {
                    viewModel.joinState.collect { state ->
                        when (state) {
                            is JoinUiState.Idle -> { }
                            is JoinUiState.Loading -> {
                            }
                            is JoinUiState.Success -> {
                                Toast.makeText(this@JoinActivity, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is JoinUiState.Error -> {
                                Toast.makeText(this@JoinActivity, state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }
        }
    }
}