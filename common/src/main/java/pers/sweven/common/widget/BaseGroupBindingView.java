package pers.sweven.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * 请使用{@link BaseBindingViewGroup}<br>
 * Created by Sweven on 2021/11/30.
 * Email:sweventears@Foxmail.com
 */
@Deprecated
public abstract class BaseGroupBindingView<T extends ViewDataBinding> extends FrameLayout {
    protected T binding;

    public BaseGroupBindingView(@NonNull Context context) {
        this(context, null);
    }

    public BaseGroupBindingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseGroupBindingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
        init(context);
    }

    public BaseGroupBindingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void initAttr(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        setView(inflater);
        initView();
    }

    protected void setView(LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), this, true);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();


}
