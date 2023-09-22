package com.sanmer.mrepo.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.datastore.UserPreferencesExt
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.repository.SuRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.ui.utils.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var suRepository: SuRepository

    private var isReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            val userPreferences by userPreferencesRepository.data
                .collectAsStateWithLifecycle(
                    initialValue = UserPreferencesExt.default(),
                    onReady = { isReady = true }
                )

            val suState by suRepository.state
                .collectAsStateWithLifecycle()

            if (OsUtils.atLeastT) {
                NotificationUtils.PermissionState()
            }
            MediaStoreUtils.PermissionState()

            CompositionLocalProvider(
                LocalUserPreferences provides userPreferences,
                LocalSuState provides suState
            ) {
                AppTheme(
                    darkMode = userPreferences.isDarkMode(),
                    themeColor = userPreferences.themeColor
                ) {
                    if (userPreferences.isSetup) {
                        SetupScreen(setMode = userPreferencesRepository::setWorkingMode)
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }
}