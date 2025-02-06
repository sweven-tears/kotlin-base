plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

apply(from = "../config.gradle")

val config = rootProject.extra

android {
    namespace = "com.app.test"

    buildFeatures {
        dataBinding = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(libs.versions.storeFile.get())
            storePassword = libs.versions.storePassword.get()
            keyAlias = libs.versions.keyAlias.get()
            keyPassword = libs.versions.keyPassword.get()
        }
    }

    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()

    defaultConfig {
        applicationId = "com.app.test"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            // signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.sourceCompatibility.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.targetCompatibility.get().toInt())
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs(listOf("libs"))
        }
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "base_v${versionName}_${buildType.name}.apk"
        }
    }
}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.kotlin)
    implementation(libs.core.ktx)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso)

    implementation(libs.permission)
    implementation(libs.update.plugin)
    implementation(libs.photo.choose)

    implementation(libs.smart.refresh.kernel)
    implementation(libs.smart.refresh.classics)
    implementation(libs.smart.refresh.classics.footer)

    kapt(libs.arouter.compiler)

    implementation(project(":common"))
}