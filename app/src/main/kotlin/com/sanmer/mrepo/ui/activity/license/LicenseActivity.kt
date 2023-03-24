package com.sanmer.mrepo.ui.activity.license

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sanmer.mrepo.ui.theme.AppTheme

class LicenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val licenseId = intent.getStringExtra(LICENSE_ID) ?: "UNKNOWN"

        setContent {
            AppTheme {
                LicenseScreen(
                    licenseId = licenseId
                )
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