package com.sanmer.mrepo.ui.activity.install

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.core.view.WindowCompat
import com.sanmer.mrepo.app.runtime.Configure
import com.sanmer.mrepo.utils.InstallUtils
import com.sanmer.mrepo.app.status.Event
import com.sanmer.mrepo.ui.theme.AppTheme

class InstallActivity : ComponentActivity() {
    val utils: InstallUtils = InstallUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme(
                darkTheme = Configure.isDarkTheme(),
                themeColor = Configure.themeColor
            ) {
                val focusRequester = remember { FocusRequester() }

                BackHandler(utils.event == Event.LOADING) {}

                LaunchedEffect(focusRequester) {
                    focusRequester.requestFocus()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .onKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_VOLUME_UP -> {
                                    true
                                }
                                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                                    true
                                }
                                else -> false
                            }
                        }
                        .focusRequester(focusRequester)
                        .focusable(),

                    color = MaterialTheme.colorScheme.background
                ) {
                    InstallScreen(
                        list = utils.console
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        utils.clear()
    }
}