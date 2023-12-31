import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.pro.application)
    alias(libs.plugins.pro.compose)
    alias(libs.plugins.pro.hilt)
    alias(libs.plugins.pro.room)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

val baseVersionName = "2.4.2"
val isDevVersion get() = exec("git tag --contains HEAD").isEmpty()
val verNameSuffix get() = if (isDevVersion) ".dev" else ""

android {
    namespace = "com.sanmer.mrepo"

    defaultConfig {
        applicationId = namespace
        versionName = "${baseVersionName}${verNameSuffix}.${commitId}"
        versionCode = commitCount

        resourceConfigurations += arrayOf(
            "en",
            "ar",
            "de",
            "es",
            "fr",
            "hi",
            "in",
            "ja",
            "pl",
            "pt",
            "ro",
            "ru",
            "tr",
            "vi",
            "zh-rCN",
            "zh-rTW"
        )
    }

    val releaseSigning = if (project.hasReleaseKeyStore) {
        signingConfigs.create("release") {
            storeFile = project.releaseKeyStore
            storePassword = project.releaseKeyStorePassword
            keyAlias = project.releaseKeyAlias
            keyPassword = project.releaseKeyPassword
            enableV2Signing = true
            enableV3Signing = true
        }
    } else {
        signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        all {
            signingConfig = releaseSigning
            buildConfigField("Boolean", "IS_DEV_VERSION", isDevVersion.toString())
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging.resources.excludes += setOf(
        "META-INF/**",
        "okhttp3/**",
        "kotlin/**",
        "org/**",
        "**.properties",
        "**.bin",
        "**/*.proto"
    )

    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "MRepo-${versionName}-${versionCode}-${name}.apk"
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

// Workaround for https://github.com/google/ksp/issues/1590
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            @Suppress("DEPRECATION")
            val capName = variant.name.capitalize()
            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}

dependencies {
    compileOnly(projects.hiddenApi)
    implementation(projects.compat)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.markwon.core)
    implementation(libs.timber)

    implementation(libs.libsu.core)
    implementation(libs.libsu.service)

    implementation(libs.rikka.shizuku.api)
    implementation(libs.rikka.shizuku.provider)

    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.moshi)
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.dnsoverhttps)
    implementation(libs.square.logging.interceptor)
    implementation(libs.square.moshi)
    ksp(libs.square.moshi.kotlin)
}