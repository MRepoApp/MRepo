plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "dev.sanmer.mrepo.core"

    defaultConfig {
        consumerProguardFile("proguard-rules.pro")
    }

    buildFeatures {
        aidl = true
    }
}