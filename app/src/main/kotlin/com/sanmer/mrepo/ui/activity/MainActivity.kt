package com.sanmer.mrepo.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.Compat
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.database.entity.Repo.Companion.toRepo
import com.sanmer.mrepo.datastore.UserPreferencesCompat.Companion.isRoot
import com.sanmer.mrepo.datastore.UserPreferencesCompat.Companion.isSetup
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject lateinit var localRepository: LocalRepository

    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            val userPreferences by userPreferencesRepository.data
                .collectAsStateWithLifecycle(initialValue = null)

            val preferences = if (userPreferences == null) {
                return@setContent
            } else {
                isLoading = false
                checkNotNull(userPreferences)
            }

            LaunchedEffect(userPreferences) {
                if (preferences.workingMode.isSetup) {
                    Timber.d("add default repository")
                    localRepository.insertRepo(Const.DEMO_REPO_URL.toRepo())
                }

                Compat.init(preferences.workingMode)
                NetworkUtils.setEnableDoh(preferences.useDoh)
                setInstallActivityEnabled(preferences.workingMode.isRoot)
            }

            CompositionLocalProvider(
                LocalUserPreferences provides preferences
            ) {
                AppTheme(
                    darkMode = preferences.isDarkMode(),
                    themeColor = preferences.themeColor
                ) {
                    Crossfade(
                        targetState = preferences.workingMode.isSetup,
                        label = "MainActivity"
                    ) { isSetup ->
                        if (isSetup) {
                            SetupScreen(
                                setMode = ::setWorkingMode
                            )
                        } else {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }

    private fun setWorkingMode(value: WorkingMode) {
        lifecycleScope.launch {
            userPreferencesRepository.setWorkingMode(value)
        }
    }

    private fun setInstallActivityEnabled(enable: Boolean) {
        val component = ComponentName(
            this, InstallActivity::class.java
        )

        val state = if (enable) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        packageManager.setComponentEnabledSetting(
            component,
            state,
            PackageManager.DONT_KILL_APP
        )
    }
}