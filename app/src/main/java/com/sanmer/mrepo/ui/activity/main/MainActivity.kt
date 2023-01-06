package com.sanmer.mrepo.ui.activity.main

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
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.app.runtime.Configure
import com.sanmer.mrepo.ui.activity.setup.SetupActivity
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.works.Works

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Config.WORKING_MODE == Config.FIRST_SETUP) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        } else {
            Works.start()
        }

        setContent {
            AppTheme(
                darkTheme = Configure.isDarkTheme(),
                themeColor = Configure.themeColor
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    NotificationUtils.PermissionState()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}