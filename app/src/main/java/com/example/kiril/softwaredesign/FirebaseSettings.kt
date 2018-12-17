package com.example.kiril.softwaredesign

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

public class FirebaseSettings : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}