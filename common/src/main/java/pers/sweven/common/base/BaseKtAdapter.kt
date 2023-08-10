package pers.sweven.common.base

import androidx.databinding.ViewDataBinding

/**
 * Created by Sweven on 2021/7/28--21:26.
 * Email: sweventears@163.com
 */
abstract class BaseKtAdapter<T, R : ViewDataBinding?>(layoutId: Int) :
    BaseAdapter<T, R>(layoutId) {
    class AdapterIt<T>(var position: Int, var data: T)

    fun setOnViewClickListener(resId: Int, onViewClick: ((AdapterIt<T>) -> Unit)?) {
        super.setOnViewClickListener({ position, data ->
            onViewClick?.invoke(AdapterIt(position, data))
        }, resId)
    }

    fun setOnItemClickListener(onItemClick: ((AdapterIt<T>) -> Unit)?) {
        super.setOnItemClickListener { position, data ->
            onItemClick?.invoke(AdapterIt(position, data))
        }
    }
}
