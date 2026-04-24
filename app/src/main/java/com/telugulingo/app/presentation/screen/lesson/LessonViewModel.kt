package com.telugulingo.app.presentation.screen.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.Lesson
import com.telugulingo.app.domain.model.Vocabulary
import com.telugulingo.app.domain.repository.LessonRepository
import com.telugulingo.app.domain.repository.UserRepository
import com.telugulingo.app.domain.repository.VocabularyRepository
import com.telugulingo.app.service.audio.TextToSpeechService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lesson: Lesson? = null,
    val vocabulary: List<Vocabulary> = emptyList(),
    val currentCardIndex: Int = 0,
    val isLoading: Boolean = true,
    val isSpeaking: Boolean = false,
    val allCardsViewed: Boolean = false
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val lessonRepository: LessonRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userRepository: UserRepository,
    private val ttsService: TextToSpeechService
) : ViewModel() {

    private val lessonId: Long = savedStateHandle.get<Long>("lessonId") ?: 1L

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
        observeTTS()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            val lesson = lessonRepository.getLessonById(lessonId)
            val vocabulary = vocabularyRepository.getVocabularyForLessonOnce(lessonId)

            _uiState.update {
                it.copy(
                    lesson = lesson,
                    vocabulary = vocabulary,
                    isLoading = false
                )
            }
        }
    }

    private fun observeTTS() {
        viewModelScope.launch {
            ttsService.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
            }
        }
    }

    fun nextCard() {
        _uiState.update { state ->
            val nextIndex = state.currentCardIndex + 1
            state.copy(
                currentCardIndex = nextIndex.coerceAtMost(state.vocabulary.size - 1),
                allCardsViewed = nextIndex >= state.vocabulary.size
            )
        }
    }

    fun previousCard() {
        _uiState.update { state ->
            val prevIndex = (state.currentCardIndex - 1).coerceAtLeast(0)
            state.copy(
                currentCardIndex = prevIndex,
                allCardsViewed = false
            )
        }
    }

    fun playAudio() {
        val currentVocab = _uiState.value.vocabulary.getOrNull(_uiState.value.currentCardIndex)
        currentVocab?.let { ttsService.speak(it.wordTelugu) }
    }

    fun markCardViewed() {
        viewModelScope.launch {
            userRepository.addXP(5)
        }
    }

    fun completeLesson() {
        viewModelScope.launch {
            val lesson = _uiState.value.lesson ?: return@launch
            userRepository.addXP(lesson.xpReward)
            userRepository.updateStreak()

            // Advance daily lesson index
            val user = userRepository.getUserOnce()
            user?.let {
                userRepository.updateUser(it.copy(dailyLessonIndex = it.dailyLessonIndex + 1))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.stop()
    }
}
