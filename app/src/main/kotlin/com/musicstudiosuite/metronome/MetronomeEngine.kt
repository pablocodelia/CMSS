package com.musicstudiosuite.metronome

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin

class MetronomeEngine {
    @Volatile
    private var bpm: Int = 120
    private var isPlaying: Boolean = false
    private var job: Job? = null

    private val sampleRate = 44100
    private val audioTrack: AudioTrack by lazy {
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(
                AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    private val clickSound: ShortArray by lazy {
        val durationMs = 10
        val numSamples = (sampleRate * durationMs / 1000)
        val samples = ShortArray(numSamples)
        val frequency = 1000.0 // 1kHz click
        for (i in 0 until numSamples) {
            // Apply a simple fade-out to avoid pop
            val envelope = 1.0 - (i.toDouble() / numSamples)
            samples[i] = (sin(2.0 * PI * i * frequency / sampleRate) * Short.MAX_VALUE * 0.5 * envelope).toInt().toShort()
        }
        samples
    }

    fun setBpm(newBpm: Int) {
        bpm = newBpm
    }

    fun start(scope: CoroutineScope, onBeat: (Int) -> Unit) {
        if (isPlaying) return
        isPlaying = true
        audioTrack.play()

        job = scope.launch(Dispatchers.Default) {
            var beatCount = 0
            var nextBeatSample = 0L
            var clickSampleIndex = -1
            val bufferSize = 1024
            val buffer = ShortArray(bufferSize)
            var sampleCount = 0L

            while (isActive && isPlaying) {
                val currentBpm = bpm
                val samplesPerBeat = (sampleRate * 60.0 / currentBpm).toLong()

                for (i in 0 until bufferSize) {
                    val currentSample = sampleCount + i
                    if (currentSample >= nextBeatSample) {
                        val beat = beatCount % 4
                        scope.launch(Dispatchers.Main) {
                            onBeat(beat)
                        }
                        beatCount++
                        nextBeatSample += samplesPerBeat
                        clickSampleIndex = 0
                    }

                    if (clickSampleIndex in clickSound.indices) {
                        buffer[i] = clickSound[clickSampleIndex]
                        clickSampleIndex++
                    } else {
                        buffer[i] = 0
                    }
                }

                audioTrack.write(buffer, 0, buffer.size)
                sampleCount += bufferSize
            }
        }
    }

    fun stop() {
        isPlaying = false
        job?.cancel()
        audioTrack.pause()
        audioTrack.flush()
    }
}
