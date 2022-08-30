package com.example.silentmapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.silentmapapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private val TAG = "MapsActivity"
    private var geofenceID = "test"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE=1002
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geofenceHelper= GeofenceHelper(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startNotification(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        enableUserLocation()
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng,"200.0")
        addGeofence(latLng,"200.0")
    }

    private fun addCircle(LatLng: LatLng, radius: String) {

        val circleOptions = CircleOptions()
        circleOptions.center(LatLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
        //TODO Aggiungere colore come opzione
    }

    private fun addMarker(LatLng: LatLng) {
        val markerOptions: MarkerOptions = MarkerOptions().position(LatLng)
        mMap.addMarker(markerOptions)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(LatLng: LatLng, radius: String) {
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
    fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            if (Build.VERSION.SDK_INT >= 29) {
                enableBackgroundLocation()
            }else{
                getDeviceLocation()
            }

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                FINE_LOCATION_ACCESS_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.ACCESS_FINE_LOCATION },
                FINE_LOCATION_ACCESS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableBackgroundLocation(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getDeviceLocation()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q))
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null)
                    {
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation !!.latitude ,
                                    lastKnownLocation !!.longitude
                                ) , DEFAULT_ZOOM.toFloat()
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
                if (Build.VERSION.SDK_INT >= 29) {
                    enableBackgroundLocation()
                }
                getDeviceLocation()
            } else {
                println("Stampare all'utente che l'app ha bisogno dei permessi di localizzazione per funzionare correttamente")
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MapsActivity,"Permessi accettati", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MapsActivity,"Permessi negati non è possibile utilizzare l'applicazione",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}