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
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        loginButton.setOnClickListener {
            disableButtons()
            val email = email.editText?.text.toString().trim()
            val password = password.editText?.text.toString().trim()
            if (email.isNotBlank() && password.isNotBlank()){
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { auth ->
                            enableButtons()
                            if (auth.isSuccessful){
                                findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                            }
                            else
                                Toast.makeText(
                                    context,
                                    auth.exception.toString(),
                                    Toast.LENGTH_SHORT).show()
                        }
            } else {
                enableButtons()
                Toast.makeText(
                        context,
                        getString(R.string.email_password_blank),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enableButtons()
    {
        registerButton.isEnabled = true
        loginButton.isEnabled = true
    }

    private fun disableButtons()
    {
        registerButton.isEnabled = false
        loginButton.isEnabled = false
    }

    private fun toggleButtons() {
        registerButton.isEnabled = !registerButton.isEnabled
        loginButton.isEnabled = !loginButton.isEnabled
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
