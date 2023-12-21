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
import androidx.navigation.fragment.findNavController
import com.idlegame.dao.ActionMoveDAO
import com.idlegame.dao.ItemDAO
import com.idlegame.databinding.FragmentLoginBinding
import com.idlegame.model.GoogleSignInModel
import com.idlegame.objects.ActionMove
import com.idlegame.objects.Item
import com.idlegame.viewModel.LoginViewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(GoogleSignInModel(requireContext()))
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val tag = "LoginFragment" // Tag for logging


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

//        //
//        val actionMove1 = ActionMove("AM1", "Uppercut", 10, "action_move_uppercut")
//        val actionMove2 = ActionMove("AM2", "Explosion", 30, "action_move_explosion")
//        val actionMove3 = ActionMove("AM3", "Armor", 0, "action_move_armor")
//        val actionMove4 = ActionMove("AM4", "Run", 0)
//
//        val item1 = Item("I1", "Cigar", "One will not hurt...", actionMove1, "inventory_cigar1")
//        val item2 = Item("I2", "Pack of Cigars", "Pack of cigars, good for smoking a lot", actionMove2, "inventory_cigar2")
//        val item3 = Item("I3", "Shoes", "Gotta keep moving", actionMove3, "inventory_shoes1")
//        val item4 = Item("I4", "Rainbow Shoes", "Color overload", actionMove4, "inventory_shoes2")
//
//        val actionMoveDAO: ActionMoveDAO = ActionMoveDAO()
//        val itemDAO: ItemDAO = ItemDAO()
//
//        actionMoveDAO.addActionMove(actionMove1);
//        actionMoveDAO.addActionMove(actionMove2);
//        actionMoveDAO.addActionMove(actionMove3);
//        actionMoveDAO.addActionMove(actionMove4);
//
//        itemDAO.addItem(item1)
//        itemDAO.addItem(item2)
//        itemDAO.addItem(item3)
//        itemDAO.addItem(item4)
//        //
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(R.id.homeFragment)
            } else {
                initializeSignInButton()
                initializeRegisterButton()
                initializeGoogleSignInButton()
                observeViewModel()
            }
        }

        return binding.root
    }

    private fun initializeSignInButton() {
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
            val signInIntent = viewModel.getLoginIntent()
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
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
                Log.i(tag, "Google Sign-In successful for user: ${it.displayName}")
                findNavController().navigate(R.id.homeFragment)
            }
        }

        viewModel.loginError.observe(viewLifecycleOwner) { exception ->
            exception?.let {
                Log.e(tag, "Google Sign-In failed", it)
                Toast.makeText(context, "Sign-In failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
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