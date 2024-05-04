package com.sanmer.mrepo.viewmodel

import androidx.lifecycle.ViewModel
import com.sanmer.mrepo.compat.ProviderCompat
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val isProviderAlive get() = ProviderCompat.isAlive

    val version get() = ProviderCompat.get("") {
        with(moduleManager) { "$version (${versionCode})" }
    }

    init {
        Timber.d("SettingsViewModel init")
    }

    fun setWorkingMode(value: WorkingMode) =
        userPreferencesRepository.setWorkingMode(value)

    fun setDarkTheme(value: DarkMode) =
        userPreferencesRepository.setDarkTheme(value)

    fun setThemeColor(value: Int) =
        userPreferencesRepository.setThemeColor(value)

    fun setDeleteZipFile(value: Boolean) =
        userPreferencesRepository.setDeleteZipFile(value)

    fun setUseDoh(value: Boolean) =
        userPreferencesRepository.setUseDoh(value)

    fun setDownloadPath(value: File) =
        userPreferencesRepository.setDownloadPath(value)
}