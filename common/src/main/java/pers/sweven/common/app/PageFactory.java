package pers.sweven.common.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityOptionsCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PageFactory 是一个功能完备的 Android 页面导航框架，提供了灵活且高效的页面跳转解决方案。
 * 该框架采用单例模式实现，支持路由表管理、参数传递、拦截器机制、动画过渡和错误处理等功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>使用建造者模式构建导航请求，支持链式调用</li>
 *   <li>全局和局部拦截器机制，支持优先级排序</li>
 *   <li>丰富的参数传递方式，包括基本类型和复杂对象</li>
 *   <li>支持自定义 Intent 配置和过渡动画</li>
 *   <li>完善的错误处理机制和回调接口</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 注册路由
 * PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);
 *
 * // 构建导航请求
 * PageFactory.getInstance().build("/user/profile")
 *     .withString("userId", "12345")
 *     .withBoolean("isVip", true)
 *     .addInterceptor(new LoginInterceptor())
 *     .withTransitionAnim(R.anim.slide_in_right, R.anim.slide_out_left)
 *     .navigation(context);
 * }</pre>
 *
 * @author Sweven
 * @version 1.0.0
 * @since 2025-05-14
 */
public abstract class PageFactory<T extends PageFactory.NavigationBuilder<?>> {
    // 路由表，用于存储路径与Activity类的映射关系
    protected final ConcurrentHashMap<String, Class<?>> routeTable = new ConcurrentHashMap<>();
    // 全局拦截器列表，存储所有全局拦截器，执行时按优先级排序
    protected final List<RouteInterceptor> globalInterceptors = new ArrayList<>();


    /**
     * 创建导航构建器，作为建造者模式的入口。
     *
     * @param path 目标路由路径
     * @return 导航构建器实例
     * @throws IllegalArgumentException 如果路径为空
     */
    public T build(String path) {
        return createNavigationBuilder(path);
    }

    // 可重写的工厂方法（扩展点）
    protected abstract T createNavigationBuilder(String path);

    /**
     * 向路由表中添加路由信息。
     *
     * @param path  路由路径
     * @param clazz 对应的Activity类
     */
    public void addRoute(@NotNull String path,@NotNull Class<?> clazz) {
        routeTable.put(path, clazz);
    }

    /**
     * 添加全局拦截器。
     *
     * @param interceptor 要添加的拦截器
     */
    public void addGlobalInterceptor(@NotNull RouteInterceptor interceptor) {
        globalInterceptors.add(interceptor);
    }

    /**
     * 导航错误回调接口，用于处理导航过程中出现的错误。
     */
    public interface OnNavigationErrorListener {
        /**
         * 当导航过程中出现错误时调用。
         *
         * @param e 抛出的异常
         */
        void onError(Exception e);
    }

    /**
     * 通用消费型接口，用于对Intent进行自定义操作。
     *
     * @param <T> 操作的对象类型
     */
    public interface Consumer<T> {
        /**
         * 对传入的对象进行操作。
         *
         * @param t 操作的对象
         */
        void accept(T t);
    }

    /**
     * 路由拦截器接口，用于拦截导航请求。
     * 实现PrioritySupport接口可支持优先级控制。
     */
    public interface RouteInterceptor {
        // 默认优先级
        int DEFAULT_PRIORITY = 0;

        /**
         * 获取拦截器的优先级，兼容新旧实现。
         *
         * @return 拦截器的优先级，数值越大优先级越高
         */
        default int getPriority() {
            return DEFAULT_PRIORITY;
        }

        /**
         * 进行拦截检查，返回true表示拦截该导航请求。
         *
         * @param clazz   目标路由路径
         * @param extras 携带的额外参数
         * @return 是否拦截该导航请求
         */
        boolean intercept(Class<?> clazz, Bundle extras);

        /**
         * 当拦截发生时的回调方法。
         *
         * @param clazz 被拦截的路由路径
         */
        default void onIntercepted(Class<?> clazz) {
            // 默认空实现
        }
    }

