package com.sanmer.mrepo.service

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.utils.log.LogItem
import com.sanmer.mrepo.utils.log.SystemLogcat
import com.sanmer.mrepo.utils.log.SystemLogcat.Companion.toLogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LogcatService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        console.clear()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val logcat = SystemLogcat(applicationInfo.uid)
        lifecycleScope.launch(Dispatchers.IO) {
            console.addAll(
                logcat.dumpCrash()
                    .map { it.toLogItem() }
            )

            while (isActive) {
                val texts = logcat.dumpCrash()
                val log = texts.last().toLogItem()
                if (log !in console) console.add(log)

                delay(1000)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        console.clear()
    }

    companion object {
        val console = mutableStateListOf<LogItem>()

        fun start(
            context: Context,
        ) {
            val intent = Intent(context, LogcatService::class.java)
            context.startService(intent)
        }

        fun stop(
            context: Context,
        ) {
            val intent = Intent(context, LogcatService::class.java)
            context.stopService(intent)
        }
    }
}