package com.idlegame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.idlegame.databinding.FragmentHomeBinding
import com.idlegame.model.LoginModel
import com.idlegame.viewModel.ImageViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginModel: LoginModel
    private lateinit var imageViewModel: ImageViewModel
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        loginModel = LoginModel(requireContext())
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        setupBackPressHandler()
        setupAnimationsAndClickListeners()
        loadImages()

        return binding.root
    }

    private fun setupAnimationsAndClickListeners() {
        val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)

        with(binding) {
            initializeButton(homeSingleplayerImageButton, bounceAnimation, 0) {
                findNavController().navigate(R.id.singleplayerFragment)
            }
            initializeButton(homeTeamImageButton, bounceAnimation, 200) {
                // Inactive for now
            }
            initializeButton(homeInventoryImageButton, bounceAnimation, 400) {
                findNavController().navigate(R.id.inventoryFragment)
            }
            initializeButton(homeMultiplayerImageButton, bounceAnimation, 600) {
                // Will Be Inactive for a while
            }

            logoutImageButton.setOnClickListener {
                loginModel.logOutUser { success ->
                    if (success) {
                        findNavController().navigate(R.id.loginFragment)
                    } else {
                        Toast.makeText(context, "Logout failed, please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initializeButton(button: View, animation: Animation, delay: Long, onClick: () -> Unit) {
        animation.startOffset = delay  // Set startOffset on the animation
        button.apply {
            startAnimation(animation)
            setOnClickListener { onClick() }
        }
    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish() // Exit the app
                    return
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                // Reset the flag after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun loadImages() {
        imageViewModel.imageURLs.observe(viewLifecycleOwner) { urls ->
            Glide.with(this)
                .load(urls.registerBackground)
                .into(binding.homeBackgroundImage)
            Glide.with(this)
                .load(urls.homeSingleplayerImageButton)
                .into(binding.homeSingleplayerImageButton)
            Glide.with(this)
                .load(urls.homeInventoryImageButton)
                .into(binding.homeInventoryImageButton)
            Glide.with(this)
                .load(urls.homeTeamImageButton)
                .into(binding.homeTeamImageButton)
            Glide.with(this)
                .load(urls.homeMultiplayerImageButton)
                .into(binding.homeMultiplayerImageButton)
            Glide.with(this)
                .load(urls.muteImageButton)
                .into(binding.muteImageButton)
            Glide.with(this)
                .load(urls.logoutImageButton)
                .into(binding.logoutImageButton)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
