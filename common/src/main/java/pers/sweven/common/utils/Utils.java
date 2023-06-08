package pers.sweven.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Utils {
    public static boolean hiddenKeyboard(Context context, View v) {
        if (v == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            v.clearFocus();
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v     组件
     * @param event 点击事件
     * @return .
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right &&
                    event.getY() > top && event.getY() < bottom);
        }
        return false;
    }


    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static void preventQuickClick(View view) {
        view.setEnabled(false);
        view.postDelayed(() -> view.setEnabled(true), 300);
    }

    public static void onClickView(View.OnClickListener listener, View... views) {
        View.OnClickListener onClickListener = v -> {
            preventQuickClick(v);
            listener.onClick(v);
        };
        for (View view : views) {
            view.setOnClickListener(listener == null ? null : onClickListener);
        }
    }

    public static String replaceNull(String s, String... def) {
        return s == null ? def.length == 0 ? "" : def[0] : s;
    }

    public static boolean isEmpty(String t) {
        return t == null || t.trim().length() == 0;
    }

    public static boolean isNotEmpty(String t) {
        return !isEmpty(t);
    }

    public static <T> boolean isEmpty(T[] t) {
        return t == null || t.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] t) {
        return !isEmpty(t);
    }

    public static boolean isEmpty(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else if (o instanceof List<?>) {
            return isEmpty((List<?>) o);
        }
        return o == null;
    }

    public static boolean isNotEmpty(Object t) {
        return !isEmpty(t);
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmpty(List<?> t) {
        return !isEmpty(t);
    }

    public static <T> List<T> getList(List<T> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * @param context 上下文
     * @return 判断软键盘是否显示
     */
    public static boolean isSoftShowing(Activity context) {
        // 获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        // 获取View可见区域的bottom
        Rect rect = new Rect();
        // DecorView即为activity的顶级view
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        // 考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        // 选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    /**
     * @param name      键
     * @param path      文件路径
     * @param mediaType 文件类型
     * @param encode    文件编码格式
     * @return 获取file part 用于上传文件
     */
    public static MultipartBody.Part getFilePart(String name, String path, String mediaType, String encode) {
        File file = new File(path);
        MediaType type = MediaType.parse(mediaType);
        RequestBody body = RequestBody.create(type, file);
        return MultipartBody.Part.createFormData(name, encode, body);
    }

    /**
     * 给 TextView 设置字体大小自适应（仅限一行，多行需要用\n分隔）
     *
     * @param view textView
     * @param text 文本内容
     */
    public static void setAutoSizeText(TextView view, String text) {
        String trim = text.trim();
        view.setText(trim);
        String[] split = trim.split("\n");
        float maxLength = 0;
        for (String s : split) {
            Paint paint = new Paint();
            paint.setTextSize(view.getTextSize());
            maxLength = Math.max(maxLength, paint.measureText(s));
        }
        float measureText = maxLength;
        view.post(() -> {
            int width = view.getWidth() - view.getPaddingStart() - view.getPaddingEnd();
            if (measureText > width) {
                float textSize = view.getTextSize();
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 0.1f);
                setAutoSizeText(view, text);
            }
        });
    }

    /**
     * 给 TextView 设置字体大小自适应（仅限一行，多行需要用\n分隔）
     *
     * @param view textView
     * @param text 文本内容
     */
    public static void setAutoSizeText(TextView view, String text, int maxLines) {
        String trim = text.trim();
        view.setText(trim);
        String[] split = trim.split("\n");
        float maxLength = 0;
        for (String s : split) {
            Paint paint = new Paint();
            paint.setTextSize(view.getTextSize());
            maxLength = Math.max(maxLength, paint.measureText(s));
        }
        float measureText = maxLength;
        view.post(() -> {
            int width = view.getWidth() - view.getPaddingStart() - view.getPaddingEnd();
            if (measureText > width * maxLines) {
                float textSize = view.getTextSize();
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 0.1f);
                setAutoSizeText(view, text);
            }
        });
    }

    /**
     * @param context 上下文
     * @param assets asset下的文件名
     * @return 将assets下的json文件转化为JSON文本
     */
    public static String getJsonFormAssets(Context context, String assets) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(assets)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * @param textView 走马灯效果
     */
    public static void marquee(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
    }
}