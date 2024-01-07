package com.idlegame.viewModel

import InventoryDAO
import UserDAO
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idlegame.dao.ItemDAO
import com.idlegame.objects.Item
import com.google.firebase.auth.FirebaseAuth

class InventoryViewModel : ViewModel() {
    private val itemDAO = ItemDAO()
    private val inventoryDAO = InventoryDAO()
    private val user = FirebaseAuth.getInstance().currentUser
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items
    private val _pickedItemIds = MutableLiveData<List<String>>()
    val pickedItemIds: LiveData<List<String>> = _pickedItemIds
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val userDAO = UserDAO()

    fun fetchAllItems() {
        user?.uid?.let { userId ->
            Log.d("InventoryDAO", "userId1: $userId")
            inventoryDAO.getItemsByUserId(userId) { items, error ->
                if (error != null) {
                    Log.e("InventoryViewModel", "Error fetching inventory: $error")
                    _errorMessage.postValue("Error fetching inventory: $error")
                } else {
                    _items.postValue(items)
                }
            }
        } ?: _errorMessage.postValue("User ID is not available")
    }


    fun addItemToInventory(userId: String, itemId: String) {
        inventoryDAO.getItemsByUserId(userId) { items, inventoryError ->
            if (inventoryError != null) {
                Log.e("InventoryViewModel", "Error fetching inventory: $inventoryError")
                _errorMessage.postValue("Error fetching inventory: $inventoryError")
            } else {
                val updatedItems = items.toMutableList()
                val itemExists = updatedItems.any { it.id == itemId }

                if (!itemExists) {
                    // If the item does not exist in the inventory, fetch it and add
                    itemDAO.getItemById(itemId) { newItem ->
                        newItem?.let {
                            updatedItems.add(it)
                            // Update the inventory with the new list of items
                            updateInventory(userId, updatedItems)
                        } ?: Log.e("InventoryViewModel", "New item not found with id: $itemId")
                    }
                } else {
                    //Do nothing
                }
            }
        }
    }


    private fun updateInventory(userId: String, items: List<Item>) {
        // Create or modify a method in inventoryDAO to handle updating the entire list of items.
        inventoryDAO.updateInventoryItems(userId, items) { success, errorMessage ->
            if (!success) {
                Log.e("InventoryViewModel", "Error updating inventory: $errorMessage")
                _errorMessage.postValue(errorMessage ?: "Unknown error updating inventory")
            } else {
                // Update LiveData with the latest items and their picked status
                _items.postValue(items)
                _pickedItemIds.postValue(items.filter { it.isPicked == true }.map { it.id })
                Log.d("InventoryViewModel", "Inventory updated successfully with new items")
            }
        }
    }


    fun saveCurrentSelections() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let { uid ->
            val currentPickedItemIds = _pickedItemIds.value ?: return

            // Call the DAO method to update the picked items
            inventoryDAO.updatePickedItems(uid, currentPickedItemIds) { success, errorMessage ->
                if (success) {
                    Log.d("InventoryViewModel", "Successfully saved current selections")
                } else {
                    Log.e("InventoryViewModel", "Error saving current selections: $errorMessage")
                    _errorMessage.postValue(errorMessage ?: "Error saving selections")
                }
            }
        } ?: run {
            Log.e("InventoryViewModel", "User email is null")
            _errorMessage.postValue("User email not available")
        }
    }

    fun fetchPickedItemIds() {
        user?.email?.let { email ->
            inventoryDAO.getPickedItemsByEmail(email) { pickedIds, error ->
                if (error != null) {
                    _errorMessage.postValue("Error fetching picked items: $error")
                } else {
                    _pickedItemIds.postValue(pickedIds)
                }
            }
        } ?: _errorMessage.postValue("User email is not available")
    }

    fun updateSelectedItems(selectedItemIds: List<String>) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let { uid ->
            inventoryDAO.updatePickedItems(uid, selectedItemIds) { success, errorMessage ->
                if (!success) {
                    Log.e("InventoryViewModel", "Error updating selected items: $errorMessage")
                } else {
                    _pickedItemIds.postValue(selectedItemIds)
                }
            }
        }
    }

    fun toggleItemPicked(itemId: String) {
        val currentItems = _items.value?.toMutableList() ?: mutableListOf()
        val currentPickedItems = currentItems.filter { it.isPicked ?: false }.map { it.id }.toMutableList()

        if (currentPickedItems.contains(itemId)) {
            currentPickedItems.remove(itemId)
            currentItems.find { it.id == itemId }?.isPicked = false
        } else if (currentPickedItems.size < 4) { // Assuming a maximum of 4 items can be picked
            currentPickedItems.add(itemId)
            currentItems.find { it.id == itemId }?.isPicked = true
        }

        _pickedItemIds.value = currentPickedItems
        _items.value = currentItems
    }

}
