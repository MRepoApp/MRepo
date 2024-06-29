package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.datastore.UserPreferencesDataSource
import dev.sanmer.mrepo.datastore.model.DarkMode
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.ModulesMenu
import dev.sanmer.mrepo.datastore.model.RepositoryMenu
import dev.sanmer.mrepo.datastore.model.WorkingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) {
    val data get() = userPreferencesDataSource.data

    suspend fun setWorkingMode(value: WorkingMode) = userPreferencesDataSource.setWorkingMode(value)

    suspend fun setDarkTheme(value: DarkMode) = userPreferencesDataSource.setDarkTheme(value)

    suspend fun setThemeColor(value: Int) = userPreferencesDataSource.setThemeColor(value)

    suspend fun setDeleteZipFile(value: Boolean) = userPreferencesDataSource.setDeleteZipFile(value)

    suspend fun setDownloadPath(value: String) = userPreferencesDataSource.setDownloadPath(value)

    suspend fun setHomepage(value: Homepage) = userPreferencesDataSource.setHomepage(value)

    suspend fun setRepositoryMenu(value: RepositoryMenu) = userPreferencesDataSource.setRepositoryMenu(value)

    suspend fun setModulesMenu(value: ModulesMenu) = userPreferencesDataSource.setModulesMenu(value)
}