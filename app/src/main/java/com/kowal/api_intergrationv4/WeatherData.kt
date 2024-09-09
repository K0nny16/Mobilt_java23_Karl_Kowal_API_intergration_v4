package com.kowal.api_intergrationv4

data class WeatherData(
    val temp: Double,
    val humidity: Int,
    val description: String,
    val mainWeather: String,
    val icon: String
)
