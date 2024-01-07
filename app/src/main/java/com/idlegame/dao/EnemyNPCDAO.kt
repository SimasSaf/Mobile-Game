package com.idlegame.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.idlegame.objects.EnemyNPC

class EnemyNPCDAO {
    private val db = FirebaseFirestore.getInstance()
    private val enemyNPCsCollection = db.collection("enemyNPCs")

    fun createEnemyNPC(enemyNPC: EnemyNPC, onComplete: (Boolean, String?) -> Unit) {
        enemyNPCsCollection.document(enemyNPC.id)
            .set(enemyNPC)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun getAllEnemyNPCIds(onComplete: (List<String>, String?) -> Unit) {
        enemyNPCsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val npcIds = querySnapshot.documents.mapNotNull { it.id }
                onComplete(npcIds, null)
            }
            .addOnFailureListener { e ->
                onComplete(emptyList(), e.message)
            }
    }

    private fun getEnemyNPC(id: String, onComplete: (EnemyNPC?, String?) -> Unit) {
        enemyNPCsCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val enemyNPC = document.toObject(EnemyNPC::class.java)
                    onComplete(enemyNPC, null)
                } else {
                    onComplete(null, "Enemy NPC not found")
                }
            }
            .addOnFailureListener { e ->
                onComplete(null, e.message)
            }
    }

    fun getRandomEnemyNPC(onComplete: (EnemyNPC?, String?) -> Unit) {
        getAllEnemyNPCIds { npcIds, error ->
            if (error != null) {
                onComplete(null, "Error getting NPC IDs: $error")
                return@getAllEnemyNPCIds
            }

            if (npcIds.isEmpty()) {
                onComplete(null, "No Enemy NPCs found")
                return@getAllEnemyNPCIds
            }

            // Randomly select an ID from the list
            Log.d("EnemyNPCDAO", "NPC IDs: $npcIds")
            val randomId = npcIds.random()
            Log.d("EnemyNPCDAO", "Randomly selected NPC ID: $randomId")

            getEnemyNPC(randomId) { enemyNPC, npcError ->
                Log.d("EnemyNPCDAO", "Randomly selected NPC: $enemyNPC")
                if (npcError != null) {
                    onComplete(null, "Error getting random NPC: $npcError")
                } else {
                    onComplete(enemyNPC, null)
                }
            }
        }
    }



    fun updateEnemyNPC(id: String, updatedData: Map<String, Any>, onComplete: (Boolean, String?) -> Unit) {
        enemyNPCsCollection.document(id)
            .update(updatedData)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun deleteEnemyNPC(id: String, onComplete: (Boolean, String?) -> Unit) {
        enemyNPCsCollection.document(id)
            .delete()
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }
}