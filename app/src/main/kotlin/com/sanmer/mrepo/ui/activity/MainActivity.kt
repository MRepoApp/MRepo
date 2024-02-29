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
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.provider.ProviderCompat
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
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

            if (userPreferences == null) {
                // Keep on splash screen
                return@setContent
            } else {
                isLoading = false
            }

            LaunchedEffect(userPreferences) {
                if (userPreferences!!.isSetup) {
                    Timber.d("add default repository")
                    localRepository.insertRepo(Const.DEMO_REPO_URL.toRepo())
                }

                ProviderCompat.init(userPreferences!!.workingMode)
                NetworkUtils.setEnableDoh(userPreferences!!.useDoh)
                setInstallActivityEnabled(userPreferences!!.isRoot)
            }

            CompositionLocalProvider(
                LocalUserPreferences provides userPreferences!!
            ) {
                AppTheme(
                    darkMode = userPreferences!!.isDarkMode(),
                    themeColor = userPreferences!!.themeColor
                ) {
                    Crossfade(
                        targetState = userPreferences!!.isSetup,
                        label = "MainActivity"
                    ) { isSetup ->
                        if (isSetup) {
                            SetupScreen(
                                setMode = userPreferencesRepository::setWorkingMode
                            )
                        } else {
                            MainScreen()
                        }
                    }
                }
            }

            if (OsUtils.atLeastT) {
                NotificationUtils.PermissionState()
            }

            MediaStoreUtils.PermissionState()
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