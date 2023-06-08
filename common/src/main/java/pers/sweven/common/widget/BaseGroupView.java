package pers.sweven.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Sweven on 2021/11/30.
 * Email:sweventears@Foxmail.com
 */
public abstract class BaseGroupView extends FrameLayout {

    public BaseGroupView(@NonNull Context context) {
        this(context, null);
    }

    public BaseGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
        init(context);
    }

    public BaseGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        inflater.inflate(getLayoutId(), this);
    }

    protected boolean useBinding() {
        return false;
    }

    protected abstract int getLayoutId();

    protected abstract void initView();


}
