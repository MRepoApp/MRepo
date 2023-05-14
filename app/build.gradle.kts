import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("mrepo.android.application")
    id("mrepo.android.application.compose")
    id("mrepo.android.hilt")
    id("mrepo.android.room")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

val baseVersionName = "1.2.4"
val commitId get() = "git rev-parse --short HEAD".exec()
val commitCount get() = "git rev-list --count HEAD".exec()

android {
    namespace = "com.sanmer.mrepo"

    defaultConfig {
        applicationId = namespace
        versionCode = commitCount.toInt()
        versionName = "${baseVersionName}.${commitId}"

        resourceConfigurations += arrayOf("en", "zh-rCN", "zh-rTW", "fr", "ro", "es", "ar")
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            enableV2Signing = true
            enableV3Signing = true
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
            (this as ApkVariantOutputImpl).outputFileName =
                "mrepo-v${versionName}-${name}.apk"
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.datastore.core)
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
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.moshi)

    implementation(libs.square.moshi)
    ksp(libs.square.moshi.kotlin)

    implementation(libs.work.ktx)
    implementation(libs.google.material)
    implementation(libs.markwon.core)
    implementation(libs.timber)
}

fun Project.exec(command: String): String = providers.exec {
    commandLine(command.split(" "))
}.standardOutput.asText.get().trim()

fun String.exec() = exec(this)
