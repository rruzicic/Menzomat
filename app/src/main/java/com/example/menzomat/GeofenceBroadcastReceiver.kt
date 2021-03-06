package com.example.menzomat

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    val channelId = "12345"
    val description = "Menzomat"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition



        // setting up the notification
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, LauncherActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            if (context != null) {
                builder = Notification.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground))
                    .setContentIntent(pendingIntent)
            }
        }


        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            Log.i(TAG, "TRANSITION OCCURRED: " + geofenceTransition)
            val toast = Toast.makeText(context, "YOU DWELLED!", Toast.LENGTH_LONG)
            toast.show()

            // getting the time
            val currentDateTime = LocalDateTime.now()
            val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val timeList = formattedTime.split(":")
            val hrs = timeList[0].toInt()
            val mins = timeList[1].toInt()

            // checking the time so that we could determine the meal type
            // I've extended the actual time window for the meal because the user might stay after the working hours
            // TODO: offer the user to revoke the change in meals
            // TODO: add exceptions for saturday and sunday
            if((hrs >= 7 && hrs <= 9) || (hrs == 10 && mins < 15)){
                var breakfastNum = cntBreakfastG.text.toString().toInt()
                breakfastNum--
                cntBreakfastG.setText(breakfastNum.toString())

                builder
                    .setContentTitle("Doru??ak")
                    .setContentText("Upravo Vam je skinut jedan doru??ak!")

                notificationManager.notify(12345, builder.build())
            }
            if((hrs >= 11 && hrs <= 15) || (hrs == 10 && mins >= 15) || (hrs == 15 && mins <= 15)) {
                var lunchNum = cntLunchG.text.toString().toInt()
                lunchNum--
                cntLunchG.setText(lunchNum.toString())

                builder
                    .setContentTitle("Ru??ak")
                    .setContentText("Upravo Vam je skinut jedan ru??ak!")

                notificationManager.notify(12345, builder.build())
            }
            if((hrs >= 17 && hrs <= 20) || (hrs == 17 && mins <= 15)) {
                var dinnerNum = cntDinnerG.text.toString().toInt()
                dinnerNum--
                cntDinnerG.setText(dinnerNum.toString())

                builder
                    .setContentTitle("Ve??era")
                    .setContentText("Upravo Vam je skinuta jedna ve??era!")

                notificationManager.notify(12345, builder.build())
            }

        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "TRANSITION OCCURRED: " + geofenceTransition)
            val toast = Toast.makeText(context, "YOU EXITED!", Toast.LENGTH_LONG)
            toast.show()
        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "TRANSITION OCCURRED: " + geofenceTransition)
            val toast = Toast.makeText(context, "YOU ENTERED!", Toast.LENGTH_LONG)
            toast.show()
        } else {
            // Log the error.
            Log.e(TAG, "Error")
        }
    }
}
