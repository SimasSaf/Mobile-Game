package com.idlegame.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.idlegame.objects.ActionMove
import com.idlegame.objects.Item

class ItemDAO {
    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")
    private val tag = "ItemDAO"

    fun addItem(item: Item) {
        itemsCollection.document(item.id).set(item)
    }

    fun getItemByName(itemName: String, onComplete: (Item?) -> Unit) {
        Log.i("ItemDAO", "Fetching item by name: $itemName")

        itemsCollection.whereEqualTo("name", itemName).limit(1).get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    Log.i("ItemDAO", "Item found: $itemName")
                    val item = result.documents.first().toObject<Item>()
                    onComplete(item)
                } else {
                    Log.i("ItemDAO", "Item not found: $itemName")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ItemDAO", "Error fetching item: $itemName, Error: ${e.message}")
                onComplete(null)
            }
    }

    fun getItemById(itemId: String, onComplete: (Item?) -> Unit) {
        Log.d("ItemDAO", "Fetching item by id: $itemId")

        itemsCollection.document(itemId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("ItemDAO", "Item found with id: $itemId")
                    val item = document.toObject<Item>()
                    onComplete(item)
                } else {
                    Log.d("ItemDAO", "Item not found with id: $itemId")
                    onComplete(null)  // Item not found
                }
            }
            .addOnFailureListener { e ->
                Log.e("ItemDAO", "Error fetching item by id: $itemId, Error: ${e.message}")
                onComplete(null)  // Error occurred
            }
    }


    fun getAllItems(onComplete: (List<Item>?) -> Unit) {
        itemsCollection.get()
            .addOnSuccessListener { result ->
                val itemsList = result.documents.mapNotNull { document ->
                    val id = document.getString("id")
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val image = document.getString("image")

                    // Retrieve the "actionMove" field as a Map
                    val actionMoveField = document.get("actionMove") as? Map<String, Any>?

                    // Extract values from the nested JSON object
                    val damage = actionMoveField?.get("damage") as? Int ?: 0
                    val actionMoveImage = actionMoveField?.get("image") as? String
                    val actionMoveName = actionMoveField?.get("name") as? String
                    val actionMoveId = actionMoveField?.get("id") as? String

                    // Check if all required fields are present and not null
                    if (id != null && name != null && description != null && image != null &&
                        actionMoveField != null && actionMoveName != null && actionMoveId != null) {
                        val actionMove = ActionMove(actionMoveId, actionMoveName, damage, actionMoveImage)
                        Item(id, name, description, actionMove, image)
                    } else {
                        null
                    }
                }
                onComplete(itemsList)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }


    fun updateItem(item: Item) {
        itemsCollection.document(item.id).set(item)
    }

    fun deleteItem(itemId: String) {
        itemsCollection.document(itemId).delete()
    }
}
