package com.sanmer.mrepo.ui.activity.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.LaunchedEffect
import com.sanmer.mrepo.service.LogcatService
import com.sanmer.mrepo.ui.activity.base.BaseActivity

class LogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActivityContent {
            LaunchedEffect(LogcatService.isActive) {
                if (!LogcatService.isActive) {
                    LogcatService.start(this@LogActivity)
                }
            }

            LogScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogcatService.stop(this)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LogActivity::class.java)
            context.startActivity(intent)
        }
    }
}