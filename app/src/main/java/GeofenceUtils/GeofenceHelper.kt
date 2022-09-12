package GeofenceUtils

import Helper.FileManager
import Helper.Resources
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.lang.Exception

class GeofenceHelper(base: Context?) : ContextWrapper(base) {

    private var TAG = "GeoFenceHelper"
    fun getGeoFencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeoFence(ID: String, LatLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(LatLng.latitude, LatLng.longitude, radius)
            .setRequestId(ID)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun loadGeofences(context: Context,geoFencingClient: GeofencingClient)
    {
        val file = File(Resources.Filepath , Resources.GEOFENCE_FILE_NAME)
        if (file.exists()) {
            Resources.listaGeofence = FileManager.loadFromFile(context) as ArrayList<GeofenceSettings>
            for (elemento in Resources.listaGeofence) {
                val latLng = LatLng(elemento.latitudine , elemento.longitudine)
                addGeofence(elemento.geofenceID , latLng , elemento.raggio,geoFencingClient)
            }
        }
    }

    fun drawGeofences(mMap:GoogleMap)
    {
        val file = File(Resources.Filepath, Resources.GEOFENCE_FILE_NAME)
        if (file.exists()) {
            Resources.listaGeofence = FileManager.loadFromFile(this) as ArrayList<GeofenceSettings>
            for (elemento in Resources.listaGeofence) {
                val latLng = LatLng(elemento.latitudine , elemento.longitudine)
                addMarker(latLng,mMap,elemento.geofenceID)
                addCircle(latLng , elemento.raggio , elemento.colore,mMap)
            }
        }
    }

    fun getErrorString(e: Exception): String? {

        if (e is ApiException) {
            val api: ApiException = e
            return when (api.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                else -> "UNKOWN CASE"
            }
        }
        return e.localizedMessage
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        return pendingIntent
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(geofenceID: String , LatLng: LatLng , radius: String,geoFencingClient: GeofencingClient)
    {
        val geofence = getGeoFence(
            geofenceID ,
            LatLng ,
            radius.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = getGeoFencingRequest(geofence)

        geoFencingClient.addGeofences(geofencingRequest , getPendingIntent())
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = getErrorString(e)
                Log.d(TAG , "onFailure: $errorMessage")
            }
    }

    fun addMarker(LatLng: LatLng,mMap: GoogleMap,title:String)
    {
        val markerOptions: MarkerOptions = MarkerOptions().position(LatLng).title(title).visible(true)
        mMap.addMarker(markerOptions)
    }

    fun addCircle(LatLng: LatLng , radius: String , colore: Int, mMap: GoogleMap)
    {
        val circleOptions = CircleOptions()
        circleOptions.center(LatLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(colore)
        circleOptions.fillColor(0)
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

}