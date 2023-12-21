package com.idlegame

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.idlegame.databinding.ListInventoryBinding
import com.idlegame.objects.Item

class InventoryListAdapter : RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    var dataList = listOf<Item>()
    private var selectedItemPosition = RecyclerView.NO_POSITION
    var onItemClicked: ((Int) -> Unit)? = null

    fun setData(newData: List<Item>) {
        dataList = newData
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClicked = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSelected = position == selectedItemPosition
        holder.bind(dataList[position], isSelected)

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedItemPosition)
                selectedItemPosition = currentPosition
                notifyItemChanged(currentPosition)
                onItemClicked?.invoke(currentPosition)
            }
        }

        holder.binding.listItemImage.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedItemPosition)
                selectedItemPosition = currentPosition
                notifyItemChanged(currentPosition)
                onItemClicked?.invoke(currentPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(val binding: ListInventoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, isSelected: Boolean) {
            val context = binding.root.context
            val imageResId = context.resources.getIdentifier(item.image, "drawable", context.packageName)

            if (imageResId != 0) {
                binding.listItemImage.setImageResource(imageResId)
            } else {
                binding.listItemImage.setImageResource(R.drawable.app_icon)
            }
            binding.listItemName.text = item.name

            if (isSelected) {
                val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce)
                itemView.startAnimation(bounceAnimation)
            } else {
                itemView.clearAnimation()
            }
        }
    }
}
