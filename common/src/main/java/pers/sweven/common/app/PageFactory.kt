package pers.sweven.common.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import java.io.Serializable
import java.util.*
import java.util.Collections.sort
import java.util.concurrent.ConcurrentHashMap

/**
 * PageFactory 是一个功能完备的 Android 页面导航框架，提供了灵活且高效的页面跳转解决方案。
 * 该框架采用单例模式实现，支持路由表管理、参数传递、拦截器机制、动画过渡和错误处理等功能。
 *
 *
 * 核心特性：
 *
 *  * 使用建造者模式构建导航请求，支持链式调用
 *  * 全局和局部拦截器机制，支持优先级排序
 *  * 丰富的参数传递方式，包括基本类型和复杂对象
 *  * 支持自定义 Intent 配置和过渡动画
 *  * 完善的错误处理机制和回调接口
 *
 *
 *
 * 使用示例：
 * <pre>`// 注册路由
 * PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);
 *
 * // 构建导航请求
 * PageFactory.getInstance().build("/user/profile")
 * .withString("userId", "12345")
 * .withBoolean("isVip", true)
 * .addInterceptor(new LoginInterceptor())
 * .withTransitionAnim(R.anim.slide_in_right, R.anim.slide_out_left)
 * .navigation(context);
`</pre> *
 *
 * @author Sweven
 * @version 1.0.0
 * @since 2025-05-14
 */
abstract class PageFactory<T : PageFactory.NavigationBuilder<*>> {
    // 路由表，用于存储路径与Activity类的映射关系
    protected val routeTable = ConcurrentHashMap<String, Class<*>>()

    // 全局拦截器列表，存储所有全局拦截器，执行时按优先级排序
    protected val globalInterceptors: MutableList<PrioritizedInterceptor> = ArrayList()

    /**
     * 创建导航构建器，作为建造者模式的入口。
     *
     * @param path 目标路由路径
     * @return 导航构建器实例
     */
    fun build(path: String): T {
        return createNavigationBuilder(path)
    }

    // 可重写的工厂方法（扩展点）
    protected abstract fun createNavigationBuilder(path: String): T

    /**
     * 向路由表中添加路由信息。
     *
     * @param path  路由路径
     * @param clazz 对应的Activity类
     */
    fun addRoute(path: String, clazz: Class<*>) {
        routeTable[path] = clazz
    }

    /**
     * 添加全局拦截器。
     *
     * @param interceptor 要添加的拦截器
     */
    fun addGlobalInterceptor(interceptor: PrioritizedInterceptor) {
        globalInterceptors.add(interceptor)
    }

    /**
     * 添加全局拦截器。
     *
     * @param interceptor 要添加的拦截器
     * @param priority 优先级
     */
    @JvmOverloads
    fun addGlobalInterceptor(interceptor: RouteInterceptor, priority: Int = 0) {
        addGlobalInterceptor(PrioritizedInterceptor(interceptor, priority))
    }

    /**
     * 添加全局拦截器。
     *
     * @param priority 优先级
     * @param interceptor 要添加的拦截器
     */
    fun addGlobalInterceptor(priority: Int = 0, interceptor: (Class<*>, Bundle) -> Boolean) {
        addGlobalInterceptor(PrioritizedInterceptor(object : RouteInterceptor {
            override fun intercept(clazz: Class<*>, extras: Bundle): Boolean {
                return interceptor(clazz, extras)
            }
        }, priority))
    }


    /**
     * 导航错误回调接口，用于处理导航过程中出现的错误。
     */
    interface OnNavigationErrorListener {
        fun onError(e: Exception?)
    }

    /**
     * 路由拦截器接口，用于拦截导航请求。
     * 实现PrioritySupport接口可支持优先级控制。
     */
    interface RouteInterceptor {
        fun intercept(clazz: Class<*>, extras: Bundle): Boolean
    }

    /**
     * 通用消费型接口，用于对Intent进行自定义操作。
     *
     * @param <T> 操作的对象类型
     */
    interface Consumer<T> {
        fun consume(data: T)
    }

    /**
     * 带优先级的拦截器包装类，用于统一处理全局和局部拦截器的优先级排序。
     * @param interceptor 被包装的拦截器实例
     * @param priority    优先级数值,优先级数值，越大越先执行
     */
    class PrioritizedInterceptor(
        val interceptor: RouteInterceptor,
        val priority: Int = 0,
    )

