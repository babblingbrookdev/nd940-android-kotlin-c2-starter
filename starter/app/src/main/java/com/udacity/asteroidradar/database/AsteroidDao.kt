package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {
    @Query("select * from asteroid_table ORDER BY closeApproachDate DESC")
    fun getAsteroids(): Flow<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate DESC ")
    fun getAsteroidByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<DatabaseAsteroid>>

    @Query("select * from asteroid_table WHERE closeApproachDate == :date")
    fun getAsteroidByDay(date: String): Flow<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM asteroid_table WHERE closeApproachDate < :today")
    fun deleteOldAsteroids(today: String): Int
}