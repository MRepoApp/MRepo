package com.sanmer.mrepo.viewmodel

import androidx.lifecycle.ViewModel
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val suRepository: SuRepository
) : ViewModel() {
    val userPreferences get() = userPreferencesRepository.flow
    val suState get() = suRepository.state

    val apiVersion get() = try {
        suRepository.version
    } catch (e: Exception) {
        "unknown"
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

    fun setDownloadPath(value: File) =
        userPreferencesRepository.setDownloadPath(value)

    fun setDeleteZipFile(value: Boolean) =
        userPreferencesRepository.setDeleteZipFile(value)

}