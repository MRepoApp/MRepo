plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.ksp.gradle)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "mrepo.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "mrepo.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidHilt") {
            id = "mrepo.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("androidRoom") {
            id = "mrepo.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}