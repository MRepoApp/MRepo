plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "dev.sanmer.mrepo.hidden_api"
}

dependencies {
    compileOnly(libs.androidx.annotation)
}