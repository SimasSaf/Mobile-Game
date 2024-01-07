package com.idlegame

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idlegame.databinding.ListInventoryBinding
import com.idlegame.objects.Item

class InventoryListAdapter : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {
    var dataList = listOf<Item>()
    private var selectedItems = mutableSetOf<String>()


    fun setData(newData: List<Item>, newPickedItemIds: List<String>) {
        Log.d("InventoryListAdapter", "setData called with newData: $newData, newPickedItemIds: $newPickedItemIds")
        dataList = newData
        selectedItems.clear()
        selectedItems.addAll(newPickedItemIds)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<String> {
        return selectedItems.toList()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: Item?, position: Int, description: String?)
    }

    private var onItemSelectedListener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        onItemSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        val isPicked = selectedItems.contains(item.id)

        Log.d("InventoryListAdapter", "onBindViewHolder - Position: $position, Item: ${item.name}, IsPicked: $isPicked")

        holder.bind(item, isPicked)

        holder.itemView.setOnClickListener {
            handleItemClick(position)
        }

        holder.binding.listItemImage.setOnClickListener {
            handleItemClick(position)
        }
    }

    private fun handleItemClick(position: Int) {
        val item = dataList[position]
        val itemId = item.id

        Log.d("InventoryListAdapter", "handleItemClick - Position: $position, ItemId: $itemId")

        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId)
        } else if (selectedItems.size < 4) { // Assuming a maximum of 4 items can be selected
            selectedItems.add(itemId)
        }

        onItemSelectedListener?.onItemSelected(item, position, item.description)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(val binding: ListInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, isPicked: Boolean) {
            val context = binding.root.context
            Glide.with(context).load(item.image).placeholder(R.drawable.app_icon).into(binding.listItemImage)
            binding.listItemName.text = item.name

            if (isPicked) {
                binding.listItemName.setTextColor(ContextCompat.getColor(context, R.color.pickedItemColor))
                binding.listItemName.setTypeface(null, Typeface.BOLD)
                val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce)
                itemView.startAnimation(bounceAnimation)
            } else {
                binding.listItemName.setTextColor(ContextCompat.getColor(context, R.color.unpickedItemColor))
                binding.listItemName.setTypeface(null, Typeface.NORMAL)
                itemView.clearAnimation()
            }
        }
    }
}
