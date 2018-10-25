package com.example.kiril.softwaredesign

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var imeiTextView: TextView
    private lateinit var imeiButton: Button

    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.VersionTextView).apply{
            text = "${getString(R.string.app_version)}${BuildConfig.VERSION_NAME}"
        }

        imeiTextView = findViewById(R.id.IMEITextView)

        imeiButton = findViewById<Button>(R.id.IMEIButton)
        imeiButton.setOnClickListener(
                {requestReadPhoneStatePermissons()}
        )

        requestReadPhoneStatePermissons()

    }


    fun requestReadPhoneStatePermissons(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                //Todo fix
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // Display a SnackBar with an explanation and a button to trigger the request.
                //Todo snackbar after install dependencies
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imeiTextView.text = "${getString(R.string.imei_label)} ${getIMEI()}"
                    imeiButton.visibility = View.INVISIBLE // Todo mb replace by Gone. Invisible takes space. Gone dont take
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getIMEI() : String {
        try{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val IMEI = tm.getDeviceId() // Depricated
            return IMEI
        }
        catch (e: SecurityException){
            throw e
        }
    }
}
