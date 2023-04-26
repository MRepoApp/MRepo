package com.sanmer.mrepo.repository

import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val userData get() = userPreferencesDataSource.userData

    fun setWorkingMode(value: WorkingMode) = applicationScope.launch {
        userPreferencesDataSource.setWorkingMode(value)
    }

    fun setDarkTheme(value: DarkMode) = applicationScope.launch {
        userPreferencesDataSource.setDarkTheme(value)
    }

    fun setThemeColor(value: Int) = applicationScope.launch {
        userPreferencesDataSource.setThemeColor(value)
    }

    fun setDownloadPath(value: String) = applicationScope.launch {
        userPreferencesDataSource.setDownloadPath(value)
    }
}