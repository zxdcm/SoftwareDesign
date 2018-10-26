package com.example.kiril.softwaredesign

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var imeiTextView: TextView
    private lateinit var imeiButton: Button
    private lateinit var rootView: View

    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.VersionTextView).apply{
            text = "${getString(R.string.app_version)} ${BuildConfig.VERSION_NAME}"
        }

        imeiTextView = findViewById(R.id.IMEITextView)
        rootView = findViewById(R.id.root)

        imeiButton = findViewById<Button>(R.id.IMEIButton)
        imeiButton.setOnClickListener{requestReadPhoneStatePermissons()}

        if (getResources().getBoolean(R.bool.portrait_constraint)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

        requestReadPhoneStatePermissons()


    }


    fun requestReadPhoneStatePermissons(){

        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {

                Snackbar.make(rootView, getString(R.string.readPhoneStatePermissonsMessage), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.submitButton), View.OnClickListener{
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                        })
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
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
                    // permission was granted, yay! Do the work
                    imeiTextView.text = "${getString(R.string.imei_label)} ${getIMEI()}"
                    imeiButton.visibility = View.GONE

                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.READ_PHONE_STATE)){
                        Toast.makeText(this, getString(R.string.readPhoneStatePermissonsRationalate),
                                Toast.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(rootView, getString(R.string.readPhoneStatePermissonsOnDontShowAgain),
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.settingsButton), {OpenSettings()})
                                .show()
                    }                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    private fun OpenSettings(){
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"))
        startActivityForResult(appSettingsIntent, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
        recreate()
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
