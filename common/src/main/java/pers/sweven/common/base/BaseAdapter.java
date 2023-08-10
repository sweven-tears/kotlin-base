package pers.sweven.common.base;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sweven on 2023/8/9--17:25.
 * Email: sweventears@163.com
 */
public class BaseAdapter<T, R extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder<R>> {
    private final List<T> list = new ArrayList<>();
    private final int layoutId;
    private final Map<Integer, OnAdapterClick<T>> onViewClickMap = new HashMap<>();
    private OnAdapterClick<T> onItemClick;

    public BaseAdapter(int layoutId) {
        this.layoutId = layoutId;
    }

    /**
     * Update change with no animation replace notifyItemChanged(int)
     *
     * @param position position
     */
    public void updateChangeNoAnimation(int position) {
        notifyItemChanged(position, 0);
    }

    public void addData(T data) {
        list.add(data);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addData(int position, T data) {
        list.add(position, data);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount() - position, 0);
    }

    public void addData(List<T> list) {
        int start = getItemCount();
        this.list.addAll(list);
        notifyItemRangeInserted(start, getItemCount() - start);
    }

    public void setData(int position, T data) {
        this.list.set(position, data);
        notifyItemChanged(position);
    }

    public void removeData(T data) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(data)) {
                removeDataAt(i);
                return;
            }
        }
    }

    public void removeDataAt(int position) {
        list.remove(position);
        notifyItemChanged(position);
        notifyItemRangeChanged(position, getItemCount() - position, 0);
    }

    public List<T> getList() {
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<T> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public BaseViewHolder<R> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        R binding = createView(parent, viewType);
        BaseViewHolder<R> holder = new BaseViewHolder<>(binding);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) {
                int position = holder.getAdapterPosition();
                T data = list.get(position);
                onItemClick.onClick(position, data);
            }
        });
        for (Integer id : onViewClickMap.keySet()) {
            holder.itemView.findViewById(id).setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                T data = list.get(position);
                OnAdapterClick<T> click = onViewClickMap.get(id);
                if (click != null) {
                    click.onClick(position, data);
                }
            });
        }
        return holder;
    }

    private R createView(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return DataBindingUtil.inflate(inflater, layoutId, null, false);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<R> holder, int position) {
        T data = list.get(position);
        holder.itemView.setTag(holder);
        onData(holder.binding, data);
        onData(holder.binding, data, position);
    }

    protected void onData(R binding, T data) {
    }

    protected void onData(R binding, T data, int position) {
    }

    public void setOnViewClickListener(OnAdapterClick<T> onViewClick, int resId) {
        this.onViewClickMap.put(resId, onViewClick);
    }

    public void setOnItemClickListener(OnAdapterClick<T> onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnAdapterClick<T> {
        void onClick(int position, T data);
    }

    public static class BaseViewHolder<R extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private R binding;

        public BaseViewHolder(R binding) {
            super(binding.getRoot());
        }
    }
}
