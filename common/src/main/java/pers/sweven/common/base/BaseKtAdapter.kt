package pers.sweven.common.base

import android.view.View
import androidx.databinding.ViewDataBinding

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
}
