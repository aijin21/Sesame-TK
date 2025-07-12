plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "fansirsqi.xposed.sesame"
    compileSdk = 36

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "fansirsqi.xposed.sesame"
        minSdk = 21
        targetSdk = 36

        // 版本配置
        val major = 0
        val minor = 2
        val patch = 6
        val buildTag = "alpha"

        versionCode = 1
        versionName = "v$major.$minor.$patch-$buildTag"

        buildConfigField("String", "VERSION", "\"v$major.$minor.$patch\"")
        buildConfigField("String", "BUILD_TAG", "\"$buildTag\"")

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }

        // 修复单元测试配置
        testOptions {
            unitTests {
                isIncludeAndroidResources = false
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        }
    }

    signingConfigs {
        create("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // 修复 APK 文件名设置
    applicationVariants.all {
        outputs.all {
            outputFileName = "Sesame-TK-${versionName}.apk"
        }
    }
}

dependencies {
    // 其他依赖...
}
