package pers.sweven.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 可以上下反弹的ScrollView
 * 增加用户体验的UI(让界面不再僵硬)<p>
 * tips:如果子视图有 RecyclerView，需要设置 {@link RecyclerView#setNestedScrollingEnabled(boolean)}<p>
 */
public class QBombScrollView extends NestedScrollView {

    private static boolean useFix;
    private View inner;
    private Rect rect = new Rect();
    private float y = 0;
    private int size = 4;//
    private boolean qBomb = true;

    public QBombScrollView(Context context) {
        super(context, null);
        init();
    }

    public QBombScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 全局配置使用修复ScrollView和Recyclerview一起使用导致的异常，
     * 建议需要的时候再使用{@link QBombScrollView#fixRecyclerView()}
     *
     * @param useFixRecyclerView 是否使用fixRecyclerview
     */
    public static void setDefaultUseFixRecyclerView(boolean useFixRecyclerView) {
        QBombScrollView.useFix = useFixRecyclerView;
    }

    /**
     * 初始化设置
     * 去掉ScrollView的边缘效果
     */
    private void init() {
        setFillViewport(true);
        setOverScrollMode(OVER_SCROLL_NEVER);//去掉边缘效果
    }

    /**
     * 修复 ScrollView 与 RecyclerView 一起用导致的异常
     */
    public void fixRecyclerView() {
        if (getChildAt(0) != null) {
            inner = getChildAt(0);
        }
        fixRecyclerView(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取子布局
        if (this.getChildAt(0) != null) {
            inner = this.getChildAt(0);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child != null) {
            inner = this.getChildAt(0);
        }
        if (useFix) {
            fixRecyclerView();
        }
    }

    /**
     * 给 RecyclerView 设置 {@link RecyclerView#setNestedScrollingEnabled(boolean)}<p>
     * 并添加父组件 RelativeLayout
     *
     * @param viewGroup Group
     */
    private void fixRecyclerView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof RecyclerView) {
                    child.setNestedScrollingEnabled(false);
                    ViewGroup.LayoutParams params = child.getLayoutParams();
                    viewGroup.removeView(child);
                    RelativeLayout relativeLayout = new RelativeLayout(viewGroup.getContext());
                    relativeLayout.setLayoutParams(params);
                    relativeLayout.addView(child);
                    child.setLayoutParams(new RelativeLayout.LayoutParams(params.width, params.height));
                    viewGroup.addView(relativeLayout, i);
                } else {
                    fixRecyclerView((ViewGroup) child);
                }
            }
        }
    }

    /**
     * 自定义触摸事件
     *
     * @author King
     * created at 2016/1/9 11:55
     */
    private void customerOnTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下是记录y的坐标
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起的时候计算是否需要回到正常状态
                if (isNeedAnimation()) {
                    animationToNomal();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //手指一动的时候重新布局View
                float nowY = event.getY();
                int deltaY = (int) ((y - nowY) / size);
                y = nowY;
                if (isNeedMove()) {
                    if (rect.isEmpty()) {
                        rect.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                        return;
                    }
                    int yy = inner.getTop() - deltaY;//滑动的偏移量
                    inner.layout(inner.getLeft(), yy, inner.getRight(), inner.getBottom() - deltaY);
                }
                break;
        }
    }

    /**
     * 监听ScrollView的onTouchEvent事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //如果inner为null,即scrollView没有子视图,直接返回
        if (inner == null) {
            return super.onTouchEvent(ev);
        }
        if (qBomb) {
            //自定义事件处理方法
            customerOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);

    }

    private boolean isNeedAnimation() {
        return !rect.isEmpty();
    }

    /**
     * 手指抬起后将页面回到原始状态
     */
    private void animationToNomal() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, inner.getTop(), rect.top);
        translateAnimation.setDuration(200);
        inner.startAnimation(translateAnimation);
        inner.layout(rect.left, rect.top, rect.right, rect.bottom);
        rect.setEmpty();
    }

    /**
     * 是否需要移动
     */
    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();//getScrollY表示Y轴滚动的距离
        int t = getScrollY() + getHeight();//getHeight:获取scrollView的高度
        debug("offset=" + offset + ",scrollY=" + scrollY + ",inner.getMeasuredHeight()=" + inner.getMeasuredHeight() + ",getScrollY()+getHeight()=" + String.valueOf(t));
        debug("getHeight=" + getHeight() + ",getScrollY()=" + getScrollY());
        //inner.getMeasuredHeight<=t:判断滚动条是否滚到底部
        return scrollY == 0 || inner.getMeasuredHeight() <= t;
    }


    private void debug(String msg) {
//        Log.d("QBombScrollerView", "-----------------" + msg);
    }

    public boolean isqBomb() {
        return qBomb;
    }

    public void setqBomb(boolean qBomb) {
        this.qBomb = qBomb;
    }
}