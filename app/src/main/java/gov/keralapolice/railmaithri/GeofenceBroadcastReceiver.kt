package gov.keralapolice.railmaithri;

import android.R
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

@SuppressLint("MissingPermission")
fun showNotification(context: Context, channelId: String, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT).apply { }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(gov.keralapolice.railmaithri.R.drawable.ic_search_location)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
                notify(1000, builder.build())
        }
}

class GeofenceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
                val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
                if (geofencingEvent!!.hasError()) {
                        val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                        Log.e("Railmaithri", errorMessage)
                        return
                }

                val geofenceID         = geofencingEvent.triggeringGeofences?.get(0)?.requestId
                val geofenceTransition = geofencingEvent.geofenceTransition
                if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                        Log.e("Railmaithri", "Entering watch zone")
                        showNotification(context!!, "Railmaithri", "Watch zone alert",
                                "You are in $geofenceID"
                        )
                } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
                        Log.e("Railmaithri", "Exiting watch zone")
                        showNotification(context!!, "Railmaithri", "Watch zone alert",
                                "You are in $geofenceID"
                        )
                }
        }
}

