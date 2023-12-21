package com.idlegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.idlegame.databinding.FragmentSingleplayerBinding

class SingleplayerFragment : Fragment() {

    private var _binding: FragmentSingleplayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSingleplayerBinding.inflate(inflater, container, false)



        return binding.root
    }

    private fun initializeAttack(){
        binding.attackQuickSlash.setOnClickListener {
            binding.friendChar.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.uppercut))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
