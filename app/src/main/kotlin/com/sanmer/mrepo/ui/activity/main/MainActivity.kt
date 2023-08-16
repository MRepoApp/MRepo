package com.sanmer.mrepo.ui.activity.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.app.utils.MediaStoreUtils
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.app.utils.OsUtils
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPreferencesRepository.flow
                    .distinctUntilChanged()
                    .collect {
                        if (it.isSetup) {
                            setSetup()
                        } else {
                            setMain()
                        }
                        isReady = true
                    }
            }
        }
    }

    private fun setSetup() = setActivityContent {
        PermissionsState()
        SetupScreen(setMode = userPreferencesRepository::setWorkingMode)
    }

    private fun setMain() = setActivityContent {
        PermissionsState()
        MainScreen(it)
    }

    @Composable
    private fun PermissionsState() {
        if (OsUtils.atLeastT) {
            NotificationUtils.PermissionState()
        }

        MediaStoreUtils.PermissionState()
    }
}