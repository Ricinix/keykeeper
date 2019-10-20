package com.example.keykeeper.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.keykeeper.R


class SideBar(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int):
    View(context, attributeSet, defStyleAttr) {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)

    private lateinit var onChooseLetterChangedListener: OnChooseLetterChangedListener
    private val textPaint = Paint()
    private val bitmapPaint = Paint()
    private val titlePaint = Paint()
    private val shapePaint = Paint()

    private var choose = -1
    private val maxTextWidth = getMaxTextWidth()
    private val waterPoint = BitmapFactory.decodeResource(resources, R.drawable.water2)

    private val marginT = 100
    private val marginB = 300

    private val touchRange = Rect()

    init {
        titlePaint.color = Color.WHITE
        titlePaint.textSize = 80f
        titlePaint.isFakeBoldText = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("SizeTest", "onMeasure")
        textPaint.isAntiAlias = true
        textPaint.textSize = 25f
        val width = waterPoint.width + maxTextWidth + 30
        touchRange.bottom = MeasureSpec.getSize(heightMeasureSpec) - marginB
        touchRange.left = waterPoint.width
        touchRange.right = touchRange.left + maxTextWidth + 30
        touchRange.top = marginT
        textPaint.reset()
        setMeasuredDimension(paddingLeft + width + paddingRight, heightMeasureSpec)
        Log.v("SizeTest", "width:$width, height:${MeasureSpec.getSize(heightMeasureSpec)}")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val height = height - marginB - marginT
        val width = width
        val singleHeight = height / letters.size
        for (i in letters.indices){
            textPaint.color = Color.BLACK
            textPaint.isAntiAlias = true
            textPaint.textSize = 25f
            Log.v("PaintTest", waterPoint.toString())
            val x = width - (maxTextWidth + 30) / 2 - getTextWidth(letters[i]) / 2
            val y = (singleHeight * i + singleHeight + marginT).toFloat()
            if (i == choose){
                //画圈
                shapePaint.color = context.getColor(R.color.colorPrimary)
                canvas?.drawCircle(x + getTextWidth(letters[i]) / 2, y - getTextHeight(letters[i]) / 2,
                    (getMaxOfTextWidthHeight(letters[i]).toFloat() + 10) / 2, shapePaint)
                //设置字体为白色
                textPaint.color = Color.WHITE
                textPaint.isFakeBoldText = true
                //画水滴
                canvas?.drawBitmap(waterPoint, 0f,getBitMapYCor(i, singleHeight), bitmapPaint)
                //画水滴里的字
                canvas?.drawText(letters[i], waterPoint.width * 5 / 12 - getTitleWidth(letters[i]) / 2, getTitleYCor(i, singleHeight), titlePaint)
            }
            canvas?.drawText(letters[i], x, y, textPaint)
            textPaint.reset()
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.v("TouchTest", "dispatch!")
        val action = event?.action
        val x = event?.x?:0f
        val y = event?.y?:0f
        if (!touchRange.contains(x.toInt(), y.toInt())){
            Log.v("TouchTest", "cor is ($touchRange) \n press is (${x.toInt()}, ${y.toInt()})")
            Log.v("TouchTest", "not in range")
            cancelSelect()
            return false
        }
        val c = ((y - marginT) / (height - marginT - marginB) * letters.size).toInt()
        when (action){
            MotionEvent.ACTION_DOWN ->{
                if (choose != c){
                    if (-1 < c && c < letters.size){
                        onChooseLetterChangedListener.onChooseLetter(letters[c])
                        choose = c
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (choose != c){
                    if (-1 < c && c < letters.size){
                        onChooseLetterChangedListener.onChooseLetter(letters[c])
                        choose = c
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_CANCEL ->{
                cancelSelect()
            }
            MotionEvent.ACTION_UP ->{
                cancelSelect()
            }
        }
        return true
    }

    private fun getMaxTextWidth():Int{
        textPaint.textSize = 25f
        textPaint.isAntiAlias = true
        var max = 0f
        for (letter in letters){
            val wid = getTextWidth(letter)
            if (max < wid)
                max = wid
        }
        textPaint.reset()
        return max.toInt()
    }

    private fun getMaxOfTextWidthHeight(letter: String):Int{
        val height = getTextHeight(letter)
        val width = getTextWidth(letter)
        return if (height > width) height else width.toInt()
    }

    private fun getTextHeight(letter: String):Int{
        val rect = Rect()
        textPaint.getTextBounds(letter, 0, 1, rect)
        return rect.height()
    }

    private fun getTextWidth(letter: String):Float = textPaint.measureText(letter)

    private fun getTitleHeight(letter:String):Int{
        val rect = Rect()
        titlePaint.getTextBounds(letter, 0, 1, rect)
        return rect.height()
    }

    private fun getTitleWidth(letter: String):Float = titlePaint.measureText(letter)

    private fun getTitleYCor(i: Int, singleHeight: Int): Float{
        return singleHeight * i + singleHeight + marginT - getTextHeight(letters[i]).toFloat() / 2 + getTitleHeight(letters[i]) / 2
    }

    private fun getBitMapYCor(i: Int, singleHeight: Int):Float{
        return singleHeight * i + singleHeight + marginT - waterPoint.height / 2 - getTextHeight(letters[i]).toFloat() / 2
    }

    private fun cancelSelect(){
        choose = -1
        onChooseLetterChangedListener.onNoChooseLetter()
        invalidate()
    }

    fun setListener(OnChooseLetterChangedListener: OnChooseLetterChangedListener){
        this.onChooseLetterChangedListener = OnChooseLetterChangedListener
    }

    companion object{
        @JvmStatic
        val letters = listOf("↑", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "#")

    }



}
interface OnChooseLetterChangedListener{
    //滑动时
    fun onChooseLetter(s:String)

    //手指离开
    fun onNoChooseLetter()
}