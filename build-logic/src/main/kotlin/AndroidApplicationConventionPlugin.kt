
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 33
                buildToolsVersion = "33.0.2"
                ndkVersion = "25.2.9519653"

                defaultConfig {
                    minSdk = 26
                    targetSdk = 33
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
                        optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                        optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                        optIn("androidx.compose.animation.ExperimentalAnimationApi")
                        optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                        optIn("com.google.accompanist.permissions.ExperimentalPermissionsApi")
                        optIn("kotlin.ExperimentalStdlibApi")
                        optIn("kotlinx.coroutines.FlowPreview")
                    }
                }
            }
        }
    }
}