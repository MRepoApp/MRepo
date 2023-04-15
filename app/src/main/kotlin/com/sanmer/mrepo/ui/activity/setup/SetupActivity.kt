package com.sanmer.mrepo.ui.activity.setup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.provider.SuProvider
import com.sanmer.mrepo.repository.LocalRepository
import com.sanmer.mrepo.ui.activity.main.MainActivity
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SetupActivity : ComponentActivity() {
    @Inject
    lateinit var suProvider: SuProvider

    @Inject
    lateinit var localRepository: LocalRepository

    override fun finish() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = intent.action
        }
        startActivity(intent)

        if (Config.isNonRoot) {
            lifecycleScope.launch {
                localRepository.deleteLocalAll()
            }
        }

        super.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (!Config.isSetup) finish()

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                NotificationUtils.PermissionState()
            }

            AppTheme {
                SetupScreen()
            }
        }
    }

    fun initSu() = suProvider.init()
}