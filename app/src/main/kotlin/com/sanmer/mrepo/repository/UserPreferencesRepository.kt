package com.sanmer.mrepo.repository

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val localRepository: LocalRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val flow get() = userPreferencesDataSource.dataFlow

    init {
        applicationScope.launch {
            val value = flow.first()
            if (value.isSetup) {
                Timber.d("add default repository")
                localRepository.insertRepo(Const.MY_REPO_URL.toRepo())
            }
        }
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

    fun setDownloadPath(value: File) = applicationScope.launch {
        userPreferencesDataSource.setDownloadPath(value.absolutePath)
    }

    fun setDeleteZipFile(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setDeleteZipFile(value)
    }
}