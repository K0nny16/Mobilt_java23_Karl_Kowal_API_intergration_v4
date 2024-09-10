package com.kowal.api_intergrationv4.dto

data class UserPreferences (
    val notificationEnabled: Boolean = true,
    val updateInterval: String = "1 hour",
    val metric: String = "C"
)
