package com.sanmer.mrepo.repository

import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val userData get() = userPreferencesDataSource.userData

    private val default = UserData.default()
    private var _downloadPath = default.downloadPath
    private var _deleteZipFile = default.deleteZipFile
    val downloadPath get() = _downloadPath
    val deleteZipFile get() = _deleteZipFile

    init {
        userPreferencesDataSource.userData
            .distinctUntilChanged()
            .onEach {
                _downloadPath = it.downloadPath
                _deleteZipFile = it.deleteZipFile
            }.launchIn(applicationScope)
    }

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

    fun setDeleteZipFile(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setDeleteZipFile(value)
    }
}