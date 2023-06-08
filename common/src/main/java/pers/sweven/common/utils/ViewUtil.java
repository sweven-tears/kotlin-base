package pers.sweven.common.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sweven on 2019/4/25.
 * Email:sweventears@Foxmail.com
 */
public class ViewUtil {
    public static final int REAL_WINDOW_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int REAL_WINDOW_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

    /**
     * 获取{@link View#getMeasuredHeight()}
     * 和{@link View#getMeasuredWidth()}
     * <p>单位是px
     *
     * @param view 组件
     */
    public static void notifyMeasure(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = View.MeasureSpec.makeMeasureSpec(tempHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * {@link View view}的父类组件必须是{@link ViewGroup}类型的才行
     * 好像不用啊？？
     * 设置 {@param scale=0} 即表示高度自适应
     *
     * @param view     组件
     * @param multiple 占显示区域屏幕宽度的倍数
     * @param scale    宽高比例 例：宽高比例为16:9则输入16/9.0
     */
    public static void setWidthHeight(View view, double multiple, double scale) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int width = (int) (REAL_WINDOW_WIDTH * multiple);
        layoutParams.width = width;
        if (scale != 0) {
            layoutParams.height = (int) (width / scale);
        }
        view.setLayoutParams(layoutParams);
    }

    /**
     * 同时为多个组件设置相同宽高
     *
     * @param views    多个组件
     * @param multiple 占显示区域屏幕宽度的倍数
     * @param scale    宽高比例 例：宽高比例为16:9则输入16/9.0
     */
    public static void setWidthHeightForViews(double multiple, double scale, View... views) {
        for (View view : views) {
            setWidthHeight(view, multiple, scale);
        }
    }

    /**
     * 同时为多个组件设置相同宽高
     *
     * @param views    多个组件
     * @param multiple 占真实屏幕宽度的倍数
     * @param scale    宽高比例 例：宽高比例为16:9则输入16/9.0
     */
    public static void setWidthHeightForViews(View[] views, double multiple, double scale) {
        for (View view : views) {
            setWidthHeight(view, multiple, scale);
        }
    }

    /**
     * {@link View view}的父类组件必须是{@link ViewGroup}类型的才行
     *
     * @param view      组件
     * @param width_px  view的宽度 等于0时为不设置
     * @param height_px view的高度 等于0时为不设置
     */
    public static void setWidthHeight(View view, int width_px, int height_px) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (width_px != 0) {
            layoutParams.width = width_px;
        }
        if (height_px != 0) {
            layoutParams.height = height_px;
        }
        view.setLayoutParams(layoutParams);
    }

    public static void animOpen(final View view, int height) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator va = createDropAnim(view, 0, height);
        va.start();
    }

    public static void animClose(final View view) {
        ViewUtil.notifyMeasure(view);
        int origHeight = view.getMeasuredHeight();
        ValueAnimator va = createDropAnim(view, origHeight, 0);
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        va.start();
    }

    /**
     * 使用动画的方式来改变高度解决visible不一闪而过出现
     *
     * @param view  组件
     * @param start 初始状态值
     * @param end   结束状态值
     * @return
     */
    private static ValueAnimator createDropAnim(final View view, int start, int end) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();//根据时间因子的变化系数进行设置高度
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);//设置高度
            }
        });
        return va;
    }
}
