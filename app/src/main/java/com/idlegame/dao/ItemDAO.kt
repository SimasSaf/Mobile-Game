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

    fun getItem(itemId: String, onComplete: (Item?) -> Unit) {
        itemsCollection.document(itemId).get()
            .addOnSuccessListener { document ->
                val item = document.toObject<Item>()
                onComplete(item)
            }
            .addOnFailureListener { onComplete(null) }
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
