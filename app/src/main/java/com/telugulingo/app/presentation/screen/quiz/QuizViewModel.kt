package com.telugulingo.app.presentation.screen.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.QuestionType
import com.telugulingo.app.domain.model.QuizQuestion
import com.telugulingo.app.domain.repository.QuizRepository
import com.telugulingo.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val fillBlankAnswer: String = "",
    val isAnswerRevealed: Boolean = false,
    val isLoading: Boolean = true,
    val isComplete: Boolean = false,
    val correctAnswers: Int = 0,
    val xpEarned: Int = 0,
    val xpPerQuestion: Int = 15,
    val currentHearts: Int = 5,
    val maxHearts: Int = 5,
    val heartsDepleted: Boolean = false,
    val hasStreakFreeze: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val lessonId: Long = savedStateHandle.get<Long>("lessonId") ?: 1L

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
        loadUserState()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            val questions = quizRepository.getQuizForLessonOnce(lessonId)
            _uiState.update {
                it.copy(
                    questions = questions.shuffled(),
                    isLoading = false
                )
            }
        }
    }

    private fun loadUserState() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            currentHearts = it.currentHearts,
                            maxHearts = it.maxHearts,
                            hasStreakFreeze = it.streakFreezes > 0
                        )
                    }
                }
            }
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isAnswerRevealed) return
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun setFillBlankAnswer(answer: String) {
        if (_uiState.value.isAnswerRevealed) return
        _uiState.update { it.copy(fillBlankAnswer = answer) }
    }

    fun submitAnswer() {
        val state = _uiState.value
        val currentQuestion = state.questions.getOrNull(state.currentQuestionIndex) ?: return

        val isCorrect = when (currentQuestion.questionType) {
            QuestionType.FILL_IN_BLANK -> {
                state.fillBlankAnswer.trim().equals(currentQuestion.correctAnswerRomanized.trim(), ignoreCase = true) ||
                state.fillBlankAnswer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
            }
            else -> {
                val selectedIndex = state.selectedOptionIndex ?: return
                currentQuestion.options.getOrNull(selectedIndex) == currentQuestion.correctAnswer
            }
        }

        viewModelScope.launch {
            if (isCorrect) {
                userRepository.addXP(state.xpPerQuestion)
            } else {
                userRepository.loseHeart()
                val updatedUser = userRepository.getUserOnce()
                if ((updatedUser?.currentHearts ?: 5) <= 0) {
                    _uiState.update { it.copy(heartsDepleted = true) }
                }
            }
        }

        _uiState.update {
            it.copy(
                isAnswerRevealed = true,
                correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers
            )
        }
    }

    fun nextQuestion() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.currentQuestionIndex >= state.questions.size - 1) {
                val xp = state.correctAnswers * state.xpPerQuestion +
                    if (state.correctAnswers == state.questions.size) 25 else 0
                userRepository.addXP(xp)
                userRepository.incrementLessonsCompleted()
                _uiState.update {
                    it.copy(
                        isComplete = true,
                        xpEarned = xp
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentQuestionIndex = it.currentQuestionIndex + 1,
                        selectedOptionIndex = null,
                        fillBlankAnswer = "",
                        isAnswerRevealed = false
                    )
                }
            }
        }
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            userRepository.useStreakFreeze()
            userRepository.refillHearts()
            _uiState.update {
                it.copy(
                    currentHearts = it.maxHearts,
                    heartsDepleted = false,
                    hasStreakFreeze = false
                )
            }
        }
    }
}
