package com.example.menzomat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {


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