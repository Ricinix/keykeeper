package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.keykeeper.R
import kotlin.math.max


class SideBar(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    View(context, attributeSet, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    private lateinit var onChooseLetterChangedListener: OnChooseLetterChangedListener
    // 字母画笔
    private val textPaint = Paint().apply {
        isAntiAlias = true
        textSize = 25f
    }

    // 大字母画笔
    private val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 80f
        isFakeBoldText = true
    }
    // 选中的圆形背景的画笔
    private val shapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorPrimary)
    }

    // 现在选的是第几个字母
    private var choose = -1

    // 获取26个字母中的最宽宽度
    private val maxTextWidth = getMaxTextWidth()

    // 获取26个字母中的最高高度
    private val maxTextHeight = getMaxTextHeight()

    // 图片
    private val waterPoint = context.getDrawable(R.drawable.ic_waterpoint)
    private val waterPointWidth = waterPoint?.intrinsicWidth ?: 0
    private val waterPointHeight = waterPoint?.intrinsicHeight ?: 0


    // 触摸的范围
    private val touchRange = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("SizeTest", "onMeasure!")
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        // 每个字母的高度乘以字母数量
        val mHeight = maxTextHeight * letters.size + PADDING_TOP + PADDING_BOTTOM
        // 图片宽度加字母宽度
        val mWidth = waterPointWidth + maxTextWidth + PADDING_LEFT + PADDING_RIGHT
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(
                mWidth,
                mHeight
            )
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mHeight)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }

        // 触摸范围为字母区域(PADDING_RIGHT也算在触摸范围内，因为这样效果更好)
        touchRange.run {
            bottom =
                if (heightMode == MeasureSpec.AT_MOST)
                    mHeight - PADDING_BOTTOM - paddingBottom
                else
                    heightSize - PADDING_BOTTOM - paddingBottom
            right =
                if (widthMode == MeasureSpec.AT_MOST)
                    mWidth - paddingRight
                else
                    widthSize - paddingRight
            left = right - PADDING_RIGHT - maxTextWidth
            top = PADDING_TOP + paddingTop
        }
        Log.v("SizeTest", "width:$width, height:$height")
    }

    override fun onDraw(canvas: Canvas?) {
        // 每个字母所占空间的高度
        val singleHeight =
            (height - PADDING_BOTTOM - PADDING_TOP - paddingTop - paddingBottom) / letters.size
        for (i in letters.indices) {
            textPaint.color = Color.BLACK
            textPaint.isFakeBoldText = false
            //坐标为字母的左下角
            val x =
                width - (maxTextWidth + PADDING_RIGHT + paddingRight) / 2 - getTextWidth(letters[i]) / 2
            val y = (singleHeight * (i + 1) + PADDING_TOP + paddingTop).toFloat()
            if (i == choose) {
                //画圈
                canvas?.drawCircle(
                    x + getTextWidth(letters[i]) / 2, y - getTextHeight(letters[i]) / 2,
                    (getMaxOfTextWidthHeight(letters[i]).toFloat() + 10) / 2, shapePaint
                )
                //设置字体为白色
                textPaint.color = Color.WHITE
                textPaint.isFakeBoldText = true
                //画水滴
                waterPoint?.run {
                    setBounds(
                        0,
                        getDrawableYCor(i, singleHeight).toInt(),
                        waterPointWidth,
                        (getDrawableYCor(i, singleHeight) + waterPointHeight).toInt()
                    )
                    if (canvas != null)
                        draw(canvas)
                }
                //画水滴里的字
                canvas?.drawText(
                    letters[i],
                    waterPointWidth * 5 / 12 - getTitleWidth(letters[i]) / 2,
                    getTitleYCor(i, singleHeight),
                    titlePaint
                )
            }
            // 画导航栏中的字
            canvas?.drawText(letters[i], x, y, textPaint)
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.v("TouchTest", "dispatch!")
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        // 如果不在范围内就不消费此事件，并使其分发给父级view/viewGroup
        if (!touchRange.contains(x.toInt(), y.toInt())) {
            Log.v("TouchTest", "cor is ($touchRange) \n press is (${x.toInt()}, ${y.toInt()})")
            Log.v("TouchTest", "not in range")
            cancelSelect()
            return false
        }
        return super.dispatchTouchEvent(event)
    }

    // 处理触摸事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        val y = event?.y ?: 0f
        // 触摸位置所占控件高度
        val touchHeightScale =
            (y - PADDING_TOP - paddingTop) / (height - PADDING_TOP - PADDING_BOTTOM - paddingTop - paddingBottom)
        // 触摸的是第几个字母
        val c = (touchHeightScale * letters.size).toInt()
        Log.v("TouchTest", "onTouch:   c:$c, touchHeightScale:$touchHeightScale")
        when (action) {
            // 按下或者滑动的时候，选中字母
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (choose != c) {
                    onChooseLetterChangedListener.onChooseLetter(letters[c])
                    choose = c
                    invalidate()
                }
            }
            // 滑出边缘时，撤销选中
            MotionEvent.ACTION_CANCEL -> {
                cancelSelect()
            }
            // 抬起手指时，撤销选中
            MotionEvent.ACTION_UP -> {
                performClick()
                cancelSelect()
            }
        }
        return true
    }

    // 不重写这个方法就有黄色警告
    override fun performClick(): Boolean {
        return super.performClick()
    }

    // 获取所有字母中最宽宽度
    private fun getMaxTextWidth(): Int {
        var max = 0f
        for (letter in letters) {
            max = max(max, getTextWidth(letter))
        }
        return max.toInt()
    }

    // 获取所有字母中的最高高度
    private fun getMaxTextHeight(): Int {
        var max = 0
        for (letter in letters) {
            max = max(max, getTextHeight(letter))
        }
        return max
    }

    // 获取此字母中宽度和高度中较大的那个（便于画背景圆）
    private fun getMaxOfTextWidthHeight(letter: String): Int {
        val height = getTextHeight(letter)
        val width = getTextWidth(letter)
        return if (height > width) height else width.toInt()
    }

    // 获取字母的高度
    private fun getTextHeight(letter: String): Int {
        val rect = Rect()
        textPaint.getTextBounds(letter, 0, 1, rect)
        return rect.height()
    }

    /**
     * 获取字母的宽度（这个比[getTextBounds()]更好，因为获取到的宽度会稍微宽一点，使得效果更好）
     * @link getTextBounds
     */
    private fun getTextWidth(letter: String): Float = textPaint.measureText(letter)

    // 测量标题（水滴里面的字母）的高度
    private fun getTitleHeight(letter: String): Int {
        val rect = Rect()
        titlePaint.getTextBounds(letter, 0, 1, rect)
        return rect.height()
    }

    // 测量标题（水滴里面的字母）的宽度
    private fun getTitleWidth(letter: String): Float = titlePaint.measureText(letter)

    // 获取水滴里的字母的y坐标(保证与字母导航栏中的对应字母的中心高度相同)
    private fun getTitleYCor(i: Int, singleHeight: Int): Float {
        return singleHeight * (i + 1) + paddingTop + PADDING_TOP - getTextHeight(letters[i]).toFloat() / 2 + getTitleHeight(
            letters[i]
        ) / 2
    }

    // 获取水滴的y坐标（保证与对应字母的中心对齐）
    private fun getDrawableYCor(i: Int, singleHeight: Int): Float {
        return singleHeight * (i + 1) + paddingTop + PADDING_TOP - waterPointHeight / 2 - getTextHeight(
            letters[i]
        ).toFloat() / 2
    }

    // 取消对字母的选中
    private fun cancelSelect() {
        choose = -1
        onChooseLetterChangedListener.onNoChooseLetter()
        invalidate()
    }

    // 设置监听（在外部的初始化中就需要设置）
    fun setListener(OnChooseLetterChangedListener: OnChooseLetterChangedListener) {
        this.onChooseLetterChangedListener = OnChooseLetterChangedListener
    }

    companion object {
        @JvmStatic
        val letters = listOf(
            "↑", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "#"
        )

        // 自己设置的padding，与在layout中设置的padding相独立，这里的设置是为了使显示效果更好
        const val PADDING_LEFT = 30
        const val PADDING_RIGHT = 30
        const val PADDING_TOP = 100
        const val PADDING_BOTTOM = 300

    }


}

interface OnChooseLetterChangedListener {
    //滑动时
    fun onChooseLetter(s: String)

    //手指离开
    fun onNoChooseLetter()
}