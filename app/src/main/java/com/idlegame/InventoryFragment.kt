package com.idlegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.idlegame.databinding.FragmentInventoryBinding
import com.idlegame.viewModel.InventoryViewModel
import androidx.lifecycle.ViewModelProvider
import com.idlegame.objects.Item
import com.idlegame.viewModel.ImageViewModel

class InventoryFragment : Fragment() {
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        viewModel.fetchAllItems()
        viewModel.fetchPickedItemIds()
        setupRecyclerView()
        observeItems()
        loadImages()
    }

    private fun setupRecyclerView() {
        val adapter = InventoryListAdapter().apply {
            setOnItemSelectedListener(object : InventoryListAdapter.OnItemSelectedListener {
                override fun onItemSelected(item: Item?, position: Int, description: String?) {
                    // Update the TextView with the item description
                    binding.inventoryDescriptionText.text = description ?: "No description available"

                    // Additional logic for item selection, if any
                    item?.let {
                        viewModel.toggleItemPicked(it.id)
                    }
                }
            })
        }
        binding.inventoryList.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    private fun observeItems() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            updateAdapterData()
        }

        viewModel.pickedItemIds.observe(viewLifecycleOwner) { pickedIds ->
            updateAdapterData()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errMsg ->
            if (errMsg.isNotEmpty()) {
                Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadImages() {
        imageViewModel.imageURLs.observe(viewLifecycleOwner) { urls ->
            Glide.with(this)
                .load(urls.inventoryDescriptionChatBox)
                .into(binding.inventoryDescriptionChatBox)
        }
    }

    private fun updateAdapterData() {
        val items = viewModel.items.value ?: emptyList()
        val pickedIds = viewModel.pickedItemIds.value ?: emptyList()
        (binding.inventoryList.adapter as InventoryListAdapter).setData(items, pickedIds)
    }

    override fun onDestroyView() {
        viewModel.saveCurrentSelections()
        super.onDestroyView()
        _binding = null
    }

}
