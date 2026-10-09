package com.example.game.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playTap() {
        vibrate(30, 80)
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 700f, durationMs = 40, attackMs = 5, decayMs = 35)
        }
    }

    fun playEscape() {
        vibrate(50, 160)
        if (!isSoundEnabled) return
        scope.launch {
            // Ascending quick swoosh tone
            playFrequencySweep(startFreq = 450f, endFreq = 950f, durationMs = 120)
        }
    }

    fun playBlocked() {
        vibrate(80, 255)
        if (!isSoundEnabled) return
        scope.launch {
            // Dull double low thud
            playTone(frequency = 160f, durationMs = 70, attackMs = 5, decayMs = 65)
        }
    }

    fun playHeartLost() {
        vibrate(120, 200)
        if (!isSoundEnabled) return
        scope.launch {
            playFrequencySweep(startFreq = 380f, endFreq = 180f, durationMs = 180)
        }
    }

    fun playHint() {
        vibrate(25, 100)
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 880f, durationMs = 80, attackMs = 10, decayMs = 70)
            playTone(frequency = 1174f, durationMs = 100, attackMs = 10, decayMs = 90)
        }
    }

    fun playLevelComplete() {
        vibratePattern(longArrayOf(0, 60, 40, 80, 40, 120))
        if (!isSoundEnabled) return
        scope.launch {
            // Uplifting victory arpeggio: C5 -> E5 -> G5 -> C6
            val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (note in notes) {
                playTone(frequency = note, durationMs = 90, attackMs = 10, decayMs = 80)
            }
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int = 128) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Graceful fallback if device lacks vibrator
        }
    }

    private fun vibratePattern(timings: LongArray) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    private fun playTone(frequency: Float, durationMs: Int, attackMs: Int, decayMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val attackSamples = (attackMs * sampleRate) / 1000
            val decaySamples = (decayMs * sampleRate) / 1000

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val raw = sin(2.0 * PI * frequency * time)

                // Envelope
                val envelope = when {
                    i < attackSamples -> i.toFloat() / attackSamples.coerceAtLeast(1)
                    i > numSamples - decaySamples -> (numSamples - i).toFloat() / decaySamples.coerceAtLeast(1)
                    else -> 1.0f
                }

                buffer[i] = (raw * envelope * Short.MAX_VALUE * 0.7f).toInt().toShort()
            }

            writeAudio(buffer, sampleRate)
        } catch (_: Exception) {
            // Ignore audio generation issues
        }
    }

    private fun playFrequencySweep(startFreq: Float, endFreq: Float, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val phaseIncrement = 2.0 * PI * currentFreq / sampleRate
                phase += phaseIncrement

                val envelope = (1.0 - progress * 0.4) * (if (i < 100) i / 100.0 else 1.0)
                buffer[i] = (sin(phase) * envelope * Short.MAX_VALUE * 0.6f).toInt().toShort()
            }

            writeAudio(buffer, sampleRate)
        } catch (_: Exception) {
            // Ignore
        }
    }

    private fun writeAudio(buffer: ShortArray, sampleRate: Int) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
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
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
    }
}
