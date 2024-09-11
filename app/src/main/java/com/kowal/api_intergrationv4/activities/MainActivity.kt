package com.kowal.api_intergrationv4.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.dto.UserPreferences
import com.kowal.api_intergrationv4.utils.FirebaseHelper

class MainActivity : AppCompatActivity() {

    private val firebaseHelper = FirebaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Hämta preferenser från Firebase vid start
        loadUserPreferences()
        val cityET = findViewById<EditText>(R.id.etCity)
        val weatherBtn = findViewById<Button>(R.id.btnGetWeather)

        weatherBtn.setOnClickListener {
            val city = cityET.text.toString()
            if (city.isNotEmpty()) {
                val intent = Intent(this, WeatherActivity::class.java)
                //Skickar med namnet på staden till nästa activity.
                intent.putExtra("City_name", city)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a name of a city!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Hämta användarpreferenser och applicera dem(ifall det finns. Bör dock göra eftersom att DB inte är tom)
    private fun loadUserPreferences() {
        firebaseHelper.getPreferences { preferences ->
            if (preferences != null) {
                applyPreferences(preferences)
            }
        }
    }
    //Ifall det fanns prefs i FB så sparas det i SP.
    private fun applyPreferences(preferences: UserPreferences) {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("temperature_metric", preferences.metric) // Spara metriska enheter i SharedPreferences
            putBoolean("notifications",preferences.notificationEnabled) //Om man vill ha notiser på.
            apply()
        }
    }
}