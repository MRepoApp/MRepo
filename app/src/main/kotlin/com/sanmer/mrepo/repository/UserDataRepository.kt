package com.sanmer.mrepo.repository

import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.database.entity.toRepo
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
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val localRepository: LocalRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {
    val userData get() = userPreferencesDataSource.userData

    private var _value = UserData.default()
    val value get() = _value

    init {
        userPreferencesDataSource.userData
            .distinctUntilChanged()
            .onEach {
                if (it.isSetup) {
                    Timber.d("add default repository")
                    localRepository.insertRepo(Const.MY_REPO_URL.toRepo())
                }

                _value = it
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

    fun setEnableNavigationAnimation(value: Boolean) = applicationScope.launch {
        userPreferencesDataSource.setEnableNavigationAnimation(value)
    }
}