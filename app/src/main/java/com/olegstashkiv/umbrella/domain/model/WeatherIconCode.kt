package com.olegstashkiv.umbrella.domain.model

enum class WeatherIconCode(val code: String) {
    PARTLY_CLOUDY_DAY("partly-cloudy-day"),
    CLEAR_DAY("clear-day"),
    RAIN("rain"),
    SNOW("snow"),
    CLOUDY("cloudy"),
    STORM("storm"),
    DEFAULT("windy")
}
