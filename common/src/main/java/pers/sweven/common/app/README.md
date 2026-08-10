# 全局页面管理工具

## 前言
研究过一段时间的阿里的 ARouter，不仅能管理 activity，还能管理 service，对于小项目来说有点复杂，于是我仿照写了一个简单的 PageManager / PageFactory 来管理页面的跳转功能。

> 本模块已作为 SDK（`com.github.sweven-tears:kotlin-base`）发布，直接依赖即可，**无需**再把源码文件复制到自己的工程。

# PageFactory - Android 页面导航框架

## 简介
`PageFactory` 是一个功能完备的 Android 页面导航框架，采用单例模式实现，支持路由表管理、参数传递、拦截器机制、动画过渡和错误处理等功能。

- `PageFactory` 是抽象类，SDK 已内置默认实现 `DefaultPageFactory`，通过 `PageFactory.getInstance()` 直接获取，一般业务无需自行继承。
- 如需扩展（自定义拦截逻辑、换肤等），可继承 `PageFactory<NavigationBuilder<*>>` 并实现 `createNavigationBuilder(path)`。

## 核心特性
- **建造者模式**：使用建造者模式构建导航请求，支持链式调用。
- **拦截器机制**：提供全局和局部拦截器机制，支持优先级排序。
- **丰富的参数传递**：支持 `int`, `long`, `float`, `double`, `boolean`, `String`, `Parcelable`, `Serializable` 等。
- **自定义配置**：支持自定义 `Intent` 配置和过渡动画。
- **完善的错误处理**：提供错误处理机制和回调接口。

## 安装与初始化

在 `settings.gradle` / `build.gradle` 中引入 JitPack 仓库后，添加依赖：

```groovy
dependencies {
    implementation 'com.github.sweven-tears:kotlin-base:1.3.3-sdk30'
}
```

> ⚠️ **必须初始化 `GlobalApp`**：本 SDK 的 `ToastUtils`、`RetrofitClient` 以及 `PageInit` 生命周期管理都依赖 `GlobalApp`。
> 请在你的 `Application.onCreate()` 中二选一：
> 1. 继承 `pers.sweven.common.app.BaseApplication`；
> 2. 或手动调用 `GlobalApp.setInstance(this).registerActivity()`，并在需要时 `initCacheManager()`。
> 否则运行时会因 `GlobalApp.getInstance()` 未初始化而抛出 IllegalStateException。

## 权限说明
本库已在 `AndroidManifest` 中声明 `ACCESS_FINE_LOCATION` 与 `ACCESS_COARSE_LOCATION`，
会随 AAR 自动合并进你的 App。但定位属于危险权限，仍需在运行时自行调用
`Activity.requestPermissions(...)` 向用户申请，否则取不到位置。

## 使用方法

### 注册路由
```java
PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);
```

### 构建导航请求
```java
PageFactory.getInstance().build("/user/profile")
    .withString("userId", "12345")
    .withBoolean("isVip", true)
    .addInterceptor(new LoginInterceptor())
    .withTransitionAnim(R.anim.slide_in_right, R.anim.slide_out_left)
    .navigation(context);
```

### 添加全局拦截器
```java
PageFactory.getInstance().addGlobalInterceptor(new GlobalInterceptor());
// 或带优先级
PageFactory.getInstance().addGlobalInterceptor(new LoginInterceptor(), 10);
```

### 处理导航错误
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
- `getInstance()`: 获取默认单例实现。
- `build(String path)`: 创建导航构建器。
- `addRoute(String path, Class<?> clazz)`: 向路由表中添加路由信息。
- `addGlobalInterceptor(RouteInterceptor interceptor)` / `addGlobalInterceptor(RouteInterceptor interceptor, int priority)`: 添加全局拦截器。

### NavigationBuilder 类
- **参数添加方法**：
   - `withUrl(String url)`: 从 URL 格式的字符串中解析基础类型参数。
   - `withString(String key, String value)`
   - `withInt(String key, int value)`
   - `withBoolean(String key, boolean value)`
   - `withLong(String key, long value)`
   - `withFloat(String key, float value)`
   - `withDouble(String key, double value)`
   - `withParcelable(String key, Parcelable value)`
   - `withSerializable(String key, Serializable value)`
   - `withStringArrayList(String key, ArrayList<String> list)`
   - `withBundle(Bundle bundle)`
