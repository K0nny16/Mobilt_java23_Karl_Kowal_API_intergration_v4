package com.kowal.api_intergrationv4.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kowal.api_intergrationv4.utils.ForecastAdapter
import com.kowal.api_intergrationv4.OpenWeatherAPI
import com.kowal.api_intergrationv4.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForecastActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var weatherService: OpenWeatherAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forcast)

        recycler = findViewById(R.id.recyclerViewForecast)
        recycler.layoutManager = LinearLayoutManager(this)
        weatherService = OpenWeatherAPI(this)

        fetchForecastData()
    }

    private fun fetchForecastData(){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val forecastList = weatherService.getFiveDayForecast()
                recycler.adapter = ForecastAdapter(forecastList)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}