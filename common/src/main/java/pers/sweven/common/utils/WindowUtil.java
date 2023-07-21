package pers.sweven.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by Sweven on 2019/4/3.
 * Email:sweventears@Foxmail.com
 */
public class WindowUtil {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavigationBarColor(Activity activity, int color) {
        if (checkDeviceHasNavigationBar(activity)) {
            activity.getWindow().setNavigationBarColor(color);
        }
    }

    /**
     * 设置状态栏文字为黑色
     * 当前适配分割点 API 22 LOLLIPOP_MR1
     *
     * @param activity       activity
     * @param statusBarColor status bar color 状态栏颜色
     */
    public static void setBlackFontBar(Activity activity, int statusBarColor) {
        removeImmersion(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            setBlackFontBarFor22Up(activity, statusBarColor);
        } else {
            setBlackFontBarFor22Down(activity, statusBarColor);
        }
    }

    /**
     * 设置状态栏文字为白色
     * 当前适配分割点 API 22 LOLLIPOP_MR1
     *
     * @param activity       activity
     * @param statusBarColor status bar color 状态栏颜色
     */
    public static void setWhiteFontBar(Activity activity, int statusBarColor) {
        removeImmersion(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            setWhiteFontBarFor22Up(activity, statusBarColor);
        } else {
            setWhiteFontBarFor22Down(activity, statusBarColor);
        }
    }

    /**
     * 设置状态栏文字为黑色
     * 当前适配分割点 API 22 LOLLIPOP_MR1
     *
     * @param activity activity
     */
    public static void setBlackFontBar(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            setBlackFontBarFor22Up(activity);
        } else {
            setBlackFontBarFor22Down(activity);
        }
    }

    /**
     * 设置状态栏文字为白色
     * 当前适配分割点 API 22 LOLLIPOP_MR1
     *
     * @param activity activity
     */
    public static void setWhiteFontBar(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            setWhiteFontBarFor22Up(activity);
        } else {
            setWhiteFontBarFor22Down(activity);
        }
    }

    public static int getWindowWidth(Activity activity) {
        return activity.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getWindowHeight(Activity activity) {
        return activity.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 添加沉浸式效果
     *
     * @param activity activity
     */
    public static void addImmersion(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 移除沉浸式效果
     *
     * @param activity activity
     */
    public static void removeImmersion(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
//        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
    }

    /**
     * 全屏显示
     *
     * @param activity activity
     */
    public static void fullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * 获取虚拟功能键高度
     *
     * @param activity
     * @return
     */
    public static int getVirtualBarHeight(Activity activity) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //--------------------------------private method----------------------------------------//

    /**
     * 获取是否存在NavigationBar
     *
     * @param context
     * @return
     */
    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setBlackFontBarFor22Down(Activity context, int statusBarColor) {
        context.getWindow().setStatusBarColor(statusBarColor);
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setWhiteFontBarFor22Down(Activity context, int statusBarColor) {
        context.getWindow().setStatusBarColor(statusBarColor);
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setWhiteFontBarFor22Up(Activity context, int statusBarColor) {
        context.getWindow().setStatusBarColor(statusBarColor);
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setBlackFontBarFor22Up(Activity context, int statusBarColor) {
        context.getWindow().setStatusBarColor(statusBarColor);
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setBlackFontBarFor22Down(Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setWhiteFontBarFor22Down(Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setWhiteFontBarFor22Up(Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void setBlackFontBarFor22Up(Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}