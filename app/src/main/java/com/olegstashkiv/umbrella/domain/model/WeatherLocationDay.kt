package com.olegstashkiv.umbrella.domain.model

data class WeatherLocationDay(
    val tempmax: Double,
    val tempmin: Double,
    val temp: Double,
    val conditions: String
)