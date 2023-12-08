import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 34
                buildToolsVersion = "34.0.0"

                defaultConfig {
                    minSdk = 26
                    targetSdk = compileSdk
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            extensions.configure<JavaPluginExtension> {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(17))
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)

                sourceSets.all {
                    languageSettings {
                        optIn("androidx.compose.material.ExperimentalMaterialApi")
                        optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                        optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                        optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
                        optIn("com.google.accompanist.permissions.ExperimentalPermissionsApi")
                        optIn("kotlin.ExperimentalStdlibApi")
                        optIn("kotlinx.coroutines.FlowPreview")
                    }
                }
            }
        }
    }
}