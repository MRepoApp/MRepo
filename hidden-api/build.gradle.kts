plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "dev.sanmer.mrepo.hidden_api"
}

dependencies {
    annotationProcessor(libs.rikka.refine.compiler)
    compileOnly(libs.rikka.refine.annotation)
    compileOnly(libs.androidx.annotation)
}