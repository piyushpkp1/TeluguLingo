package com.telugulingo.app.service.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitializing = false
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private var teluguAvailable = false
    private var pendingSpeak: SpeakRequest? = null

    data class SpeakRequest(val telugu: String, val romanized: String)

    fun initialize() {
        if (_isReady.value || isInitializing) return
        isInitializing = true
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val teluguResult = tts?.setLanguage(Locale("te", "IN"))
                teluguAvailable = teluguResult != TextToSpeech.LANG_MISSING_DATA &&
                        teluguResult != TextToSpeech.LANG_NOT_SUPPORTED

                if (!teluguAvailable) {
                    // Fall back to English for romanized pronunciation
                    tts?.setLanguage(Locale.US)
                }

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })

                _isReady.value = true
                isInitializing = false

                // Speak any text that was requested while initializing
                pendingSpeak?.let { request ->
                    pendingSpeak = null
                    speakInternal(request)
                }
            } else {
                isInitializing = false
            }
        }
    }

    /**
     * Speak a word — pass both the Telugu script and romanized form.
     * If Telugu TTS is available on the device, the Telugu text is spoken.
     * Otherwise, the romanized text is spoken using English TTS as a fallback.
     */
    fun speak(teluguText: String, romanizedText: String = teluguText) {
        val request = SpeakRequest(teluguText, romanizedText)
        if (_isReady.value) {
            speakInternal(request)
        } else {
            pendingSpeak = request
            initialize()
        }
    }

    private fun speakInternal(request: SpeakRequest) {
        val textToSpeak = if (teluguAvailable) request.telugu else request.romanized

        // Ensure the correct locale is active
        if (teluguAvailable) {
            tts?.setLanguage(Locale("te", "IN"))
        } else {
            tts?.setLanguage(Locale.US)
        }

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate.coerceIn(0.25f, 4f))
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isReady.value = false
        isInitializing = false
        teluguAvailable = false
    }
}
