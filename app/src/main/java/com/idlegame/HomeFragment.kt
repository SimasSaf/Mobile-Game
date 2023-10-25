package com.idlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bounceAnimation1 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        val bounceAnimation2 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        val bounceAnimation3 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        val bounceAnimation4 = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)

        initializeSingleplayerImageButton(view, bounceAnimation1, 0)
        initializeTeamImageButton(view, bounceAnimation2, 200)
        initializeInventoryButton(view, bounceAnimation3, 400)
        initializeMultiplayerButton(view, bounceAnimation4, 600)

        return view
    }

    private fun initializeSingleplayerImageButton(view: View, bounceAnimation: Animation, delay: Long) {
        val singleplayerImageButton = view.findViewById<ImageButton>(R.id.homeSingleplayerImageButton)
        bounceAnimation.startOffset = delay
        singleplayerImageButton.startAnimation(bounceAnimation)

        singleplayerImageButton.setOnClickListener {
            findNavController().navigate(R.id.singleplayerFragment)
        }
    }

    private fun initializeTeamImageButton(view: View, bounceAnimation: Animation, delay: Long) {
        val teamImageButton = view.findViewById<ImageButton>(R.id.homeTeamImageButton)
        bounceAnimation.startOffset = delay
        teamImageButton.startAnimation(bounceAnimation)
    }

    private fun initializeInventoryButton(view: View, bounceAnimation: Animation, delay: Long) {
        val inventoryImageButton = view.findViewById<ImageButton>(R.id.homeInventoryImageButton)
        bounceAnimation.startOffset = delay
        inventoryImageButton.startAnimation(bounceAnimation)
    }

    private fun initializeMultiplayerButton(view: View, bounceAnimation: Animation, delay: Long) {
//        val multiplayerImageButton = view.findViewById<ImageButton>(R.id.homeMultiplayerImageButton)
//        bounceAnimation.startOffset = delay
//        multiplayerImageButton.startAnimation(bounceAnimation)
    }
}
