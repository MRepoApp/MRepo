package dev.sanmer.mrepo.datastore.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.sanmer.mrepo.app.Const
import dev.sanmer.mrepo.compat.BuildCompat
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isNonRoot
import dev.sanmer.mrepo.ui.theme.Colors
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoNumber
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class UserPreferences(
    val workingMode: WorkingMode = WorkingMode.Setup,
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val themeColor: Int = if (BuildCompat.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
    val deleteZipFile: Boolean = false,
    val downloadPath: String = Const.PUBLIC_DOWNLOADS.path,
    val homepage: Homepage = Homepage.Repository,
    @ProtoNumber(20)
    val repositoryMenu: RepositoryMenu = RepositoryMenu(),
    @ProtoNumber(30)
    val modulesMenu: ModulesMenu = ModulesMenu()
) {
    val currentHomepage by lazy {
        when {
            workingMode.isNonRoot -> Homepage.Repository
            else -> homepage
        }
    }

    @Composable
    fun isDarkMode() = when (darkMode) {
        DarkMode.AlwaysOff -> false
        DarkMode.AlwaysOn -> true
        DarkMode.FollowSystem -> isSystemInDarkTheme()
    }

    fun encodeTo(output: OutputStream) = output.write(
        ProtoBuf.encodeToByteArray(this)
    )

    companion object {
        fun decodeFrom(input: InputStream): UserPreferences =
            ProtoBuf.decodeFromByteArray(input.readBytes())
    }
}
