# 全局页面管理工具

## 前言
研究过一段时间的阿里的ARouter，不仅能管理activity，还能管理service，对于小项目来说有点复杂，于是我仿照写了一个简单的PageManager来管理页面的跳转功能

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

