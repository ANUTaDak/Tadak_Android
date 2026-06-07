package kr.ac.anu.tadak.presentation.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.ac.anu.tadak.data.local.TokenManager
import kr.ac.anu.tadak.data.remote.TireAnalysisResponse
import kr.ac.anu.tadak.data.repository.TireRepository
import java.io.File
import javax.inject.Inject

sealed class DiagnoseUiState {
    object Idle : DiagnoseUiState()
    object Loading : DiagnoseUiState() // 분석 중 (로딩 띄우기)
    data class Success(val result: TireAnalysisResponse) : DiagnoseUiState() // 분석 성공 (결과 데이터)
    data class Error(val message: String) : DiagnoseUiState() // 실패
}

@HiltViewModel
class DiagnoseViewModel @Inject constructor(
    private val tireRepository: TireRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _diagnoseState = MutableStateFlow<DiagnoseUiState>(DiagnoseUiState.Idle)
    val diagnoseState: StateFlow<DiagnoseUiState> = _diagnoseState.asStateFlow()

    fun analyzeTire(imageFile: File) {
        _diagnoseState.value = DiagnoseUiState.Loading // 💡 로딩 시작!

        viewModelScope.launch {
            val token = tokenManager.getToken()

            if (token == null) {
                _diagnoseState.value = DiagnoseUiState.Error("토큰이 없습니다.")
                return@launch
            }

            val result = tireRepository.analyzeTire(token, imageFile)

            result.onSuccess { response ->
                _diagnoseState.value = DiagnoseUiState.Success(response)
            }.onFailure { exception ->
                _diagnoseState.value = DiagnoseUiState.Error(exception.message ?: "분석 실패")
            }
        }
    }
}