    /**
     * 带优先级的拦截器包装类，用于统一处理全局和局部拦截器的优先级排序。
     */
    private static class PrioritizedInterceptor {
        // 优先级数值，越大越先执行
        final int priority;
        // 被包装的拦截器实例
        final RouteInterceptor interceptor;

        /**
         * 构造方法，初始化优先级和拦截器实例。
         *
         * @param interceptor 被包装的拦截器实例
         * @param priority    优先级数值
         */
        PrioritizedInterceptor(RouteInterceptor interceptor, int priority) {
            this.interceptor = interceptor;
            this.priority = priority;
        }
    }

    /**
     * 导航构建器，采用建造者模式，负责收集导航参数、处理拦截逻辑并执行最终跳转。
     */
    public static class NavigationBuilder<T extends NavigationBuilder<T>> {
        // 目标路由路径
        protected final String path;
        protected final PageFactory<?> factory;
        // 参数容器，用于存储导航时携带的额外参数
        private final Bundle extras = new Bundle();
        // 局部拦截器列表，存储所有局部拦截器
        private final List<PrioritizedInterceptor> localInterceptors = new ArrayList<>();
        // Intent启动标志位
        private int flags;
        // Intent的Action
        private String action;
        // Intent的Data
        private Uri data;
        // Intent的MIME类型
        private String type;
        // Intent的类别列表
        private List<String> categories;
        // 是否自动添加NEW_TASK标志，默认为true
        private boolean autoNewTask = true;
        // 启动选项：之一view跟随动画
        private ActivityOptionsCompat options;
        // 进入动画资源ID
        private int enterAnim;
        // 退出动画资源ID
        private int exitAnim;
        // Intent自定义钩子，用于对Intent进行自定义操作
        private Consumer<Intent> intentCustomizer;
        // 错误监听器，用于处理导航过程中出现的错误
        private OnNavigationErrorListener errorListener;

        protected T self(){
            return (T) this;
        }

        /**
         * 构造方法，初始化目标路由路径。
         *
         * @param path 目标路由路径
         */
        public NavigationBuilder(PageFactory<?> factory,String path) {
            this.path = path;
            this.factory = factory;
        }

        // region 参数添加方法（链式调用）

        /**
         * 从URL格式的字符串中解析基础类型参数和数组。
         * 支持解析以下类型：
         * - 基本类型：int, long, float, double, boolean, String
         * - 数组类型：支持两种格式
         * 1. 标准格式：key[]=value1&key[]=value2
         * 2. 逗号分隔：key=value1,value2
         *
         * @param url 参数URL
         * @return 导航构建器实例，支持链式调用
         */
        public T withUrl(String url) {
            if (url == null || url.isEmpty()) {
                return self();
            }

            // 移除URL中的路径部分，只保留查询参数
            int queryStart = url.indexOf('?');
            String queryPart = (queryStart >= 0) ? url.substring(queryStart + 1) : url;

            if (queryPart.isEmpty()) {
                return self();
            }


            // 分割参数对
            String[] params = queryPart.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length != 2) {
                    continue; // 跳过无效的参数对
                }

                String key = keyValue[0];
                String value = keyValue[1];

                // 处理标准数组格式: key[]=value
//                if (key.endsWith("[]")) {
//                    String arrayKey = key.substring(0, key.length() - 2);
//                    arrayParams.computeIfAbsent(arrayKey, k -> new ArrayList<>()).add(value);
//                    continue;
//                }

                // 处理单个值的情况
                handleSimpleParameter(key, value);
            }