    /**
     * 导航构建器，采用建造者模式，负责收集导航参数、处理拦截逻辑并执行最终跳转。
     *
     * @param path 目标路由路径
     */
    open class NavigationBuilder<T : NavigationBuilder<T>?>(
        private val factory: PageFactory<*>,
        private val path: String,
    ) {
        // 参数容器，用于存储导航时携带的额外参数
        protected val extras = Bundle()

        // 局部拦截器列表，存储所有局部拦截器
        protected val localInterceptors: MutableList<PrioritizedInterceptor> = ArrayList()

        // Intent启动标志位
        protected var flags = 0

        // Intent的Action
        protected var action: String? = null

        // Intent的Data
        protected var data: Uri? = null

        // Intent的MIME类型
        protected var type: String? = null

        // Intent的类别列表
        protected var categories: MutableList<String>? = null

        // 是否自动添加NEW_TASK标志，默认为true
        protected var autoNewTask = true

        // 启动选项：之一view跟随动画
        protected var options: ActivityOptionsCompat? = null

        // 进入动画资源ID
        protected var enterAnim = 0

        // 退出动画资源ID
        protected var exitAnim = 0

        // Intent自定义钩子，用于对Intent进行自定义操作
        protected var intentCustomizer: Consumer<Intent>? = null

        // 错误监听器，用于处理导航过程中出现的错误
        protected var errorListener: OnNavigationErrorListener? = null

        protected fun self(): T {
            return this as T
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
        fun withUrl(url: String?): T {
            if (url == null || url.isEmpty()) {
                return self()
            }

            // 移除URL中的路径部分，只保留查询参数
            val queryStart = url.indexOf('?')
            val queryPart: String = if (queryStart >= 0) url.substring(queryStart + 1) else url
            if (queryPart.isEmpty()) {
                return self()
            }


            // 分割参数对
            val params = queryPart.split("&").toTypedArray()
            for (param in params) {
                val keyValue = param.split("=", limit = 2).toTypedArray()
                if (keyValue.size != 2) {
                    continue  // 跳过无效的参数对
                }
                val key = keyValue[0]
                val value = keyValue[1]

                // 处理标准数组格式: key[]=value
//                if (key.endsWith("[]")) {
//                    String arrayKey = key.substring(0, key.length() - 2);
//                    arrayParams.computeIfAbsent(arrayKey, k -> new ArrayList<>()).add(value);
//                    continue;
//                }

                // 处理单个值的情况
                handleSimpleParameter(key, value)
            }
            return self()
        }

        /**
         * 处理简单参数（基本类型） - 优化版本（避免try-catch）
         */
        private fun handleSimpleParameter(key: String, value: String) {
            // 检查是否为布尔值
            if ("true".equals(value, ignoreCase = true)) {
                extras.putBoolean(key, true)
                return
            } else if ("false".equals(value, ignoreCase = true)) {
                extras.putBoolean(key, false)
                return
            }

            // 移除Long类型的"L"后缀
            var longValue = value
            var hasLongSuffix = false
            if (value.lowercase(Locale.getDefault()).endsWith("l")) {
                longValue = value.substring(0, value.length - 1)
                hasLongSuffix = true
            }

            // 整数模式（不含小数部分）
            if (longValue.matches(Regex("^[+-]?\\d+$"))) {
                // 检查是否为Integer范围
                if (isInIntegerRange(longValue)) {
                    extras.putInt(key, longValue.toInt())
                    return
                }

                // 检查是否为Long范围
                if (!hasLongSuffix || longValue.matches(Regex("^[+-]?\\d{1,18}$"))) {
                    extras.putLong(key, longValue.toLong())
                    return
                }
            }

            // 浮点模式（包含小数或指数）
            if (value.matches(Regex("^[+-]?\\d*\\.\\d+(?:[eE][+-]?\\d+)?$"))) {
                // 检查是否可以用Float表示
                if (isFloatValue(value)) {
                    extras.putFloat(key, value.toFloat())
                    return
                }

                // 否则使用Double
                extras.putDouble(key, value.toDouble())
                return
            }

            // 默认作为String处理
            extras.putString(key, value)
        }

        /**
         * 检查字符串是否在Integer范围内
         */
        private fun isInIntegerRange(it: String): Boolean {
            return try {
                val value: Long = it.toLong()
                value >= Int.MIN_VALUE && value <= Int.MAX_VALUE
            } catch (e: NumberFormatException) {
                false
            }
        }

        /**
         * 检查字符串是否可以用Float精确表示
         */
        private fun isFloatValue(value: String): Boolean {
            val d = value.toDouble()
            val f = d.toFloat()
            return d == f.toDouble()
        }

        /**
         * 添加字符串类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withString(key: String?, value: String?): T {
            extras.putString(key, value)
            return self()
        }

        /**
         * 添加整数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withInt(key: String?, value: Int): T {
            extras.putInt(key, value)
            return self()
        }

        /**
         * 添加布尔类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withBoolean(key: String?, value: Boolean): T {
            extras.putBoolean(key, value)
            return self()
        }

        /**
         * 添加长整数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withLong(key: String?, value: Long): T {
            extras.putLong(key, value)
            return self()
        }

        /**
         * 添加浮点数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withFloat(key: String?, value: Float): T {
            extras.putFloat(key, value)
            return self()
        }

        /**
         * 添加双精度浮点数类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withDouble(key: String?, value: Double): T {
            extras.putDouble(key, value)
            return self()
        }

        /**
         * 添加Parcelable类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withParcelable(key: String?, value: Parcelable?): T {
            extras.putParcelable(key, value)
            return self()
        }

        /**
         * 添加Serializable类型的参数。
         *
         * @param key   参数键
         * @param value 参数值
         * @return 导航构建器实例，支持链式调用
         */
        fun withSerializable(key: String?, value: Serializable?): T {
            extras.putSerializable(key, value)
            return self()
        }

        fun withStringArrayList(key: String?, list: ArrayList<String>?): T {
            extras.putStringArrayList(key, list)
            return self()
        }

        /**
         * 添加Bundle类型的参数。
         *
         * @param bundle 参数Bundle
         * @return 导航构建器实例，支持链式调用
         */
        fun withBundle(bundle: Bundle?): T {
            if (bundle != null) {
                extras.putAll(bundle)
            }
            return self()
        }

        // endregion

        // region Intent属性增强

        /**
         * 设置Intent的Action。
         *
         * @param action Intent的Action
         * @return 导航构建器实例，支持链式调用
         */
        fun withAction(action: String?): T {
            this.action = action
            return self()
        }

        /**
         * 设置Intent的Data。
         *
         * @param data Intent的Data
         * @return 导航构建器实例，支持链式调用
         */
        fun withData(data: Uri?): T {
            this.data = data
            return self()
        }

        /**
         * 设置Intent的MIME类型。
         *
         * @param type Intent的MIME类型
         * @return 导航构建器实例，支持链式调用
         */
        fun withType(type: String?): T {
            this.type = type
            return self()
        }

        /**
         * 添加Intent的类别。
         *
         * @param category Intent的类别
         * @return 导航构建器实例，支持链式调用
         */
        fun addCategory(category: String): T {
            if (categories == null) {
                categories = ArrayList()
            }
            categories!!.add(category)
            return self()
        }

        // endregion

        // region 启动标志控制

        /**
         * 设置Intent的启动标志位。
         *
         * @param flags Intent的启动标志位
         * @return 导航构建器实例，支持链式调用
         */
        fun setFlags(flags: Int): T {
            this.flags = flags
            return self()
        }

        fun addFlags(flags: Int):T{
            this.flags = this.flags or flags
            return self()
        }

        /**
         * 禁用自动添加NEW_TASK标志，适用于非Activity上下文。
         *
         * @return 导航构建器实例，支持链式调用
         */
        fun disableAutoNewTask(): T {
            autoNewTask = false
            return self()
        }

        /**
         * 设置过渡动画。
         *
         * @param enterAnim 进入动画资源ID
         * @param exitAnim  退出动画资源ID
         * @return 导航构建器实例，支持链式调用
         */
        fun withTransitionAnim(enterAnim: Int, exitAnim: Int): T {
            this.enterAnim = enterAnim
            this.exitAnim = exitAnim
            return self()
        }

        /**
         * 启动选项
         *
         * @param options 选项
         * @return [T]
         */
        fun setOptions(options: ActivityOptionsCompat?): T {
            this.options = options
            return self()
        }
        // endregion

        // region 拦截器管理

        /**
         * 添加局部拦截器，指定优先级。
         * 兼容java
         * @param interceptor 要添加的拦截器
         * @param priority    拦截器的优先级
         * @return 导航构建器实例，支持链式调用
         * @throws IllegalArgumentException 如果拦截器为空
         */
        @JvmOverloads
        fun addInterceptor(interceptor: RouteInterceptor, priority: Int = 0): T {
            localInterceptors.add(PrioritizedInterceptor(interceptor, priority))
            return self()
        }

        /**
         * 添加局部拦截器，指定优先级。
         *
         * @param interceptor 要添加的拦截器
         * @param priority    拦截器的优先级
         * @return 导航构建器实例，支持链式调用
         * @throws IllegalArgumentException 如果拦截器为空
         */
        fun addInterceptor(
            priority: Int = 0,
            interceptor: (clazz: Class<*>, extras: Bundle) -> Boolean,
        ): T {
            addInterceptor(object : RouteInterceptor {
                override fun intercept(clazz: Class<*>, extras: Bundle): Boolean {
                    return interceptor(clazz, extras)
                }
            }, priority)
            return self()
        }

        // endregion

        //region 其他设置

        /**
         * 设置Intent自定义钩子，用于对Intent进行自定义操作。
         *
         * @param customizer Intent自定义钩子
         * @return 导航构建器实例，支持链式调用
         */
        fun customizeIntent(customizer: Consumer<Intent>?): T {
            intentCustomizer = customizer
            return self()
        }

        /**
         * 设置Intent自定义钩子，用于对Intent进行自定义操作。
         *
         * @param customizer Intent自定义钩子
         * @return 导航构建器实例，支持链式调用
         */
        fun customizeIntent(customizer: (Intent) -> Unit): T {
            intentCustomizer = object : Consumer<Intent> {
                override fun consume(data: Intent) {
                    customizer(data)
                }
            }
            return self()
        }


        /**
         * 设置导航错误监听器，用于处理导航过程中出现的错误。
         *
         * @param listener 导航错误监听器
         * @return 导航构建器实例，支持链式调用
         */
        fun setOnNavigationErrorListener(listener: OnNavigationErrorListener?): T {
            errorListener = listener
            return self()
        }

        /**
         * 设置导航错误监听器，用于处理导航过程中出现的错误。
         *
         * @param listener 导航错误监听器
         * @return 导航构建器实例，支持链式调用
         */
        fun setOnNavigationErrorListener(listener: (Exception) -> Unit): T {
            errorListener = object : OnNavigationErrorListener {
                override fun onError(e: Exception?) {
                    e?.let { listener(it) }
                }
            }
            return self()
        }

        /**
         * 执行导航操作，兼容旧版本API。
         * 此方法已被弃用，请使用更灵活的 [.navigation] 方法替代。
         *
         * @param activity    当前Activity实例
         * @param requestCode 请求码，用于接收返回结果
         */
        @Deprecated("使用新的 Activity Result API 替代")
        fun navigation(activity: Activity, requestCode: Int) {
            // 1. 路由表校验
            val target = factory.routeTable[path]
            if (target == null) {
                handleError(activity, RuntimeException("Route not found: $path"))
                return
            }

            // 2. 拦截器处理流程
            if (checkInterceptors()) return

            // 3. Intent构造
            val intent = buildBaseIntent(activity, target)

            // 4. 根据是否有请求码选择合适的启动方式
            try {
                if (requestCode >= 0) {
                    // 使用startActivityForResult（旧方式）
                    activity.startActivityForResult(intent, requestCode)
                } else {
                    // 直接启动Activity
                    activity.startActivity(intent)
                }

                // 应用过渡动画
                if (enterAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(enterAnim, exitAnim)
                }
            } catch (e: Exception) {
                handleError(activity, e)
            }
        }

        // endregion

        // region 跳转执行核心逻辑

        /**
         * 执行导航操作，为主入口方法。
         *
         * @param context 上下文对象
         * @param launcher Activity结果启动器，可选
         */
        @JvmOverloads
        fun navigation(context: Context, launcher: ActivityResultLauncher<Intent>? = null) {
            // 1. 路由表校验
            val target = factory.routeTable[path]
            if (target == null) {
                handleError(context, RuntimeException("Route not found: $path"))
                return
            }

            // 2. 拦截器处理流程
            if (checkInterceptors()) return

            // 3. Intent构造
            val intent = buildBaseIntent(context, target)

            // 4. 执行跳转
            executeNavigation(context, launcher, options, intent)
        }

        /**
         * 拦截器检查流程，判断导航请求是否被拦截。
         * 使用Java标准库的Collections.sort进行排序，性能更优。
         *
         * @return 是否被拦截
         */
        @SuppressLint("ObsoleteSdkInt")
        private fun checkInterceptors(): Boolean {
            // 合并全局和局部拦截器
            val allInterceptors = mergeInterceptors()

            // 使用Java标准库的Collections.sort进行排序（基于TimSort，O(n log n)）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sort(allInterceptors, Comparator.comparingInt { a -> -a.priority })
            } else {
                // Android N以下版本使用传统Comparator实现
                sort(allInterceptors) { a, b ->
                    b.priority.compareTo(a.priority)
                }
            }

            // 执行拦截检查
            for (wrapper in allInterceptors) {
                val aClass = factory.routeTable[path]!!
                if (wrapper.interceptor.intercept(aClass, extras)) {
                    return true
                }
            }
            return false
        }

        // 合并全局和局部拦截器，全局拦截器在前
        private fun mergeInterceptors(): List<PrioritizedInterceptor> {
            val result: MutableList<PrioritizedInterceptor> = ArrayList()

            // 转换全局拦截器为带优先级包装
            for (interceptor in factory.globalInterceptors) {
                result.add(interceptor)
            }
            result.addAll(localInterceptors)
            return result
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
        private fun buildBaseIntent(context: Context, target: Class<*>): Intent {
            val intent = Intent(context, target)
                .putExtras(extras)
                .addFlags(flags)

            // 设置可选属性
            if (action != null) intent.action = action
            if (data != null || type != null) intent.setDataAndType(data, type)
            if (categories != null) {
                for (category in categories!!) {
                    intent.addCategory(category)
                }
            }

            // 应用自定义修改
            if (intentCustomizer != null) {
                intentCustomizer?.consume(intent)
            }

            // 自动处理NEW_TASK标志
            if (context !is Activity && autoNewTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return intent
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
        private fun executeNavigation(
            context: Context,
            launcher: ActivityResultLauncher<Intent>?,
            options: ActivityOptionsCompat?,
            intent: Intent,
        ) {
            try {
                val finalOptions = resolveOptions(context, options)
                if (launcher != null) {
                    launchWithLauncher(launcher, intent, finalOptions)
                } else {
                    launchDirectly(context, intent, finalOptions)
                }
            } catch (e: Exception) {
                handleError(context, e)
            }
        }

        /**
         * 解析过渡动画选项。
         *
         * @param context 上下文对象
         * @param options 启动选项
         * @return 解析后的启动选项
         */
        private fun resolveOptions(
            context: Context,
            options: ActivityOptionsCompat?,
        ): ActivityOptionsCompat? {
            if (options != null) return options
            return if (enterAnim != 0 || exitAnim != 0) {
                ActivityOptionsCompat.makeCustomAnimation(
                    context, enterAnim, exitAnim)
            } else null
        }

        /**
         * 通过ActivityResultLauncher启动Activity，适用于需要结果回调的情况。
         *
         * @param launcher Activity结果启动器
         * @param intent   要启动的Intent
         * @param options  启动选项
         */
        private fun launchWithLauncher(
            launcher: ActivityResultLauncher<Intent>,
            intent: Intent,
            options: ActivityOptionsCompat?,
        ) {
            if (options != null) {
                launcher.launch(intent, options)
            } else {
                launcher.launch(intent)
            }
        }

        /**
         * 直接启动Activity。
         *
         * @param context 上下文对象
         * @param intent  要启动的Intent
         * @param options 启动选项
         */
        private fun launchDirectly(
            context: Context, intent: Intent,
            options: ActivityOptionsCompat?,
        ) {
            if (options != null && context is Activity) {
                context.startActivity(intent, options.toBundle())
            } else {
                context.startActivity(intent)
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
        private fun handleError(context: Context, e: Exception) {
            errorListener?.onError(e) ?: showDefaultError(context, e)
        }

        /**
         * 显示默认错误提示。
         *
         * @param context 上下文对象
         * @param e       抛出的异常
         */
        private fun showDefaultError(context: Context, e: Exception) {
            Toast.makeText(context, "Navigation failed: " + e.message, Toast.LENGTH_SHORT).show()
        }

        // endregion
    }

    companion object {
        /**
         * 获取默认 [PageFactory] 单例实现（内置路由表与全局拦截器管理）。
         * 一般业务直接调用此方法即可，无需自行继承 [PageFactory]。
         */
        @JvmStatic
        fun getInstance(): PageFactory<*> = DefaultPageFactory
    }
}
