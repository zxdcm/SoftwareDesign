package com.example.kiril.softwaredesign

import android.content.Intent
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import android.Manifest
import android.annotation.SuppressLint
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {

    private lateinit var imeiTextView: TextView
    private lateinit var imeiButton: Button
    private lateinit var rootView: View
    private lateinit var versionTextView: TextView

    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        if (getResources().getBoolean(R.bool.portrait_constraint))
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (savedInstanceState != null){
            val savedImei = savedInstanceState.getString("IMEI")

            if (savedImei.isNullOrEmpty())        // check does the permissions
                requestReadPhoneStatePermissons() // were granted after app closing or folding
            else
                imeiTextView.text = savedInstanceState.getString("IMEI")

            versionTextView.text = savedInstanceState.getString("VERSION")

            return
        }

        versionTextView = VersionTextView.apply {
            text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        }

        imeiTextView = IMEITextView
        rootView = imeiTextView

        imeiButton = IMEIButton
        imeiButton.setOnClickListener { requestReadPhoneStatePermissons() }

        requestReadPhoneStatePermissons()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString("IMEI", imeiTextView.text.toString())
        outState?.putString("VERSION", imeiTextView.text.toString())
    }



    private fun requestReadPhoneStatePermissons(){

        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {

                Snackbar.make(rootView, getString(R.string.readPhoneStatePermissonsMessage), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.submitButton), {
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                        })
                        .show()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
            }
        } else {
            setIMEIView()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the work
                    setIMEIView()
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
    }

    private fun setIMEIView(){
        imeiTextView.text = getString(R.string.imei_label, getIMEI())
        imeiButton.visibility = View.GONE

    }

    @SuppressLint("HardwareIds")
    private fun getIMEI() : String {
        try{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val IMEI = tm.deviceId
            return IMEI
        }
        catch (e: SecurityException){
            throw e
        }
    }
}
