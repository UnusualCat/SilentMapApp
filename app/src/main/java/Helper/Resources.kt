package Helper

import GeofenceUtils.GeofenceSettings

class Resources {
    companion object{
        val Filepath="/data/data/com.example.silentmapapp/files"
        val GEOFENCE_FILE_NAME = "listaGeofence.data"
        var listaGeofence: ArrayList<GeofenceSettings> = arrayListOf()
        val DEFAULT_ZOOM = 15
    }
}