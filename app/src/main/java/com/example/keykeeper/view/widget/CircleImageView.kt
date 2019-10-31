package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView

class CircleImageView(context: Context): ImageView(context) {
    private val circlePaint = Paint()
    private var mWidth = 0f
    private var mHeight = 0f

    init {
        initPaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    private fun initPaint(){
        circlePaint.run {
            style = Paint.Style.STROKE
            color = Color.WHITE
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 6, circlePaint)
    }

    fun setFill(){
        circlePaint.style = Paint.Style.FILL
        invalidate()
    }

    fun setStroke(){
        circlePaint.style = Paint.Style.STROKE
        invalidate()
    }
}