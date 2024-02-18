package pers.sweven.common.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

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

/**
 * 初始化选项卡
 * @param [titles] 标题
 * @param [onTabSelected] 在选定选项卡上
 */
fun TabLayout?.initTab(
    titles: List<String>,
    onTabSelected: ((tab: TabLayout.Tab?, title: String, selected: Boolean) -> Unit)? = null,
    initTab: ((TabLayout) -> Boolean)? = null
) {
    this?.apply {
        titles.forEach {
            if (initTab?.invoke(this) != true) {
                val newTab = newTab()
                newTab.text = it
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

/**
 * 添加enter侦听器
 * @param [actionId] 操作id
 * @param [tvSearch] 电视搜索
 * @param [listener] 听众
 */
fun EditText.addEnterListener(
    actionId: Int = EditorInfo.IME_ACTION_DONE,
    tvSearch: TextView? = null,
    listener: (View) -> Boolean
) {
    setOnEditorActionListener { v, id, event ->
        if (id == actionId || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
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

fun View?.onClickView(listener: (View) -> Unit) {
    Utils.onClickView(listener, this)
}

fun <T : View> Array<T>?.onClickView(listener: (View) -> Unit) {
    if (this != null) {
        Utils.onClickView(listener, *this)
    }
}

fun <T> Boolean?.ifElse(`true`: T, `false`: T = `true`): T {
    return if (this == true) `true` else `false`
}

fun <T> T?.ifNull(def: T): T {
    if (this == null) {
        return def
    }
    return this
}

fun <T> T?.ifNullEmpty(isNull: T): T {
    if (this == null) {
        return isNull
    }
    if (this is String) {
        if (this.isEmpty()) {
            return isNull
        }
    } else if (this is Int) {
        if (this == 0) {
            return isNull
        }
    } else if (this is Number) {
        if (this == 0) {
            return isNull
        }
    } else if (this is List<*>) {
        if (this.isEmpty()) {
            return isNull
        }
    } else if (this is ArrayList<*>) {
        if (this.isEmpty()) {
            return isNull
        }
    } else if (this is Map<*, *>) {
        if (this.isEmpty()) {
            return isNull
        }
    } else if (this is Set<*>) {
        if (this.isEmpty()) {
            return isNull
        }
    }
    return this
}

fun String?.parseInt(def: Int = 0): Int {
    val number = NumberUtils.parseInt(this)
    return if (number == 0) def else number
}