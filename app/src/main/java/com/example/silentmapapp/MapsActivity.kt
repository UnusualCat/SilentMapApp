package com.example.silentmapapp

import GeofenceUtils.GeofenceHelper
import GeofenceUtils.GeofenceSettings
import GeofenceUtils.GeofenceSetupActivity
import Helper.FileManager
import Helper.Permissions
import Helper.Resources
import Helper.startNotification
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.silentmapapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private val TAG = "MapsActivity"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var permission: Permissions
    private var geofenceToVisualize: LatLng? = null
    private var locationUpdateUserPermission = true
    private var savePowerModeUserPermission = false
    private var locationRequest = LocationRequest.create()

    private val creatingNewGeofence =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val nome = result.data?.getStringExtra("nome")
                val raggio = result.data?.getStringExtra("raggio")
                val colore = result.data?.getIntExtra("colore" , 0)
                val silenzioso = result.data?.getBooleanExtra("silenzioso" , false)
                val latLng: LatLng = result.data?.getParcelableExtra("latLng") !!

                geofenceHelper.addMarker(latLng,mMap,nome!!)
                geofenceHelper.addCircle(latLng , raggio.toString() , colore !!,mMap)

                val geofenceSettings = GeofenceSettings(
                    nome,
                    latLng.latitude ,
                    latLng.longitude ,
                    raggio.toString() ,
                    colore ,
                    silenzioso !!
                )

                Resources.listaGeofence.add(geofenceSettings)
                FileManager.saveToFile(Resources.listaGeofence , this)
                geofenceHelper.addGeofence(nome , latLng , raggio.toString(),geoFencingClient)
            }
        }

    object Geofence_Help{
        var Geofences: ArrayList<String> = arrayListOf()
        fun addGeofenceToRemove(id: String){
            Geofences.add(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.getParcelableExtra<LatLng>("coordinate") != null)
            geofenceToVisualize = intent.getParcelableExtra("coordinate")

        val sharedPreference = getSharedPreferences("PREFERENCES" , Context.MODE_PRIVATE)
        if (sharedPreference.contains("locationUpdates"))
            locationUpdateUserPermission = sharedPreference.getBoolean("locationUpdates" , false)
        if (sharedPreference.contains("savePower"))
            savePowerModeUserPermission = sharedPreference.getBoolean("savePower" , false)

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

        if(Geofence_Help.Geofences.isNotEmpty()) {
            geoFencingClient.removeGeofences(Geofence_Help.Geofences)
        }

        geofenceHelper.drawGeofences(mMap)

        if(geofenceToVisualize != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geofenceToVisualize !! , Resources.DEFAULT_ZOOM.toFloat()))
        else
            permission.getDeviceLocation()

        if (locationUpdateUserPermission){

            permission.enableUserLocation()

            if(savePowerModeUserPermission)
                locationRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            else locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY

            if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mMap.isMyLocationEnabled = true
        }
        else {
            Snackbar.make(findViewById(R.id.map), "La localizzazione é stata disattivata dall'utente." , 1000000000).show()
            mMap.uiSettings.isZoomControlsEnabled = false
        }
    }

    override fun onMapLongClick(latLng: LatLng) {

        val intent = Intent(applicationContext , GeofenceSetupActivity::class.java)
        intent.putExtra("latLng" , latLng)
        creatingNewGeofence.launch(intent)
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