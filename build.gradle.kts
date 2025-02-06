// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply(from = "config.gradle")

buildscript {
    // 从根项目扩展中获取配置
    extra["config"] = rootProject.extra

    repositories {
        // Google Maven 仓库
        google()
        // Maven 中央仓库
        mavenCentral()
        // Spark 包仓库
        maven(url = "https://repos.spark-packages.org")
        // JitPack 仓库
        maven(url = "https://jitpack.io")
    }

    dependencies {
        // Android Gradle 插件
        classpath(libs.android.gradle.plugin)
        // Kotlin Gradle 插件
        classpath(libs.kotlin.gradle.plugin)
        // Android Maven Gradle 插件
        classpath(libs.android.maven.gradle.plugin)

//        classpath("com.neenbedankt.gradle.plugins:android-apt:1.8")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.txt files
    }
}

// 定义一个名为 clean 的任务，用于删除根项目的构建目录
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}