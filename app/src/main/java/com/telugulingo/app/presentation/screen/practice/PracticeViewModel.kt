package com.telugulingo.app.presentation.screen.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telugulingo.app.domain.model.Vocabulary
import com.telugulingo.app.domain.repository.VocabularyRepository
import com.telugulingo.app.service.audio.TextToSpeechService
import com.telugulingo.app.service.speech.RecognitionResult
import com.telugulingo.app.service.speech.SpeechRecognitionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeUiState(
    val vocabulary: List<Vocabulary> = emptyList(),
    val currentIndex: Int = 0,
    val isListening: Boolean = false,
    val recognitionResult: RecognitionResult? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vocabularyRepository: VocabularyRepository,
    private val ttsService: TextToSpeechService,
    private val speechRecognitionService: SpeechRecognitionService
) : ViewModel() {

    private val lessonId: Long = savedStateHandle.get<Long>("lessonId") ?: 1L

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    init {
        loadVocabulary()
        observeListening()
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

    private fun observeListening() {
        viewModelScope.launch {
            speechRecognitionService.isListening.collect { listening ->
                _uiState.update { it.copy(isListening = listening) }
            }
        }
        viewModelScope.launch {
            speechRecognitionService.result.collect { result ->
                _uiState.update { it.copy(recognitionResult = result) }
            }
        }
    }

    fun playAudio() {
        val currentVocab = _uiState.value.vocabulary.getOrNull(_uiState.value.currentIndex)
        currentVocab?.let { ttsService.speak(it.wordTelugu) }
    }

    fun startListening() {
        val currentVocab = _uiState.value.vocabulary.getOrNull(_uiState.value.currentIndex) ?: return
        speechRecognitionService.startListening(currentVocab.wordRomanized) { result ->
            _uiState.update { it.copy(recognitionResult = result) }
        }
    }

    fun stopListening() {
        speechRecognitionService.stopListening()
    }

    fun nextWord() {
        _uiState.update {
            it.copy(
                currentIndex = (it.currentIndex + 1).coerceAtMost(it.vocabulary.size - 1),
                recognitionResult = null
            )
        }
    }

    fun previousWord() {
        _uiState.update {
            it.copy(
                currentIndex = (it.currentIndex - 1).coerceAtLeast(0),
                recognitionResult = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognitionService.shutdown()
        ttsService.stop()
    }
}
