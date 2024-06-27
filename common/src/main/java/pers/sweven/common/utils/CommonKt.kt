package pers.sweven.common.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import java.math.BigDecimal
import java.util.*

/**
 * Created by Sweven on 2023/6/9.
 * Email:sweventears@Foxmail.com
 */
open class CommonKt

/**
 * res快速转换颜色
 * @param [context] 上下文
 * @return [Int]
 */
@ColorInt
fun Int.toColor(context: Context): Int {
    return ContextCompat.getColor(context, this)
}

fun Int.toColorStateList(context: Context): ColorStateList? {
    return ContextCompat.getColorStateList(context, this)
}

/**
 * 初始化选项卡
 * @param [titles] 标题
 * @param [onTabSelected] 在选定选项卡上
 */
fun TabLayout?.initTab(
    titles: List<String>,
    onTabSelected: ((tab: TabLayout.Tab?, title: String, selected: Boolean) -> Unit)? = null,
    icons: List<Drawable?>? = null,
    initTab: ((TabLayout) -> Boolean)? = null,
) {
    this?.apply {
        titles.forEachIndexed { index, it ->
            if (initTab?.invoke(this) != true) {
                val newTab = newTab()
                newTab.text = it
                newTab.icon = icons?.get(index)
                addTab(newTab)
            }
        }
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                onTabSelected?.invoke(tab, titles[tab.position], true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                onTabSelected?.invoke(tab, titles[tab.position], false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }
}

fun TabLayout?.setCurrentTab(index: Int) {
    if (this == null) {
        return
    }
    if (index in 0 until tabCount) {
        selectTab(getTabAt(index))
    }
}

fun TabLayout?.getCurrentTab(): TabLayout.Tab? {
    if (this == null) {
        return null
    }
    return getTabAt(selectedTabPosition)
}

/**
 * 添加enter侦听器
 * @param [actionId] 操作id
 * @param [tvSearch] 电视搜索
 * @param [listener] 听众
 */
fun EditText.addEnterListener(
    actionId: Int = EditorInfo.IME_ACTION_DONE,
    tvSearch: TextView? = null,
    listener: (View) -> Boolean,
) {
    setOnEditorActionListener { v, id, event ->
        if (id == actionId || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP)) {
            return@setOnEditorActionListener listener.invoke(v)
        }
        return@setOnEditorActionListener false
    }
    if (tvSearch != null) {
        Utils.onClickView({
            listener.invoke(it)
        }, tvSearch)
    }
}

fun <T> List<T>?.getList(): List<T> {
    return this ?: listOf()
}

fun TextView.drawables(): Array<Drawable> {
    return compoundDrawables
}

/**
 * 点击查看
 * @param [listener] 听者
 */
fun View?.onClickView(listener: (View) -> Unit) {
    Utils.onClickView(listener, this)
}

/**
 * 批量点击查看设置
 * @param [listener] 听者
 */
fun <T : View> Array<T>?.onClickView(listener: (View) -> Unit) {
    if (this != null) {
        Utils.onClickView(listener, *this)
    }
}

/**
 * java的[Bolean]?block:block
 * @param [true] 为真时参数
 * @param [false] 为假时参数
 * @return [T]
 */
fun <T> Boolean?.ifElse(`true`: T, `false`: T = `true`): T {
    return if (this == true) `true` else `false`
}

/**
 * 当参数为null输出[def],否则输出参数本身
 * 平替  String?:""=>改成通用参数
 * @param [def] 空返回值
 * @return [T]
 */
fun <T> T?.ifNull(def: T): T {
    if (this == null) {
        return def
    }
    return this
}

/**
 * 是否==nullOrEmpty
 * @param [null] 定义空值
 * @return [Boolean]
 */
fun <T> T?.isNullEmpty(`null`: T? = null): Boolean {
    return this == null || this == `null`
}

/**
 * null时直接返回 [nullDef] ,否则进行 [nulls] 判断，没有[nulls]时进行基础空值判断
 * @param [nullDef] 为空时默认返回值
 * @param [nulls] 以下值皆定义为空值,有值时仅判断当前值
 * @return [T] 返回 [nullDef]
 */
fun <T> T?.ifNullEmpty(nullDef: T, vararg nulls: T): T {
    if (this == null) {
        return nullDef
    }
    if (nulls.isNotEmpty()) {
        nulls.forEach {
            if (this == it) {
                return nullDef
            }
        }
        return this
    }
    if (this is String) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is Int) {
        if (this == 0) {
            return nullDef
        }
    } else if (this is Number) {
        if (this == 0) {
            return nullDef
        }
    } else if (this is List<*>) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is ArrayList<*>) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is Map<*, *>) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is Array<*>) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is IntArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is BooleanArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is ByteArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is FloatArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is DoubleArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is LongArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is ShortArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is CharArray) {
        if (this.isEmpty()) {
            return nullDef
        }
    } else if (this is Set<*>) {
        if (this.isEmpty()) {
            return nullDef
        }
    }
    return this
}

/**
 * 解析整数
 * @param [def] 定义
 * @return [Int]
 */
fun String?.parseInt(def: Int = 0): Int {
    val number = NumberUtils.parseInt(this)
    return if (number == 0) def else number
}

fun String?.parseDouble(
    def: Double = 0.0,
    newScale: Int = -1,
    @NumberUtils.RoundingMode roundingMode: Int = BigDecimal.ROUND_HALF_UP
): Double {
    val double = if (newScale > -1) {
        NumberUtils.parseDouble(this, newScale, roundingMode)
    } else {
        NumberUtils.parseDouble(this)
    }
    return if (double == 0.0) def else double
}

fun TextView?.setRichText(text: String) {
    if (this != null) {
        Utils.setRichText(this, text)
    }
}

fun String?.toRichText(): CharSequence {
    return Utils.toRichText(this) ?: ""
}

