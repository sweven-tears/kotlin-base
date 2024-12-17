package com.app.test.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.app.test.Constant
import com.app.test.R

class HeadPictureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var text: String? = null
    private var textColor = Color.WHITE
    private var circleColor = Constant.colorPrimary
    private val paint = Paint()

    init {
        paint.typeface = Typeface.DEFAULT_BOLD

        context.obtainStyledAttributes(attrs, R.styleable.HeadPictureView).apply {
            text = getString(R.styleable.HeadPictureView_android_text)
            textColor = getColor(R.styleable.HeadPictureView_android_textColor, Color.WHITE)
            circleColor = getColor(R.styleable.HeadPictureView_circleColor, Constant.colorPrimary)
            recycle()
        }
    }

    fun setText(text: String?) {
        this.text = text
        invalidate()
    }

    fun setCircleColor(circleColor: Int) {
        this.circleColor = circleColor
        invalidate()
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = width.coerceAtMost(height) / 2f

        // 画圆
        paint.color = circleColor
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)

        // 画文字
        paint.color = textColor
        paint.textSize = radius
        paint.textAlign = Paint.Align.CENTER
        val textHeight = -paint.ascent() + paint.descent()
        val y = height / 2 + textHeight / 3.5f
        if (!text.isNullOrEmpty()) {
            val first = text!!.first().toString()
            canvas.drawText(first, width / 2f, y, paint)
        }
    }
}