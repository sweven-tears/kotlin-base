package pers.sweven.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pers.sweven.common.helper.textview.TextSize;
import pers.sweven.common.helper.textview.TextViewHelper;
import pers.sweven.common.helper.textview.TextViewStyle;

public class Utils {
    /**
     * 富文本设置<br/>
     * example:46541^size:2.4;font:D-DIN;bold:1|\n供应商已采购药店^strikethrough:1
     */
    public static void setRichText(TextView textView, CharSequence charSequence) {
        // 46541^size:2.4;font:D-DIN;bold:1|\n供应商已采购药店
        String s = charSequence.toString();
        // 多个段落样式分组
        String[] wordsGroup = s.split("\\|");//["46541^size:2.4;font:D-DIN;bold:1","\n供应商已采购药店"]
        TextViewHelper helper = new TextViewHelper(textView);
        for (String words : wordsGroup) {
            // 文本和样式分组
            String[] styles = words.split("\\^");//["46541","size:2.4;font:D-DIN;bold:1"]
            String text = styles[0];// 获取文本

            // 提取样式
            TextViewStyle textViewStyle = new TextViewStyle();
            if (styles.length > 1) {
                // 多个样式分组
                String[] textStyles = styles[1].split(";");//["size:2.4","font:D-DIN","bold:1"]
                for (String textStyle : textStyles) {
                    // 样式名称键值分组
                    String[] styleValue = textStyle.split(":");//["size","2.4"]
                    String key = styleValue[0];//"size"
                    if (styleValue.length > 1) {
                        String value = styleValue[1];
                        if ("color".equals(key)) {// 颜色赋值
                            textViewStyle.setTextColor(Color.parseColor(value));
                        } else if ("size".equals(key)) {// 字体大小赋值
                            textViewStyle.setTextSize(new TextSize(NumberUtils.parseFloat(value)));
                        } else if ("font".equals(key)) {// 字体赋值
                            Log.e("Utils", "字体样式请自定义");
                        } else if ("bold".equals(key)) {// 字体粗细赋值
                            StyleSpan bold = new StyleSpan(Typeface.BOLD);
                            StyleSpan normal = new StyleSpan(Typeface.NORMAL);
                            StyleSpan italic = new StyleSpan(Typeface.ITALIC);
                            textViewStyle.setSpan("1".equals(value) ? bold : "2".equals(value) ? italic : normal);
                        } else if ("strikethrough".equals(key)) {// 删除线添加
                            textViewStyle.setStrikethrough("1".equals(value));
                        }
                    }
                }
            }

            // 赋予文本和样式
            helper.addText(text, textViewStyle);
        }
        // 文本、样式渲染
        helper.build();
    }

    /**
     * 转富文本<br/>
     * example:46541^size:2.4;font:D-DIN;bold:1|\n供应商已采购药店^strikethrough:1
     *
     * @param charSequence char 序列
     * @return {@link Spanned}
     */
    public static Spanned toRichText(String charSequence) {
        if (charSequence == null) {
            return null;
        }
        // 46541^size:2.4;font:D-DIN;bold:1|\n供应商已采购药店
        // 多个段落样式分组
        String[] wordsGroup = charSequence.split("\\|");//["46541^size:2.4;font:D-DIN;bold:1","\n供应商已采购药店"]
        TextViewHelper helper = new TextViewHelper();
        for (String words : wordsGroup) {
            // 文本和样式分组
            String[] styles = words.split("\\^");//["46541","size:2.4;font:D-DIN;bold:1"]
            String text = styles[0];// 获取文本

            // 提取样式
            TextViewStyle textViewStyle = new TextViewStyle();
            if (styles.length > 1) {
                // 多个样式分组
                String[] textStyles = styles[1].split(";");//["size:2.4","font:D-DIN","bold:1"]
                for (String textStyle : textStyles) {
                    // 样式名称键值分组
                    String[] styleValue = textStyle.split(":");//["size","2.4"]
                    String key = styleValue[0];//"size"
                    if (styleValue.length > 1) {
                        String value = styleValue[1];
                        if ("color".equals(key)) {// 颜色赋值
                            textViewStyle.setTextColor(Color.parseColor(value));
                        } else if ("size".equals(key)) {// 字体大小赋值
                            textViewStyle.setTextSize(new TextSize(NumberUtils.parseFloat(value)));
                        } else if ("font".equals(key)) {// 字体赋值
                            Log.e("Utils", "字体样式请自定义");
                        } else if ("bold".equals(key)) {// 字体粗细赋值
                            StyleSpan bold = new StyleSpan(Typeface.BOLD);
                            StyleSpan normal = new StyleSpan(Typeface.NORMAL);
                            StyleSpan italic = new StyleSpan(Typeface.ITALIC);
                            textViewStyle.setSpan("1".equals(value) ? bold : "2".equals(value) ? italic : normal);
                        } else if ("strikethrough".equals(key)) {// 删除线添加
                            textViewStyle.setStrikethrough("1".equals(value));
                        }
                    }
                }
            }

            // 赋予文本和样式
            helper.addText(text, textViewStyle);
        }
        // 文本、样式渲染
        return helper.generateSpanner();
    }


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
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
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
     * @param assets  asset下的文件名
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
        if (textView == null) {
            return;
        }
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
    }

    /**
     * 着色器
     *
     * @param drawable 需要着色的对象
     * @param color    颜色
     */
    public static void tintColor(Drawable drawable, @ColorInt int color) {
        tintColor(drawable, color, 0);
    }

    /**
     * 着色器
     *
     * @param drawable   需要着色的图像
     * @param color      颜色
     * @param pressColor 实现简单的按压变换着色，下压的颜色
     */
    public static void tintColor(Drawable drawable, @ColorInt int color, @ColorInt int pressColor) {
        if (drawable == null) {
            return;
        }
        if (pressColor == 0) {
            DrawableCompat.setTint(drawable.mutate(), color);
            return;
        }
        int[][] selector = new int[][]{new int[]{android.R.attr.state_pressed}, StateSet.WILD_CARD};
        ColorStateList stateList = new ColorStateList(selector, new int[]{pressColor, color});
        DrawableCompat.setTintList(drawable.mutate(), stateList);
    }

    //------------------------------------------------------------------------------

    public static String getRandomChinese(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(getRandomChinese());
        }
        return builder.toString();
    }

    private static String getRandomChinese() {
        boolean useSimpleMethod = true;
        if (!useSimpleMethod) {
            char c = (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
            return String.valueOf(c);
        }
        String str = "";
        int hightPos;
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        str = new String(b, Charset.forName("GBK"));
        return str;
    }

    public static String random(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        return random(length, str);
    }

    public static String random(int length, String str) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(str.length());
            builder.append(str.charAt(index));
        }
        return builder.toString();
    }
}