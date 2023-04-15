buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    @Suppress("GradleDependency", "GradleDynamicVersion")
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")

        val kotlinVersion = "1.8.10"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:${kotlinVersion}-1.0.9")
    }
}

plugins {
    id("com.google.dagger.hilt.android") version "2.45" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}