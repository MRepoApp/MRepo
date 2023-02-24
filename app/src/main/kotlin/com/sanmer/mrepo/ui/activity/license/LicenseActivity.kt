package com.sanmer.mrepo.ui.activity.license

import android.content.Context
import android.content.Intent
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

class LicenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val licenseId = intent.getStringExtra(LICENSE_ID) ?: "UNKNOWN"

        setContent {
            AppTheme(
                darkTheme = State.isDarkTheme(),
                themeColor = State.themeColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LicenseScreen(
                        licenseId = licenseId
                    )
                }
            }
        }
    }

    companion object {
        private const val LICENSE_ID = "LICENSE_ID"

        fun start(
            context: Context,
            licenseId: String
        ) {
            val intent = Intent(context, LicenseActivity::class.java)
            intent.putExtra(LICENSE_ID, licenseId)
            context.startActivity(intent)
        }
    }
}