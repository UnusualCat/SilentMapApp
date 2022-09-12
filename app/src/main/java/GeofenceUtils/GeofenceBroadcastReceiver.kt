package GeofenceUtils

import Helper.FileManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


private const val TAG = "BroadcastReceiver"
lateinit var audioManager: AudioManager

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_SHORT).show()
        audioManager = getSystemService(context,AudioManager::class.java) as AudioManager
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            val errorMessage =
                GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        when (val geofenceTransition = geofencingEvent?.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                var listSettings: List<GeofenceSettings> = FileManager.loadFromFile(context) as List<GeofenceSettings>
                for (a in geofencingEvent.triggeringGeofences!!){
                    for(setting in listSettings){
                        if(a.requestId==setting.geofenceID){
                            setting.EnableFavoriteMode(context)
                        }
                    }
                }
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                var listSettings: List<GeofenceSettings> = FileManager.loadFromFile(context) as List<GeofenceSettings>
                for (a in geofencingEvent.triggeringGeofences!!){
                    for(setting in listSettings){
                        if(a.requestId==setting.geofenceID){
                            setting.DisableFavoriteMode(context)
                        }
                    }
                }
            }
        }
    }
}
