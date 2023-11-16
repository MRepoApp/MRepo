package com.sanmer.mrepo.datastore

import androidx.datastore.core.DataStore
import com.sanmer.mrepo.datastore.modules.ModulesMenuExt
import com.sanmer.mrepo.datastore.modules.toProto
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.datastore.repository.toProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val data get() = userPreferences.data.map { it.toExt() }

    suspend fun setWorkingMode(value: WorkingMode) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                workingMode = value
            }
        }
    }

    suspend fun setDarkTheme(value: DarkMode) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                darkMode = value
            }
        }
    }

    suspend fun setThemeColor(value: Int) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                themeColor = value
            }
        }
    }

    suspend fun setDeleteZipFile(value: Boolean) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                deleteZipFile = value
            }
        }
    }

    suspend fun setUseDoh(value: Boolean) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                useDoh = value
            }
        }
    }

    suspend fun setDownloadPath(value: String) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                downloadPath = value
            }
        }
    }

    suspend fun setRepositoryMenu(value: RepositoryMenuExt) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                repositoryMenu = value.toProto()
            }
        }
    }

    suspend fun setModulesMenu(value: ModulesMenuExt) = withContext(Dispatchers.IO) {
        userPreferences.updateData {
            it.new {
                modulesMenu = value.toProto()
            }
        }
    }
}