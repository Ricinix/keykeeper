package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

/**
 * 自定义的圆
 */
class CircleImageView(context: Context, attributeSet: AttributeSet?, defStyleRes: Int) :
    ImageView(context, attributeSet, defStyleRes) {
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    // 新建画笔
    private val circlePaint = Paint()

    init {
        initPaint()
    }

    // 白色抗锯齿线条
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
        // 圆的直径（包括画笔宽度）加上padding
        val mWidth = (RADIUS + STROKE_WIDTH) * 2 + paddingLeft + paddingRight
        val mHeight = (RADIUS + STROKE_WIDTH) * 2 + paddingBottom + paddingTop
        // 解决自适应
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
        // 画圆
        canvas?.drawCircle(width / 2f, height / 2f, RADIUS.toFloat(), circlePaint)
    }

    /**
     * 设置为实心圆
     */
    fun setFill() {
        circlePaint.style = Paint.Style.FILL
        invalidate()
    }

    /**
     * 设置为空心圆
     */
    fun setStroke() {
        circlePaint.style = Paint.Style.STROKE
        invalidate()
    }

    companion object {
        const val RADIUS = 40
        const val STROKE_WIDTH = 10
    }
}