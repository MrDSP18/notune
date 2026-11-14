package echo.music.iad1tya.eq.audio

import echo.music.iad1tya.eq.data.FilterType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class BiquadFilter(
    private val sampleRate: Int,
    private val frequency: Double,
    private val gain: Double,
    private val q: Double = 1.41,
    private val filterType: FilterType = FilterType.PK
) {
    
    private val safeGain = if (gain.isFinite()) gain.coerceIn(-18.0, 18.0) else 0.0
    private val safeQ = if (q.isFinite()) q.coerceIn(0.1, 18.0) else 1.41

    private var a0 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0
    private var b0 = 0.0
    private var b1 = 0.0
    private var b2 = 0.0

    
    private var x1L = 0.0
    private var x2L = 0.0
    private var y1L = 0.0
    private var y2L = 0.0

    private var x1R = 0.0
    private var x2R = 0.0
    private var y1R = 0.0
    private var y2R = 0.0

    init {
        calculateCoefficients()
    }

    
    private fun setPassthrough() {
        b0 = 1.0; b1 = 0.0; b2 = 0.0
        a0 = 1.0; a1 = 0.0; a2 = 0.0
    }

    private fun normalizeCoefficients(rawB0: Double, rawB1: Double, rawB2: Double, rawA0: Double, rawA1: Double, rawA2: Double) {
        val raw = doubleArrayOf(rawB0, rawB1, rawB2, rawA0, rawA1, rawA2)
        if (!raw.all { it.isFinite() } || kotlin.math.abs(rawA0) < 1.0e-12) {
            setPassthrough()
            return
        }

        val normalized = doubleArrayOf(
            rawB0 / rawA0,
            rawB1 / rawA0,
            rawB2 / rawA0,
            rawA1 / rawA0,
            rawA2 / rawA0
        )
        if (!normalized.all { it.isFinite() }) {
            setPassthrough()
            return
        }

        b0 = normalized[0]
        b1 = normalized[1]
        b2 = normalized[2]
        a1 = normalized[3]
        a2 = normalized[4]
        a0 = 1.0
    }

    private fun calculateCoefficients() {
        if (sampleRate <= 0 || !frequency.isFinite() || frequency <= 0.0 ||
            frequency >= sampleRate / 2.0 || !gain.isFinite() || !q.isFinite() || q <= 0.0
        ) {
            setPassthrough()
            return
        }
        when (filterType) {
            FilterType.PK -> calculatePeakingCoefficients()
            FilterType.LSC -> calculateLowShelfCoefficients()
            FilterType.HSC -> calculateHighShelfCoefficients()
            else -> calculatePeakingCoefficients()
        }
    }

    
    private fun calculatePeakingCoefficients() {
        val A = 10.0.pow(safeGain / 40.0) 
        val omega = 2.0 * PI * frequency / sampleRate
        val sinOmega = sin(omega)
        val cosOmega = cos(omega)
        val alpha = sinOmega / (2.0 * safeQ)

        
        b0 = 1.0 + alpha * A
        b1 = -2.0 * cosOmega
        b2 = 1.0 - alpha * A
        a0 = 1.0 + alpha / A
        a1 = -2.0 * cosOmega
        a2 = 1.0 - alpha / A

        
        normalizeCoefficients(b0, b1, b2, a0, a1, a2)
    }

    
    private fun calculateLowShelfCoefficients() {
        val A = sqrt(10.0.pow(safeGain / 20.0)) 
        val omega = 2.0 * PI * frequency / sampleRate
        val sinOmega = sin(omega)
        val cosOmega = cos(omega)
        val S = 1.0 
        val alpha = sinOmega / 2.0 * sqrt((A + 1.0 / A) * (1.0 / S - 1.0) + 2.0)
        val sqrtA = sqrt(A)

        
        val aPlusOne = A + 1.0
        val aMinusOne = A - 1.0
        val twoSqrtAAlpha = 2.0 * sqrtA * alpha

        b0 = A * (aPlusOne - aMinusOne * cosOmega + twoSqrtAAlpha)
        b1 = 2.0 * A * (aMinusOne - aPlusOne * cosOmega)
        b2 = A * (aPlusOne - aMinusOne * cosOmega - twoSqrtAAlpha)
        a0 = aPlusOne + aMinusOne * cosOmega + twoSqrtAAlpha
        a1 = -2.0 * (aMinusOne + aPlusOne * cosOmega)
        a2 = aPlusOne + aMinusOne * cosOmega - twoSqrtAAlpha

        
        normalizeCoefficients(b0, b1, b2, a0, a1, a2)
    }

    
    private fun calculateHighShelfCoefficients() {
        val A = sqrt(10.0.pow(safeGain / 20.0)) 
        val omega = 2.0 * PI * frequency / sampleRate
        val sinOmega = sin(omega)
        val cosOmega = cos(omega)
        val S = 1.0 
        val alpha = sinOmega / 2.0 * sqrt((A + 1.0 / A) * (1.0 / S - 1.0) + 2.0)
        val sqrtA = sqrt(A)

        
        val aPlusOne = A + 1.0
        val aMinusOne = A - 1.0
        val twoSqrtAAlpha = 2.0 * sqrtA * alpha

        b0 = A * (aPlusOne + aMinusOne * cosOmega + twoSqrtAAlpha)
        b1 = -2.0 * A * (aMinusOne + aPlusOne * cosOmega)
        b2 = A * (aPlusOne + aMinusOne * cosOmega - twoSqrtAAlpha)
        a0 = aPlusOne - aMinusOne * cosOmega + twoSqrtAAlpha
        a1 = 2.0 * (aMinusOne - aPlusOne * cosOmega)
        a2 = aPlusOne - aMinusOne * cosOmega - twoSqrtAAlpha

        
        normalizeCoefficients(b0, b1, b2, a0, a1, a2)
    }

    
    fun processSample(input: Double): Double {
        if (!input.isFinite()) {
            reset()
            return 0.0
        }

        val output = b0 * input + b1 * x1L + b2 * x2L - a1 * y1L - a2 * y2L
        if (!output.isFinite()) {
            reset()
            return 0.0
        }

        x2L = x1L
        x1L = input
        y2L = y1L
        y1L = output
        return output
    }

    
    fun processStereo(inputLeft: Double, inputRight: Double): Pair<Double, Double> {
        val left = inputLeft.takeIf { it.isFinite() } ?: 0.0
        val right = inputRight.takeIf { it.isFinite() } ?: 0.0
        val outputLeft = b0 * left + b1 * x1L + b2 * x2L - a1 * y1L - a2 * y2L
        val outputRight = b0 * right + b1 * x1R + b2 * x2R - a1 * y1R - a2 * y2R
        if (!outputLeft.isFinite() || !outputRight.isFinite()) {
            reset()
            return 0.0 to 0.0
        }

        x2L = x1L
        x1L = left
        y2L = y1L
        y1L = outputLeft
        x2R = x1R
        x1R = right
        y2R = y1R
        y1R = outputRight
        return outputLeft to outputRight
    }

    
    fun reset() {
        x1L = 0.0
        x2L = 0.0
        y1L = 0.0
        y2L = 0.0
        x1R = 0.0
        x2R = 0.0
        y1R = 0.0
        y2R = 0.0
    }
}