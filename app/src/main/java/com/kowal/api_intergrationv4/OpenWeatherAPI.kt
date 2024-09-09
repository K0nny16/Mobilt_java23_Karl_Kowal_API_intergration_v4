package com.kowal.api_intergrationv4

import kotlinx.coroutines.suspendCancellableCoroutine
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OpenWeatherAPI(private val context: Context) {

    private val apiKey = "6e86d40c1ccec69010c71630afb27d8c"
    private val requestQueue = Volley.newRequestQueue(context)

    suspend fun getCords(cityName: String): Pair<Double,Double> = suspendCancellableCoroutine { continuation ->
        val url = "https://api.openweathermap.org/geo/1.0/direct?q=$cityName&limit=1&appid=$apiKey"

        val stringRequest = StringRequest(
            Request.Method.GET, url,{
                response ->
                try {
                    val jsonArray = JSONArray(response)
                    if(jsonArray.length() > 0){
                        val jsonObject = jsonArray.getJSONObject(0)
                        val lat = jsonObject.getDouble("lat")
                        val lon = jsonObject.getDouble("lon")

                        continuation.resume(Pair(lat,lon))
                    }
                }catch (e: Exception){
                    continuation.resumeWithException(e)
                    Log.d("Parsing error",":$e")
                }
            },
            {error ->
                Toast.makeText(context,"API Error!",Toast.LENGTH_SHORT).show()
                continuation.resumeWithException(error)
            }
        )
        requestQueue.add(stringRequest)
        continuation.invokeOnCancellation { stringRequest.cancel() }
    }

    suspend fun getWeather(lat: Double,lon:Double):WeatherData = suspendCancellableCoroutine{ continuation ->
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    //Hämtar ut data från main objektet.
                    val jsonObject = JSONObject(response)
                    val main = jsonObject.getJSONObject("main")
                    val temp = main.getDouble("temp")
                    val humidity = main.getInt("humidity")

                    //Hämtar weather-arrayen
                    val weatherArray = jsonObject.getJSONArray("weather")
                    val weatherObject = weatherArray.getJSONObject(0)
                    val description = weatherObject.getString("description")
                    val mainWeather = weatherObject.getString("main")
                    val icon = weatherObject.getString("icon")

                    val weatherData = WeatherData(
                        temp,
                        humidity,
                        description,
                        mainWeather,
                        icon
                    )

                    continuation.resume(weatherData)
                }catch (e:Exception){
                    continuation.resumeWithException(e)
                }
            },
            {error ->
                continuation.resumeWithException(error)
                Toast.makeText(context,"API error!",Toast.LENGTH_SHORT).show()
            }
        )
        //Lägger till frörfrågan
        requestQueue.add(stringRequest)
        continuation.invokeOnCancellation { stringRequest.cancel() }
    }
}