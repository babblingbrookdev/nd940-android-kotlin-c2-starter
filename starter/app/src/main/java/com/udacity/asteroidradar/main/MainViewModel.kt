package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)

    private val _navigateToAsteroid = MutableSharedFlow<Asteroid?>()
    val navigateToAsteroid = _navigateToAsteroid.asSharedFlow()

    private val _pictureOfDay = MutableStateFlow<PictureOfDay?>(null)
    val pictureOfDay = _pictureOfDay.asStateFlow()

    private val _pictureContentDesc = MutableStateFlow("")
    val pictureContentDesc = _pictureContentDesc.asStateFlow()

    private val _asteroids = MutableStateFlow<List<Asteroid>?>(emptyList())
    val asteroids = _asteroids.asStateFlow()

    private val _detailAsteroid = MutableSharedFlow<Asteroid?>()
    val detailAsteroid = _detailAsteroid.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                repository.refreshAsteroids(
                    Date().todayFormatted(),
                    Date().futureDateFormatted(Constants.WEEK_END_DATE_DAYS)
                )
            }
            launch {
                repository.getPictureOfDay().collectLatest {
                    _pictureOfDay.emit(it)
                    _pictureContentDesc.emit(it.title)
                }
            }
            launch {
                _loading.emit(true)
                repository.getAllAsteroids(Date().todayFormatted()).collectLatest {
                    _asteroids.emit(it.asDomainModel())
                    _loading.emit(false)
                }
            }
        }
    }

    fun showAsteroidDetails(asteroid: Asteroid) {
        viewModelScope.launch {
            _navigateToAsteroid.emit(asteroid)
        }
    }

    fun showWeekClicked() {
        viewModelScope.launch {
            _loading.emit(true)
            repository.getAsteroidByDateRange(
                Date().todayFormatted(),
                Date().futureDateFormatted(Constants.WEEK_END_DATE_DAYS)
            ).collect {
                _asteroids.emit(it.asDomainModel())
                _loading.emit(false)
            }
        }

    }

    fun showDayClicked() {
        viewModelScope.launch {
            _loading.emit(true)
            repository.getAsteroidByDate(Date().todayFormatted()).collect {
                _asteroids.emit(it.asDomainModel())
                _loading.emit(false)
            }
        }
    }

    fun showSavedClicked() {
        viewModelScope.launch {
            _loading.emit(true)
            repository.getSavedAsteroids().collect {
                _asteroids.emit(it.asDomainModel())
                _loading.emit(false)
            }
        }
    }

    fun updateAsteroid(asteroid: Asteroid) {
        viewModelScope.launch {
            asteroid.isSaved = !asteroid.isSaved
            _detailAsteroid.emit(asteroid)
            repository.updateAsteroid(asteroid.asDatabaseModel())
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}