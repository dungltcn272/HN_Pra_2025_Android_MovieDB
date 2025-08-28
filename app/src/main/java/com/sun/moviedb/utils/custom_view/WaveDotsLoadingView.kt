package com.sun.moviedb.utils.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.cos

class WaveDotsLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val baseDotRadius = 16f
    private val dotSpacing = 28f
    private val waveHeight = 8f
    private val waveDelay = 200L

    private val colors = listOf(Color.RED, Color.BLUE, Color.YELLOW)
    private val paints = colors.map { color ->
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            this.color = color
        }
    }

    private var offsets = FloatArray(3)
    private var scales = FloatArray(3) { 1f }
    private val animators = mutableListOf<ValueAnimator>()

    init {
        startWaveAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val contentWidth = baseDotRadius * 2 * 3 + dotSpacing * 2
        val contentHeight = baseDotRadius * 2 + waveHeight * 2

        val desiredWidth = (contentWidth + paddingLeft + paddingRight).toInt()
        val desiredHeight = (contentHeight + paddingTop + paddingBottom).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val startX = (width - (baseDotRadius * 2 * 3 + dotSpacing * 2)) / 2f + baseDotRadius

        for (i in 0..2) {
            val cx = startX + i * (baseDotRadius * 2 + dotSpacing)
            val cy = centerY + offsets[i]
            val radius = baseDotRadius * scales[i]
            canvas.drawCircle(cx, cy, radius, paints[i])
        }
    }

    private fun startWaveAnimation() {
        for (i in 0..2) {
            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                startDelay = waveDelay * i
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val t = it.animatedValue as Float
                    offsets[i] = -waveHeight * cos(t * 2 * PI).toFloat()
                    scales[i] = 0.75f + 0.25f * (1 - cos(t * 2 * PI).toFloat()) / 2f
                    invalidate()
                }
            }
            animators.add(animator)
        }
        animators.forEach { it.start() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animators.forEach { it.cancel() }
    }
}
