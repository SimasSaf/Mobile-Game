package com.idlegame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.idlegame.databinding.FragmentRegisterBinding
import com.idlegame.viewModel.RegisterViewModel

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val tag = "RegisterFragment" // Tag for logging



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        initializeCancelButton()
        initializeRegisterButton()

        observeViewModel()

        return binding.root
    }

    private fun initializeCancelButton() {
        binding.registerCancelButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun initializeRegisterButton() {
        binding.registerRegisterButton.setOnClickListener {
            val email = binding.registerEmailField.editText?.text.toString()
            val password1 = binding.registerPasswordField1.editText?.text.toString()
            val password2 = binding.registerPasswordField2.editText?.text.toString()

            viewModel.registerUser(email, password1, password2)
        }
    }

    private fun observeViewModel() {
        viewModel.registrationStatus.observe(viewLifecycleOwner) { statusMessage ->
            Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
            if (statusMessage == "Registration successful") {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(tag, "View is being destroyed")
        _binding = null
    }

}
