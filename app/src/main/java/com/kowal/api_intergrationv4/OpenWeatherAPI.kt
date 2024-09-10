package com.kowal.api_intergrationv4
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kowal.api_intergrationv4.dto.ForecastData
import com.kowal.api_intergrationv4.dto.WeatherData
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OpenWeatherAPI(private val context: Context) {

    private val apiKey = "6e86d40c1ccec69010c71630afb27d8c"
    private val requestQueue = Volley.newRequestQueue(context)

    // Hämtar enhet för temperatur (Celsius/Fahrenheit) från SharedPreferences
    private fun getMetricFromPreferences(): String {
        val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("temperature_metric", "metric") ?: "metric"
    }
    // Funktion för att hämta koordinater baserat på stadsnamn
    suspend fun getCords(cityName: String): Pair<Double, Double> = suspendCancellableCoroutine { continuation ->
        val url = "https://api.openweathermap.org/geo/1.0/direct?q=$cityName&limit=1&appid=$apiKey"
        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        val lat = jsonObject.getDouble("lat")
                        val lon = jsonObject.getDouble("lon")

                        continuation.resume(Pair(lat, lon))
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                    Log.d("Parsing error", ":$e")
                }
            },
            { error ->
                Toast.makeText(context, "API Error!", Toast.LENGTH_SHORT).show()
                continuation.resumeWithException(error)
            }
        )
        requestQueue.add(stringRequest)
        continuation.invokeOnCancellation { stringRequest.cancel() }
    }

    // Funktion för att hämta väderdata baserat på latitud och longitud
    suspend fun getWeather(lat: Double, lon: Double): WeatherData = suspendCancellableCoroutine { continuation ->
        val metric = getMetricFromPreferences()
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=$metric"
        val stringRequest = StringRequest(
            Request.Method.GET, url, { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val main = jsonObject.getJSONObject("main")
                    val temp = main.getDouble("temp")
                    val humidity = main.getInt("humidity")

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
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            },
            { error ->
                continuation.resumeWithException(error)
                Toast.makeText(context, "API error!", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(stringRequest)
        continuation.invokeOnCancellation { stringRequest.cancel() }
    }
    // Funktion för att hämta 5-dagarsprognos
    suspend fun getFiveDayForecast(): List<ForecastData> = suspendCancellableCoroutine { continuation ->
        val sharedPreferences = context.getSharedPreferences("cords", Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString("lat", null)
        val lon = sharedPreferences.getString("lon", null)
        if (lat != null && lon != null) {
            val metric = getMetricFromPreferences()
            val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=$metric"

            val stringRequest = StringRequest(
                Request.Method.GET, url, { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val listArray = jsonObject.getJSONArray("list")
                        val forecastList = mutableListOf<ForecastData>()
                        for (i in 0 until listArray.length()) {
                            val forecastEntry = listArray.getJSONObject(i)

                            val dateTime = forecastEntry.getString("dt_txt")
                            val main = forecastEntry.getJSONObject("main")
                            val temp = main.getDouble("temp")

                            val weatherArray = forecastEntry.getJSONArray("weather")
                            val weatherObj = weatherArray.getJSONObject(0)
                            val description = weatherObj.getString("description")
                            val icon = weatherObj.getString("icon")

                            val forecast = ForecastData(
                                dateTime,
                                temp,
                                description,
                                iconURL = "https://openweathermap.org/img/wn/$icon@2x.png"
                            )
                            forecastList.add(forecast)
                        }
                        continuation.resume(forecastList)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show()
                        continuation.resumeWithException(e)
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                    Toast.makeText(context, "API Error!", Toast.LENGTH_SHORT).show()
                }
            )
            requestQueue.add(stringRequest)
            continuation.invokeOnCancellation { stringRequest.cancel() }
        } else {
            continuation.resumeWithException(Exception("Coordinates not found in SharedPreferences!"))
        }
    }
}
