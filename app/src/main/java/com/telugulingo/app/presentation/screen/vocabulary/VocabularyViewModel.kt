package com.telugulingo.app.presentation.screen.vocabulary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.Vocabulary
import com.telugulingo.app.domain.repository.VocabularyRepository
import com.telugulingo.app.service.audio.TextToSpeechService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VocabularyUiState(
    val vocabulary: List<Vocabulary> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vocabularyRepository: VocabularyRepository,
    private val ttsService: TextToSpeechService
) : ViewModel() {

    private val lessonId: Long = savedStateHandle.get<Long>("lessonId") ?: 1L

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    init {
        loadVocabulary()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            val vocabulary = vocabularyRepository.getVocabularyForLessonOnce(lessonId)
            _uiState.update {
                it.copy(
                    vocabulary = vocabulary,
                    isLoading = false
                )
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun nextCard() {
        _uiState.update {
            val next = (it.currentIndex + 1).coerceAtMost(it.vocabulary.size - 1)
            it.copy(currentIndex = next, isFlipped = false)
        }
    }

    fun previousCard() {
        _uiState.update {
            val prev = (it.currentIndex - 1).coerceAtLeast(0)
            it.copy(currentIndex = prev, isFlipped = false)
        }
    }

    fun speakWord(vocabulary: Vocabulary) {
        ttsService.speak(vocabulary.wordTelugu)
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.stop()
    }
}
