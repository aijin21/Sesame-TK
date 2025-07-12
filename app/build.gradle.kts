import java.text.SimpleDateFormat
import java.util.*

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

        // 仅保留arm64-v8a架构
        ndk {
            abiFilters.add("arm64-v8a")
        }

        // 版本配置（保留原有逻辑）
        val major = 0
        val minor = 2
        val patch = 6
        val buildTag = "alpha"

        val buildDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }.format(Date())

        val buildTime = SimpleDateFormat("HH:mm:ss", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }.format(Date())

        val buildTargetCode = try {
            buildDate.replace("-", ".") + "." + buildTime.replace(":", ".")
        } catch (_: Exception) {
            "0000"
        }

        val gitCommitCount = try {
            val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "HEAD"))
            val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
            process.waitFor()
            if (process.exitValue() == 0) {
                output.toInt()
            } else {
                val error = process.errorStream.bufferedReader().use { it.readText() }
                println("Git error: $error")
                "1".toInt()
            }
        } catch (_: Exception) {
            "1".toInt()
        }

        versionCode = gitCommitCount
        versionName = if (buildTag.contains("alpha") || buildTag.contains("beta")) {
            "v$major.$minor.$patch-$buildTag.$buildTargetCode"
        } else {
            "v$major.$minor.$patch-$buildTag"
        }

        buildConfigField("String", "BUILD_DATE", "\"$buildDate\"")
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        buildConfigField("String", "BUILD_NUMBER", "\"$buildTargetCode\"")
        buildConfigField("String", "BUILD_TAG", "\"$buildTag\"")
        buildConfigField("String", "VERSION", "\"v$major.$minor.$patch\"")

        testOptions {
            unitTests.all {
                it.enabled = false
            }
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        // 启用核心库脱糖（已添加对应依赖）
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
        getByName("debug") {
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            versionNameSuffix = "-debug"
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
    val cmakeFile = file("src/main/cpp/CMakeLists.txt")
    if (!System.getenv("CI").toBoolean() && cmakeFile.exists()) {
        externalNativeBuild {
            cmake {
                path = cmakeFile
                version = "3.31.6"
                ndkVersion = "29.0.13113456"
            }
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            // 单一版本APK文件名
            val fileName = "Sesame-TK-${variant.versionName}.apk"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = fileName
        }
    }
}

dependencies {
    // 核心库脱糖依赖（解决本次报错的关键）
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // 原有依赖
    implementation(libs.ui.tooling.preview.android)
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    testImplementation(composeBom)
    androidTestImplementation(composeBom)
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
}
import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id 'org.jetbrains.kotlin.kapt' // 启用Kotlin注解处理（Lombok需要）
}

android {
    namespace = "fansirsqi.xposed.sesame"
    compileSdk = 36

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "fansirsqi.xposed.sesame"
        minSdk = 21
        targetSdk = 36

        // 仅保留arm64-v8a架构
        ndk {
            abiFilters.add("arm64-v8a")
        }

        // 版本配置（保留原有逻辑）
        val major = 0
        val minor = 2
        val patch = 6
        val buildTag = "alpha"

        val buildDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }.format(Date())

        val buildTime = SimpleDateFormat("HH:mm:ss", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }.format(Date())

        val buildTargetCode = try {
            buildDate.replace("-", ".") + "." + buildTime.replace(":", ".")
        } catch (_: Exception) {
            "0000"
        }

        val gitCommitCount = try {
            val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "HEAD"))
            val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
            process.waitFor()
            if (process.exitValue() == 0) {
                output.toInt()
            } else {
                val error = process.errorStream.bufferedReader().use { it.readText() }
                println("Git error: $error")
                "1".toInt()
            }
        } catch (_: Exception) {
            "1".toInt()
        }

        versionCode = gitCommitCount
        versionName = if (buildTag.contains("alpha") || buildTag.contains("beta")) {
            "v$major.$minor.$patch-$buildTag.$buildTargetCode"
        } else {
            "v$major.$minor.$patch-$buildTag"
        }

        buildConfigField("String", "BUILD_DATE", "\"$buildDate\"")
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        buildConfigField("String", "BUILD_NUMBER", "\"$buildTargetCode\"")
        buildConfigField("String", "BUILD_TAG", "\"$buildTag\"")
        buildConfigField("String", "VERSION", "\"v$major.$minor.$patch\"")

        testOptions {
            unitTests.all {
                it.enabled = false
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
        getByName("debug") {
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            versionNameSuffix = "-debug"
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
    val cmakeFile = file("src/main/cpp/CMakeLists.txt")
    if (!System.getenv("CI").toBoolean() && cmakeFile.exists()) {
        externalNativeBuild {
            cmake {
                path = cmakeFile
                version = "3.31.6"
                ndkVersion = "29.0.13113456"
            }
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val fileName = "Sesame-TK-${variant.versionName}.apk"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = fileName
        }
    }
}

dependencies {
    // 核心库脱糖依赖
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Xposed框架依赖（解决XC_MethodReplacement、XposedBridge等类）
    implementation("de.robv.android.xposed:api:82")
    implementation("de.robv.android.xposed:api:82:sources")
    implementation("de.robv.android.xposed:xlibrary:82")

    // Jackson JSON库（解决fasterxml相关类）
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    // Lombok（解决@Getter等注解）
    implementation("org.projectlombok:lombok:1.18.30")
    kapt("org.projectlombok:lombok:1.18.30")

    // OkHttp网络库（解决okhttp3相关类）
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 原有Compose及AndroidX依赖
    implementation(libs.ui.tooling.preview.android)
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    testImplementation(composeBom)
    androidTestImplementation(composeBom)
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
}
