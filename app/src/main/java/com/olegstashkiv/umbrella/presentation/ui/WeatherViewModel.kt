package com.olegstashkiv.umbrella.presentation.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olegstashkiv.umbrella.data.api.WeatherApi
import com.olegstashkiv.umbrella.data.db.DataStoreManager
import com.olegstashkiv.umbrella.domain.model.WeatherResponse
import kotlinx.coroutines.launch

const val TAG = "ViewModel";

enum class WeatherApiStatus { LOADING, ERROR, DONE }

class WeatherViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    private val _dailyWeatherData = MutableLiveData<WeatherResponse>()
    val dailyWeatherData: LiveData<WeatherResponse> get() = _dailyWeatherData

    private val _weeklyWeatherData = MutableLiveData<WeatherResponse>()
    val weeklyWeatherData: LiveData<WeatherResponse> get() = _weeklyWeatherData

    private val _status = MutableLiveData<WeatherApiStatus>()

    val status: LiveData<WeatherApiStatus> = _status

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val weatherApi = WeatherApi.create()

    fun updateLocation(newLocation: String) {
        viewModelScope.launch {
            _location.value = newLocation
            dataStoreManager.saveLocation(newLocation)
        }
    }

    fun updateStatus(status: WeatherApiStatus) {
        _status.value = status
    }

    fun getDailyWeather() {
        val locationValue = location.value
        if (locationValue != null) {
            viewModelScope.launch {
                _status.value = WeatherApiStatus.LOADING
                try {
                    _dailyWeatherData.value = weatherApi.getDailyWeather(locationValue)
                    _status.value = WeatherApiStatus.DONE
                } catch (e: Exception) {
                    _status.value = WeatherApiStatus.ERROR
                    Log.e(
                        TAG,
                        "Something went wrong when trying to execute network request for daily weather"
                    )
                    removeLocationFromDataStore()
                }
            }
        }
    }

    fun getWeeklyWeatherData() {
        val locationValue = location.value ?: ""
        viewModelScope.launch {
            _status.value = WeatherApiStatus.LOADING
            try {
                _weeklyWeatherData.value = weatherApi.getWeeklyWeather(locationValue)
                _status.value = WeatherApiStatus.DONE
            } catch (e: Exception) {
                _status.value = WeatherApiStatus.ERROR
                Log.e(
                    TAG,
                    "Something went wrong when trying to execute network request for weekly weather"
                )
                removeLocationFromDataStore()
            }
        }
    }

    private fun removeLocationFromDataStore() {
        viewModelScope.launch {
            dataStoreManager.removeLocation()
        }
    }
}

class WeatherViewModelFactory(private val dataStoreManager: DataStoreManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(dataStoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
