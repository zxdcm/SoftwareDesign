package com.example.kiril.softwaredesign

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_change_rss.*


class ChangeRssFragment : Fragment() {
    private var userProfile : UserProfile? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_rss, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("rss", rssSourceTextView?.editText?.text.toString().trim())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userProfileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile::class.java)
                if (userProfile != null) {
                    if (!userProfile?.rssSource.isNullOrBlank()) {
                        rssSourceTextView?.editText?.setText(userProfile?.rssSource)
                    }
                }
                if (savedInstanceState != null) {
                    rssSourceTextView?.editText?.setText(savedInstanceState.getString("rss"))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        saveButton.setOnClickListener {
            saveButton.isEnabled = false
            val rssUrl = rssSourceTextView.editText?.text.toString()
            if (rssUrl.isBlank()){
                Toast.makeText(context, "RSS url cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (!URLUtil.isValidUrl(rssUrl)){
                Toast.makeText(context, "RSS url is invalid", Toast.LENGTH_SHORT).show()
            } else {
                if (userProfile == null) {
                    userProfile = UserProfile(rssSource = rssUrl)
                } else {
                    userProfile?.rssSource = rssUrl
                }
                updateProfile(currentUser, userProfile)
                (activity as MainActivity).cleanArticlesCache()
            }
            saveButton.isEnabled = true
        }
        FirebaseDatabase.getInstance().reference.child(currentUser?.uid.toString()).addValueEventListener(userProfileListener)
    }

    private fun updateProfile(user : FirebaseUser?, userProfile : UserProfile?) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        if (user != null) {
            databaseReference.child(user.uid).setValue(userProfile).addOnCompleteListener {
                if (it.isSuccessful)
                    findNavController().popBackStack()
            }
        }
    }
}
