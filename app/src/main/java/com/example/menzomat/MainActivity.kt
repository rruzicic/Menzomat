package com.example.menzomat

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

const val TAG = "MenzomatFence"

// class is not abstract because the app was throwing Instantiation Exception
class MainActivity : AppCompatActivity() {

    lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<Geofence>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val cntDorucak = findViewById<EditText>(R.id.cntBreakfast)
        val cntRucak = findViewById<EditText>(R.id.cntLunch)
        val cntVecera = findViewById<EditText>(R.id.cntDinner)

        val pref = getPreferences(Context.MODE_PRIVATE)

        val breakfastNum = pref.getInt("dorucak_cnt", 0)
        val lunchNum = pref.getInt("rucak_cnt", 0)
        val dinnerNum = pref.getInt("vecera_cnt", 0)

        cntDorucak.setText(breakfastNum.toString())
        cntRucak.setText(lunchNum.toString())
        cntVecera.setText(dinnerNum.toString())

        //val locations = arrayOf(Location("tgh", 58.798233, 11.123959))
        // MENZA: 45.24609692364816, 19.848956664010046
        // TOCIONICA: 45.2425682891253, 19.84705162541752
        geofencingClient = LocationServices.getGeofencingClient(this)

        geofenceList.add(Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId("MenzaFence")

            // Set the circular region of this geofence.
            .setCircularRegion(45.24609692364816, 19.848956664010046, 150f)

            // Set the expiration duration of the geofence. This geofence gets automatically removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

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
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)  // changed from INITIAL_TRIGGER_ENTER
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

        // save the data for being used the next time
        val cntBreakfast = findViewById<EditText>(R.id.cntBreakfast)
        val cntLunch = findViewById<EditText>(R.id.cntLunch)
        val cntDinner = findViewById<EditText>(R.id.cntDinner)

        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putInt("dorucak_cnt", cntBreakfast.getText().toString().toInt())
        editor.putInt("rucak_cnt", cntLunch.getText().toString().toInt())
        editor.putInt("vecera_cnt", cntDinner.getText().toString().toInt())

        editor.commit()
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
