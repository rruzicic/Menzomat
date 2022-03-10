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
        val cntDorucak = findViewById<EditText>(R.id.cntDorucak)
        val cntRucak = findViewById<EditText>(R.id.cntRucak)
        val cntVecera = findViewById<EditText>(R.id.cntVecera)

        val pref = getPreferences(Context.MODE_PRIVATE)

        val dorucak_cnt = pref.getInt("dorucak_cnt", 0)
        val rucak_cnt = pref.getInt("rucak_cnt", 0)
        val vecera_cnt = pref.getInt("vecera_cnt", 0)

        cntDorucak.setText(dorucak_cnt.toString())
        cntRucak.setText(rucak_cnt.toString())
        cntVecera.setText(vecera_cnt.toString())
    }
    override fun onPause() {
        super.onPause()

        // save the data for being used the next time
        val cntDorucak = findViewById<EditText>(R.id.cntDorucak)
        val cntRucak = findViewById<EditText>(R.id.cntRucak)
        val cntVecera = findViewById<EditText>(R.id.cntVecera)

        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putInt("dorucak_cnt", cntDorucak.getText().toString().toInt())
        editor.putInt("rucak_cnt", cntRucak.getText().toString().toInt())
        editor.putInt("vecera_cnt", cntVecera.getText().toString().toInt())

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