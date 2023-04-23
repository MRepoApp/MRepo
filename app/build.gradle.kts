import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("mrepo.android.application")
    id("mrepo.android.application.compose")
    id("mrepo.android.hilt")
    id("mrepo.android.room")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}

val verName = "1.2.4"
val verCode = 124

android {
    namespace = "com.sanmer.mrepo"

    signingConfigs {
        create("release") {
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    defaultConfig {
        applicationId = namespace
        versionCode = verCode
        versionName = verName
        resourceConfigurations += arrayOf("en", "zh-rCN", "zh-rTW", "fr", "ro", "es", "ar")
        multiDexEnabled = true

        ndk {
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = ".dev"
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/**",
                "okhttp3/**",
                "kotlin/**",
                "org/**",
                "**.properties",
                "**.bin",
                "**/*.proto"
            )
        }
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            if (filters.isNotEmpty()) {
                val abi = filters.toList().first().identifier
                (this as ApkVariantOutputImpl).outputFileName =
                    "MRepo-${verName}-${verCode}-${abi}.apk"
            }
        }
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewModel.compose)
    implementation(libs.lifecycle.service)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.moshi)

    implementation(libs.square.moshi)
    ksp(libs.square.moshi.kotlin)

    implementation(libs.work.ktx)
    implementation(libs.google.material)
    implementation(libs.markwon.core)
    implementation(libs.timber)
}
