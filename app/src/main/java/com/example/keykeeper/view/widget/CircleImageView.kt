package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import androidx.lifecycle.extensions.R
import kotlin.math.min

class CircleImageView(context: Context, attributeSet: AttributeSet?, defStyleRes: Int) :
    ImageView(context, attributeSet, defStyleRes) {
    constructor(context: Context, attributeSet: AttributeSet?):this(context, attributeSet, 0)
    constructor(context: Context):this(context, null)

    private val circlePaint = Paint()

    init {
        initPaint()
    }

    private fun initPaint() {
        circlePaint.run {
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH.toFloat()
            color = Color.WHITE
            isAntiAlias = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val mWidth = (RADIUS + STROKE_WIDTH) * 2 + paddingLeft + paddingRight
        val mHeight = (RADIUS + STROKE_WIDTH)  * 2 + paddingBottom + paddingTop
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight)
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(width / 2f, height / 2f, RADIUS.toFloat(), circlePaint)
    }

    fun setFill() {
        circlePaint.style = Paint.Style.FILL
        invalidate()
    }

    fun setStroke() {
        circlePaint.style = Paint.Style.STROKE
        invalidate()
    }

    companion object{
        const val RADIUS = 40
        const val STROKE_WIDTH = 10
    }
}