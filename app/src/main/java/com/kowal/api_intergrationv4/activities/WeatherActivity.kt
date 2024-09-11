package com.kowal.api_intergrationv4.activities

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.kowal.api_intergrationv4.OpenWeatherAPI
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.utils.Utils.Companion.capitalizeFirstLetter
import com.kowal.api_intergrationv4.dto.WeatherData
import com.kowal.api_intergrationv4.utils.NotificationHelper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class WeatherActivity : AppCompatActivity() {

    private lateinit var weatherService: OpenWeatherAPI
    private lateinit var tvCityName: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvWeatherStatus: TextView
    private lateinit var vtHumid: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)

        // Initiera vyer
        tvCityName = findViewById(R.id.tvCityName)
        tvTemp = findViewById(R.id.tvTemperature)
        tvWeatherStatus = findViewById(R.id.tvWeatherStatus)
        vtHumid = findViewById(R.id.tvHumidity)
        tvDescription = findViewById(R.id.tvDescription)
        ivIcon = findViewById(R.id.ivWeatherIcon)

        // Initiera OpenWeatherAPI
        weatherService = OpenWeatherAPI(this)

        // Hämta sparade koordinater och stadsnamn från SharedPreferences
        val sharedPreferences = getSharedPreferences("cords", MODE_PRIVATE)
        val savedLat = sharedPreferences.getString("lat", null)
        val savedLon = sharedPreferences.getString("lon", null)
        val savedCity = sharedPreferences.getString("city_name", null)
        val cityName = intent.getStringExtra("City_name")

        if (savedLat != null && savedLon != null) {
            // Om koordinaterna finns alltså om man bara har bytat Activity och går tillbaka
            tvCityName.text = buildString {
                append("Location: ")
                append(savedCity)
            }
            getWeatherForCoordinates(savedLat, savedLon)
        } else {
            // Om inget är sparat, använd stadens namn från intentet
            if (cityName != null) {
                tvCityName.text = buildString {
                    append("Location: ")
                    append(cityName)
                }
                getWeatherForCity(cityName)
            }
        }
    }
    // Metod för att hämta väder med stadens namn
    private fun getWeatherForCity(cityName: String) {
        //Säker ställer att coroutinen avbryts när activityn eller fragmentet förstörs.
        //Kör också anropen Async vilket gör att UI tråden kan vara mer responsiv.
        lifecycleScope.launch {
            try {
                val (lat, lon) = weatherService.getCords(cityName)
                // Spara koordinater och stadens namn i SharedPreferences
                val sharedPreferences = getSharedPreferences("cords", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("lat", lat.toString())
                    putString("lon", lon.toString())
                    putString("city_name", cityName)
                    apply()
                }
                // Hämta vädret med hjälp av koordinaterna
                val weatherData = weatherService.getWeather(lat, lon)
                updateUI(weatherData)
            } catch (e: Exception) {
                Log.d("WeatherActivity", "Error: $e")
            }
        }
    }
    // Metod för att hämta väder med latitud och longitud
    private fun getWeatherForCoordinates(lat: String, lon: String) {
        lifecycleScope.launch {
            try {
                val weatherData = weatherService.getWeather(lat.toDouble(), lon.toDouble())
                updateUI(weatherData)
            } catch (e: Exception) {
                Log.d("WeatherActivity", "Error: $e")
            }
        }
    }
    // Uppdaterar UI med väderdata
    private fun updateUI(weatherData: WeatherData) {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val metric = sharedPreferences.getString("metric", "C")
        tvTemp.text = buildString {
            append("Temp: ")
            append(weatherData.temp)
            append(if(metric == "F")"°F" else "°C")
        }
        tvWeatherStatus.text = buildString {
            append("Status: ")
            append(weatherData.mainWeather)
        }
        vtHumid.text = buildString {
            append("Humidity: ")
            append(weatherData.humidity)
            append("%")
        }
        tvDescription.text = buildString {
            append("Description: ")
            append(weatherData.description.capitalizeFirstLetter())
        }
        val iconURL = "https://openweathermap.org/img/wn/${weatherData.icon}@2x.png"
        Picasso.get().load(iconURL).into(ivIcon)
        val notice = sharedPreferences.getBoolean("notifications_enabled",true)
        Log.d("Notice","$notice")
        if(notice &&(weatherData.mainWeather == "Rain" || weatherData.description.contains("rain",true))){
            val notificationHelper = NotificationHelper(this)
            notificationHelper.sendSimpleNotification()
        }
    }
}