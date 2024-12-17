package pers.sweven.common.helper.textview

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.widget.TextView
import androidx.annotation.IntDef

/**
 * Created by Sweven on 2024/12/11--10:29.
 * Email: sweventears@163.com
 */
class TextViewHelperBuilder(val textView: TextView? = null) {
    var helper: TextViewHelper = TextViewHelper(textView)

    /**
     * 添加文本
     * @param [text] 文本
     * @param [textColor] 文本颜色 Int,String,TextColor
     * @param [textSize] 文本大小 Float,Int,TextSize
     * @param [textStyle] 文本样式
     * @param [strikethrough] 删除线
     * @param [underline] 下划线
     * @param [background] 背景
     * @param [clickListener] 点击监听器
     * @param [typefaceSpan] 字体样式
     * @param [span] 一个自定义样式span
     * @param [styles] 更多字符样式
     * @return [TextViewHelperBuilder]
     */
    fun addText(
        text: Any? = null,
        textColor: Any? = null,
        textSize: Any? = null,
        @TextStyle
        textStyle: Int = Typeface.NORMAL,
        strikethrough: Boolean = false,
        underline: Boolean = false,
        background: Int = 0,
        clickListener: ClickableSpan? = null,
        typefaceSpan: TypefaceSpan? = null,
        span: Any? = null,
        vararg styles: CharacterStyle
    ): TextViewHelperBuilder {
        if (text == null) {
            return this
        }

        val color = when (textColor) {
            is String -> TextColor(textColor)
            is Int -> TextColor(textColor)
            is TextColor -> textColor
            else -> null
        }
        val size = when (textSize) {
            is Float -> TextSize(textSize)
            is Int -> TextSize(textSize)
            is TextSize -> textSize
            else -> null
        }

        val style = TextViewStyle(
            color?.toColor() ?: 0,
            size,
            background,
            underline,
            strikethrough,
            clickListener,
            typefaceSpan,
            span
        )

        // 字体粗细
        style.styleSpan = StyleSpan(textStyle)

        // 更多
        style.setStyles(*styles)

        helper.addText(text.toString(), style)
        return this
    }

    fun addFlags(flags: Int): TextViewHelperBuilder {
        helper.addFlags(flags)
        return this
    }

    fun build(): TextView? {
        if (textView == null) {
            return null
        }
        return helper.build()
    }

    fun generateSpanner(): Spanned {
        return helper.generateSpanner()
    }

    @IntDef(value = [Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC])
    @Retention(AnnotationRetention.SOURCE)
    annotation class TextStyle
}