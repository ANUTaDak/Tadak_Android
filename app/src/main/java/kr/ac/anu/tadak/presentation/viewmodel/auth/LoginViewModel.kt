package kr.ac.anu.tadak.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.data.repository.AuthRepository
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(id: String, pw: String) {
        if (id.isBlank() || pw.isBlank()) {
            _loginState.value = LoginUiState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }

        _loginState.value = LoginUiState.Loading

        viewModelScope.launch {
            val result = authRepository.login(id, pw)

            result.onSuccess { response ->
                _loginState.value = LoginUiState.Success(response.token)
            }.onFailure { exception ->
                _loginState.value = LoginUiState.Error(exception.message ?: "로그인 실패")
            }
        }
    }


}