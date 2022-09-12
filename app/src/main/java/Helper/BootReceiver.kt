package Helper

import GeofenceUtils.GeofenceHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingClient

class BootReceiver : BroadcastReceiver()
{
    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    override fun onReceive(context: Context , intent: Intent?)
    {
        if (intent != null)
        {
            if(intent.action.equals(Intent.ACTION_BOOT_COMPLETED)){
                Log.d("Boot 123","Boot effettuato")
                geoFencingClient=GeofencingClient(context)
                geofenceHelper = GeofenceHelper(context)
                geofenceHelper.loadGeofences(context,geoFencingClient)
            }
        }
    }
}