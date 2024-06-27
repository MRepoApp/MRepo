package dev.sanmer.mrepo.ui.activity

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
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.mrepo.Compat
import dev.sanmer.mrepo.app.Const
import dev.sanmer.mrepo.datastore.model.WorkingMode
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isRoot
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isSetup
import dev.sanmer.mrepo.repository.LocalRepository
import dev.sanmer.mrepo.repository.UserPreferencesRepository
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.theme.AppTheme
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
                    Timber.d("add demo repository")
                    localRepository.insertRepo(Const.DEMO_REPO_URL)
                }

                Compat.init(preferences.workingMode)
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