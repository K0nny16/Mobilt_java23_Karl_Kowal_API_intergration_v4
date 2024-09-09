package com.kowal.api_intergrationv4

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        tvCityName = findViewById(R.id.tvCityName)
        tvTemp = findViewById(R.id.tvTemperature)
        tvWeatherStatus = findViewById(R.id.tvWeatherStatus)
        vtHumid = findViewById(R.id.tvHumidity)
        tvDescription = findViewById(R.id.tvDescription)
        ivIcon = findViewById(R.id.ivWeatherIcon)

        val cityName = intent.getStringExtra("City_name")
        tvCityName.text = cityName

        weatherService = OpenWeatherAPI(this)

        cityName?.let { getWeatherForCity(it) }
    }

    private fun getWeatherForCity(cityName: String){
        lifecycleScope.launch {
            try {
                val (lat,lon) = weatherService.getCords(cityName)
                val weatherData = weatherService.getWeather(lat,lon)
                updateUI(weatherData)
            }catch (e: Exception){
               Log.d("WeatherActivity","Error: $e")
            }
        }
    }
    private fun updateUI(weatherData: WeatherData){
        tvTemp.text = "${weatherData.temp}"
        tvWeatherStatus.text = weatherData.mainWeather
        vtHumid.text = "${weatherData.humidity}"
        tvDescription.text = weatherData.description

        val iconURL = "https://openweathermap.org/img/wn/${weatherData.icon}@2x.png"
        Picasso.get().load(iconURL).into(ivIcon)
    }
}