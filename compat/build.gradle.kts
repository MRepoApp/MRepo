plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "dev.sanmer.mrepo.compat"

    buildFeatures {
        aidl = true
    }
}

dependencies {
    compileOnly(projects.hiddenApi)

    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.libsu.core)
    implementation(libs.libsu.service)

    implementation(libs.rikka.shizuku.api)
    implementation(libs.rikka.shizuku.provider)
}