            return self();
        }

        /**
         * 处理简单参数（基本类型） - 优化版本（避免try-catch）
         */
        private void handleSimpleParameter(String key, String value) {
            // 检查是否为布尔值
            if ("true".equalsIgnoreCase(value)) {
                extras.putBoolean(key, true);
                return;
            } else if ("false".equalsIgnoreCase(value)) {
                extras.putBoolean(key, false);
                return;
            }

            // 移除Long类型的"L"后缀
            String longValue = value;
            boolean hasLongSuffix = false;
            if (value.toLowerCase().endsWith("l")) {
                longValue = value.substring(0, value.length() - 1);
                hasLongSuffix = true;
            }

            // 整数模式（不含小数部分）
            if (longValue.matches("^[+-]?\\d+$")) {
                // 检查是否为Integer范围
                if (isInIntegerRange(longValue)) {
                    extras.putInt(key, Integer.parseInt(longValue));
                    return;
                }

                // 检查是否为Long范围
                if (!hasLongSuffix || longValue.matches("^[+-]?\\d{1,18}$")) {
                    extras.putLong(key, Long.parseLong(longValue));
                    return;
                }
            }

            // 浮点模式（包含小数或指数）
            if (value.matches("^[+-]?\\d*\\.\\d+(?:[eE][+-]?\\d+)?$")) {
                // 检查是否可以用Float表示
                if (isFloatValue(value)) {
                    extras.putFloat(key, Float.parseFloat(value));
                    return;
                }

                // 否则使用Double
                extras.putDouble(key, Double.parseDouble(value));
                return;
            }

            // 默认作为String处理
            extras.putString(key, value);
        }

        /**
         * 检查字符串是否在Integer范围内
         */
        private boolean isInIntegerRange(String value) {
            if (value.startsWith("-")) {
                if (value.length() < 11) return true;
                if (value.length() > 11) return false;
                return value.compareTo(String.valueOf(Integer.MIN_VALUE)) <= 0;
            } else {
                if (value.startsWith("+")) value = value.substring(1);
                if (value.length() < 10) return true;
                if (value.length() > 10) return false;
                return value.compareTo(String.valueOf(Integer.MAX_VALUE)) <= 0;
            }
        }

        /**
         * 检查字符串是否可以用Float精确表示
         */
        private boolean isFloatValue(String value) {
            double d = Double.parseDouble(value);
            float f = (float) d;
            return d == f;
        }

        /**
         * 添加字符串类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withString(String key, String value) {
            extras.putString(key, value);
            return self();
        }

        /**
         * 添加整数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withInt(String key, int value) {
            extras.putInt(key, value);
            return self();
        }

        /**
         * 添加布尔类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withBoolean(String key, boolean value) {
            extras.putBoolean(key, value);
            return self();
        }

        /**
         * 添加长整数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withLong(String key, long value) {
            extras.putLong(key, value);
            return self();
        }

        /**
         * 添加浮点数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withFloat(String key, float value) {
            extras.putFloat(key, value);
            return self();
        }

        /**
         * 添加双精度浮点数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withDouble(String key, double value) {
            extras.putDouble(key, value);
            return self();
        }

        /**
         * 添加Parcelable类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withParcelable(String key, Parcelable value) {
            extras.putParcelable(key, value);
            return self();
        }

        /**
         * 添加Serializable类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        public T withSerializable(String key, Serializable value) {
            extras.putSerializable(key, value);
            return self();
        }

        public T withStringArrayList(String key, ArrayList<String> list) {
            extras.putStringArrayList(key, list);
            return self();
        }


        /**
         * 添加Bundle类型的参数。
         *
         * @param bundle 参数Bundle
         * @return 导航构建器实例，支持链式调用
         */
        public T withBundle(Bundle bundle) {
            if (bundle != null) {
                extras.putAll(bundle);
            }
            return self();
        }
        // endregion

        // region Intent属性增强

        /**
         * 设置Intent的Action。
         *
         * @param action Intent的Action
         * @return 导航构建器实例，支持链式调用
         */
        public T withAction(String action) {
            this.action = action;
            return self();
        }

        /**
         * 设置Intent的Data。
         *
         * @param data Intent的Data
         * @return 导航构建器实例，支持链式调用
         */
        public T withData(Uri data) {
            this.data = data;
            return self();
        }

        /**
         * 设置Intent的MIME类型。
         *
         * @param type Intent的MIME类型
         * @return 导航构建器实例，支持链式调用
         */
        public T withType(String type) {
            this.type = type;
            return self();
        }

        /**
         * 添加Intent的类别。
         *
         * @param category Intent的类别
         * @return 导航构建器实例，支持链式调用
         */
        public T addCategory(String category) {
            if (this.categories == null) {
                this.categories = new ArrayList<>();
            }
            this.categories.add(category);
            return self();
        }
        // endregion

        // region 启动标志控制

        /**
         * 设置Intent的启动标志位。
         *
         * @param flags Intent的启动标志位
         * @return 导航构建器实例，支持链式调用
         */
        public T setFlags(int flags) {
            this.flags = flags;
            return self();
        }

        /**
         * 禁用自动添加NEW_TASK标志，适用于非Activity上下文。
         *
         * @return 导航构建器实例，支持链式调用
         */
        public T disableAutoNewTask() {
            this.autoNewTask = false;
            return self();
        }

        /**
         * 设置过渡动画。
         *
         * @param enterAnim 进入动画资源ID
         * @param exitAnim  退出动画资源ID
         * @return 导航构建器实例，支持链式调用
         */
        public T withTransitionAnim(int enterAnim, int exitAnim) {
            this.enterAnim = enterAnim;
            this.exitAnim = exitAnim;
            return self();
        }

        /**
         * 启动选项
         *
         * @param options 选项
         * @return {@link T}
         */
        public T setOptions(ActivityOptionsCompat options){
            this.options = options;
            return self();
        }

        // endregion

        // region 拦截器管理

        /**
         * 添加局部拦截器，使用默认优先级。
         *
         * @param interceptor 要添加的拦截器
         * @return 导航构建器实例，支持链式调用
         * @throws IllegalArgumentException 如果拦截器为空
         */
        public T addInterceptor(RouteInterceptor interceptor) {
            if (interceptor == null) {
                throw new IllegalArgumentException("Interceptor cannot be null");
            }
            addInterceptor(interceptor, interceptor.getPriority());
            return self();
        }

        /**
         * 添加局部拦截器，指定优先级。
         *
         * @param interceptor 要添加的拦截器
         * @param priority    拦截器的优先级
         * @return 导航构建器实例，支持链式调用
         * @throws IllegalArgumentException 如果拦截器为空
         */
        public T addInterceptor(RouteInterceptor interceptor, int priority) {
            if (interceptor == null) {
                throw new IllegalArgumentException("Interceptor cannot be null");
            }
            localInterceptors.add(new PrioritizedInterceptor(interceptor, priority));
            return self();
        }
        // endregion

        //region 其他设置

        /**
         * 设置Intent自定义钩子，用于对Intent进行自定义操作。
         *
         * @param customizer Intent自定义钩子
         * @return 导航构建器实例，支持链式调用
         */
        public T customizeIntent(Consumer<Intent> customizer) {
            this.intentCustomizer = customizer;
            return self();
        }

        /**
         * 设置导航错误监听器，用于处理导航过程中出现的错误。
         *
         * @param listener 导航错误监听器
         * @return 导航构建器实例，支持链式调用
         */
        public T setOnNavigationErrorListener(OnNavigationErrorListener listener) {
            this.errorListener = listener;
            return self();
        }
        // endregion

        // region 跳转执行核心逻辑

        /**
         * 执行导航操作，为主入口方法。
         *
         * @param context 上下文对象
         */
        public void navigation(Context context) {
            navigation(context, null);
        }

        /**
         * 执行导航操作，兼容旧版本API。
         * 此方法已被弃用，请使用更灵活的 {@link #navigation(Context, ActivityResultLauncher)} 方法替代。
         *
         * @param activity    当前Activity实例
         * @param requestCode 请求码，用于接收返回结果
         * @deprecated 使用新的 Activity Result API 替代
         */
        @Deprecated
        public void navigation(Activity activity, int requestCode) {
            // 1. 路由表校验
            Class<?> target = (Class<?>) factory.routeTable.get(path);
            if (target == null) {
                handleError(activity, new RuntimeException("Route not found: " + path));
                return;
            }

            // 2. 拦截器处理流程
            if (checkInterceptors()) return;

            // 3. Intent构造
            Intent intent = buildBaseIntent(activity, target);

            // 4. 根据是否有请求码选择合适的启动方式
            try {
                if (requestCode >= 0) {
                    // 使用startActivityForResult（旧方式）
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    // 直接启动Activity
                    activity.startActivity(intent);
                }

                // 应用过渡动画
                if (enterAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(enterAnim, exitAnim);
                }
            } catch (Exception e) {
                handleError(activity, e);
            }
        }

        /**
         * 执行导航操作，为主入口方法。
         *
         * @param context  上下文对象
         * @param launcher Activity结果启动器，可选
         */
        public void navigation(Context context, ActivityResultLauncher<Intent> launcher) {
            // 1. 路由表校验
            Class<?> target = factory.routeTable.get(path);
            if (target == null) {
                handleError(context, new RuntimeException("Route not found: " + path));
                return;
            }

            // 2. 拦截器处理流程
            if (checkInterceptors()) return;

            // 3. Intent构造
            Intent intent = buildBaseIntent(context, target);

            // 4. 执行跳转
            executeNavigation(context, launcher, options, intent);
        }

        /**
         * 拦截器检查流程，判断导航请求是否被拦截。
         * 使用Java标准库的Collections.sort进行排序，性能更优。
         *
         * @return 是否被拦截
         */
        @SuppressLint("ObsoleteSdkInt")
        private boolean checkInterceptors() {
            // 合并全局和局部拦截器
            List<PrioritizedInterceptor> allInterceptors = mergeInterceptors();

            // 使用Java标准库的Collections.sort进行排序（基于TimSort，O(n log n)）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(allInterceptors, Comparator.comparingInt(a -> -a.priority));
            } else {
                // Android N以下版本使用传统Comparator实现
                Collections.sort(allInterceptors, (a, b) -> {
                    // 降序排序：优先级高的在前
                    return Integer.compare(b.priority, a.priority);
                });
            }

            // 执行拦截检查
            for (PrioritizedInterceptor wrapper : allInterceptors) {
                Class<?> aClass = factory.routeTable.get(this.path);
                if (wrapper.interceptor.intercept(aClass, extras)) {
                    wrapper.interceptor.onIntercepted(aClass);
                    return true;
                }
            }
            return false;
        }

        // 合并全局和局部拦截器，全局拦截器在前
        private List<PrioritizedInterceptor> mergeInterceptors() {
            List<PrioritizedInterceptor> result = new ArrayList<>();

            // 转换全局拦截器为带优先级包装
            for (RouteInterceptor interceptor : factory.globalInterceptors) {
                result.add(new PrioritizedInterceptor(interceptor, interceptor.getPriority()));
            }

            result.addAll(localInterceptors);
            return result;
        }
        // endregion

        // region Intent构造相关

        /**
         * 构建基础Intent。
         *
         * @param context 上下文对象
         * @param target  目标Activity类
         * @return 构建好的Intent
         */
        private Intent buildBaseIntent(Context context, Class<?> target) {
            Intent intent = new Intent(context, target)
                    .putExtras(extras)
                    .addFlags(flags);

            // 设置可选属性
            if (action != null) intent.setAction(action);
            if (data != null || type != null) intent.setDataAndType(data, type);
            if (categories != null) {
                for (String category : categories) {
                    intent.addCategory(category);
                }
            }

            // 应用自定义修改
            if (intentCustomizer != null) {
                intentCustomizer.accept(intent);
            }

            // 自动处理NEW_TASK标志
            if (!(context instanceof Activity) && autoNewTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            return intent;
        }
        // endregion

        // region 跳转执行相关

        /**
         * 执行导航跳转。
         *
         * @param context  上下文对象
         * @param launcher Activity结果启动器
         * @param options  启动选项
         * @param intent   要启动的Intent
         */
        private void executeNavigation(Context context,
                                       ActivityResultLauncher<Intent> launcher,
                                       ActivityOptionsCompat options,
                                       Intent intent) {
            try {
                ActivityOptionsCompat finalOptions = resolveOptions(context, options);

                if (launcher != null) {
                    launchWithLauncher(launcher, intent, finalOptions);
                } else {
                    launchDirectly(context, intent, finalOptions);
                }
            } catch (Exception e) {
                handleError(context, e);
            }
        }

        /**
         * 解析过渡动画选项。
         *
         * @param context 上下文对象
         * @param options 启动选项
         * @return 解析后的启动选项
         */
        private ActivityOptionsCompat resolveOptions(Context context,
                                                     ActivityOptionsCompat options) {
            if (options != null) return options;
            if (enterAnim != 0 || exitAnim != 0) {
                return ActivityOptionsCompat.makeCustomAnimation(
                        context, enterAnim, exitAnim);
            }
            return null;
        }

        /**
         * 通过ActivityResultLauncher启动Activity，适用于需要结果回调的情况。
         *
         * @param launcher Activity结果启动器
         * @param intent   要启动的Intent
         * @param options  启动选项
         */
        private void launchWithLauncher(ActivityResultLauncher<Intent> launcher,
                                        Intent intent,
                                        ActivityOptionsCompat options) {
            if (options != null) {
                launcher.launch(intent, options);
            } else {
                launcher.launch(intent);
            }
        }

        /**
         * 直接启动Activity。
         *
         * @param context 上下文对象
         * @param intent  要启动的Intent
         * @param options 启动选项
         */
        private void launchDirectly(Context context, Intent intent,
                                    ActivityOptionsCompat options) {
            if (options != null && context instanceof Activity) {
                ((Activity) context).startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        }
        // endregion

        // region 错误处理

        /**
         * 处理导航过程中出现的错误。
         * 优先使用自定义错误监听器处理错误，若未设置则显示默认错误提示。
         *
         * @param context 上下文对象
         * @param e       抛出的异常
         */
        private void handleError(Context context, Exception e) {
            if (errorListener != null) {
                errorListener.onError(e); // 优先使用自定义处理
            } else {
                showDefaultError(context, e);
            }
        }

        /**
         * 显示默认错误提示。
         *
         * @param context 上下文对象
         * @param e       抛出的异常
         */
        private void showDefaultError(Context context, Exception e) {
            Toast.makeText(context,
                    "Navigation failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        // endregion
    }

    public static class BundleUrl {
        private final String url;
        private final Bundle extras;

        public BundleUrl(String url, Bundle extras) {
            this.url = url;
            this.extras = extras;
        }

        public void inject() {
            if (url == null || url.isEmpty()) {
                return;
            }

            // 移除URL中的路径部分，只保留查询参数
            int queryStart = url.indexOf('?');
            String queryPart = (queryStart >= 0) ? url.substring(queryStart + 1) : url;

            if (queryPart.isEmpty()) {
                return;
            }

            // 用于存储数组参数的映射
            Map<String, List<String>> arrayParams = new HashMap<>();

            // 分割参数对
            String[] params = queryPart.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length != 2) {
                    continue; // 跳过无效的参数对
                }

                String key = keyValue[0];
                String value = keyValue[1];

                // 处理标准数组格式: key[]=value
                if (key.endsWith("[]")) {
                    String arrayKey = key.substring(0, key.length() - 2);
                    arrayParams.computeIfAbsent(arrayKey, k -> new ArrayList<>()).add(value);
                    continue;
                }

                // 处理JSON格式的参数
                if (isJsonString(value)) {
                    handleJsonParameter(key, value);
                    continue;
                }

                // 处理普通参数（包括逗号分隔的数组）
                handleNormalParameter(key, value, arrayParams);
            }

            // 处理收集到的所有数组参数
            for (Map.Entry<String, List<String>> entry : arrayParams.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                handleArrayParameter(key, values.toArray(new String[0]));
            }
        }

        /**
         * 判断字符串是否是JSON格式
         */
        private boolean isJsonString(String value) {
            value = value.trim();
            return (value.startsWith("{") && value.endsWith("}")) ||
                    (value.startsWith("[") && value.endsWith("]"));
        }

        /**
         * 处理JSON格式的参数
         */
        private void handleJsonParameter(String key, String jsonValue) {
            try {
                // 移除首尾的引号（如果有的话）
                if ((jsonValue.startsWith("\"") && jsonValue.endsWith("\"")) ||
                        (jsonValue.startsWith("'") && jsonValue.endsWith("'"))) {
                    jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
                }

                // 尝试解析为JSONObject
                if (jsonValue.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(jsonValue);
                    Bundle jsonBundle = new Bundle();

                    // 将JSON对象的键值对转换为Bundle
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String jsonKey = keys.next();
                        Object jsonValueObj = jsonObject.get(jsonKey);

                        // 根据JSON值的类型放入Bundle
                        if (jsonValueObj instanceof Integer) {
                            jsonBundle.putInt(jsonKey, (Integer) jsonValueObj);
                        } else if (jsonValueObj instanceof Long) {
                            jsonBundle.putLong(jsonKey, (Long) jsonValueObj);
                        } else if (jsonValueObj instanceof Double) {
                            jsonBundle.putDouble(jsonKey, (Double) jsonValueObj);
                        } else if (jsonValueObj instanceof Boolean) {
                            jsonBundle.putBoolean(jsonKey, (Boolean) jsonValueObj);
                        } else if (jsonValueObj instanceof String) {
                            jsonBundle.putString(jsonKey, (String) jsonValueObj);
                        } else {
                            // 对于其他类型，保存为字符串
                            jsonBundle.putString(jsonKey, jsonValueObj.toString());
                        }
                    }

                    // 将JSON对象作为Bundle存储
                    extras.putBundle(key, jsonBundle);
                    return;
                }

                // 尝试解析为JSONArray
                if (jsonValue.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    ArrayList<String> arrayList = new ArrayList<>();

                    // 将JSON数组转换为字符串列表
                    for (int i = 0; i < jsonArray.length(); i++) {
                        arrayList.add(jsonArray.getString(i));
                    }

                    // 将JSON数组作为字符串列表存储
                    extras.putStringArrayList(key, arrayList);
                }
            } catch (JSONException e) {
                // 如果解析失败，将JSON作为普通字符串处理
                extras.putString(key, jsonValue);
            }
        }

        /**
         * 处理普通参数（非数组格式）
         */
        private void handleNormalParameter(String key, String value, Map<String, List<String>> arrayParams) {
            // 处理逗号分隔的数组
            if (value.contains(",") && !isQuotedString(value) && !isNumeric(value)) {
                String[] values = value.split(",");
                arrayParams.put(key, Arrays.asList(values));
                return;
            }

            // 先检查是否为布尔值（效率高）
            if ("true".equalsIgnoreCase(value)) {
                extras.putBoolean(key, true);
                return;
            } else if ("false".equalsIgnoreCase(value)) {
                extras.putBoolean(key, false);
                return;
            }

            // 检查是否为数值类型（使用正则表达式）
            if (value.matches("^-?\\d+$")) {
                // 整数字面量
                try {
                    // 尝试解析为int
                    int intValue = Integer.parseInt(value);
                    extras.putInt(key, intValue);
                    return;
                } catch (NumberFormatException e) {
                    // 如果超出int范围，尝试long
                    long longValue = Long.parseLong(value);
                    extras.putLong(key, longValue);
                    return;
                }
            } else if (value.matches("^-?\\d+\\.\\d+$")) {
                // 小数字面量
                try {
                    // 尝试解析为float
                    float floatValue = Float.parseFloat(value);
                    extras.putFloat(key, floatValue);
                    return;
                } catch (NumberFormatException e) {
                    // 如果超出float范围，尝试double
                    double doubleValue = Double.parseDouble(value);
                    extras.putDouble(key, doubleValue);
                    return;
                }
            }

            // 尝试解析为日期
//            Date dateValue = parseDate(value);
//            if (dateValue != null) {
//                extras.putLong(key, dateValue.getTime());
//                return;
//            }

            // 默认作为字符串处理
            extras.putString(key, value);
        }

        /**
         * 判断字符串是否是带引号的字符串（不视为数组）
         */
        private boolean isQuotedString(String value) {
            return (value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"));
        }

        /**
         * 判断字符串是否是纯数字（避免将数字列表误判为数组）
         */
        private boolean isNumeric(String value) {
            return value.matches("^-?\\d+(\\.\\d+)?$");
        }

        /**
         * 处理数组类型的参数
         */
        private void handleArrayParameter(String key, String[] values) {
            if (values.length == 0) return;

            // 尝试检测数组元素类型
            try {
                // 尝试解析为整数数组
                int[] intArray = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    intArray[i] = Integer.parseInt(values[i]);
                }
                extras.putIntArray(key, intArray);
                return;
            } catch (NumberFormatException e) {
                // 不是整数数组，继续尝试其他类型
            }

            try {
                // 尝试解析为长整数数组
                long[] longArray = new long[values.length];
                for (int i = 0; i < values.length; i++) {
                    longArray[i] = Long.parseLong(values[i]);
                }
                extras.putLongArray(key, longArray);
                return;
            } catch (NumberFormatException e) {
                // 不是长整数数组，继续尝试其他类型
            }

            try {
                // 尝试解析为浮点数数组
                float[] floatArray = new float[values.length];
                for (int i = 0; i < values.length; i++) {
                    floatArray[i] = Float.parseFloat(values[i]);
                }
                extras.putFloatArray(key, floatArray);
                return;
            } catch (NumberFormatException e) {
                // 不是浮点数数组，继续尝试其他类型
            }

            try {
                // 尝试解析为双精度浮点数数组
                double[] doubleArray = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    doubleArray[i] = Double.parseDouble(values[i]);
                }
                extras.putDoubleArray(key, doubleArray);
                return;
            } catch (NumberFormatException e) {
                // 不是双精度浮点数数组，继续尝试其他类型
            }

            // 尝试解析为布尔数组
            boolean[] booleanArray = new boolean[values.length];
            boolean allBoolean = true;
            for (int i = 0; i < values.length; i++) {
                if ("true".equalsIgnoreCase(values[i]) || "1".equals(values[i])) {
                    booleanArray[i] = true;
                } else if ("false".equalsIgnoreCase(values[i]) || "0".equals(values[i])) {
                    booleanArray[i] = false;
                } else {
                    allBoolean = false;
                    break;
                }
            }

            if (allBoolean) {
                // 将布尔数组转换为字节数组存储（Bundle不直接支持boolean[]）
                byte[] byteArray = new byte[booleanArray.length];
                for (int i = 0; i < booleanArray.length; i++) {
                    byteArray[i] = (byte) (booleanArray[i] ? 1 : 0);
                }
                extras.putByteArray(key, byteArray);
                return;
            }

            // 默认作为字符串数组处理
            extras.putStringArray(key, values);
        }

        /**
         * 解析日期字符串，支持多种常见日期格式
         */
        private Date parseDate(String value) {
            // 定义支持的日期格式
            List<SimpleDateFormat> formats = Arrays.asList(
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US),
                    new SimpleDateFormat("yyyy/MM/dd", Locale.US),
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US)
            );

            for (SimpleDateFormat format : formats) {
                try {
                    return format.parse(value);
                } catch (ParseException e) {
                    // 尝试下一个格式
                }
            }

            return null; // 无法解析的日期格式
        }
    }
}