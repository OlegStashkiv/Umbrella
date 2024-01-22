package com.olegstashkiv.umbrella.domain.model

data class WeatherForLocationResponse(
    val timezone: String,
    val days: List<WeatherLocationDay>
)