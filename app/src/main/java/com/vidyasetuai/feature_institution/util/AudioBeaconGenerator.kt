package com.vidyasetuai.feature_institution.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object AudioBeaconGenerator {

    private const val SAMPLE_RATE = 44100

    /**
     * Synthesizes and plays a 3.0-second Near-Ultrasonic Tone (18.5kHz - 19.5kHz)
     * corresponding to the staff's 4-digit audio code.
     * 
     * Human ears hear 100% ABSOLUTE SILENCE (Inaudible), while PC microphone captures the frequency.
     */
    suspend fun playBeaconTone(audioCode: String, durationMs: Long = 3000): Boolean = withContext(Dispatchers.IO) {
        var audioTrack: AudioTrack? = null
        try {
            val codeNum = audioCode.toIntOrNull() ?: 1001
            // Map 4-digit code (e.g. 1004) to high-frequency band 18,000Hz - 19,500Hz with 10Hz steps
            val frequencyHz = 18000.0 + ((codeNum - 1000) % 1500) * 10.0

            val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val sampleBuffer = ShortArray(totalSamples)

            for (i in 0 until totalSamples) {
                val time = i.toDouble() / SAMPLE_RATE
                val sineValue = sin(2.0 * Math.PI * frequencyHz * time)
                sampleBuffer[i] = (sineValue * Short.MAX_VALUE * 0.8).toInt().toShort()
            }

            val bufferSize = sampleBuffer.size * 2 // 16-bit PCM = 2 bytes per sample

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sampleBuffer, 0, sampleBuffer.size)
            audioTrack.play()

            // Wait for 3.0 seconds playback to finish
            kotlinx.coroutines.delay(durationMs)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (e: Exception) {
                // Ignore cleanup exceptions
            }
        }
    }
}
