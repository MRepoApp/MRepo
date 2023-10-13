package com.sanmer.mrepo.repository

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserPreferencesDataSource
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.datastore.modules.ModulesMenuExt
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val localRepository: LocalRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val data get() = userPreferencesDataSource.data

    init {
        applicationScope.launch {
            val value = data.first()
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

    fun setDeleteZipFile(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setDeleteZipFile(value)
    }

    fun setRepositoryMenu(value: RepositoryMenuExt) = applicationScope.launch {
        userPreferencesDataSource.setRepositoryMenu(value)
    }

    fun setModulesMenu(value: ModulesMenuExt) = applicationScope.launch {
        userPreferencesDataSource.setModulesMenu(value)
    }
}