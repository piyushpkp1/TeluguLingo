package com.telugulingo.app.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.Lesson
import com.telugulingo.app.domain.model.Streak
import com.telugulingo.app.domain.model.User
import com.telugulingo.app.domain.model.XPAndLevel
import com.telugulingo.app.domain.repository.LessonRepository
import com.telugulingo.app.domain.repository.QuizRepository
import com.telugulingo.app.domain.repository.UserRepository
import com.telugulingo.app.domain.repository.VocabularyRepository
import com.telugulingo.app.service.audio.TextToSpeechService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val streak: Streak? = null,
    val xpAndLevel: XPAndLevel = XPAndLevel(0, 100, 1, 0f),
    val lessons: List<Lesson> = emptyList(),
    val todaysLesson: Lesson? = null,
    val lessonsCompleted: Int = 0,
    val wordsLearned: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val quizRepository: QuizRepository,
    private val ttsService: TextToSpeechService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        // Eagerly initialize TTS so the first speaker tap is instant
        ttsService.initialize()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Initialize data (each call is idempotent — checks before writing)
            userRepository.getUserOnce() ?: userRepository.createUser(User())
            lessonRepository.initializeLessons()
            vocabularyRepository.initializeVocabulary()
            quizRepository.initializeQuizzes()

            combine(
                userRepository.getUser(),
                userRepository.getStreak(),
                userRepository.getXPAndLevel(),
                lessonRepository.getAllLessons()
            ) { user, streak, xpAndLevel, lessons ->
                val todaysLesson = user?.dailyLessonIndex?.let { index ->
                    lessons.getOrNull(index.coerceAtMost(lessons.size - 1))
                } ?: lessons.firstOrNull()

                HomeUiState(
                    user = user,
                    streak = streak,
                    xpAndLevel = xpAndLevel,
                    lessons = lessons,
                    todaysLesson = todaysLesson,
                    lessonsCompleted = user?.dailyLessonIndex ?: 0,
                    wordsLearned = lessons.take(user?.dailyLessonIndex ?: 1).sumOf { it.vocabularyCount },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
