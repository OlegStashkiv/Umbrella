package com.olegstashkiv.umbrella.domain.usecases

import com.olegstashkiv.umbrella.R
import com.olegstashkiv.umbrella.domain.model.WeatherIconCode

enum class WeatherIcon(val iconCode: String, val iconResourceId: Int) {
    PARTLY_CLOUDY_DAY(WeatherIconCode.PARTLY_CLOUDY_DAY.code, R.drawable.cloudy_sunny),
    CLEAR_DAY(WeatherIconCode.CLEAR_DAY.code, R.drawable.sunny),
    RAIN(WeatherIconCode.RAIN.code, R.drawable.rainy),
    SNOW(WeatherIconCode.SNOW.code, R.drawable.snowy),
    CLOUDY(WeatherIconCode.CLOUDY.code, R.drawable.cloudy),
    STORM(WeatherIconCode.STORM.code, R.drawable.storm),
    DEFAULT(WeatherIconCode.DEFAULT.code, R.drawable.windy);

    companion object {
        fun getIconByCode(code: String): WeatherIcon {
            return values().firstOrNull { it.iconCode == code } ?: DEFAULT
        }
    }
}