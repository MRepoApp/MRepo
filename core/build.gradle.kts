plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "dev.sanmer.mrepo.core"

    buildFeatures {
        aidl = true
    }
}

dependencies {
    api(libs.sanmer.su)
}