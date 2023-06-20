import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.time.Instant
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    id("mrepo.android.application")
    id("mrepo.android.application.compose")
    id("mrepo.android.hilt")
    id("mrepo.android.room")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

val baseVersionName = "1.5.0-alpha02"
val isDevVersion: Boolean get() = exec("git tag -l v${baseVersionName}").isEmpty()
val verNameSuffix: String get() = if (isDevVersion) ".dev" else ""

android {
    namespace = "com.sanmer.mrepo"

    defaultConfig {
        applicationId = namespace
        versionName = "${baseVersionName}${verNameSuffix}.${commitId}"
        versionCode = commitCount

        resourceConfigurations += getLocales(file("src/main/res/xml/locales_config.xml"))
        multiDexEnabled = true
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
        debug {
            versionNameSuffix = ".debug"
        }

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
            buildConfigField("String", "BUILD_TIME", "\"${Instant.now()}\"")
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
            this as ApkVariantOutputImpl
            outputFileName = "mrepo-${versionName}-${versionCode}-${name}.apk"
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.ktx)
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

    implementation(libs.markwon.core)
    implementation(libs.timber)
}

fun getLocales(file: File): Array<String> {
    val builderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder = builderFactory.newDocumentBuilder()
    val xmlDocument = docBuilder.parse(file)

    val localesNode = xmlDocument.getElementsByTagName("locale")

    val locales = mutableListOf<String>()
    for (i in 0 until localesNode.length) {
        val localeNode = localesNode.item(i)
        if (localeNode.nodeType == Node.ELEMENT_NODE) {
            val localeElement = localeNode as Element
            val value = localeElement.getAttribute("android:name")
            val name = when  {
                "-" in value && value.substring(3).all(Char::isUpperCase) ->
                    value.substring(0, 2) + "-r" + value.substring(3)
                else -> value
            }

            locales.add(name)
        }
    }

    return locales.toTypedArray()
}