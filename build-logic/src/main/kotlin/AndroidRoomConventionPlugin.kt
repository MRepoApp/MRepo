
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                arg("room.incremental", "true")
                arg("room.expandProjection", "true")
                arg("room.schemaLocation", "$projectDir/schemas")
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.room.ktx").get())
                "implementation"(libs.findLibrary("androidx.room.runtime").get())
                "ksp"(libs.findLibrary("androidx.room.compiler").get())
            }
        }
    }
}