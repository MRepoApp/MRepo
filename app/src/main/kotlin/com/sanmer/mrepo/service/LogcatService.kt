package com.sanmer.mrepo.service

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sanmer.mrepo.utils.log.LogText
import com.sanmer.mrepo.utils.log.Logcat
import com.sanmer.mrepo.utils.log.Logcat.toLogTextList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LogcatService : LifecycleService() {
    override fun onCreate() {
        super.onCreate()
        isActive =  true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleScope.launch(Dispatchers.Default) {
            val old = Logcat.readLogs()
            console.addAll(
                old.filter { it !in console }
            )

            while (isActive) {
                val logs = Logcat.getCurrent().toLogTextList()
                val new = logs.filter { it !in console }
                console.addAll(new)
                new.forEach {
                    Logcat.writeLog(it)
                }

                delay(1000)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        isActive = false
    }

    companion object {
        val console = mutableStateListOf<LogText>()
        var isActive by mutableStateOf(false)
            private set

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