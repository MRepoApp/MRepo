package com.sanmer.mrepo.ui.activity.setup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sanmer.mrepo.app.Config
import com.sanmer.mrepo.provider.SuProviderImpl
import com.sanmer.mrepo.ui.theme.AppTheme
import com.sanmer.mrepo.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupActivity : ComponentActivity() {
    @Inject
    lateinit var suProviderImpl: SuProviderImpl

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

    fun initSu() = suProviderImpl.init()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SetupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }
    }
}