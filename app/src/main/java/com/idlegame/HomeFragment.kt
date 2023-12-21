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
import androidx.navigation.fragment.findNavController
import com.idlegame.databinding.FragmentHomeBinding
import com.idlegame.model.GoogleSignInModel
import com.idlegame.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInModel: GoogleSignInModel
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        googleSignInModel = GoogleSignInModel(requireContext())
        setupBackPressHandler()
        setupAnimationsAndClickListeners()

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
                googleSignInModel.logOutUser { success ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
