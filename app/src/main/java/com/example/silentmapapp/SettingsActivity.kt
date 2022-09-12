package com.example.silentmapapp

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchLocationUpdates: SwitchCompat = findViewById(R.id.locationUpdates)
        val tvUpdates: TextView = findViewById(R.id.tv_updates)
        val tvSensor: TextView = findViewById(R.id.tv_sensor)
        val powerMode: SwitchCompat = findViewById(R.id.powerMode)

        val sharedPreference = getSharedPreferences("PREFERENCES",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        if(sharedPreference.contains("locationUpdates")){

            switchLocationUpdates.isChecked = sharedPreference.getBoolean("locationUpdates", true)

            if(switchLocationUpdates.isChecked) tvUpdates.text = "ON"
            else tvUpdates.text = "OFF"
        }

        if(sharedPreference.contains("savePower")){

            powerMode.isChecked = sharedPreference.getBoolean("savePower", false)

            if(powerMode.isChecked) tvSensor.text = "Torre cellulare + WIFI"
            else tvSensor.text = "GPS"
        }

        switchLocationUpdates.setOnClickListener {

            if(switchLocationUpdates.isChecked){

                editor.putBoolean("locationUpdates",true)
                tvUpdates.text = "ON"
            }
            else{
                editor.putBoolean("locationUpdates",false)
                tvUpdates.text = "OFF"

            }
            editor.apply()
        }

        powerMode.setOnClickListener {

            if(powerMode.isChecked){

                editor.putBoolean("savePower",true)
                tvSensor.text = "Torre cellulare + WIFI"}

            else{
                editor.putBoolean("savePower",false)
                tvSensor.text = "GPS"
            }
            editor.apply()
        }
    }
}