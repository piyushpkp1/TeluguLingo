package com.telugulingo.app.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.User
import com.telugulingo.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val user: User? = null,
    val speechRate: Float = 1.0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
    }

    fun resetProgress() {
        viewModelScope.launch {
            val currentUser = userRepository.getUserOnce() ?: return@launch
            userRepository.updateUser(
                currentUser.copy(
                    totalXP = 0,
                    currentLevel = 1,
                    currentStreak = 0,
                    longestStreak = 0,
                    dailyLessonIndex = 0
                )
            )
        }
    }
}
