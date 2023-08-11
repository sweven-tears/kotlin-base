package pers.sweven.common.base

import android.view.View
import androidx.databinding.ViewDataBinding

/**
 * Created by Sweven on 2021/7/28--21:26.
 * Email: sweventears@163.com
 */
abstract class BaseKtAdapter<T, R : ViewDataBinding?>(layoutId: Int) :
    BaseAdapter<T, R>(layoutId) {
    open class AdapterIt<T>(var view: View, var position: Int, var data: T)

    fun setOnViewClickListener(resId: Int, onViewClick: ((AdapterIt<T>) -> Unit)?) {
        super.setOnViewClickListener({ view, position, data ->
            onViewClick?.invoke(AdapterIt(view, position, data))
        }, resId)
    }

    fun setOnItemClickListener(onItemClick: ((AdapterIt<T>) -> Unit)?) {
        super.setOnItemClickListener { view, position, data ->
            onItemClick?.invoke(AdapterIt(view, position, data))
        }
    }
}
