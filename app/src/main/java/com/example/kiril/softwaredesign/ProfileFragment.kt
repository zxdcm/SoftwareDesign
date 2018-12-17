package com.example.kiril.softwaredesign


import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email = FirebaseAuth.getInstance().currentUser?.email
        profileEmail.text = email
        var currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userProfileListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                    if (userProfile != null) {
                        profileFirstName?.text = userProfile.firstName
                        profileLastName?.text = userProfile.lastName
                        profilePhone?.text = userProfile.phone
                        if (!userProfile.image.isNullOrBlank()) {
                            var tempFile = File.createTempFile("img", "png");
                            FirebaseStorage.getInstance()
                                    .getReference(userProfile.image!!)
                                    .getFile(tempFile)
                                    .addOnSuccessListener {
                                        var bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                                        profileImage?.setImageBitmap(bitmap)
                                    }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    //Todo Fix
                }
            }
            FirebaseDatabase.getInstance().reference.child(currentUser.uid)
                    .addValueEventListener(userProfileListener)

        }

        profileEditButton.setOnClickListener {
            // TODO: FIX
            //findNavController().popBackStack()
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).startAuthorizationActivity()
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
