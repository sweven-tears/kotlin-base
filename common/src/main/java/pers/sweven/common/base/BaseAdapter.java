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

import pers.sweven.common.utils.Utils;

/**
 * Created by Sweven on 2023/8/9--17:25.
 * Email: sweventears@163.com
 */
public class BaseAdapter<T, R extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder<R>> {
    private final List<T> list = new ArrayList<>();
    private final int layoutId;
    private final Map<Integer, OnAdapterViewClick<T>> onViewClickMap = new HashMap<>();
    private OnAdapterViewClick<T> onItemViewClick;
    private OnLoadCompleteListener mLoadCompleteListener;

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
        if (list == null) {
            return;
        }
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

    public void refreshData(T data) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(data)) {
                notifyItemChanged(i, 0);
                return;
            }
        }
    }

    public void refreshData(int index) {
        notifyItemChanged(index, 0);
    }

    public void removeDataAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public List<T> getList() {
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<T> list) {
        this.list.clear();
        if (list != null) {
            this.list.addAll(list);
        }
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
        BaseViewHolder<R> holder = new BaseViewHolder<>(binding, this::initHolderView);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            T data = list.get(position);
            if (onItemViewClick != null) {
                onItemViewClick.onClick(new AdapterIt<>(v, position, data));
            }
        });
        for (Integer id : onViewClickMap.keySet()) {
            View view = holder.itemView.findViewById(id);
            if (view == null) {
                continue;
            }
            Utils.onClickView(v -> {
                int position = holder.getAdapterPosition();
                T data = list.get(position);
                OnAdapterViewClick<T> click = onViewClickMap.get(id);
                if (click != null) {
                    click.onClick(new AdapterIt<>(v, position, data));
                }
            }, view);
        }
        return holder;
    }

    protected R createView(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return DataBindingUtil.inflate(inflater, layoutId, null, false);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<R> holder, int position) {
        T data = list.get(position);
        holder.itemView.setTag(holder);
        onData(holder.binding, data);
        onData(holder.binding, data, position);
        onData(holder, data);
        if (getItemCount() - 1 == position && mLoadCompleteListener != null) {
            mLoadCompleteListener.onLoadComplete();
        }
    }

    protected void initHolderView(BaseViewHolder<R> holder){

    }

    protected void onData(BaseViewHolder<R> holder, T data) {
    }

    protected void onData(R binding, T data) {
    }

    protected void onData(R binding, T data, int position) {
    }

    public void setOnViewClickListener(OnAdapterViewClick<T> onViewClick, int... resId) {
        for (int id : resId) {
            this.onViewClickMap.put(id, onViewClick);
        }
    }

    public void setOnItemClickListener(OnAdapterViewClick<T> onItemClick) {
        this.onItemViewClick = onItemClick;
    }

    /**
     * @param loadCompleteListener 加载完成侦听器
     */
    public void setOnLoadCompleteListener(OnLoadCompleteListener loadCompleteListener) {
        mLoadCompleteListener = loadCompleteListener;
    }

    @Deprecated
    public void setOnViewClick(OnAdapterClick<T> onViewClick, int resId) {
        this.onViewClickMap.put(resId, it -> onViewClick.onClick(it.position,it.data));
    }

    @Deprecated
    public void setOnItemClick(OnAdapterClick<T> onItemClick) {
        this.onItemViewClick = it -> onItemClick.onClick(it.position,it.data);
    }

    /**
     * 后续版本将删除该构建
     */
    @Deprecated
    public interface OnAdapterClick<T> {
        void onClick(int position, T data);
    }

    public interface OnAdapterViewClick<T> {
        void onClick(AdapterIt<T> it);
    }

    public interface OnLoadCompleteListener {
        void onLoadComplete();
    }

    public static class AdapterIt<T> {
        public View view;
        public int position;
        public T data;

        public AdapterIt() {
        }

        public AdapterIt(View view, int position, T data) {
            this.view = view;
            this.position = position;
            this.data = data;
        }
    }

    public static class BaseViewHolder<R extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public final R binding;

        public BaseViewHolder(R binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public BaseViewHolder(R binding,OnInit<BaseViewHolder<R>> onInit) {
            super(binding.getRoot());
            this.binding = binding;
            if (onInit != null) {
                onInit.init(this);
            }
        }
    }

    interface OnInit<H>{
        void init(H holder);
    }
}
