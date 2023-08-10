package pers.sweven.common.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pers.sweven.common.base.BaseKtAdapter.BaseViewHolder

/**
 * Created by Sweven on 2021/7/28--21:26.
 * Email: sweventears@163.com
 */
abstract class BaseKtAdapter<T, R : ViewDataBinding?>(private val layoutId: Int) :
    RecyclerView.Adapter<BaseViewHolder<R>>() {
    private val list = arrayListOf<T>()
    private var onItemClick: ((Int, T) -> Unit)? = null
    private var onViewClickMap = hashMapOf<Int, ((Int, T) -> Unit)?>()

    open fun addData(t: T) {
        val pos = list.size
        list.add(t)
        notifyItemInserted(pos)
    }

    open fun addData(position: Int, t: T) {
        list.add(position, t)
        notifyItemInserted(position)
    }

    open fun setData(position: Int, t: T) {
        list[position] = t
        updateChangeNoAnimation(position)
    }

    /**
     * Update change with no animation replace notifyItemChanged(int)
     * @see notifyItemChanged(int)
     * @param position
     */
    open fun updateChangeNoAnimation(position: Int){
        notifyItemChanged(position, 0)
    }

    open fun removeData(t: T) {
        for (i in 0 until list.size) {
            if (list.contains(t)) {
                list.remove(t)
                notifyItemRemoved(i)
            }
        }
    }

    open fun removeDataAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun addData(list: List<T>) {
        val start = this.list.size
        val itemCount = list.size
        this.list.addAll(list)
        notifyItemRangeInserted(start, itemCount)
    }

    open fun getList(): ArrayList<T> {
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun setList(list: List<T>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<R> {
        val binding: R = createView(parent, viewType)
        val holder = BaseViewHolder(binding)
        holder.itemView.setOnClickListener { v: View ->
            if (onItemClick != null) {
                val position = holder.adapterPosition
                val data = list[position]
                onItemClick!!.invoke(position, data)
            }
        }
        for (entry in onViewClickMap) {
            if (entry.key > 0) {
                holder.itemView.findViewById<View>(entry.key).setOnClickListener {
                    if (entry.value != null) {
                        val position = holder.adapterPosition
                        val data = list[position]
                        entry.value!!.invoke(position, data)
                    }
                }
            }
        }
        return holder
    }

    open fun createView(parent: ViewGroup, viewType: Int): R {
        val inflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(inflater, layoutId, null, false)
    }

    override fun onBindViewHolder(@NonNull holder: BaseViewHolder<R>, position: Int) {
        val t = list[position]
        holder.itemView.tag = holder
        onData(holder.binding, t)
        onData(holder.binding, t, position)
    }

    open fun onData(binding: R, data: T) {

    }

    open fun onData(binding: R, data: T, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    open fun setOnViewClickListener(onViewClick: (position: Int, data: T) -> Unit, clickId: Int) {
        onViewClickMap[clickId] = onViewClick
    }

    open fun setOnViewClickListener(onViewClick: BaseAdapter.OnAdapterClick<T>, clickId: Int) {
        this.onViewClickMap[clickId] = fun(position: Int, t: T) {
            onViewClick.onClick(position, t)
        }
    }

    open fun setOnItemClickListener(onItemClick: (position: Int, data: T) -> Unit) {
        this.onItemClick = onItemClick
    }

    open fun setOnItemClickListener(onItemClick: BaseAdapter.OnAdapterClick<T>) {
        this.onItemClick = fun(position: Int, t: T) {
            onItemClick.onClick(position, t)
        }
    }

    class BaseViewHolder<R : ViewDataBinding?>(var binding: R) : ViewHolder(binding!!.root)
}