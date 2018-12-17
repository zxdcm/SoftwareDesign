package com.example.kiril.softwaredesign

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*


class ProfileEditFragment : Fragment() {

    private var photoChanged = false
    private var userProfile : UserProfile? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        photoChanged = false
        if (currentUser != null) {
            val userProfileListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userProfile = dataSnapshot.getValue(UserProfile::class.java)
                    if (userProfile != null) {
                        profileFirstName?.editText?.setText(userProfile?.firstName)
                        profileLastName?.editText?.setText(userProfile?.lastName)
                        profilePhone?.editText?.setText(userProfile?.phone)
                        if (!userProfile?.image.isNullOrBlank()) {
                            var tempFile = File.createTempFile("img", "png");
                            userProfile?.image?.let { imageUri ->
                                FirebaseStorage.getInstance()
                                        .getReference(imageUri)
                                        .getFile(tempFile)
                                        .addOnSuccessListener {
                                            var bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                                            profileImage?.setImageBitmap(bitmap)
                                        }
                            }
                        }
                    }
                    showInputs()
                }
                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            FirebaseDatabase.getInstance().reference.child(currentUser.uid).addValueEventListener(userProfileListener)

        }

        profileEditImageButton.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this.context!!)
            val pictureDialogItems = arrayOf(getString(R.string.select_photo_from_gallery), getString(R.string.capture_photo))
            pictureDialog.setItems(pictureDialogItems)
            { _, which ->
                when (which) {
                    0 -> getPhotoFromGallery()
                    1 -> getPhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
        saveButton.setOnClickListener {
            hideInputs()
            var firstName = profileFirstName.editText?.text.toString().trim()
            var lastName = profileLastName.editText?.text.toString().trim()
            var phone = profilePhone.editText?.text.toString().trim()
            if (userProfile == null)
                userProfile = UserProfile(firstName = firstName, lastName = lastName, phone = phone)
            else {
                userProfile?.firstName = firstName
                userProfile?.lastName = lastName
                userProfile?.phone = phone
            }

            if (photoChanged) {
                val storageReference = FirebaseStorage.getInstance().reference
                val imageBytes = imageViewToByteArray(profileImage)
                val imageReference = storageReference
                        .child(currentUser!!.uid)
                        .child("profilePhotos/${UUID.nameUUIDFromBytes(
                                imageBytes)}"
                        )
                imageReference.putBytes(imageBytes).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userProfile!!.image = imageReference.path
                        updateProfile(currentUser, userProfile)
                    }
                    else {
                        showInputs()
                    }
                }
            }
            else {
                updateProfile(currentUser, userProfile)
            }
        }
    }

    private fun updateProfile(user : FirebaseUser?, userProfile : UserProfile?) {
        var databaseReference = FirebaseDatabase.getInstance().reference
        if (user != null) {
            databaseReference.child(user.uid).setValue(userProfile).addOnCompleteListener {
                if (it.isSuccessful)
                    findNavController().popBackStack()
                else
                    showInputs()
            }
        }
    }

    private fun imageViewToByteArray(imageView : ImageView) : ByteArray{
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }

    private val CameraRequest = 1
    private val GalleryRequest = 2
    private val PermissionRequestCamera = 3

    private fun getPhotoFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        this.startActivityForResult(galleryIntent, GalleryRequest)
    }

    private fun getPhotoFromCamera(){
        if (this.context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
        else
            openCamera()

    }

    private fun requestCameraPermission(){
        requestPermissions(arrayOf(Manifest.permission.CAMERA), PermissionRequestCamera)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionRequestCamera -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK)
            return
        when (requestCode) {
            CameraRequest -> {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as Bitmap
                photoChanged = true
                profileImage.setImageBitmap(imageBitmap)
            }
            GalleryRequest -> {
                val selectedImage = data?.data
                photoChanged = true
                profileImage.setImageURI(selectedImage)
            }
        }
    }

    private fun openCamera(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(takePictureIntent, CameraRequest)
    }

    private fun showInputs(){
        profileFirstName?.visibility = View.VISIBLE
        profileLastName?.visibility = View.VISIBLE
        profilePhone?.visibility = View.VISIBLE
    }

    private fun hideInputs(){
        profileFirstName?.visibility = View.INVISIBLE
        profileLastName?.visibility = View.INVISIBLE
        profilePhone?.visibility = View.INVISIBLE
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
