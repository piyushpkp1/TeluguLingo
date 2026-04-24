package com.telugulingo.app.service.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class RecognitionResult(
    val spokenText: String,
    val confidence: Float,
    val isAccurate: Boolean
)

@Singleton
class SpeechRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var recognizer: SpeechRecognizer? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _result = MutableStateFlow<RecognitionResult?>(null)
    val result: StateFlow<RecognitionResult?> = _result

    private var expectedText: String = ""

    fun initialize() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) return
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    fun startListening(expectedPhrase: String, onResult: (RecognitionResult) -> Unit) {
        if (recognizer == null) initialize()
        expectedText = expectedPhrase

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "te-IN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spoken = matches[0]
                    val similarity = calculateSimilarity(expectedText.lowercase(), spoken.lowercase())
                    val accuracy = similarity >= 0.6f
                    val result = RecognitionResult(spoken, similarity, accuracy)
                    _result.value = result
                    onResult(result)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
        _isListening.value = false
    }

    private fun calculateSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1f
        if (s1.isEmpty() || s2.isEmpty()) return 0f

        val distance = levenshteinDistance(s1, s2)
        val maxLen = max(s1.length, s2.length)
        return 1f - (distance.toFloat() / maxLen)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) dp[i - 1][j - 1]
                else 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
            }
        }
        return dp[s1.length][s2.length]
    }

    fun shutdown() {
        recognizer?.destroy()
        recognizer = null
    }
}
