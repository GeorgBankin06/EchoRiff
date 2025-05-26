package com.echoriff.echoriff.login.presentation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

class Wave @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val wavePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE // Wave fill color
        isAntiAlias = true
        alpha = 100  // Make fill semi-transparent if you like
    }

//    private val backgroundPaint = Paint().apply {
//        style = Paint.Style.FILL
//        color = Color.BLACK // Background color
//    }

    // Paint for first signal line (thicker)
    private val signalLinePaint1 = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 6f
        isAntiAlias = true
    }

    // Paint for second signal line (thinner)
    private val signalLinePaint2 = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 3f
        isAntiAlias = true
        alpha = 180
    }

    private val wavePath = Path()
    private val signalPath1 = Path()
    private val signalPath2 = Path()

    private var waveShift = 0f
    private val waveAmplitude = 200f
    private val waveLength = 600f

    private val animator = ValueAnimator.ofFloat(0f, waveLength).apply {
        duration = 3000L
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            waveShift = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawPaint(backgroundPaint)

        // Make baseline move up and down over time
        val verticalShift = 50f * sin(waveShift * 2 * Math.PI / waveLength) // adjust amplitude & speed
        val baseCenterY = height / 2f + verticalShift

        // First wave line
        signalPath1.reset()
        signalPath1.moveTo(0f, baseCenterY.toFloat())
        var x = 0f
        while (x <= width) {
            val amplitudeVariation = 0.7f + 0.6f * sin(x * 0.05)
            val y = (waveAmplitude * amplitudeVariation * sin((x + waveShift) * 2 * Math.PI / waveLength)).toFloat()
            signalPath1.lineTo(x, baseCenterY.toFloat() + y)
            x += 5f
        }
        canvas.drawPath(signalPath1, signalLinePaint1)

        // Second wave line
        signalPath2.reset()
        signalPath2.moveTo(0f, baseCenterY.toFloat())
        x = 0f
        while (x <= width) {
            val amplitudeVariation = 0.6f + 0.7f * cos(x * 0.03)
            val y = (waveAmplitude * amplitudeVariation * sin((x + waveShift + waveLength / 4) * 2 * Math.PI / waveLength)).toFloat()
            signalPath2.lineTo(x, baseCenterY.toFloat() + y)
            x += 5f
        }
        canvas.drawPath(signalPath2, signalLinePaint2)
    }
}