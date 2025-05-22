# 全局页面管理工具

## 前言
研究过一段时间的阿里的ARouter，不仅能管理activity，还能管理service，对于小项目来说有点复杂，于是我仿照写了一个简单的PageManager来管理页面的跳转功能

# PageFactory - Android 页面导航框架

## 简介
`PageFactory` 是一个功能完备的 Android 页面导航框架，提供了灵活且高效的页面跳转解决方案。该框架采用单例模式实现，支持路由表管理、参数传递、拦截器机制、动画过渡和错误处理等功能。

## 核心特性
- **建造者模式**：使用建造者模式构建导航请求，支持链式调用，使代码更加简洁易读。
- **拦截器机制**：提供全局和局部拦截器机制，支持优先级排序，方便对导航请求进行拦截和处理。
- **丰富的参数传递**：支持基本类型和复杂对象的参数传递，包括 `int`, `long`, `float`, `double`, `boolean`, `String`, `Parcelable`, `Serializable` 等。
- **自定义配置**：支持自定义 `Intent` 配置和过渡动画，满足不同场景的需求。
- **完善的错误处理**：提供错误处理机制和回调接口，方便处理导航过程中出现的错误。

## 安装
将 `PageFactory.java` 文件复制到你的 Android 项目中，确保包名和文件路径正确。

## 使用方法

### 注册路由
在应用启动时，注册路由信息，将路由路径与对应的 `Activity` 类进行映射。
```java
PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);
```

### 构建导航请求
使用 `build` 方法创建导航构建器，并通过链式调用添加参数、拦截器、动画等信息。
```java
PageFactory.getInstance().build("/user/profile")
    .withString("userId", "12345")
    .withBoolean("isVip", true)
    .addInterceptor(new LoginInterceptor())
    .withTransitionAnim(R.anim.slide_in_right, R.anim.slide_out_left)
    .navigation(context);
```

### 添加全局拦截器
可以添加全局拦截器，对所有导航请求进行拦截和处理。
```java
PageFactory.getInstance().addGlobalInterceptor(new GlobalInterceptor());
```

### 处理导航错误
可以设置导航错误监听器，处理导航过程中出现的错误。
```java
PageFactory.getInstance().build("/user/profile")
    .setOnNavigationErrorListener(new PageFactory.OnNavigationErrorListener() {
        @Override
        public void onError(Exception e) {
            Toast.makeText(context, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    })
    .navigation(context);
```

## 详细 API 说明

### PageFactory 类
- `build(String path)`: 创建导航构建器，作为建造者模式的入口。
- `addRoute(String path, Class<?> clazz)`: 向路由表中添加路由信息。
- `addGlobalInterceptor(RouteInterceptor interceptor)`: 添加全局拦截器。

### NavigationBuilder 类
- **参数添加方法**：
   - `withUrl(String url)`: 从 URL 格式的字符串中解析基础类型参数和数组。
   - `withString(String key, String value)`: 添加字符串类型的参数。
   - `withInt(String key, int value)`: 添加整数类型的参数。
   - `withBoolean(String key, boolean value)`: 添加布尔类型的参数。
   - `withLong(String key, long value)`: 添加长整数类型的参数。
   - `withFloat(String key, float value)`: 添加浮点数类型的参数。
   - `withDouble(String key, double value)`: 添加双精度浮点数类型的参数。
   - `withParcelable(String key, Parcelable value)`: 添加 `Parcelable` 类型的参数。
   - `withSerializable(String key, Serializable value)`: 添加 `Serializable` 类型的参数。
   - `withStringArrayList(String key, ArrayList<String> list)`: 添加字符串列表类型的参数。
   - `withBundle(Bundle bundle)`: 添加 `Bundle` 类型的参数。
- **Intent 属性增强方法**：
   - `withAction(String action)`: 设置 `Intent` 的 `Action`。
   - `withData(Uri data)`: 设置 `Intent` 的 `Data`。
   - `withType(String type)`: 设置 `Intent` 的 MIME 类型。
   - `addCategory(String category)`: 添加 `Intent` 的类别。
- **启动标志控制方法**：
   - `setFlags(int flags)`: 设置 `Intent` 的启动标志位。
   - `disableAutoNewTask()`: 禁用自动添加 `NEW_TASK` 标志，适用于非 `Activity` 上下文。
   - `withTransitionAnim(int enterAnim, int exitAnim)`: 设置过渡动画。
   - `setOptions(ActivityOptionsCompat options)`: 设置启动选项。
