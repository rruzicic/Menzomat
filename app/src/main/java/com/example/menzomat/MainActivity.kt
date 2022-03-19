package com.example.menzomat

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

const val TAG = "MenzomatFence"
lateinit var cntBreakfast: TextView
lateinit var cntLunch: TextView
lateinit var cntDinner: TextView


// class is not abstract because the app was throwing Instantiation Exception
class MainActivity : AppCompatActivity() {

    lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<Geofence>()
    // TODO: add plus 1 and minus 1 button for each meal
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cntBreakfast = findViewById<EditText>(R.id.cntBreakfast)
        cntLunch = findViewById<EditText>(R.id.cntLunch)
        cntDinner = findViewById<EditText>(R.id.cntDinner)
        // requesting permissions
        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        // retrieve data upon reopening the app
        val pref = getPreferences(Context.MODE_PRIVATE)

        val breakfastNum = pref.getInt("breakfast_cnt", 0)
        val lunchNum = pref.getInt("lunch_cnt", 0)
        val dinnerNum = pref.getInt("dinner_cnt", 0)

        cntBreakfast.setText(breakfastNum.toString())
        cntLunch.setText(lunchNum.toString())
        cntDinner.setText(dinnerNum.toString())

        geofencingClient = LocationServices.getGeofencingClient(this)

        geofenceList.add(Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId("MenzaFence")

            // Set the circular region of this geofence.
            .setCircularRegion(45.24609692364816, 19.848956664010046, 50f)

            // Set the expiration duration of the geofence. This geofence gets automatically removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            // Set the transition types of interest. Alerts are only generated for these transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)

            .setLoiteringDelay(300000) // 5 minutes
            // Create the geofence.
            .build())

        geofencingClient?.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                val toast = Toast.makeText(getApplicationContext(), "Geofence successfully added!", Toast.LENGTH_LONG)
                toast.show()
                Log.i(TAG, "Geofence successfully added!")
            }
            addOnFailureListener {
                Log.i(TAG, "Failed to add geofence!")
            }
        }


    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(geofenceList)
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onPause() {
        super.onPause()

        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putInt("breakfast_cnt", cntBreakfast.getText().toString().toInt())
        editor.putInt("lunch_cnt", cntLunch.getText().toString().toInt())
        editor.putInt("dinner_cnt", cntDinner.getText().toString().toInt())

        editor.apply()
    }

    // requesting permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}
