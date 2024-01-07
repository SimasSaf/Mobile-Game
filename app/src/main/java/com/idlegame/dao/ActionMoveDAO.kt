package com.idlegame.dao

import InventoryDAO
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.idlegame.objects.ActionMove

class ActionMoveDAO {
    private val db = FirebaseFirestore.getInstance()
    private val actionMovesCollection = db.collection("actionMoves")
    private val inventoryDao = InventoryDAO()

    fun addActionMove(actionMove: ActionMove) {
        actionMovesCollection.document(actionMove.id).set(actionMove)
            .addOnFailureListener { e ->
                Log.e("ActionMoveDAO", "Error adding document", e)
            }
    }

    fun getActionMovesForUser(userId: String, onComplete: (List<ActionMove>, String?) -> Unit) {
        inventoryDao.getPickedItemsByUserId(userId) { items, error ->
            if (error != null) {
                onComplete(emptyList(), error)
                return@getPickedItemsByUserId // Return early if there's an error
            }

            val actionMoves = mutableListOf<ActionMove>()
            var count = 0

            if (items.isNotEmpty()) {
                items.forEach { item ->
                    val actionMoveId = item.actionMove.id
                    Log.d("ActionMoveDAO", "Fetching ActionMove for ID: $actionMoveId")

                    getActionMove(actionMoveId) { actionMove, actionMoveError ->
                        count++
                        if (actionMoveError == null && actionMove != null) {
                            actionMoves.add(actionMove)
                        }

                        if (count == items.size) {
                            onComplete(actionMoves, null)
                        }
                    }
                }
            } else {
                onComplete(emptyList(), "No action moves found for user")
            }
        }
    }

    fun getActionMove(actionMoveId: String, onComplete: (ActionMove?, String?) -> Unit) {
        Log.d("ActionMoveDAO", "Getting ActionMove with ID: $actionMoveId")

        actionMovesCollection.document(actionMoveId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val actionMove = document.toObject<ActionMove>()
                    if (actionMove != null) {
                        Log.d("ActionMoveDAO", "Successfully fetched ActionMove: $actionMoveId")
                        onComplete(actionMove, null)
                    } else {
                        Log.e("ActionMoveDAO", "Failed to convert document to ActionMove: $actionMoveId")
                        onComplete(null, "Failed to convert document")
                    }
                } else {
                    Log.e("ActionMoveDAO", "No ActionMove found with ID: $actionMoveId")
                    onComplete(null, "ActionMove not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ActionMoveDAO", "Error fetching ActionMove: $actionMoveId, Error: ${e.message}")
                onComplete(null, "Error fetching ActionMove: ${e.message}")
            }
    }

    fun updateActionMove(actionMove: ActionMove) {
        actionMovesCollection.document(actionMove.id).set(actionMove)
    }

    fun deleteActionMove(actionMoveId: String) {
        actionMovesCollection.document(actionMoveId).delete()
    }
}