- **拦截器管理方法**：
   - `addInterceptor(RouteInterceptor interceptor)`: 添加局部拦截器，使用默认优先级。
   - `addInterceptor(RouteInterceptor interceptor, int priority)`: 添加局部拦截器，指定优先级。
- **其他设置方法**：
   - `customizeIntent(Consumer<Intent> customizer)`: 设置 `Intent` 自定义钩子，用于对 `Intent` 进行自定义操作。
   - `setOnNavigationErrorListener(OnNavigationErrorListener listener)`: 设置导航错误监听器，用于处理导航过程中出现的错误。
- **跳转执行方法**：
   - `navigation(Context context)`: 执行导航操作，为主入口方法。
   - `navigation(Activity activity, int requestCode)`: 执行导航操作，兼容旧版本 API，已被弃用。
   - `navigation(Context context, ActivityResultLauncher<Intent> launcher)`: 执行导航操作，支持新的 `Activity Result API`。

## 示例代码
```java
// 注册路由
PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);

// 构建导航请求
PageFactory.getInstance().build("/user/profile")
    .withString("userId", "12345")
    .withBoolean("isVip", true)
    .addInterceptor(new LoginInterceptor())
    .withTransitionAnim(R.anim.slide_in_right, R.anim.slide_out_left)
    .setOnNavigationErrorListener(new PageFactory.OnNavigationErrorListener() {
        @Override
        public void onError(Exception e) {
            Toast.makeText(context, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    })
    .navigation(context);
```

## 注意事项
- 确保在使用 `navigation` 方法之前，已经注册了相应的路由信息。
- 处理导航错误时，建议在 `OnNavigationErrorListener` 中进行相应的提示或处理。
- 对于旧版本 API，建议使用新的 `Activity Result API` 替代 `startActivityForResult` 方法。

## 贡献
如果你发现任何问题或有改进建议，欢迎提交 `issue` 或 `pull request`。

# PageManager 旧版页面导航框架-更轻便简单

<details>
<summary>关于PageManager的说明（点击展开）</summary>

## 认识
ARouter固然复杂，不过很全面完整，只想偷懒做个简单的也就那么简陋了，需要一些新的功能就必须继续更新，否则就用不上

1. 这是所用到的常量控制，简单的处理一些问题：
   1. keepOnly : 保证启动的activity页面唯一，重复启动后关闭其他同样的activity，这里没有借用启动模式
   2. options : 已废弃，改用optionsCompat
   3. forbiddenRepeat : 禁止重复启动，“只要存在当前页面，且已显示则无法再次启动”，相当于是判断了是否快速点击导致的页面重复打卡，这里的交互应该交给点击事件处理
```java
public class PageManager {
    private final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    public Class<?> clazz;
    public String page;
    public int flags;
    @Deprecated
    public Bundle options;
    public ActivityOptionsCompat optionsCompat;
    private boolean forbiddenRepeat;
    private boolean keepOnly;
}
```

截止2025-1-6
后续更新文档，文档写着写着发现存在太多问题没考虑清楚了....

## 配置
继承PageManager，写一个子类，通过任何方式进行页面映射class和name的关系，例如：

```java
import pers.sweven.common.app.PageManager;

@Retention(RetentionPolicy.RUNTIME)
public @interface Router {
    String value();
    // 配置页面name
    // String LOGIN = "login/activity";
    // String MAIN = "main/activity";
}
```
如下继承实现子类
```kotlin
import pers.sweven.common.app.PageInit
import pers.sweven.common.app.PageManager as PManager

class PageManager private constructor() : PManager() {

    /**
     * @param page [Router]
     */
    fun build(@Router("") page: String?): Navigation {
        this.page = page
        clazz = map[page]
        return Navigation()
    }

    companion object {
        private val map = hashMapOf<String, Class<*>>()

        @JvmStatic
        fun initActivities(context: Context): List<Class<*>> {
            val packageManager: PackageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_ACTIVITIES
            )


            val list = arrayListOf<Class<*>>()
            for (activity in packageInfo.activities) {
                val aClass = Class.forName(activity.name)
                // 这里需要在activity中配置Router
                aClass.getAnnotation(Router::class.java)?.apply {
                    map[value] = aClass
                }
                list.add(aClass)
            }
            return list
        }

        @JvmStatic
        fun getInstance(): PageManager {
            return PageManager()
        }
    }
}
```

## 使用
```kotlin
// 简单使用
PageManager.getInstance()
    .build(Router.MAIN)
    .withString("name","test")
    .withInt("age",18)
    .start(context)
```

## 结语
用法上简单，没有复杂的实现方法，适用于轻量使用，更复杂的，请查阅ARouter，
关于使用上的一些异常，发现了会及时修复

</details>