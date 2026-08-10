# ============================================================
# ProGuard / R8 规则（会随 AAR 合并到三方 App 的 release 构建）
# 作用：防止 R8 在 consumer 的 minify/release 构建中误删或混淆本 SDK
#       的公开 API，以及通过反射 / 注解 / DataBinding 使用的类。
# ============================================================

# 1) 保留本 SDK 的所有 public API（类名与公开成员不被删除 / 重命名）
-keep public class pers.sweven.common.** {
    public *;
}

# 2) Glide @GlideModule 通过注解处理器生成的元数据 / 反射被发现，必须保留
-keep class pers.sweven.common.glide.** { *; }

# 3) DataBinding 生成的 Binding 类
-keep class pers.sweven.common.databinding.** { *; }
-keep class androidx.databinding.** { *; }
-keep class * extends androidx.databinding.ViewDataBinding { *; }

# 4) 序列化相关：跨 Bundle / 进程传递的模型需保留
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 5) 抑制已知三方依赖在 consumer release 构建中的告警（避免被当作 error 中断构建）
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn com.squareup.okhttp.**
-dontwarn io.reactivex.**
-dontwarn com.trello.rxlifecycle2.**
-dontwarn com.bumptech.glide.**
-dontwarn com.gyf.immersionbar.**
