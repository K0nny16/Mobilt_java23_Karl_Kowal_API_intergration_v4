package com.kowal.api_intergrationv4.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.kowal.api_intergrationv4.R
import com.kowal.api_intergrationv4.dto.UserPreferences
import com.kowal.api_intergrationv4.utils.FirebaseHelper
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchNotification: SwitchCompat
    private lateinit var spinnerUpdateInterval: Spinner
    private lateinit var radioGroupUnits: RadioGroup
    private val firebaseHelper = FirebaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        switchNotification = findViewById(R.id.switchNotifications)
        spinnerUpdateInterval = findViewById(R.id.spinnerUpdateInterval)
        radioGroupUnits = findViewById(R.id.radioGroupUnits)

        val intervals = arrayOf("5 min", "30 min", "1 hour")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervals)
        spinnerUpdateInterval.adapter = adapter
        loadPreferences()

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            savePreferences(isChecked = isChecked)
        }
        radioGroupUnits.setOnCheckedChangeListener { _, checkedId ->
            val metric = if (checkedId == R.id.radioCelsius) "C" else "F"
            savePreferences(metric = metric)
        }
        spinnerUpdateInterval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedInterval = intervals[position]
                savePreferences(updateInterval = selectedInterval)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun loadPreferences() {
        //Hämtar preferences från FB ifall och sätter settingsen till vad dom senast var.
        lifecycleScope.launch {
            val preferences = firebaseHelper.getPreferencesLocally()
            switchNotification.isChecked = preferences.notificationEnabled
            radioGroupUnits.check(if (preferences.metric == "C") R.id.radioCelsius else R.id.radioFahrenheit)
            val position = (spinnerUpdateInterval.adapter as ArrayAdapter<String>).getPosition(preferences.updateInterval)
            spinnerUpdateInterval.setSelection(position)
        }
    }

    //Sparar dom ny prefsen till FB och sedan i savedPreferences sparas dom även lokalt i SP.
    private fun savePreferences(
        isChecked: Boolean = switchNotification.isChecked,
        updateInterval: String = spinnerUpdateInterval.selectedItem.toString(),
        metric: String = if (radioGroupUnits.checkedRadioButtonId == R.id.radioCelsius) "C" else "F"
    ) {
        val userPreferences = UserPreferences(isChecked, updateInterval, metric)
        firebaseHelper.savePreferences(userPreferences) { success, error ->
            if (!success) {
                Toast.makeText(this, "Failed to save preferences: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}