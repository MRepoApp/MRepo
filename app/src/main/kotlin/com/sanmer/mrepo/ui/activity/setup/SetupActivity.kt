package com.sanmer.mrepo.ui.activity.setup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.app.Shortcut
import com.sanmer.mrepo.data.ModuleManager
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.works.Works
import kotlinx.coroutines.launch

class SetupActivity : ComponentActivity() {
    override fun finish() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = intent.action
        }
        startActivity(intent)

        SuProvider.init(this)
        EnvProvider.init()
        Works.start()

        if (EnvProvider.isNonRoot && ModuleManager.local != 0) {
            lifecycleScope.launch {
                ModuleManager.deleteLocalAll()
            }
        }

        super.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Shortcut.push()
        if (!EnvProvider.isSetup) finish()

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupScreen()
                }
            }
        }
    }
}