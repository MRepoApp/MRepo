package com.sanmer.mrepo.repository

import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.datastore.modules.ModulesMenuCompat
import com.sanmer.mrepo.datastore.repository.RepositoryMenuCompat
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

    suspend fun setUseDoh(value: Boolean) = userPreferencesDataSource.setUseDoh(value)

    suspend fun setDownloadPath(value: File) = userPreferencesDataSource.setDownloadPath(value)

    suspend fun setRepositoryMenu(value: RepositoryMenuCompat) = userPreferencesDataSource.setRepositoryMenu(value)

    suspend fun setModulesMenu(value: ModulesMenuCompat) = userPreferencesDataSource.setModulesMenu(value)
}