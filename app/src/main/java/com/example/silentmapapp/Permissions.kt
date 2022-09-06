package com.example.silentmapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class Permissions(var activity: MapsActivity , var context: Context , var mMap: GoogleMap , var fusedLocationProviderClient: FusedLocationProviderClient)
{
    val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002
    private val TAG="Permission"
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 15

    @SuppressLint("MissingPermission")
    fun getDeviceLocation()
    {
        try
        {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful)
                {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null)

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
        } catch (e: SecurityException)
        {
            Log.e("Exception: %s" , e.message , e)
        }
    }

    @SuppressLint("MissingPermission")
    fun enableUserLocation()
    {
        if (ContextCompat.checkSelfPermission(
                context ,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {

            if (Build.VERSION.SDK_INT >= 29)
            {
                enableBackgroundLocation()
            }

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity ,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        {
            ActivityCompat.requestPermissions(
                activity ,
                Array(1) { Manifest.permission.ACCESS_FINE_LOCATION } ,
                FINE_LOCATION_ACCESS_REQUEST_CODE
            )
        } else
        {
            ActivityCompat.requestPermissions(
                activity ,
                Array(1) { Manifest.permission.ACCESS_FINE_LOCATION } ,
                FINE_LOCATION_ACCESS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun enableBackgroundLocation()
    {
        if (ContextCompat.checkSelfPermission(
                context ,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            return
        } else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity ,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
            {
                if (Build.VERSION.SDK_INT >= 29)
                {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION) ,
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                }
            } else
            {
                ActivityCompat.requestPermissions(
                    activity ,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION) ,
                    BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

}