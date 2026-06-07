package kr.ac.anu.tadak.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.data.repository.AuthRepository
import javax.inject.Inject

sealed class JoinUiState {
    object Idle : JoinUiState()
    object Loading : JoinUiState()
    object Success : JoinUiState()
    data class Error(val message: String) : JoinUiState()
}

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _joinState = MutableStateFlow<JoinUiState>(JoinUiState.Idle)
    val joinState: StateFlow<JoinUiState> = _joinState.asStateFlow()

    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled: StateFlow<Boolean> = _isButtonEnabled.asStateFlow()

    fun validateInput(id: String, pw: String, pwCheck: String, name: String) {
        val isPwMatched = pw == pwCheck

        _isButtonEnabled.value = id.isNotBlank() &&
                pw.isNotBlank() &&
                pwCheck.isNotBlank() &&
                name.isNotBlank() &&
                isPwMatched
    }

    fun register(id: String, pw: String, name: String) {
        // 빈 값 체크
        if (id.isBlank() || pw.isBlank() || name.isBlank()) {
            _joinState.value = JoinUiState.Error("모든 정보를 입력해주세요.")
            return
        }

        _joinState.value = JoinUiState.Loading

        viewModelScope.launch {
            val result = authRepository.register(id, pw, name)

            result.onSuccess {
                _joinState.value = JoinUiState.Success
            }.onFailure { exception ->
                _joinState.value = JoinUiState.Error(exception.message ?: "회원가입 중 오류가 발생했습니다.")
            }
        }
    }
}