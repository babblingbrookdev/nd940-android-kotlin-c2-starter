package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.DeleteDataWork
import com.udacity.asteroidradar.work.RefreshDataWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AsteroidRadarApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    /**
     * Setup recurring work for refreshing asteroid data as well as deleting
     * old data daily if constraints are met. (unmetered connection,
     * battery not low / charging.
     */
    private fun setupRecurringWork() {
        Timber.d("Setting up recurring work")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val refreshDataRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints).build()
        val deleteDataRequest = PeriodicWorkRequestBuilder<DeleteDataWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWork.REFRESH_DATA_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshDataRequest
        )
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            DeleteDataWork.DELETE_DATA_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            deleteDataRequest
        )
    }
}