package pers.sweven.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by Sweven on 2021/12/9.
 * Email:sweventears@Foxmail.com
 */
public abstract class BaseBindingViewGroup<T extends ViewDataBinding> extends FrameLayout {
    protected T binding;

    public BaseBindingViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public BaseBindingViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseBindingViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    public BaseBindingViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(context, attrs);
        init();
    }

    protected void getAttrs(@NonNull Context context, AttributeSet attrs) {

    }

    private void init() {
        setView();
        onCreateView(getContext());
    }

    protected void setView() {

        if (isInEditMode()) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), (ViewGroup) getRootView(), true);
        }else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), getLayoutId(), (ViewGroup) getRootView(), true);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void onCreateView(Context context);

    @ColorInt
    protected int color(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

}
