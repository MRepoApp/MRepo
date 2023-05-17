package com.sanmer.mrepo.viewmodel

import androidx.lifecycle.ViewModel
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val suRepository: SuRepository
) : ViewModel() {
    val userData get() = userDataRepository.userData
    val suState get() = suRepository.state
    val apiVersion get() = suRepository.version

    init {
        Timber.d("SettingsViewModel init")
    }

    fun setWorkingMode(value: WorkingMode) = userDataRepository.setWorkingMode(value)
    fun setDarkTheme(value: DarkMode) = userDataRepository.setDarkTheme(value)
    fun setThemeColor(value: Int) = userDataRepository.setThemeColor(value)
    fun setDownloadPath(value: String) = userDataRepository.setDownloadPath(value)
    fun setDeleteZipFile(value: Boolean) = userDataRepository.setDeleteZipFile(value)
    fun setEnableNavigationAnimation(value: Boolean) = userDataRepository.setEnableNavigationAnimation(value)
}