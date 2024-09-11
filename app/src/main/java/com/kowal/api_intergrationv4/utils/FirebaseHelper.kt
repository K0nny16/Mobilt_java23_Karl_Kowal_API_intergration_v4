package com.kowal.api_intergrationv4.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kowal.api_intergrationv4.dto.UserPreferences

class FirebaseHelper(private val context: Context) {

    private val db = FirebaseDatabase.getInstance("https://api-intergrationv4-default-rtdb.europe-west1.firebasedatabase.app").reference
    // Spara preferenser i DB
    fun savePreferences(userPreferences: UserPreferences, onComplete: (Boolean, String?) -> Unit) {
        db.child("preferences").setValue(userPreferences)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    savePreferencesLocally(userPreferences)
                    onComplete(true, null)
                } else {
                    val errorMsg = task.exception?.message ?: "Error saving preferences!"
                    onComplete(false, errorMsg)
                }
            }
    }
    // Hämta preferenser från DB
    fun getPreferences(onComplete: (UserPreferences?) -> Unit) {
        db.child("preferences").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val preferences = snapshot.getValue(UserPreferences::class.java)
                onComplete(preferences)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to retrieve preferences!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // Spara lokalt i SP
    private fun savePreferencesLocally(preferences: UserPreferences) {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("notifications_enabled", preferences.notificationEnabled)
            putString("update_interval", preferences.updateInterval)
            putString("metric", preferences.metric)
            apply()
        }
    }
    // Hämta lokalt från SharedPreferences
    fun getPreferencesLocally(): UserPreferences {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return UserPreferences(
            sharedPref.getBoolean("notifications_enabled", true),
            sharedPref.getString("update_interval", "1 hour") ?: "1 hour",
            sharedPref.getString("metric", "C") ?: "C"
        )
    }
}

