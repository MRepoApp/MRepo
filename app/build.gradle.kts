@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    kotlin("android")
    kotlin("plugin.parcelize")
}

val verName = "1.2.1-beta03"
val verCode = 121

android {
    namespace = "com.sanmer.mrepo"
    compileSdk = 33
    buildToolsVersion = "33.0.2"
    ndkVersion = "25.2.9519653"

    signingConfigs {
        create("release") {
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    defaultConfig {
        applicationId = namespace
        minSdk = 26
        targetSdk = 33
        versionCode = verCode
        versionName = verName
        resourceConfigurations += arrayOf("en", "zh-rCN")
        multiDexEnabled = true

        ndk {
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
        }

        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
            }
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    packagingOptions {
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

    setProperty("archivesBaseName", "mrepo-$verName")
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }
}

kotlin {
    jvmToolchain(11)

    sourceSets.all {
        languageSettings {
            optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            optIn("androidx.compose.ui.ExperimentalComposeUiApi")
            optIn("androidx.compose.animation.ExperimentalAnimationApi")
            optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            optIn("com.google.accompanist.permissions.ExperimentalPermissionsApi")
            optIn("kotlin.ExperimentalStdlibApi")
        }
    }
}

ksp {
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.work:work-runtime-ktx:2.8.0")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-service:2.5.1")
    implementation("com.google.android.material:material:1.9.0-alpha02")

    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.compose.material3:material3:1.1.0-alpha07")

    val vCompose = "1.4.0-beta02"
    implementation("androidx.compose.ui:ui:${vCompose}")
    implementation("androidx.compose.ui:ui-tooling-preview:${vCompose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${vCompose}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${vCompose}")

    val vAccompanist = "0.29.1-alpha"
    implementation("com.google.accompanist:accompanist-systemuicontroller:${vAccompanist}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${vAccompanist}")
    implementation("com.google.accompanist:accompanist-permissions:${vAccompanist}")

    val vRoom = "2.5.0"
    implementation("androidx.room:room-runtime:${vRoom}")
    implementation("androidx.room:room-ktx:${vRoom}")
    ksp("androidx.room:room-compiler:${vRoom}")

    val vLibsu = "5.0.4"
    implementation("com.github.topjohnwu.libsu:core:${vLibsu}")
    implementation("com.github.topjohnwu.libsu:nio:${vLibsu}")
    implementation("com.github.topjohnwu.libsu:service:${vLibsu}")

    val vRetrofit = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:${vRetrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${vRetrofit}")

    val vMoshi = "1.14.0"
    implementation("com.squareup.moshi:moshi:${vMoshi}")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:${vMoshi}")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}
