package com.sanmer.mrepo

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.sanmer.mrepo.app.Shortcut
import com.sanmer.mrepo.app.isNotReady
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.di.MainScope
import com.sanmer.mrepo.provider.SuProviderImpl
import com.sanmer.mrepo.repository.UserDataRepository
import com.sanmer.mrepo.utils.MediaStoreUtils
import com.sanmer.mrepo.utils.NotificationUtils
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import com.sanmer.mrepo.works.LocalWork
import com.sanmer.mrepo.works.RepoWork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var suProviderImpl: SuProviderImpl

    @MainScope
    @Inject
    lateinit var mainScope: CoroutineScope

    private val workManger by lazy { WorkManager.getInstance(this) }

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        MediaStoreUtils.init(this)
        NotificationUtils.init(this)

        Shortcut.push()
        initSuProviderImpl()
        workManger.enqueue(RepoWork.OneTimeWork)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun initSuProviderImpl() {
        userDataRepository.userData
            .map { it.isRoot }
            .combine(suProviderImpl.state) { isRoot, state ->
                if (state.isNotReady && isRoot) {
                    suProviderImpl.init()
                }

                if (state.isSucceeded) {
                    workManger.enqueue(LocalWork.OneTimeWork)
                }

            }.launchIn(mainScope)
    }

    companion object {
        private lateinit var app: App
        val context: Context get() = app
    }
}