package com.kowal.api_intergrationv4.dto

data class UserPreferences (
    val notificationEnabled: Boolean = true,
    val metric: String = "C"
)
