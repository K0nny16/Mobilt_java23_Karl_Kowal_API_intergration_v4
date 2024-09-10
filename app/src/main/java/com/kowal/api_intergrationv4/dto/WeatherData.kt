package com.kowal.api_intergrationv4.dto

data class WeatherData(
    val temp: Double,
    val humidity: Int,
    val description: String,
    val mainWeather: String,
    val icon: String
)
