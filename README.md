# kotlin-base
一个通用基类

[![](https://jitpack.io/v/sweven-tears/kotlin-base.svg)](https://jitpack.io/#sweven-tears/kotlin-base)

## 本项目建议使用MVVM模式的databinding开发
如果不想使用databinding，最好项目里增加一个BaseActivity继承本插件中得BaseActivity，提供了不适用databinding的适配方法：
1. BaseActivity中的registerLayout
2. 覆写BaseFragment中的onCreateView
3. ~~覆写BaseDialog的init(还未实现，等更新)~~
4. BaseAdapter暂时无法覆写

## 实用功能
### TextViewHelper
这是一款我认为比较好用的具有富文本编辑功能的TextView使用插件，
实用方法
```java
new TextViewHelper(textView)
    .addText("Hello",Color.RED)
    .addText(" ")
    .addText("World!",new TextViewStyle(Color.BLACK,0.75f))
    .build();
```
更多用法请查看[[TextViewStyle](https://github.com/sweven-tears/kotlin-base/blob/main/common/src/main/java/pers/sweven/common/helper/textview/TextViewStyle.java)]

可以快速继承BaseActivity，BaseFragment，BaseDialog...
config.gradle可以给你带来整洁的gradle文件，泛用性强
