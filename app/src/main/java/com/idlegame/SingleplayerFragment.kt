package com.idlegame

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.auth.FirebaseAuth
import com.idlegame.databinding.FragmentSingleplayerBinding
import com.idlegame.databinding.PopupBattleOutcomeBinding
import com.idlegame.objects.Battle
import com.idlegame.viewModel.ImageViewModel
import com.idlegame.viewModel.SingleplayerViewModel

class SingleplayerFragment : Fragment() {

    private var _binding: FragmentSingleplayerBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper()) // Initialize the handler in your Fragment or Activity

    // RequestOptions for Glide
    private val glideOptions = RequestOptions()
        .placeholder(R.drawable.app_icon) // Default image while loading
        .error(R.drawable.app_icon)       // Default image in case of error

    private lateinit var imageViewModel: ImageViewModel
    private lateinit var singleplayerViewModel: SingleplayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleplayerBinding.inflate(inflater, container, false)

        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        singleplayerViewModel = ViewModelProvider(this)[SingleplayerViewModel::class.java]

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        singleplayerViewModel.checkOrCreateBattle(userId)

        singleplayerViewModel.battleData.observe(viewLifecycleOwner) { battle ->
            updateHpDisplay(battle)
            loadBattleImages(battle)
            loadAttacks(battle)
        }

        singleplayerViewModel.battleOutcome.observe(viewLifecycleOwner) { outcome ->
            if (outcome != null) {
                showBattleOutcomePopup(outcome)
            }
        }

        loadImages()

        return binding.root
    }

    private fun updateHpDisplay(battle: Battle) {
        binding.battleFriendHp.text = "HP: ${battle.friendHp}/${battle.friendHpMax}"
        binding.battleEnemyHp.text = "HP: ${battle.enemyHp}/${battle.enemyHpMax}"
    }

    private fun showBattleOutcomePopup(outcome: String) {
        Log.d("SingleplayerFragment", "showBattleOutcomePopup called with outcome: $outcome")

        val popupBinding = PopupBattleOutcomeBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())

        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 35f
        }

        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        dialog.setContentView(popupBinding.root)

        val currentBattle = singleplayerViewModel.battleData.value

        // Set the text for the popup
        popupBinding.popupItemName.text =  if (outcome == "victory") "Item: ${currentBattle?.battleReward?.name}" else ""
        popupBinding.popupText.text =
            if (outcome == "victory") "Congratulations you have won!" else "Sorry, you lost"

        if (currentBattle != null && outcome == "victory") {
            Log.d("SingleplayerFragment", "Loading battle reward image from URL: ${currentBattle.battleReward}")
            Glide.with(requireContext())
                .load(currentBattle.battleReward.image)
                .into(popupBinding.popupItem)
        } else {
            Log.d("SingleplayerFragment", "Current battle is null")
        }

        // Setup the button click listener
        popupBinding.popupButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }

        // Display the dialog
        dialog.show()
        Log.d("SingleplayerFragment", "Dialog shown")
    }

    private fun loadAttacks(battle: Battle) {
        val actionMoves = battle.actionMoveListFriend
        val attackAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.uppercut)

        if (actionMoves.size == 4) {
            binding.actionMove1.text = actionMoves[0].name
            binding.actionMove1.setOnClickListener {
                binding.friendChar.startAnimation(attackAnimation)
                handler.postDelayed({
                    Glide.with(requireContext())
                        .load(actionMoves[0].image)
                        .apply(glideOptions)
                        .into(binding.onEnemy)
                    Glide.with(requireContext())
                        .load(battle.actionMoveListEnemy[0])
                        .apply(glideOptions)
                        .into(binding.onFriend)

                    handler.postDelayed({
                        binding.onEnemy.setImageDrawable(null) // Clear the image after 0.7 seconds
                        binding.onFriend.setImageDrawable(null) // Clear the image after 0.7 seconds
                    }, 700)
                }, 500) // Load the image after a 0.5-second delay
                singleplayerViewModel.performAttack(0)
            }

            binding.actionMove2.text = actionMoves[1].name
            binding.actionMove2.setOnClickListener {
                binding.friendChar.startAnimation(attackAnimation)
                handler.postDelayed({
                    Glide.with(requireContext())
                        .load(actionMoves[1].image)
                        .apply(glideOptions)
                        .into(binding.onEnemy)
                    Glide.with(requireContext())
                        .load(battle.actionMoveListEnemy[0])
                        .apply(glideOptions)
                        .into(binding.onFriend)

                    handler.postDelayed({
                        binding.onEnemy.setImageDrawable(null) // Clear the image after 0.7 seconds
                        binding.onFriend.setImageDrawable(null) // Clear the image after 0.7 seconds
                    }, 700)
                }, 500) // Load the image after a 0.5-second delay
                singleplayerViewModel.performAttack(1)
            }

            binding.actionMove3.text = actionMoves[2].name
            binding.actionMove3.setOnClickListener {
                binding.friendChar.startAnimation(attackAnimation)
                handler.postDelayed({
                    Glide.with(requireContext())
                        .load(actionMoves[2].image)
                        .apply(glideOptions)
                        .into(binding.onEnemy)
                    Glide.with(requireContext())
                        .load(battle.actionMoveListEnemy[0])
                        .apply(glideOptions)
                        .into(binding.onFriend)

                    handler.postDelayed({
                        binding.onEnemy.setImageDrawable(null) // Clear the image after 0.7 seconds
                        binding.onFriend.setImageDrawable(null) // Clear the image after 0.7 seconds
                    }, 700)
                }, 500) // Load the image after a 0.5-second delay
                singleplayerViewModel.performAttack(2)
            }

            binding.actionMove4.text = actionMoves[3].name
            binding.actionMove4.setOnClickListener {
                binding.friendChar.startAnimation(attackAnimation)
                handler.postDelayed({
                    Glide.with(requireContext())
                        .load(actionMoves[3].image)
                        .apply(glideOptions)
                        .into(binding.onEnemy)
                    Glide.with(requireContext())
                        .load(battle.actionMoveListEnemy[0])
                        .apply(glideOptions)
                        .into(binding.onFriend)

                    handler.postDelayed({
                        binding.onEnemy.setImageDrawable(null) // Clear the image after 0.7 seconds
                        binding.onFriend.setImageDrawable(null) // Clear the image after 0.7 seconds
                    }, 700)
                }, 500) // Load the image after a 0.5-second delay
                singleplayerViewModel.performAttack(3)
            }
        }
    }



    private fun loadBattleImages(battle: Battle) {
        Log.d("BattleInfo", "Background Image URL: ${battle.backgroundImage}")
        Log.d("BattleInfo", "Enemy Image URL: ${battle.enemyImage}")

        Glide.with(this)
            .load(battle.backgroundImage)
            .apply(glideOptions)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    if (_binding != null) {
                        binding.root.background = placeholder ?: ContextCompat.getDrawable(requireContext(), R.drawable.app_icon)
                    }
                }
            })

        Glide.with(this)
            .load(battle.enemyImage)
            .apply(glideOptions)
            .into(binding.enemyChar)
    }

    private fun loadImages() {
        imageViewModel.imageURLs.observe(viewLifecycleOwner) { urls ->
            Glide.with(requireView()).load(urls.battleChatbox)
                .apply(glideOptions)
                .into(binding.chatBox)

            Glide.with(requireView()).load(urls.battleFriendChar)
                .apply(glideOptions)
                .into(binding.friendChar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Cancel Glide image loading
        Glide.with(requireContext()).clear(binding.enemyChar)

        // Check if the fragment is still added to its activity
        if (isAdded) {
            // Save the latest battle data to the database
            val currentBattle = singleplayerViewModel.battleData.value
            if (currentBattle != null) {
                singleplayerViewModel.updateBattleInDatabase(currentBattle)
            }
        }

        _binding = null
    }

}
