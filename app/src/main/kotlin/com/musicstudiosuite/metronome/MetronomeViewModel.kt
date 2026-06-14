package com.musicstudiosuite.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MetronomeViewModel : ViewModel() {
    private val engine = MetronomeEngine()

    private val _bpm = MutableStateFlow(120)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentBeat = MutableStateFlow(0)
    val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    fun togglePlayback() {
        if (_isPlaying.value) {
            engine.stop()
            _isPlaying.value = false
            _currentBeat.value = 0
        } else {
            engine.setBpm(_bpm.value)
            engine.start(viewModelScope) { beat ->
                _currentBeat.value = beat
            }
            _isPlaying.value = true
        }
    }

    private val tapTimestamps = mutableListOf<Long>()

    fun tapTempo() {
        val now = System.currentTimeMillis()
        
        // Clear history if last tap was more than 2 seconds ago
        if (tapTimestamps.isNotEmpty() && now - tapTimestamps.last() > 2000) {
            tapTimestamps.clear()
        }

        tapTimestamps.add(now)

        // Keep only last 4 taps for averaging
        if (tapTimestamps.size > 4) {
            tapTimestamps.removeAt(0)
        }

        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                intervals.add(tapTimestamps[i] - tapTimestamps[i - 1])
            }
            val averageInterval = intervals.average()
            val newBpm = (60000 / averageInterval).toInt()
            setBpm(newBpm)
        }
    }

    fun setBpm(newBpm: Int) {
        val coercedBpm = newBpm.coerceIn(40, 300)
        _bpm.value = coercedBpm
        engine.setBpm(coercedBpm)
    }

    fun incrementBpm() {
        setBpm(_bpm.value + 1)
    }

    fun decrementBpm() {
        setBpm(_bpm.value - 1)
    }

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }
}
