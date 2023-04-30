package com.sanmer.mrepo.ui.activity.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sanmer.mrepo.service.LogcatService
import com.sanmer.mrepo.ui.activity.base.BaseActivity
import kotlinx.coroutines.launch

class LogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                LogcatService.isActive
                    .collect { isActive ->
                        if (!isActive) {
                            LogcatService.start(this@LogActivity)
                        }
                    }
            }
        }

        setActivityContent {
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