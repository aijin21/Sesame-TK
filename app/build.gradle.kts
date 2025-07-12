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

        // 统一版本配置
        val major = 0
        val minor = 2
        val patch = 6
        val buildTag = "alpha"

        versionCode = 1  // 固定 versionCode（根据实际需求调整）
        versionName = "v$major.$minor.$patch-$buildTag"

        buildConfigField("String", "VERSION", "\"v$major.$minor.$patch\"")
        buildConfigField("String", "BUILD_TAG", "\"$buildTag\"")

        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))  // 仅保留主流架构
        }

        testOptions {
            unitTests.enabled = false
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
            signingConfig = signingConfigs.getByName("debug")  // 生产环境需配置正式签名
        }
    }

    applicationVariants.all {
        outputs.all {
            outputFileName = "Sesame-TK-${versionName}.apk"
        }
    }
}

dependencies {
    // 移除风味相关依赖
    implementation(libs.ui.tooling.preview.android)
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("org.nanohttpd:nanohttpd:2.3.1")

    implementation(libs.androidx.constraintlayout)
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.kotlin.stdlib)
    implementation(libs.slf4j.api)
    implementation(libs.logback.android)
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation(libs.material)
    implementation(libs.webkit)
    compileOnly(libs.xposed.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.okhttp)
    implementation(libs.dexkit)

    coreLibraryDesugaring(libs.desugar)
}
