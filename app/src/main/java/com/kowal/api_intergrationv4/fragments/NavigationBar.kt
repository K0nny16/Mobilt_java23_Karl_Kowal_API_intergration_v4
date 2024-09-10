package com.kowal.api_intergrationv4.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kowal.api_intergrationv4.activities.ForecastActivity
import com.kowal.api_intergrationv4.activities.MainActivity
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.activities.SettingsActivity
import com.kowal.api_intergrationv4.activities.WeatherActivity

class NavigationBar: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragement_navbar,container,false)

        val btnHome = view.findViewById<Button>(R.id.btnHome)
        val btnForecast = view.findViewById<Button>(R.id.btnForecast)
        val btnToday = view.findViewById<Button>(R.id.btnToday)
        val btnSettings = view.findViewById<Button>(R.id.btnSettings)

        btnHome.setOnClickListener {
            //Tömmer/Raderar SP
            val sharedPreferences = activity?.getSharedPreferences("cords",android.content.Context.MODE_PRIVATE)
            sharedPreferences?.edit()?.clear()?.apply()
            //Rensar stacken och gör den nya activityn till roten av stacket.
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            //Avslutar den tidigare activityn.
            activity?.finish()
        }
        btnForecast.setOnClickListener {
            val intent = Intent(activity, ForecastActivity::class.java)
            startActivity(intent)
        }
        btnToday.setOnClickListener {
            val intent = Intent(activity, WeatherActivity::class.java)
            startActivity(intent)
        }
        btnSettings.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}