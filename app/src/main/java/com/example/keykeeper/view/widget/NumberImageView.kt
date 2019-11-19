package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.example.keykeeper.R

class NumberImageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    ImageView(context, attributeSet, defStyleAttr) {
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private val number = kotlin.run {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.NumberImageView)
        val t = a.getString(R.styleable.NumberImageView_number) ?: " "
        Log.d(TAG, "test: $t")
        a.recycle()
        t
    }
    private val mPaint = Paint()
    private val textPaint = Paint()

    init {
        mPaint.run {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = Color.WHITE
            strokeWidth = STROKE_WIDTH.toFloat()
        }
        textPaint.run {
            style = Paint.Style.STROKE
            isAntiAlias = true
            color = Color.WHITE
            textSize = sp2px(18)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val mWidth = (RADIUS + STROKE_WIDTH) * 2 + paddingLeft + paddingRight
        val mHeight = (RADIUS + STROKE_WIDTH) * 2 + paddingBottom + paddingTop
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
        canvas?.drawCircle(width / 2f, height / 2f, RADIUS.toFloat(), mPaint)
        canvas?.drawText(
            number,
            (width - getTextWidth(number)) / 2f,
            (height + getTextHeight(number)) / 2f,
            textPaint
        )
    }

    private fun getTextHeight(letter: String): Float {
        val rect = Rect()
        textPaint.getTextBounds(letter, 0, 1, rect)
        return rect.height().toFloat()
    }

    private fun sp2px(spValue: Int): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

    private fun getTextWidth(letter: String): Float = textPaint.measureText(letter)

    companion object {
        const val TAG = "NumberLockTest"
        const val RADIUS = 80
        const val STROKE_WIDTH = 10
    }
}