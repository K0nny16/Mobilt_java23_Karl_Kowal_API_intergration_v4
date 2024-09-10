package com.kowal.api_intergrationv4.utils

import com.google.firebase.database.FirebaseDatabase
import com.kowal.api_intergrationv4.dto.UserPreferences
import kotlinx.coroutines.tasks.await

class FirebaseHelper {

    private val database = FirebaseDatabase.getInstance().getReference("preferences")
    // Spara användarens preferenser i Realtime Database
    suspend fun saveUserPreferences(userPreferences: UserPreferences) {
        // Skapar en unik nod för preferenser
        database.child("user_preferences").setValue(userPreferences).await()
    }
    // Hämta användarens preferenser från Realtime Database
    suspend fun getUserPreferences(): UserPreferences? {
        val snapshot = database.child("user_preferences").get().await()
        return snapshot.getValue(UserPreferences::class.java)
    }
}



