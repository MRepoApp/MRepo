plugins {
    alias(libs.plugins.pro.library)
}

android {
    namespace = "dev.sanmer.mrepo.hidden_api"
}

dependencies {
    compileOnly(libs.androidx.annotation)
}