package com.idlegame.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.idlegame.objects.ActionMove

class ActionMoveDAO {
    private val db = FirebaseFirestore.getInstance()
    private val actionMovesCollection = db.collection("actionMoves")

    fun addActionMove(actionMove: ActionMove) {
        actionMovesCollection.document(actionMove.id).set(actionMove)
            .addOnFailureListener { e ->
                Log.e("ActionMoveDAO", "Error adding document", e)
            }
    }


    fun getActionMove(actionMoveId: String, onComplete: (ActionMove?) -> Unit) {
        actionMovesCollection.document(actionMoveId).get()
            .addOnSuccessListener { document ->
                val actionMove = document.toObject<ActionMove>()
                onComplete(actionMove)
            }
            .addOnFailureListener { onComplete(null) }
    }

    fun updateActionMove(actionMove: ActionMove) {
        actionMovesCollection.document(actionMove.id).set(actionMove)
    }

    fun deleteActionMove(actionMoveId: String) {
        actionMovesCollection.document(actionMoveId).delete()
    }
}
