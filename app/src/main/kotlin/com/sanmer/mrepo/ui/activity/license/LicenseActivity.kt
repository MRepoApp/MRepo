package com.sanmer.mrepo.ui.activity.license

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sanmer.mrepo.ui.activity.base.BaseActivity

class LicenseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val licenseId = intent.getStringExtra(LICENSE_ID) ?: "UNKNOWN"

        setActivityContent {
            LicenseScreen(
                licenseId = licenseId
            )
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