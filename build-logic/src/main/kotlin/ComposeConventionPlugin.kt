import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("androidxComposeCompiler").get().toString()
                }

                dependencies {
                    "implementation"(libs.findLibrary("androidx.compose.material3").get())
                    "implementation"(libs.findLibrary("androidx.compose.ui").get())
                    "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                    "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
                }
            }
        }
    }
}