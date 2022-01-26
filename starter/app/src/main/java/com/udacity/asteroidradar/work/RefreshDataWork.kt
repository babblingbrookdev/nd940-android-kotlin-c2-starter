package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.futureDateFormatted
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.todayFormatted
import retrofit2.HttpException
import java.util.*

class RefreshDataWork(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val REFRESH_DATA_WORK = "RefreshDataWork"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.refreshAsteroids(
                Date().todayFormatted(),
                Date().futureDateFormatted(Constants.WEEK_END_DATE_DAYS)
            )
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }

}