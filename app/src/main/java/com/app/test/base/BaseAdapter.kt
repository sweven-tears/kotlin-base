package com.yxm.scrm.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import pers.sweven.common.base.BaseKtAdapter

/**
 * Created by Sweven on 2023/7/28--9:53.
 * Email: sweventears@163.com
 */
abstract class BaseAdapter<T, R : ViewDataBinding>(val layout: Int) :
    BaseKtAdapter<T, R>(layout) {
    override fun onData(binding: R, data: T) {
    }

    open fun clear() {
        val count = itemCount
        list.clear()
        notifyItemRangeRemoved(0, count)
    }

    override fun createView(parent: ViewGroup, viewType: Int): R {
        val inflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(inflater, layout, parent, false)
    }

    override fun removeDataAt(position: Int) {
        notifyItemRemoved(position)
        list.removeAt(position)
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onData(binding: R, data: T, position: Int) {
        super.onData(binding, data, position)
    }
}