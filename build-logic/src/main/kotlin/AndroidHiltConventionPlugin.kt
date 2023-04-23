
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<KaptExtension> {
                correctErrorTypes = true
            }

            dependencies {
                "implementation"(libs.findLibrary("hilt.android").get())
                "kapt"(libs.findLibrary("hilt.compiler").get())
                "implementation"(libs.findLibrary("hilt.ext.work").get())
                "kapt"(libs.findLibrary("hilt.ext.compiler").get())
            }
        }
    }
}