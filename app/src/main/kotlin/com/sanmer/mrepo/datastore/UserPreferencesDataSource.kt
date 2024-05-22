package com.sanmer.mrepo.datastore

import androidx.datastore.core.DataStore
import com.sanmer.mrepo.datastore.modules.ModulesMenuCompat
import com.sanmer.mrepo.datastore.repository.RepositoryMenuCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferencesCompat>
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

    suspend fun setUseDoh(value: Boolean) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                useDoh = value
            )
        }
    }

    suspend fun setDownloadPath(value: File) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                downloadPath = value
            )
        }
    }

    suspend fun setRepositoryMenu(value: RepositoryMenuCompat) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                repositoryMenu = value
            )
        }
    }

    suspend fun setModulesMenu(value: ModulesMenuCompat) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.copy(
                modulesMenu = value
            )
        }
    }
}