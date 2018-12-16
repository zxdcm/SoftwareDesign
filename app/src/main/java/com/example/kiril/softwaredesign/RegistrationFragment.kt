package com.example.kiril.softwaredesign

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
        registerButton.setOnClickListener {
            toggleButtons()
            val email = email.editText?.text.toString().trim()
            val password = password.editText?.text.toString().trim()
            val password_confirmation = password_confirmation.editText?.text.toString().trim()
            if (email.isNotBlank() && password == password_confirmation){
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { auth ->
                            toggleButtons()
                            if (auth.isSuccessful)
                                findNavController().navigate(R.id.action_registrationFragment_to_profileEditFragment)
                            else
                                Toast.makeText(context, auth.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
            } else {
                toggleButtons()
                Toast.makeText(context, getString(R.string.registration_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleButtons() {
        registerButton.isEnabled = !registerButton.isEnabled
        loginButton.isEnabled = !loginButton.isEnabled
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
