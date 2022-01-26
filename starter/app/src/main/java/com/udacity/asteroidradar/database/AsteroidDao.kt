package com.udacity.asteroidradar.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {
    @Query("select * from asteroid_table WHERE closeApproachDate >= :filterDate ORDER BY closeApproachDate DESC")
    fun getAsteroids(filterDate: String): Flow<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table WHERE isSaved")
    fun getSavedAsteroids(): Flow<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate DESC ")
    fun getAsteroidByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table WHERE closeApproachDate == :date")
    fun getAsteroidByDay(date: String): Flow<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Update
    fun updateAsteroid(asteroid: DatabaseAsteroid)

    @Query("DELETE FROM asteroid_table WHERE closeApproachDate < :today AND NOT isSaved ")
    fun deleteOldAsteroids(today: String): Int
}