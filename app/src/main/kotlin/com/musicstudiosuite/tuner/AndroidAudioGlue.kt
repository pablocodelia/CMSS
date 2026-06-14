package com.musicstudiosuite.tuner

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioInputStream
import be.tarsos.dsp.io.TarsosDSPAudioFormat

class AndroidAudioInputStream(private val audioRecord: AudioRecord, private val format: TarsosDSPAudioFormat) : TarsosDSPAudioInputStream {
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return audioRecord.read(b, off, len)
    }

    override fun skip(n: Long): Long {
        return 0
    }

    override fun close() {
        audioRecord.stop()
        audioRecord.release()
    }

    override fun getFormat(): TarsosDSPAudioFormat {
        return format
    }

    override fun getFrameLength(): Long {
        return -1
    }
}

object TunerAudioDispatcherFactory {
    fun fromDefaultMicrophone(sampleRate: Int, bufferSize: Int, overlap: Int): AudioDispatcher {
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val actualBufferSize = if (minBufferSize > bufferSize) minBufferSize else bufferSize
        
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            actualBufferSize
        )
        
        audioRecord.startRecording()
        
        val format = TarsosDSPAudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val inputStream = AndroidAudioInputStream(audioRecord, format)
        
        return AudioDispatcher(inputStream, bufferSize, overlap)
    }
}
