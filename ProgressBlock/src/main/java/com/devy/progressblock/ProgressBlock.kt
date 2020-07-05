package com.devy.progressblock

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.view.animation.PathInterpolatorCompat

class ProgressBlock : View {

    var maxCount: Int = 10
        set(value) {
            field = value
            if (maxCount < currentCount) throw Exception("maxCount must bigger than currentCount")
        }
    var currentCount: Int = 5
        set(value) {
            field = value
            if (maxCount < currentCount) throw Exception("maxCount must bigger than currentCount")
        }

    private val valueAnimator = ValueAnimator()

    private val linePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
    }
    private val countedPaint: Paint = Paint()

    private var animatedPercent: Float? = null

    var interpolator = PathInterpolatorCompat.create(Path().apply {
        cubicTo(0.415f, 0.490f, 0.570f, 1.365f, 1f, 1f)
    })
        set(value) {
            field = value
            valueAnimator.interpolator = value
        }

    var lineColor: Int = Color.BLACK
        set(value) {
            linePaint.color = value
            field = value
        }
    var countedColor: Int = Color.YELLOW
    var unCountedColor: Int = Color.WHITE
    var lineWidth: Float = 2f
        set(value) {
            field = value
            linePaint.strokeWidth = lineWidth
        }
    var autoStart: Boolean = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBlock)
        maxCount = a.getInteger(R.styleable.ProgressBlock_maxCount, 10)
        currentCount = a.getInteger(R.styleable.ProgressBlock_currentCount, 5)
        setStartDelay(a.getFloat(R.styleable.ProgressBlock_startDelay, 0f).toLong())
        setDuration(a.getFloat(R.styleable.ProgressBlock_duration, 1000f).toLong())
        lineColor = a.getColor(R.styleable.ProgressBlock_lineColor, Color.BLACK)
        countedColor = a.getColor(R.styleable.ProgressBlock_countedColor, Color.YELLOW)
        unCountedColor = a.getColor(R.styleable.ProgressBlock_unCountedColor, Color.WHITE)
        lineWidth = a.getDimension(R.styleable.ProgressBlock_lineWidth, 2f)
        autoStart = a.getBoolean(R.styleable.ProgressBlock_autoStart, false)

        setBackgroundColor(unCountedColor)

        linePaint.color = lineColor
        linePaint.strokeWidth = lineWidth

        countedPaint.color = countedColor

        valueAnimator.apply {
            setFloatValues(0f, 1f)
            addUpdateListener {
                animatedPercent = it.animatedFraction
                invalidate()
            }
            interpolator = this@ProgressBlock.interpolator
        }

        if (autoStart) start()

    }

    fun setDuration(duration: Long) {
        valueAnimator.duration = duration
    }

    fun setStartDelay(startDelay: Long) {
        valueAnimator.startDelay = startDelay
    }

    fun start() {
        with(valueAnimator) {
            when {
                isRunning -> {
                    cancel()
                    start()
                }
                else -> start()
            }
        }
    }

    fun stop() {
        with(valueAnimator) {
            if (isRunning) pause()
        }
    }

    fun clear() {
        animatedPercent = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(
            0f,
            0f,
            width * currentCount / maxCount * (animatedPercent ?: 0f),
            height.toFloat(),
            countedPaint
        )
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), linePaint)
        for (i in 0..maxCount) {
            canvas?.drawLine(
                width * i / maxCount.toFloat(),
                0f,
                width * i / maxCount.toFloat(),
                height.toFloat(),
                linePaint
            )
        }
    }

}