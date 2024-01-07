package com.idlegame.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.idlegame.objects.Battle

class BattleDAO {
    private val db = FirebaseFirestore.getInstance()
    private val battleCollection = db.collection("battles")

    fun createBattle(battle: Battle, onComplete: (Boolean, String?) -> Unit) {
        val documentReference = if (battle.id.isEmpty()) {
            battleCollection.document()
        } else {
            battleCollection.document(battle.id)
        }

        documentReference.set(battle)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun getBattle(id: String, onComplete: (Battle?, String?) -> Unit) {
        Log.d("BattleDAO", "Attempting to fetch battle with ID: $id")

        battleCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("BattleDAO", "Battle found for ID: $id")
                    val battle = document.toObject(Battle::class.java)
                    onComplete(battle, null)
                } else {
                    Log.d("BattleDAO", "No battle found for ID: $id")
                    onComplete(null, null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BattleDAO", "Error fetching battle for ID: $id, Error: ${e.message}")
                onComplete(null, e.message)
            }
    }

    fun updateBattleHP(id: String, friendHp: Int, enemyHp: Int, onComplete: (Boolean, String?) -> Unit) {
        val hpUpdateData = mapOf(
            "friendHp" to friendHp,
            "enemyHp" to enemyHp
        )

        battleCollection.document(id)
            .update(hpUpdateData)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    //!!! Come back and check cause i need to know what im saving before
    fun updateBattleData(id: String, battleData: Battle, onComplete: (Boolean, String?) -> Unit) {
        val updateData = mapOf(

            "friendHp" to battleData.friendHp,
            "enemyHp" to battleData.enemyHp,

        )

        battleCollection.document(id)
            .update(updateData)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun deleteBattle(id: String, onComplete: (Boolean, String?) -> Unit) {
        battleCollection.document(id)
            .delete()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }
}