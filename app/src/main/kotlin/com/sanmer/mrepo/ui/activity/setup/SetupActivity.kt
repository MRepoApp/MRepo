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
import com.sanmer.mrepo.app.Config.State
import com.sanmer.mrepo.data.Constant
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.provider.FileProvider
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.works.Works

class SetupActivity : ComponentActivity() {
    override fun finish() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        FileProvider.init(this)
        EnvProvider.init()
        Works.start()

        EnvProvider.onNonRoot {
            Constant.apply {
                if (local.isNotEmpty()) {
                    deleteLocalAll()
                }
            }
        }

        super.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (!EnvProvider.isSetup) finish()

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            AppTheme(
                darkTheme = State.isDarkTheme(),
                themeColor = State.themeColor
            ) {
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