package com.kowal.api_intergrationv4.utils
import android.content.Context
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

    //Hanterar viewn för varje rad
    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvDateTime: TextView = view.findViewById(R.id.tvDate)
        val tvTemp: TextView = view.findViewById(R.id.tvTemp)
        val tvDescription: TextView = view.findViewById(R.id.tvWeatherStatus)
        val ivIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
    }

    //Körs när en ny ViewHolder körs/skapas
    //Lägger till designen för dom enskilda viewsen till den större viewen.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }
    //Skickar den mindre viewen (den ovan) till Holdern.
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]
        val sharedPreferences = holder.itemView.context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val metric = sharedPreferences.getString("metric","C")
        holder.tvDateTime.text = forecast.date
        holder.tvTemp.text = buildString {
            append("Temp: ")
            append(forecast.temp.toString())
            append(if(metric == "F")"°F" else "°C")
        }
        holder.tvDescription.text = forecast.description.capitalizeFirstLetter()
        //Bästa biblioteket för att GETa bilder ??
        Picasso.get().load(forecast.iconURL).into(holder.ivIcon)
    }
    //Ger storleken på listan så att den vet hur många av dom mindre viewsen som ska göras och läggas till iden större.
    override fun getItemCount(): Int {
        return forecastList.size
    }
}