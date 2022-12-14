package GeofenceUtils

import Helper.sendGeofenceMuteNotification
import Helper.sendGeofenceUnmuteNotification
import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat
import java.io.Serializable

data class GeofenceSettings(val geofenceID: String, val latitudine: Double, val longitudine: Double, val raggio: String, val colore: Int, val silentMode: Boolean): Serializable {

    fun EnableFavoriteMode(context: Context)
    {
        audioManager = ContextCompat.getSystemService(
            context,
            AudioManager::class.java
        ) as AudioManager

        if (silentMode)
        {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING , AudioManager.ADJUST_MUTE , 0)
            sendGeofenceMuteNotification(context,geofenceID)
        } else
        {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_RING ,
                AudioManager.ADJUST_UNMUTE ,
                0
            )
            sendGeofenceUnmuteNotification(context,geofenceID)
        }
    }

    fun DisableFavoriteMode(context: Context)
    {
        audioManager = ContextCompat.getSystemService(
            context,
            AudioManager::class.java
        ) as AudioManager

        if (!silentMode)
        {
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING , AudioManager.ADJUST_MUTE , 0)
            sendGeofenceMuteNotification(context,geofenceID)
        } else
        {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_RING ,
                AudioManager.ADJUST_UNMUTE ,
                0
            )
            sendGeofenceUnmuteNotification(context,geofenceID)
        }
    }

    override fun toString(): String {

        var stringa = "Nome: '$geofenceID'\nRaggio: '$raggio'm\nModalitá: "

        stringa += if(silentMode)
            "Silenzioso"
        else
            "Suoneria"

        return stringa
    }
}