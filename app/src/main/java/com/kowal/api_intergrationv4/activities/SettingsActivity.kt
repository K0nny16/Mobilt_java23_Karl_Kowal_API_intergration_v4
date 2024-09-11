package com.kowal.api_intergrationv4.activities

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
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
    private lateinit var radioGroupUnits: RadioGroup
    private val firebaseHelper = FirebaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        switchNotification = findViewById(R.id.switchNotifications)
        radioGroupUnits = findViewById(R.id.radioGroupUnits)

        loadPreferences()

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            savePreferences(isChecked = isChecked)
        }
        radioGroupUnits.setOnCheckedChangeListener { _, checkedId ->
            val metric = if (checkedId == R.id.radioCelsius) "C" else "F"
            savePreferences(metric = metric)
        }
    }
    private fun loadPreferences() {
        // Hämtar preferences från Firebase och ställer in tidigare sparade inställningar.
        lifecycleScope.launch {
            val preferences = firebaseHelper.getPreferencesLocally()
            switchNotification.isChecked = preferences.notificationEnabled
            radioGroupUnits.check(if (preferences.metric == "C") R.id.radioCelsius else R.id.radioFahrenheit)
        }
    }
    // Sparar nya preferenser till Firebase och lokalt i SharedPreferences.
    private fun savePreferences(
        isChecked: Boolean = switchNotification.isChecked,
        metric: String = if (radioGroupUnits.checkedRadioButtonId == R.id.radioCelsius) "C" else "F"
    ) {
        val userPreferences = UserPreferences(isChecked, metric = metric)
        firebaseHelper.savePreferences(userPreferences) { success, error ->
            if (!success) {
                Toast.makeText(this, "Failed to save preferences: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}