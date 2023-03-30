package com.sanmer.mrepo.ui.activity.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.app.isNotReady
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.ui.activity.setup.SetupActivity
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.works.Works
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {
    fun setup() {
        val intent = Intent(this, SetupActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        SuProvider.state.onEach {
            if (it.isNotReady) {
                SuProvider.init(this)
            }
            if (it.isSucceeded && EnvProvider.event.isNotReady) {
                EnvProvider.init()
                Works.start()
            }
        }.launchIn(lifecycleScope)

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            AppTheme {
                MainScreen()
            }
        }
    }
}