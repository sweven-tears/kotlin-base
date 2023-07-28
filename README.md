# kotlin-base

一个通用基类

[![](https://jitpack.io/v/sweven-tears/kotlin-base.svg)](https://jitpack.io/#sweven-tears/kotlin-base)

## 获取kotlin-base（通过Gradle方式）

首先，在项目的 build.gradle（project） 和settings.gradle(取决于你得Gradle版本)文件里面添加:

```groovy
// build.gradle(Project)
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// settings.gradle，编译时提示不通过的时候看一下settings.gradle文件里是否有这个参数，
// 如果有，记得也需要添加一次maven
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven { url 'https://jitpack.io' }
    }
}
```

然后

```groovy
dependencies {
    implementation 'com.github.sweven-base:kotlin-base:1.0.7'
}
```

## 本项目建议使用MVVM模式的databinding开发

如果不想使用databinding，最好项目里增加一个BaseActivity继承本插件中得BaseActivity，提供了不适用databinding的适配方法：

1. BaseActivity中的registerLayout
2. 覆写BaseFragment中的onCreateView
3. ~~覆写BaseDialog的init(还未实现，等更新)~~
4. BaseAdapter暂时无法覆写

## 实用功能

### TextViewHelper

这是一款我认为比较好用的具有富文本编辑功能的TextView使用插件， 实用方法

```java
new TextViewHelper(textView)
        .addText("Hello",Color.RED)
        .addText(" ")
        .addText("World!",new TextViewStyle(Color.BLACK,0.75f))
        .build();
```

更多用法请查看[[TextViewStyle](https://github.com/sweven-tears/kotlin-base/blob/main/common/src/main/java/pers/sweven/common/helper/textview/TextViewStyle.java)]

可以快速继承BaseActivity，BaseFragment，BaseDialog...

## config.gradle

config.gradle可以给你带来整洁的gradle文件，泛用性强
> 部分第三方包说明

1. photo-choose 来源：[![EasyPhotos](https://github.com/HuanTanSheng/EasyPhotos)]
再使用前需要创建一个implement ImageEngine的图像加载类,由于本插件已经集成了Glide，你可以不需要再次集成Glide，因此你需要如下创建一个图像加载类：

```java
public class GlideEngine extends pers.sweven.common.glide.GlideEngine implements ImageEngine {
    //单例
    private static GlideEngine instance = null;

    //单例模式，私有构造方法
    private GlideEngine() {
    }

    //获取单例
    public static GlideEngine getInstance() {
        if (null == instance) {
            synchronized (GlideEngine.class) {
                if (null == instance) {
                    instance = new GlideEngine();
                }
            }
        }
        return instance;
    }
}
```

如果需要更全面的Glide实现方法，建议直接下载项目导入lib即可
