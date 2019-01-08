package com.example.kiril.softwaredesign

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*



class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        profileEmail.text = currentUser?.email
        val userProfileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                if (userProfile != null) {
                    profileFirstName?.text = userProfile.firstName
                    profileLastName?.text = userProfile.lastName
                    val fullName = """${userProfile.firstName} ${userProfile.lastName}"""
                    if (activity != null)
                        (activity as MainActivity).getNameTextViewFromNavView().text = fullName
                    profilePhone?.text = userProfile.phone
                    if (!userProfile.image.isBlank()) {
                        val imageReference = FirebaseStorage.getInstance()
                                .getReference(userProfile.image)
                        if (profileImage != null) {
                            GlideApp.with(this@ProfileFragment)
                                    .load(imageReference)
                                    .into(profileImage)
                            GlideApp.with(this@ProfileFragment)
                                    .load(imageReference)
                                    .into((activity as MainActivity).getProfileImageViewFromNavView())
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseDatabase.getInstance().reference.child(currentUser?.uid.toString()).addValueEventListener(userProfileListener)

        profileEditButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        }
        logoutButton.setOnClickListener {
            (activity as MainActivity).cleanArticlesCache()
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).startAuthActivity()
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}

