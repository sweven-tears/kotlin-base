package pers.sweven.common.base;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
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
    private final Map<Integer, OnAdapterViewClick<T>> onViewClickMap = new HashMap<>();
    private final Map<Integer, OnAdapterClick<T>> onClickMap = new HashMap<>();
    private OnAdapterViewClick<T> onItemViewClick;
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
            int position = holder.getAdapterPosition();
            T data = list.get(position);
            if (onItemViewClick != null) {
                onItemViewClick.onClick(v, position, data);
            }
            if (onItemClick != null) {
                onItemClick.onClick(position, data);
            }
        });
        for (Integer id : onViewClickMap.keySet()) {
            View view = holder.itemView.findViewById(id);
            if (view == null) {
                continue;
            }
            view.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                T data = list.get(position);
                OnAdapterViewClick<T> click = onViewClickMap.get(id);
                if (click != null) {
                    click.onClick(v, position, data);
                }
            });
        }
        for (Integer id : onClickMap.keySet()) {
            View view = holder.itemView.findViewById(id);
            if (view == null) {
                continue;
            }
            view.setOnClickListener(v->{
                int position = holder.getAdapterPosition();
                T data = list.get(position);
                OnAdapterClick<T> click = onClickMap.get(id);
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

    public void setOnViewClickListener(OnAdapterViewClick<T> onViewClick, int resId) {
        this.onViewClickMap.put(resId, onViewClick);
    }

    public void setOnItemClickListener(OnAdapterViewClick<T> onItemClick) {
        this.onItemViewClick = onItemClick;
    }

    @Deprecated
    public void setOnViewClickListener(OnAdapterClick<T> onViewClick, int resId) {
        this.onClickMap.put(resId, onViewClick);
    }

    @Deprecated
    public void setOnItemClick(OnAdapterClick<T> onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * 后续版本将删除该构建
     */
    @Deprecated
    public interface OnAdapterClick<T> {
        @Deprecated
        void onClick(int position, T data);
    }

    public interface OnAdapterViewClick<T> {
        void onClick(View view, int position, T data);
    }


    public static class BaseViewHolder<R extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private final R binding;

        public BaseViewHolder(R binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
