package com.kowal.api_intergrationv4.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.utils.Utils.Companion.capitalizeFirstLetter
import com.kowal.api_intergrationv4.dto.ForecastData
import com.squareup.picasso.Picasso

class ForecastAdapter(private val forecastList: List<ForecastData>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvDateTime: TextView = view.findViewById(R.id.tvDate)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val tvDescription: TextView = view.findViewById(R.id.tvWeatherStatus)
        val ivIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]
        holder.tvDateTime.text = forecast.date
        holder.tvTemp.text = buildString {
            append("Temp: ")
            append(forecast.temp.toString())
        }
        holder.tvDescription.text = forecast.description.capitalizeFirstLetter()
        Picasso.get().load(forecast.iconURL).into(holder.ivIcon)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }
}