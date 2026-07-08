package com.shopify.carto.feature.ai_integration.voice

/**
 * Sealed state for the voice recognition lifecycle.
 * Hoisted to [AIChatScreen] so [ChatInput] stays stateless.
 */
sealed interface VoiceRecognitionState {
    /** Mic button idle — no active session. */
    data object Idle : VoiceRecognitionState

    /** System is ready; waiting for user to speak. */
    data object ReadyForSpeech : VoiceRecognitionState

    /** User has started speaking. [rmsDb] drives waveform amplitude. */
    data class Listening(val rmsDb: Float = 0f) : VoiceRecognitionState

    /** Speech recognised; partial text shown while user is still speaking. */
    data class Partial(val text: String) : VoiceRecognitionState

    /** Final result committed to the text field. */
    data class Result(val text: String) : VoiceRecognitionState

    /** Recognition failed. [errorCode] is [android.speech.SpeechRecognizer] error constant. */
    data class Error(val errorCode: Int) : VoiceRecognitionState
}
