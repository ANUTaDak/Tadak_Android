package kr.ac.anu.tadak.presentation.viewmodel.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kr.ac.anu.tadak.data.local.TokenManager
import kr.ac.anu.tadak.data.repository.CachedDiagnostic
import kr.ac.anu.tadak.data.repository.TireRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tireRepository: TireRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val latestResult: StateFlow<CachedDiagnostic?> = tireRepository.latestResult

    fun getUsername(): String {
        return tokenManager.getUsername() ?: "사용자"
    }
}
