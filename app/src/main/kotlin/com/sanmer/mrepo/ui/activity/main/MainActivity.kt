package com.sanmer.mrepo.ui.activity.main

import android.os.Bundle
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import com.sanmer.mrepo.ui.providable.LocalUserPreferences

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActivityContent {
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