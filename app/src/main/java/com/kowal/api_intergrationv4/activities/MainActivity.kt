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
import androidx.lifecycle.lifecycleScope
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.utils.FirebaseHelper
import com.kowal.api_intergrationv4.dto.UserPreferences
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val firebaseHelper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadUserPreferences()
        val cityET = findViewById<EditText>(R.id.etCity)
        val weatherBtn = findViewById<Button>(R.id.btnGetWeather)

        weatherBtn.setOnClickListener {
            val city = cityET.text.toString()
            if(city.isNotEmpty()){
                val intent = Intent(this, WeatherActivity::class.java)
                intent.putExtra("City_name",city)
                startActivity(intent)
            }else {
                Toast.makeText(this,"Please enter a name of a city!",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadUserPreferences(){
        lifecycleScope.launch {
            val preferences = firebaseHelper.getUserPreferences()
            if(preferences != null){
                applyPreferences(preferences)
            }
        }
    }
    private fun applyPreferences(preferences: UserPreferences){
        if(preferences.metric == "F"){

        }
    }
}