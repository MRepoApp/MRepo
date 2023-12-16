plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.ksp.gradle)
}

gradlePlugin {
    plugins {
        register("proApplication") {
            id = "pro.application"
            implementationClass = "ApplicationConventionPlugin"
        }

        register("proLibrary") {
            id = "pro.library"
            implementationClass = "LibraryConventionPlugin"
        }

        register("proCompose") {
            id = "pro.compose"
            implementationClass = "ComposeConventionPlugin"
        }

        register("proHilt") {
            id = "pro.hilt"
            implementationClass = "HiltConventionPlugin"
        }

        register("proRoom") {
            id = "pro.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}