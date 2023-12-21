package com.idlegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.idlegame.databinding.FragmentInventoryBinding
import com.idlegame.viewModel.InventoryViewModel
import android.util.Log
import android.view.animation.AnimationUtils

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeItems()
        viewModel.fetchAllItems()
    }

    private fun setupRecyclerView() {
        val adapter = InventoryListAdapter().apply {
            setOnItemClickListener { position ->
                val clickedView = binding.inventoryList.findViewHolderForAdapterPosition(position)?.itemView

                val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
                clickedView?.startAnimation(bounceAnimation)
            }
        }

        binding.inventoryList.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    private fun observeItems() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            (binding.inventoryList.adapter as InventoryListAdapter).setData(items)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errMsg ->
            if (errMsg.isNotEmpty()) {
                Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
