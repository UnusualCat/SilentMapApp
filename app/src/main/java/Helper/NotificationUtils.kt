package Helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.silentmapapp.MapsActivity
import com.example.silentmapapp.R

private const val CHANNEL_ID = "GeofenceChannel"
private val CHANNEL_NAME="SilentMap"
private val CHANNEL_DESC="Desc"
private var notificationID = 55

fun startNotification(context: Context){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESC
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun createNotification(context: Context, NOTIFICATION_ID: Int): PendingIntent? {
    val contentIntent = Intent(context, MapsActivity::class.java)
    val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    } else {
        PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    return contentPendingIntent
}

fun sendGeofenceUnmuteNotification(context: Context, geofenceID: String) {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText("Disattivazione modalit?? silenziosa, marker ID: $geofenceID")
        .setSmallIcon(R.drawable.ic_volume_up)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(createNotification(context,notificationID))

    with(NotificationManagerCompat.from(context)) {
        notify(notificationID, builder.build())
    }
}

fun sendGeofenceMuteNotification(context: Context , geofenceID: String) {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText("Attivazione modalit?? silenziosa, marker ID: $geofenceID")
        .setSmallIcon(R.drawable.ic_no_volume)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(createNotification(context,notificationID))

    with(NotificationManagerCompat.from(context)) {
        notify(notificationID, builder.build())
    }
}