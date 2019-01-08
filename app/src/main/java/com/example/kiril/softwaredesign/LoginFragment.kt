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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("email", email?.editText?.text.toString().trim())
        outState.putString("password", password?.editText?.text.toString().trim())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        setDataFromBundle(savedInstanceState)

        loginButton.setOnClickListener {
            toggleButtons()
            val email = email.editText?.text.toString().trim()
            val password = password.editText?.text.toString().trim()
            if (email.isNotBlank() && password.isNotBlank()){
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { auth ->
                            toggleButtons()
                            if (auth.isSuccessful)
                                (activity as AuthorizationActivity).startMainActivity()
                            else
                                Toast.makeText(context,auth.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
            } else {
                toggleButtons()
                Toast.makeText(context, getString(R.string.email_password_blank), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun toggleButtons() {
        registerButton.isEnabled = !registerButton.isEnabled
        loginButton.isEnabled = !loginButton.isEnabled
    }

    private fun setDataFromBundle(savedInstanceState: Bundle?){
        if (savedInstanceState == null)
            return

        val extractedEmail = savedInstanceState.getString("email")
        if (extractedEmail != null && !extractedEmail.equals("null"))
            email?.editText?.setText(extractedEmail)

        val extractedPassword = savedInstanceState.getString("password")
        if (extractedPassword != null && extractedPassword != "null")
            password?.editText?.setText(extractedPassword)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
