package com.example.silentmapapp

import GeofenceUtils.GeofenceSettings
import Helper.UserListAdapter
import Helper.FileManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import java.io.File

class MarkersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_markers)

        val GEOFENCE_FILE_NAME = "listaGeofence.data"

        val listView: ListView? = findViewById(R.id.listView)

        val file = File(GEOFENCE_FILE_NAME)

        if(!file.exists()){

            val listaGeofence = FileManager.loadFromFile(this) as ArrayList<GeofenceSettings>

            val adapter = UserListAdapter(this, listaGeofence)

            if (listView != null) listView.adapter = adapter
        }
    }
}