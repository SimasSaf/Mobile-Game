package com.idlegame

import LoginViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.idlegame.databinding.FragmentLoginBinding
import com.idlegame.model.LoginModel
import com.idlegame.viewModel.ImageViewModel
import com.idlegame.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginModel(requireContext()))
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageViewModel: ImageViewModel

    private val tag = "LoginFragment" // Tag for logging


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

//        PopulateDAO().populateDAO();

        loadImages()

        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(R.id.homeFragment)
            } else {
                initializeLoginInButton()
                initializeRegisterButton()
                initializeGoogleSignInButton()
                observeViewModel()
            }
        }

        return binding.root
    }

    private fun initializeLoginInButton() {
        binding.loginLoginButton.setOnClickListener {
            val email = binding.emailLoginField.editText?.text.toString()
            val password = binding.passwordLoginField.editText?.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.loginWithEmail(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeRegisterButton() {
        binding.loginRegisterButton.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun initializeGoogleSignInButton() {
        binding.googleLogInButton.setOnClickListener {
            val loginInIntent = viewModel.getLoginIntent()
            startActivityForResult(loginInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            viewModel.handleLoginResult(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { firebaseUser ->
            firebaseUser?.let {
                Log.i(tag, "Google Login-In successful for user: ${it.displayName}")
                findNavController().navigate(R.id.homeFragment)
            }
        }

        viewModel.loginError.observe(viewLifecycleOwner) { exception ->
            exception?.let {
                Log.e(tag, "Google Login-In failed", it)
                Toast.makeText(context, "Login-In failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadImages() {
        imageViewModel.imageURLs.observe(viewLifecycleOwner) { urls ->
            Glide.with(this)
                .load(urls.googleIcon)
                .into(binding.googleLogInButton)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(tag, "View is being destroyed")
        _binding = null
    }

    companion object {
        private const val GOOGLE_SIGN_IN = 100
    }
}