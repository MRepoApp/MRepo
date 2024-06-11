package dev.sanmer.mrepo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.datastore.DarkMode
import dev.sanmer.mrepo.datastore.WorkingMode
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val mm get() = Compat.moduleManager
    val isProviderAlive get() = Compat.isAlive

    val version get() = Compat.get("") {
        with(mm) { "$version (${versionCode})" }
    }

    init {
        Timber.d("SettingsViewModel init")
    }

    fun setWorkingMode(value: WorkingMode) {
        viewModelScope.launch {
            userPreferencesRepository.setWorkingMode(value)
        }
    }

    fun setDarkTheme(value: DarkMode) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(value)
        }
    }

    fun setThemeColor(value: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeColor(value)
        }
    }

    fun setDeleteZipFile(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDeleteZipFile(value)
        }
    }

    fun setDownloadPath(value: File) {
        viewModelScope.launch {
            userPreferencesRepository.setDownloadPath(value)
        }
    }
}