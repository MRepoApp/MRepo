package dev.sanmer.mrepo.repository

import dev.sanmer.mrepo.datastore.DarkMode
import dev.sanmer.mrepo.datastore.UserPreferencesDataSource
import dev.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.datastore.ModulesMenuCompat
import dev.sanmer.mrepo.datastore.RepositoryMenuCompat
import java.io.File
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

    suspend fun setDownloadPath(value: File) = userPreferencesDataSource.setDownloadPath(value)

    suspend fun setRepositoryMenu(value: RepositoryMenuCompat) = userPreferencesDataSource.setRepositoryMenu(value)

    suspend fun setModulesMenu(value: ModulesMenuCompat) = userPreferencesDataSource.setModulesMenu(value)
}