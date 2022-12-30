package com.sanmer.mrepo.ui.activity.setup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.sanmer.mrepo.app.runtime.Configure
import com.sanmer.mrepo.ui.theme.AppTheme

class SetupActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme(
                darkTheme = Configure.isDarkTheme(),
                themeColor = Configure.themeColor
            ) {
                BackHandler {
                    val home = Intent(Intent.ACTION_MAIN)
                    home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    home.addCategory(Intent.CATEGORY_HOME)
                    startActivity(home)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupScreen()
                }
            }
        }
    }
}