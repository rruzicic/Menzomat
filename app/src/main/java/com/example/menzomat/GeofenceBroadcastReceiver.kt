package com.example.menzomat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent) {
        val toast = Toast.makeText(context, "AAA", Toast.LENGTH_LONG)
        toast.show()

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            Log.i(TAG, "TRANSITION OCCURRED: " + geofenceTransition)
            val toast = Toast.makeText(context, "YOU DWELLED!", Toast.LENGTH_LONG)
            toast.show()

            var currentDateTime = LocalDateTime.now()
            var formatedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            val timeList = formatedTime.split(":")
            var hrs = timeList[0].toInt()
            var mins = timeList[1].toInt()

            if((hrs >= 7 && hrs <= 9) || (hrs == 10 && mins < 15)){
                var breakfastNum = cntBreakfast.text.toString().toInt()
                breakfastNum--
                cntBreakfast.setText(breakfastNum.toString())
            }
            if((hrs >= 11 && hrs <= 16) || (hrs == 10 && mins >= 15) || (hrs == 16 && mins <= 15)) {
                var lunchNum = cntLunch.text.toString().toInt()
                lunchNum--
                cntLunch.setText(lunchNum.toString())
            }
            if((hrs >= 17 && hrs <= 20) || (hrs == 17 && mins <= 15)) {
                var dinnerNum = cntDinner.text.toString().toInt()
                dinnerNum--
                cntDinner.setText(dinnerNum.toString())
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
