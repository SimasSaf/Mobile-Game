package com.idlegame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        initializeSignInButton(view);
        initializeRegisterButton(view);

        return view;
    }

    private fun initializeSignInButton(view: View)
    {
        val signInButton = view.findViewById<Button>(R.id.loginLoginButton);

        signInButton?.setOnClickListener{
            findNavController().navigate(R.id.homeFragment);
        }
    }

    private fun initializeRegisterButton(view: View)
    {
        val registerButton = view.findViewById<Button>(R.id.loginRegisterButton);

        registerButton?.setOnClickListener{
            findNavController().navigate(R.id.registerFragment);
        }
    }
}