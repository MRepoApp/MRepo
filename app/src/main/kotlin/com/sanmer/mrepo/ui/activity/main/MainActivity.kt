package com.sanmer.mrepo.ui.activity.main

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
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            AppTheme(
                darkTheme = State.isDarkTheme(),
                themeColor = State.themeColor
            ) {
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