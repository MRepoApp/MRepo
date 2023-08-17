package com.sanmer.mrepo.ui.activity.main

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.ui.providable.LocalUserPreferences

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setActivityContent { isReady ->
            splashScreen.setKeepOnScreenCondition { !isReady }

            val userPreferences = LocalUserPreferences.current

            if (OsUtils.atLeastT) {
                NotificationUtils.PermissionState()
            }
            MediaStoreUtils.PermissionState()

            if (userPreferences.isSetup) {
                SetupScreen(setMode = userPreferencesRepository::setWorkingMode)
            } else {
                MainScreen()
            }
        }
    }
}