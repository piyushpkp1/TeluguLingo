package com.telugulingo.app.presentation.screen.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.Lesson
import com.telugulingo.app.domain.model.Streak
import com.telugulingo.app.domain.model.XPAndLevel
import com.telugulingo.app.domain.repository.LessonRepository
import com.telugulingo.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val xpAndLevel: XPAndLevel = XPAndLevel(0, 100, 1, 0f),
    val streak: Streak? = null,
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 30,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                userRepository.getXPAndLevel(),
                userRepository.getStreak(),
                lessonRepository.getAllLessons()
            ) { xpAndLevel, streak, lessons ->
                ProgressUiState(
                    xpAndLevel = xpAndLevel,
                    streak = streak,
                    lessonsCompleted = lessons.count { it.isCompleted },
                    totalLessons = lessons.size,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
