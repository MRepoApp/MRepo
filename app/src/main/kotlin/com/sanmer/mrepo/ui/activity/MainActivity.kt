package com.sanmer.mrepo.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.isNon
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.datastore.UserPreferencesExt
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.provider.SuProviderImpl
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.ui.utils.collectAsStateWithLifecycle
import com.sanmer.mrepo.works.LocalWork
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var suProviderImpl: SuProviderImpl

    @Inject
    lateinit var localRepository: LocalRepository

    private val workManger by lazy { WorkManager.getInstance(this) }

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
                    onReady = { if (!isReady) isReady = true }
                )

            val suState by suProviderImpl.state
                .collectAsStateWithLifecycle()

            if (OsUtils.atLeastT) {
                NotificationUtils.PermissionState()
            }

            LaunchedEffect(userPreferences) {
                if (!isReady) return@LaunchedEffect

                if (userPreferences.isSetup) {
                    Timber.d("add default repository")
                    localRepository.insertRepo(Const.DEMO_REPO_URL.toRepo())
                }

                NetworkUtils.setEnableDoh(userPreferences.useDoh)
            }

            LaunchedEffect(userPreferences, suState) {
                if (!isReady) return@LaunchedEffect

                when {
                    suState.isNon && userPreferences.isRoot -> {
                        suProviderImpl.init()
                    }
                    suState.isSucceeded -> {
                        workManger.enqueue(LocalWork.OneTimeWork)
                    }
                }
            }

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