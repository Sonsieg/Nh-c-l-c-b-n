package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

enum class InstrumentTimbre(val labelVi: String) {
    PIANO("Piano (Dương Cầm)"),
    ORGAN("Organ (Phong Cầm)"),
    VIOLIN("Violon (Vĩ Cầm)"),
    BELL("Chuông Hòa Âm")
}

object MusicSynthesizer {
    private const val SAMPLE_RATE = 22050
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var activeSequenceJob: Job? = null

    fun midiToFrequency(midiNote: Int): Double {
        return 440.0 * 2.0.pow((midiNote - 69) / 12.0)
    }

    fun playNote(
        midiNote: Int,
        durationMs: Int = 550,
        volume: Float = 0.75f,
        timbre: InstrumentTimbre = InstrumentTimbre.PIANO
    ) {
        playFrequencies(listOf(midiToFrequency(midiNote)), durationMs, volume, timbre)
    }

    fun playChord(
        midiNotes: List<Int>,
        durationMs: Int = 850,
        volume: Float = 0.7f,
        timbre: InstrumentTimbre = InstrumentTimbre.PIANO
    ) {
        playFrequencies(midiNotes.map { midiToFrequency(it) }, durationMs, volume, timbre)
    }

    fun playMetronomeClick(isStrongBeat: Boolean, isSubBeat: Boolean = false) {
        scope.launch {
            try {
                val durationMs = if (isStrongBeat) 70 else 50
                val numSamples = (SAMPLE_RATE * durationMs / 1000).coerceAtLeast(1)
                val samples = ShortArray(numSamples)
                val baseFreq = when {
                    isStrongBeat -> 1180.0
                    isSubBeat -> 660.0
                    else -> 840.0
                }
                val amp = if (isStrongBeat) 0.85 else if (isSubBeat) 0.35 else 0.55
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val env = exp(-t * 48.0)
                    val wave = sin(2.0 * PI * baseFreq * t) + 0.35 * sin(2.0 * PI * baseFreq * 1.5 * t)
                    samples[i] = (wave * env * amp * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                writePcmSamples(samples)
            } catch (_: Throwable) {
            }
        }
    }

    fun playSequence(
        events: List<Pair<List<Int>, Int>>,
        timbre: InstrumentTimbre = InstrumentTimbre.PIANO,
        onStepChanged: (Int) -> Unit = {},
        onFinished: () -> Unit = {}
    ) {
        stopSequence()
        activeSequenceJob = scope.launch {
            events.forEachIndexed { index, (midiNotes, durationMs) ->
                if (!isActive) return@launch
                onStepChanged(index)
                if (midiNotes.isNotEmpty()) {
                    playChord(
                        midiNotes = midiNotes,
                        durationMs = (durationMs * 0.92).toInt().coerceAtLeast(120),
                        timbre = timbre
                    )
                }
                delay(durationMs.toLong().coerceAtLeast(80L))
            }
            onStepChanged(-1)
            onFinished()
        }
    }

    fun stopSequence() {
        activeSequenceJob?.cancel()
        activeSequenceJob = null
    }

    private fun playFrequencies(
        frequencies: List<Double>,
        durationMs: Int,
        volume: Float,
        timbre: InstrumentTimbre
    ) {
        if (frequencies.isEmpty()) return
        scope.launch {
            try {
                val safeDuration = durationMs.coerceIn(80, 3000)
                val numSamples = (SAMPLE_RATE * safeDuration / 1000).coerceAtLeast(1)
                val samples = ShortArray(numSamples)
                val norm = frequencies.size.toDouble().coerceAtLeast(1.0)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / numSamples
                    val env = when (timbre) {
                        InstrumentTimbre.PIANO -> (t / 0.012).coerceAtMost(1.0) * exp(-t * 3.2)
                        InstrumentTimbre.ORGAN -> (t / 0.02).coerceAtMost(1.0) * (if (progress > 0.88) (1.0 - progress) / 0.12 else 0.85)
                        InstrumentTimbre.VIOLIN -> (t / 0.07).coerceAtMost(1.0) * (if (progress > 0.82) (1.0 - progress) / 0.18 else 1.0)
                        InstrumentTimbre.BELL -> (t / 0.005).coerceAtMost(1.0) * exp(-t * 5.0)
                    }
                    var sum = 0.0
                    for (freq in frequencies) {
                        val f = if (timbre == InstrumentTimbre.VIOLIN) freq * (1.0 + 0.003 * sin(2.0 * PI * 5.5 * t)) else freq
                        sum += 0.65 * sin(2.0 * PI * f * t) +
                            0.24 * sin(2.0 * PI * f * 2.0 * t) +
                            0.11 * sin(2.0 * PI * f * 3.0 * t)
                    }
                    val scaled = (sum / norm) * env * volume.coerceIn(0.05f, 1.0f)
                    samples[i] = (scaled * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                writePcmSamples(samples)
            } catch (_: Throwable) {
            }
        }
    }

    private suspend fun writePcmSamples(samples: ShortArray) {
        val byteSize = samples.size * 2
        val track = AudioTrack.Builder()
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
            .setBufferSizeInBytes(byteSize.coerceAtLeast(2048))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        delay((samples.size * 1000L / SAMPLE_RATE) + 50L)
        track.stop()
        track.release()
    }
}
