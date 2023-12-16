package com.sanmer.mrepo.ui.activity

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
import androidx.work.WorkManager
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.Event.Companion.isNon
import com.sanmer.mrepo.app.Event.Companion.isSucceeded
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.network.NetworkUtils
import com.sanmer.mrepo.provider.SuProviderImpl
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.ui.providable.LocalSuState
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.theme.AppTheme
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

    private var isLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { isLoading }

        setContent {
            MediaStoreUtils.PermissionState()

            if (OsUtils.atLeastT) {
                NotificationUtils.PermissionState()
            }

            val userPreferences by userPreferencesRepository.data
                .collectAsStateWithLifecycle(initialValue = null)

            val suState by suProviderImpl.state
                .collectAsStateWithLifecycle()

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

                NetworkUtils.setEnableDoh(userPreferences!!.useDoh)
            }

            LaunchedEffect(userPreferences, suState) {
                when {
                    suState.isNon && userPreferences!!.isRoot -> {
                        suProviderImpl.init()
                    }
                    suState.isSucceeded -> {
                        workManger.enqueue(LocalWork.OneTimeWork)
                    }
                }
            }

            CompositionLocalProvider(
                LocalUserPreferences provides userPreferences!!,
                LocalSuState provides suState
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
        }
    }
}