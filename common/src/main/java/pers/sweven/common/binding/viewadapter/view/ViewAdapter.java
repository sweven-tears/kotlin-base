package pers.sweven.common.binding.viewadapter.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.BindingAdapter;

import pers.sweven.common.binding.command.BindingCommand;

/**
 * Created by Sweven on 2021/10/29.
 * Email:sweventears@Foxmail.com
 */
public class ViewAdapter {
    /**
     * @param view         组件
     * @param clickCommand 点击事件
     * @param preventQuick 是否需要防快速点击
     */
    @BindingAdapter(value = {"clickCommand", "preventQuick"}, requireAll = false)
    public static void onClickCommand(View view, BindingCommand<?> clickCommand, boolean preventQuick) {
        View.OnClickListener listener = v -> {
            if (clickCommand != null) {
                clickCommand.execute();
            }
        };
        View.OnClickListener onClickListener = v -> {
            if (preventQuick) {
                preventQuickClick(v);
            }
            listener.onClick(v);
        };
        view.setOnClickListener(onClickListener);
    }

    private static void preventQuickClick(View view) {
        view.setEnabled(false);
        view.postDelayed(() -> view.setEnabled(true), 300);
    }

    @BindingAdapter("android:layout_height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) height
            );
        } else {
            params.height = (int) height;
        }
        view.setLayoutParams(params);
    }
}
