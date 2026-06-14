package com.musicstudiosuite.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.math.log2
import kotlin.math.roundToInt

data class TuningResult(
    val note: String = "-",
    val cents: Float = 0f,
    val frequency: Float = 0f,
    val isDetected: Boolean = false
)

class TunerViewModel : ViewModel() {
    private val engine = TunerEngine()
    
    private val _tuningResult = MutableStateFlow(TuningResult())
    val tuningResult: StateFlow<TuningResult> = _tuningResult.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    init {
        engine.currentFrequency
            .onEach { freq ->
                if (freq > 0) {
                    _tuningResult.value = calculateTuning(freq)
                } else {
                    _tuningResult.value = TuningResult()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun calculateTuning(frequency: Float): TuningResult {
        // midi = 12 * log2(f / 440) + 69
        val midi = 12.0 * log2(frequency.toDouble() / 440.0) + 69.0
        val midiRounded = midi.roundToInt()
        
        val noteName = noteNames[midiRounded % 12]
        val octave = (midiRounded / 12) - 1
        val cents = ((midi - midiRounded) * 100.0).toFloat()
        
        return TuningResult(
            note = "$noteName$octave",
            cents = cents,
            frequency = frequency,
            isDetected = true
        )
    }

    fun toggleListening() {
        if (_isListening.value) {
            engine.stopListening()
            _isListening.value = false
        } else {
            engine.startListening()
            _isListening.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.stopListening()
    }
}
