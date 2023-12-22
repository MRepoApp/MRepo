plugins {
    alias(libs.plugins.pro.library)
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