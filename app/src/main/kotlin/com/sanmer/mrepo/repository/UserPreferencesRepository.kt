package com.sanmer.mrepo.repository

import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.datastore.modules.ModulesMenuCompat
import com.sanmer.mrepo.datastore.repository.RepositoryMenuCompat
import com.sanmer.mrepo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val data get() = userPreferencesDataSource.data

    fun setWorkingMode(value: WorkingMode) = applicationScope.launch {
        userPreferencesDataSource.setWorkingMode(value)
    }

    fun setDarkTheme(value: DarkMode) = applicationScope.launch {
        userPreferencesDataSource.setDarkTheme(value)
    }

    fun setThemeColor(value: Int) = applicationScope.launch {
        userPreferencesDataSource.setThemeColor(value)
    }

    fun setDeleteZipFile(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setDeleteZipFile(value)
    }

    fun setUseDoh(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setUseDoh(value)
    }

    fun setDownloadPath(value: File) = applicationScope.launch {
        userPreferencesDataSource.setDownloadPath(value.absolutePath)
    }

    fun setRepositoryMenu(value: RepositoryMenuCompat) = applicationScope.launch {
        userPreferencesDataSource.setRepositoryMenu(value)
    }

    fun setModulesMenu(value: ModulesMenuCompat) = applicationScope.launch {
        userPreferencesDataSource.setModulesMenu(value)
    }
}