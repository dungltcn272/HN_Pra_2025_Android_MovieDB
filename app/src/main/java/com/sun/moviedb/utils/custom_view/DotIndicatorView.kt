package com.sun.moviedb.utils.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.sun.moviedb.R
import kotlin.math.max

class DotIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var dotRadiusPx = dpToPx(3f)
    private var activeScale = 1.5f
    private var dotSpacingPx = dpToPx(6f)
    private var activeColor = ContextCompat.getColor(context, android.R.color.white)
    private var inactiveColor = 0xFFBDBDBD.toInt()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var count = 0
    private var current = 0

    // dynamic radii for animation
    private var radii = FloatArray(0)

    // animation duration
    private val animDur = 220L

    private var adapterObserver: RecyclerView.AdapterDataObserver? = null
    private var attachedViewPager2: ViewPager2? = null

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.DotIndicatorView) {
                dotRadiusPx = getDimension(R.styleable.DotIndicatorView_dotRadius, dotRadiusPx)
                activeScale = getFloat(R.styleable.DotIndicatorView_activeScale, activeScale)
                dotSpacingPx = getDimension(R.styleable.DotIndicatorView_dotSpacing, dotSpacingPx)
                activeColor = getColor(R.styleable.DotIndicatorView_activeColor, activeColor)
                inactiveColor = getColor(R.styleable.DotIndicatorView_inactiveColor, inactiveColor)
            }
        }
        paint.style = Paint.Style.FILL
    }

    private fun ensureRadii(size: Int) {
        if (radii.size != size) {
            radii = FloatArray(size) { dotRadiusPx }
            if (size > 0) radii[current] = dotRadiusPx * activeScale
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultSidePadding = dpToPx(8f)
        val extraEdge = dotRadiusPx * (activeScale - 1)
        val w =
            (count * 2 * dotRadiusPx) + ((count - 1).coerceAtLeast(0) * dotSpacingPx) + defaultSidePadding * 2 + extraEdge * 2
        val h = paddingTop + paddingBottom + (dotRadiusPx * activeScale * 2)
        setMeasuredDimension(
            resolveSize(w.toInt(), widthMeasureSpec),
            resolveSize(h.toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (count <= 0) return

        val extraEdge = dotRadiusPx * (activeScale - 1)
        val totalWidth = (count * 2 * dotRadiusPx) + ((count - 1) * dotSpacingPx) + extraEdge * 2

        var cx = (width - totalWidth) / 2f + extraEdge + dotRadiusPx

        for (i in 0 until count) {
            val r = if (i < radii.size) radii[i] else dotRadiusPx
            paint.color = if (i == current) activeColor else inactiveColor
            canvas.drawCircle(cx, height / 2f, r, paint)
            cx += 2 * dotRadiusPx + dotSpacingPx
        }
    }

    private fun setCount(n: Int) {
        count = max(0, n)
        ensureRadii(count)
        requestLayout()
        invalidate()
    }

    fun setCurrent(index: Int, animate: Boolean = true) {
        if (index < 0 || index >= count) return
        if (index == current) return
        val from = current
        current = index
        ensureRadii(count)
        if (!animate) {
            for (i in radii.indices) radii[i] = dotRadiusPx
            radii[index] = dotRadiusPx * activeScale
            invalidate()
            return
        }

        val startFrom = radii[from]
        val startTo = radii[index]
        val endFrom = dotRadiusPx
        val endTo = dotRadiusPx * activeScale

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = animDur
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { anim ->
            val v = anim.animatedValue as Float
            radii[from] = lerp(startFrom, endFrom, v)
            radii[index] = lerp(startTo, endTo, v)
            invalidate()
        }
        animator.start()
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    fun attachTo(viewPager2: ViewPager2) {
        setCount(viewPager2.adapter?.itemCount ?: 0)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrent(position)
            }
        })
        setCurrent(viewPager2.currentItem, false)
        // Remove old observer if exists
        attachedViewPager2?.adapter?.unregisterAdapterDataObserver(adapterObserver ?: return)
        // Register new observer
        adapterObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                setCount(viewPager2.adapter?.itemCount ?: 0)
                setCurrent(viewPager2.currentItem, false)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = onChanged()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = onChanged()
        }
        viewPager2.adapter?.registerAdapterDataObserver(adapterObserver!!)
        attachedViewPager2 = viewPager2
    }

    private fun dpToPx(dp: Float): Float = dp * context.resources.displayMetrics.density

}
