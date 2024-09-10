package com.kowal.api_intergrationv4.dto

data class ForecastData(
    val date: String,
    val temp: Double,
    val description: String,
    val iconURL: String
)
