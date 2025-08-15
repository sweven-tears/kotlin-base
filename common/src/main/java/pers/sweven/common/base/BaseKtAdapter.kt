package pers.sweven.common.base

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.Observable
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pers.sweven.common.BR
import kotlin.reflect.KProperty1


/**
 * Created by Sweven on 2021/7/28--21:26.
 * Email: sweventears@163.com
 */
abstract class BaseKtAdapter<T, R : ViewDataBinding?>(layoutId: Int) :
    BaseAdapter<T, R>(layoutId) {
    fun setOnViewClickListener(resId: Int, onViewClick: ((AdapterIt<T>) -> Unit)?) {
        super.setOnViewClickListener({ it ->
            onViewClick?.invoke(it)
        }, resId)
    }

    // 事件处理器列表
    private val eventHandlers = mutableListOf<Pair<Int, (ViewHolderEventContext<T, R>) -> Unit>>()

    /**
     * 注册事件处理器
     *
     * @param viewId 目标视图ID
     * @param eventHandler 事件处理函数
     */
    fun setEvent(viewId: Int, eventHandler: ViewHolderEventContext<T, R>.() -> Unit) {
        eventHandlers.add(viewId to eventHandler)
    }

    /**
     * 设置事件
     * @param [eventHandler] 事件处理程序
     * @param [viewId] 视图id
     */
    fun setEvent(eventHandler: ViewHolderEventContext<T, R>.() -> Unit,vararg viewId: Int) {
        for (id in viewId) {
            eventHandlers.add(id to eventHandler)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<R> {
        val holder = super.onCreateViewHolder(parent, viewType)

        // 应用所有事件处理器
        eventHandlers.forEach { (viewId, handler) ->
            val view = holder.itemView.findViewById<View>(viewId)
            if (view != null) {
                val context = ViewHolderEventContext(view, holder, this)
                handler(context)
            }
        }

        return holder
    }

    /**
     * 视图事件上下文类
     */
    inner class ViewHolderEventContext<T, R : ViewDataBinding?>(
        val view: View,
        val holder: BaseViewHolder<R>,
        val adapter: BaseKtAdapter<T, R>
    ) {
        // 获取当前位置的数据
        val data: T?
            get() {
                val position = holder.adapterPosition
                return if (position != RecyclerView.NO_POSITION) adapter.list[position] else null
            }

        // 获取当前位置
        val position: Int
            get() = holder.adapterPosition

        // 获取绑定对象
        val binding: R?
            get() = holder.binding
    }
}

/**
 * 扩展函数：设置点击事件
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnClick(action: (view: View, position: Int, data: T?) -> Unit) {
    view.setOnClickListener {
        action(it, position, data)
    }
}

/**
 * 扩展函数：设置长按事件
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnLongClick(action: (view: View, position: Int, data: T?) -> Boolean) {
    view.setOnLongClickListener {
        action(it, position, data) ?: false
    }
}

/**
 * 扩展函数：设置文本变化事件（完整）
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnTextChanged(
    before: (text: CharSequence?, start: Int, count: Int, after: Int, position: Int, data: T?) -> Unit = { _, _, _, _, _, _ -> },
    onTextChanged: (text: CharSequence?, start: Int, before: Int, count: Int, position: Int, data: T?) -> Unit = { _, _, _, _, _, _ -> },
    after: (text: Editable?, position: Int, data: T?) -> Unit = { _, _, _ -> }
) {
    (view as? EditText)?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            before(s, start, count, after, position, data)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s, start, before, count, position, data)
        }

        override fun afterTextChanged(s: Editable?) {
            after(s, position, data)
        }
    })
}

/**
 * 扩展函数：设置文本变化后事件（简化）
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doAfterTextChanged(
    action: (text: Editable?, position: Int, data: T?) -> Unit
) {
    (view as? EditText)?.doAfterTextChanged {
        action(it, position, data)
    }
}

/**
 * 扩展函数：设置文本变化前事件（简化）
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doBeforeTextChanged(
    action: (text: CharSequence?, start: Int, count: Int, after: Int, position: Int, data: T?) -> Unit
) {
    (view as? EditText)?.doBeforeTextChanged { text, start, count, after ->
        action(text, start, count, after, position, data)
    }
}

/**
 * 扩展函数：设置文本变化中事件（简化）
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnTextChanged(
    action: (text: CharSequence?, start: Int, before: Int, count: Int, position: Int, data: T?) -> Unit
) {
    (view as? EditText)?.doOnTextChanged { text, start, before, count ->
        action(text, start, before, count, position, data)
    }
}

/**
 * 扩展函数：设置复选框状态变化事件
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnCheckedChanged(
    action: (isChecked: Boolean, position: Int, data: T?) -> Unit
) {
    (view as? CompoundButton)?.setOnCheckedChangeListener { _, isChecked ->
        action(isChecked, position, data)
    }
}

/**
 * 扩展函数：设置评分变化事件
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnRatingChanged(
    action: (rating: Float, fromUser: Boolean, position: Int, data: T?) -> Unit
) {
    (view as? RatingBar)?.onRatingBarChangeListener =
        RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
            action(rating, fromUser, position, data)
        }
}

/**
 * 扩展函数：设置进度条变化事件
 */
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnSeekBarChanged(
    onProgressChanged: (progress: Int, fromUser: Boolean, position: Int, data: T?) -> Unit,
    onStartTrackingTouch: (seekBar: SeekBar, position: Int, data: T?) -> Unit = { _, _, _ -> },
    onStopTrackingTouch: (seekBar: SeekBar, position: Int, data: T?) -> Unit = { _, _, _ -> }
) {
    (view as? SeekBar)?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            onProgressChanged(progress, fromUser, position, data)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            onStartTrackingTouch(seekBar, position, data)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            onStopTrackingTouch(seekBar, position, data)
        }
    })
}

/**
 * 扩展函数：设置触摸事件
 */
@SuppressLint("ClickableViewAccessibility")
fun <T, R : ViewDataBinding?> BaseKtAdapter<T, R>.ViewHolderEventContext<T, R>.doOnTouch(action: (view: View, event: android.view.MotionEvent, position: Int, data: T?) -> Boolean) {
    view.setOnTouchListener { v, event ->
        action(v, event, position, data)
    }
}
