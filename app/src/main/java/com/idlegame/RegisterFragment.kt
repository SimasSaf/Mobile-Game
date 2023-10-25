package com.idlegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        initializeCancelButton(view);
        initializeRegisterButton(view);

        return view;
    }

    private fun initializeCancelButton(view: View)
    {
        val cancelButton = view.findViewById<Button>(R.id.registerCancelButton);

        cancelButton?.setOnClickListener{
            findNavController().navigate(R.id.loginFragment);
        }
    }

    private fun initializeRegisterButton(view: View)
    {
        val registerButton = view.findViewById<Button>(R.id.registerRegisterButton);

        registerButton?.setOnClickListener{
            findNavController().navigate(R.id.loginFragment);
        }
    }
}