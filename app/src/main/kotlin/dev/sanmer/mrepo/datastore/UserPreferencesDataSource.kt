package dev.sanmer.mrepo.datastore

import androidx.datastore.core.DataStore
import dev.sanmer.mrepo.datastore.model.DarkMode
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.ModulesMenu
import dev.sanmer.mrepo.datastore.model.RepositoryMenu
import dev.sanmer.mrepo.datastore.model.UserPreferences
import dev.sanmer.mrepo.datastore.model.WorkingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val data get() = userPreferences.data

    suspend fun setWorkingMode(value: WorkingMode) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                workingMode = value
            )
        }
    }

    suspend fun setDarkTheme(value: DarkMode) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                darkMode = value
            )
        }
    }

    suspend fun setThemeColor(value: Int) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                themeColor = value
            )
        }
    }

    suspend fun setDeleteZipFile(value: Boolean) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                deleteZipFile = value
            )
        }
    }

    suspend fun setDownloadPath(value: String) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                downloadPath = value
            )
        }
    }

    suspend fun setHomepage(value: Homepage) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                homepage = value
            )
        }
    }

    suspend fun setRepositoryMenu(value: RepositoryMenu) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                repositoryMenu = value
            )
        }
    }

    suspend fun setModulesMenu(value: ModulesMenu) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                modulesMenu = value
            )
        }
    }
}