- **Intent 属性增强方法**：
   - `withAction(String action)`
   - `withData(Uri data)`
   - `withType(String type)`
   - `addCategory(String category)`
- **启动标志控制方法**：
   - `setFlags(int flags)`
   - `addFlags(int flags)`
   - `disableAutoNewTask()`: 禁用自动添加 `NEW_TASK` 标志。
   - `withTransitionAnim(int enterAnim, int exitAnim)`
   - `setOptions(ActivityOptionsCompat options)`
- **拦截器管理方法**：
   - `addInterceptor(RouteInterceptor interceptor)`
   - `addInterceptor(RouteInterceptor interceptor, int priority)`
- **其他设置方法**：
   - `customizeIntent(Consumer<Intent> customizer)`
   - `setOnNavigationErrorListener(OnNavigationErrorListener listener)`
- **跳转执行方法**：
   - `navigation(Context context)`: 主入口。
   - `navigation(Context context, ActivityResultLauncher<Intent> launcher)`: 支持新的 Activity Result API。
   - `navigation(Activity activity, int requestCode)`: 兼容旧版 API（已废弃）。

## 示例代码
```java
PageFactory.getInstance().addRoute("/user/profile", UserProfileActivity.class);

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
- 确保在使用 `navigation` 方法之前，已经注册了相应的路由信息（调用 `addRoute`）。
- 处理导航错误时，建议在 `OnNavigationErrorListener` 中进行相应的提示或处理。
- 对于旧版本 API，建议使用新的 `Activity Result API` 替代 `startActivityForResult` 方法。

## 贡献
如果你发现任何问题或有改进建议，欢迎提交 `issue` 或 `pull request`。

---

# PageManager 旧版页面导航框架-更轻便简单

<details>
<summary>关于 PageManager 的说明（点击展开）</summary>

## 认识
ARouter 固然复杂，不过很全面完整，只想偷懒做个简单的也就那么简陋了，需要一些新的功能就必须继续更新，否则就用不上。

常量控制：
1. `keepOnly`：保证启动的 activity 页面唯一，重复启动后关闭其他同样的 activity（不借用启动模式）。
2. `options`：已废弃，改用 `optionsCompat`。
3. `forbiddenRepeat`：禁止重复启动（“只要存在当前页面，且已显示则无法再次启动”），相当于判断快速点击导致的页面重复打开。

## 公开 API（直接使用，无需继承）
`PageManager` 本身是可直接实例化的类，最简用法如下：

```java
new PageManager()
    .build(TargetActivity.class)
    .withString("name", "test")
    .withInt("age", 18)
    .navigation(context);
```

或通过 `getExtras()` 复用参数容器：

```java
new PageManager()
    .getExtras()
    .withString("name", "test")
    .withInt("age", 18)
    .navigation(context);
```

> ⚠️ **注意 `Navigation` 的构造**：`new Navigation()`（无参）会创建一个**全新的** `PageManager` 实例，
> 因此如果你在外部 `PageManager` 上设置了 `clazz` / `page`，再调用无参 `Navigation()` 会丢失这些设置。
> 正确做法是用 `new Navigation(this)` 包裹当前实例（详见下方子类示例）。

## 配置（按名称路由，需自行建表）
下面给出一个**可正常工作的**子类示例：把 `Router` 注解标记的 Activity 收集进 map，再通过名称跳转。

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Router {
    String value();
}
```

```kotlin
import pers.sweven.common.app.PageManager

class AppPageManager private constructor() : PageManager() {

    companion object {
        private val map = hashMapOf<String, Class<*>>()

        @JvmStatic
        fun init(context: Context) {
            val pm = context.packageManager
            val info = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            for (activity in info.activities) {
                val aClass = Class.forName(activity.name)
                aClass.getAnnotation(Router::class.java)?.let {
                    map[it.value] = aClass
                }
            }
        }

        @JvmStatic
        fun get(): AppPageManager = AppPageManager()
    }

    fun build(page: String): Navigation {
        this.page = page
        clazz = map[page]      // 设置到当前的 PageManager 实例上
        return Navigation(this) // 必须传 this，不能调用无参 Navigation()
    }
}
```

## 使用
```kotlin
AppPageManager.get()
    .build(Router.MAIN)
    .withString("name", "test")
    .withInt("age", 18)
    .navigation(context)
```

## 结语
用法上简单，没有复杂的实现方法，适用于轻量使用，更复杂的，请查阅 ARouter。关于使用上的一些异常，发现了会及时修复。

</details>
