package com.musicstudiosuite.tuner

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.thread

class TunerEngine {
    private var dispatcher: AudioDispatcher? = null
    
    private val _currentFrequency = MutableStateFlow(-1f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()

    fun startListening() {
        if (dispatcher != null) return
        
        val sampleRate = 22050
        val bufferSize = 1024
        val overlap = 0

        dispatcher = TunerAudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap)

        val pdh = PitchDetectionHandler { result, _ ->
            val pitch = result.pitch
            _currentFrequency.value = pitch
        }

        val p = PitchProcessor(PitchEstimationAlgorithm.YIN, sampleRate.toFloat(), bufferSize, pdh)
        dispatcher?.addAudioProcessor(p)

        thread {
            dispatcher?.run()
        }
    }

    fun stopListening() {
        dispatcher?.stop()
        dispatcher = null
        _currentFrequency.value = -1f
    }
}
