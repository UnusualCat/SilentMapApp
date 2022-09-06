package com.example.silentmapapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.silentmapapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private val TAG = "MapsActivity"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var permission: Permissions
    val GEOFENCE_FILE_NAME = "listaGeofence.data"
    var listaGeofence: ArrayList<GeofenceSettings> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geoFencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper= GeofenceHelper(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startNotification(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        permission= Permissions(this , this , mMap , fusedLocationProviderClient)
        mMap.setOnMapLongClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        permission.enableUserLocation()
        permission.getDeviceLocation()
        loadGeofencesIfTheyExist()
        mMap.isMyLocationEnabled = true
    }

    private fun loadGeofencesIfTheyExist() {

        val file = File(GEOFENCE_FILE_NAME)

            if (file.exists()) {

                listaGeofence = FileManager.loadFromFile(this,
                    GEOFENCE_FILE_NAME
                ) as ArrayList<GeofenceSettings>

                for (elemento in listaGeofence)
                {
                    val latLng = LatLng(elemento.latitudine , elemento.longitudine)
                    addMarker(latLng)
                    addCircle(latLng , elemento.raggio , elemento.colore)
                    addGeofence(elemento.geofenceID , latLng , elemento.raggio)
                }
            }
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng,"200.0",0)
        //TODO CREARE FINESTRA PER SETTARE UN GEOFENCE
    }

    private fun addCircle(LatLng: LatLng , radius: String , colore: Int)
    {
        val circleOptions = CircleOptions()
        circleOptions.center(LatLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(colore)
        circleOptions.fillColor(0)
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

    private fun addMarker(LatLng: LatLng) {
        val markerOptions: MarkerOptions = MarkerOptions().position(LatLng)
        mMap.addMarker(markerOptions)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(geofenceID: String , LatLng: LatLng , radius: String) {
        val geofence = geofenceHelper.getGeoFence(
            geofenceID,
            LatLng,
            radius.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geofenceHelper.getGeoFencingRequest(geofence)

        geoFencingClient.addGeofences(geofencingRequest, geofenceHelper.getPendingIntent())
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int ,
        permissions: Array<String> ,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults)

        when(requestCode){

            permission.FINE_LOCATION_ACCESS_REQUEST_CODE->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= 29) {
                        permission.enableBackgroundLocation()
                    }else mMap.isMyLocationEnabled = true
                }
                else {
                    println("Stampare all'utente che l'app ha bisogno dei permessi di localizzazione per funzionare correttamente")
                }
            }

            permission.BACKGROUND_LOCATION_ACCESS_REQUEST_CODE->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                    Toast.makeText(this, "You can add geofences.." , Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this ,
                        "Background location access is necessary to trigger geofences" ,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}