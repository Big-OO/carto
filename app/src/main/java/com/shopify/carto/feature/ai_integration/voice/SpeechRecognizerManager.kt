package com.shopify.carto.feature.ai_integration.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Thin wrapper around [SpeechRecognizer] that reports state through a callback.
 *
 * Lifecycle contract:
 *  - Call [startListening] to begin a recognition session.
 *  - Call [stopListening] to end early (onEndOfSpeech fires, results still delivered).
 *  - Call [destroy] when the owning composable leaves composition (DisposableEffect onDispose).
 *
 * All callbacks are delivered on the main thread by the platform.
 */
class SpeechRecognizerManager(
    private val context: Context,
    private val onStateChange: (VoiceRecognitionState) -> Unit,
) {
    private var recognizer: SpeechRecognizer? = null

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            onStateChange(VoiceRecognitionState.ReadyForSpeech)
        }

        override fun onBeginningOfSpeech() {
            onStateChange(VoiceRecognitionState.Listening(rmsDb = 0f))
        }

        override fun onRmsChanged(rmsdB: Float) {
            onStateChange(VoiceRecognitionState.Listening(rmsDb = rmsdB))
        }

        override fun onBufferReceived(buffer: ByteArray?) = Unit

        override fun onEndOfSpeech() {
            // Keep current state; result/error will arrive shortly
        }

        override fun onError(error: Int) {
            onStateChange(VoiceRecognitionState.Error(error))
        }

        override fun onResults(results: Bundle?) {
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
            onStateChange(VoiceRecognitionState.Result(text))
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val text = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
            if (text.isNotEmpty()) {
                onStateChange(VoiceRecognitionState.Partial(text))
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    fun startListening() {
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
            it.setRecognitionListener(listener)
            it.startListening(intent)
        }
    }

    fun stopListening() {
        recognizer?.stopListening()
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }
}
