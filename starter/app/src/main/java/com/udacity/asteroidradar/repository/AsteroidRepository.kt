package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.todayFormatted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * to build project add entry for api_key and value to gradle.properties
     */
    private val apiKey = BuildConfig.API_KEY

    /**
     * Return all asteroids in database, this will be refreshed daily if possible
     * using WorkManager or manually on each start of the application. Stale entries
     * will also be periodically deleted from the database.
     */
    fun getAllAsteroids(filterDate: String): Flow<List<DatabaseAsteroid>> =
        database.asteroidDao.getAsteroids(filterDate).flowOn(Dispatchers.IO)

    /**
     * Not currently needed function since our only filter is by week/day and the
     * limit through api is also a week. This offers flexibility if new date ranges
     * are added.
     */
    fun getAsteroidByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<DatabaseAsteroid>> =
        database.asteroidDao.getAsteroidByDateRange(startDate, endDate).flowOn(Dispatchers.IO)

    /**
     * Returns all asteroids by specific date. Currently only possible as "today",
     * but add functionality for *specific days.
     */
    fun getAsteroidByDate(date: String): Flow<List<DatabaseAsteroid>> =
        database.asteroidDao.getAsteroidByDay(date).flowOn(Dispatchers.IO)

    /**
     * Used by viewmodel and work manager to refresh the database data of asteroids.
     */
    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            try {
                val asteroids = AsteroidApi.retrofitService.getAsteroids(startDate, endDate, apiKey)
                val networkAsteroids = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDao.insertAll(*networkAsteroids.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }

        }
    }

    /**
     * Retrieve list of saved asteroids in database
     */
    fun getSavedAsteroids(): Flow<List<DatabaseAsteroid>> = database.asteroidDao.getSavedAsteroids()

    /**
     * Update asteroid as saved to the database
     */
    suspend fun updateAsteroid(asteroid: DatabaseAsteroid) {
        withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.updateAsteroid(asteroid)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /**
     * Used by work manager only to remove old asteroids.
     */
    suspend fun deleteOldAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.deleteOldAsteroids(Date().todayFormatted())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /**
     * Returns the NASA picture of the day, this is not stored in database.
     */
    suspend fun getPictureOfDay(): Flow<PictureOfDay> {
        return flow {
            emit(AsteroidApi.retrofitService.getPictureOfDay(apiKey, Constants.SHOW_THUMBS))
        }.catch {
            Timber.e(it)
        }.flowOn(Dispatchers.IO)
    }
}