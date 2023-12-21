package com.idlegame.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idlegame.dao.ItemDAO
import com.idlegame.objects.Item

class InventoryViewModel : ViewModel() {
    private val itemDAO = ItemDAO()
    private val tag = "InventoryViewModel"

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchAllItems() {
        itemDAO.getAllItems { itemList ->
            if (itemList != null) {
                Log.d(tag, "Items fetched successfully: ${itemList.size} items")
                _items.postValue(itemList)
            } else {
                val errorMsg = "Failed to fetch items"
                Log.e(tag, errorMsg)
                _errorMessage.postValue(errorMsg)
            }
        }
    }
}
