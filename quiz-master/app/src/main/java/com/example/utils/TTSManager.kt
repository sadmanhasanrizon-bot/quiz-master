package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object TTSManager : TextToSpeech.OnInitListener {

    private const val TAG = "TTSManager"

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var currentLocale: Locale = Locale("bn", "BD")

    private val _speakingText = MutableStateFlow<String?>(null)
    val speakingText: StateFlow<String?> = _speakingText.asStateFlow()

    private val _isTtsAvailable = MutableStateFlow(true)
    val isTtsAvailable: StateFlow<Boolean> = _isTtsAvailable.asStateFlow()

    private val _ttsErrorMessage = MutableStateFlow<String?>(null)
    val ttsErrorMessage: StateFlow<String?> = _ttsErrorMessage.asStateFlow()

    fun initialize(context: Context, locale: Locale = Locale("bn", "BD")) {
        currentLocale = locale
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            updateLanguage(currentLocale)
            setupUtteranceListener()
            Log.d(TAG, "TTS initialized successfully")
        } else {
            isInitialized = false
            _isTtsAvailable.value = false
            _ttsErrorMessage.value = "TTS Initialization failed"
            Log.e(TAG, "TTS Initialization failed with status $status")
        }
    }

    fun updateLanguage(locale: Locale) {
        currentLocale = locale
        if (!isInitialized || tts == null) return

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Try generic language locale if country specific fails
            val genericLocale = Locale(locale.language)
            val genericResult = tts?.setLanguage(genericLocale)
            if (genericResult == TextToSpeech.LANG_MISSING_DATA || genericResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                _isTtsAvailable.value = false
                _ttsErrorMessage.value = "Voice data missing for ${locale.displayName}"
                Log.w(TAG, "Language missing or not supported for ${locale.displayName}")
            } else {
                _isTtsAvailable.value = true
                _ttsErrorMessage.value = null
            }
        } else {
            _isTtsAvailable.value = true
            _ttsErrorMessage.value = null
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Speech started
            }

            override fun onDone(utteranceId: String?) {
                _speakingText.value = null
            }

            override fun onError(utteranceId: String?) {
                _speakingText.value = null
            }
        })
    }

    fun speak(text: String, locale: Locale? = null) {
        if (text.isBlank()) return

        locale?.let { updateLanguage(it) }

        if (isSpeakingText(text)) {
            // Toggle off if currently speaking same text
            stop()
            return
        }

        // Stop any ongoing speech first (No overlapping voice!)
        stop()

        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not initialized yet")
            return
        }

        _speakingText.value = text
        val utteranceId = "utterance_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
        _speakingText.value = null
    }

    fun isSpeakingText(text: String): Boolean {
        return _speakingText.value == text
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
        tts = null
        isInitialized = false
    }
}
