package com.sanmer.mrepo.ui.activity.install

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.sanmer.mrepo.app.Config.State
import com.sanmer.mrepo.provider.local.InstallUtils
import com.sanmer.mrepo.ui.theme.AppTheme

class InstallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme(
                darkTheme = State.isDarkTheme(),
                themeColor = State.themeColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstallScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InstallUtils.clear()
    }
}