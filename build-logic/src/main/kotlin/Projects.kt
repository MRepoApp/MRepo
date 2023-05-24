import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.File

val Project.commitId: String get() = exec("git rev-parse --short HEAD")
val Project.commitCount: Int get() = exec("git rev-list --count HEAD").toInt()

fun Project.exec(command: String): String = providers.exec {
    commandLine(command.split(" "))
}.standardOutput.asText.get().trim()


val Project.releaseKeyStore: File get() = File(getLocalProperty("keyStore"))
val Project.releaseKeyStorePassword: String get() = getLocalProperty("keyStorePassword")
val Project.releaseKeyAlias: String get() = getLocalProperty("keyAlias")
val Project.releaseKeyPassword: String get() = getLocalProperty("keyPassword")

val Project.hasReleaseKeyStore: Boolean get() =
    gradleLocalProperties(rootDir).hasProperty("keyStore")

fun Project.getLocalProperty(key: String): String =
    gradleLocalProperties(rootDir).getProperty(key)