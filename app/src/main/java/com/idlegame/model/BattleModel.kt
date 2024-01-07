package com.idlegame.model

import android.util.Log
import com.idlegame.dao.ActionMoveDAO
import com.idlegame.dao.BattleDAO
import com.idlegame.dao.EnemyNPCDAO
import com.idlegame.objects.Battle

class BattleModel {
    private val battleDao = BattleDAO()
    private val enemyNpcDao = EnemyNPCDAO()
    private val actionMoveDAO = ActionMoveDAO()

    fun checkOrCreateBattle(userId: String, onComplete: (Battle?, String?) -> Unit) {
        battleDao.getBattle(userId) { battle, error ->
            if (battle != null) {
                onComplete(battle, null)
            } else if (error != null) {
                onComplete(null, error)
            } else {
                createBattleWithRandomEnemyNPC(userId) { newBattle, creationError ->
                    if (newBattle != null) {
                        onComplete(newBattle, null)
                    } else {
                        onComplete(null, creationError ?: "Failed to create new battle")
                    }
                }
            }
        }
    }

    fun performAttack(battle: Battle, attackIndex: Int, isPlayer: Boolean, onComplete: (Battle, String?) -> Unit) {
        val actionMove = if (isPlayer) {
            battle.actionMoveListFriend.getOrNull(attackIndex)
        } else {
            battle.actionMoveListEnemy.getOrNull(attackIndex)
        }

        if (actionMove == null) {
            onComplete(battle, "Invalid attack index")
            return
        }

        val updatedFriendHp: Int
        val updatedEnemyHp: Int

        if (isPlayer) {
            updatedFriendHp = battle.friendHp // Player's HP doesn't change when they attack
            updatedEnemyHp = if (actionMove.damage > 0) {
                battle.enemyHp - actionMove.damage // Damage to enemy
            } else {
                battle.enemyHp
            }
        } else {
            updatedEnemyHp = battle.enemyHp // Enemy's HP doesn't change when they attack
            updatedFriendHp = if (actionMove.damage > 0) {
                battle.friendHp - actionMove.damage // Damage to player
            } else {
                battle.friendHp
            }
        }

        Log.d("BattleModel", "Updating HP: Friend HP = $updatedFriendHp, Enemy HP = $updatedEnemyHp")

        val updatedBattle = battle.copy(friendHp = updatedFriendHp, enemyHp = updatedEnemyHp)
        onComplete(updatedBattle, null)
    }


    fun createBattleWithRandomEnemyNPC(userId: String, onComplete: (Battle?, String?) -> Unit) {
        Log.d("BattleModel", "Creating battle with random enemy NPC for User ID: $userId")
        Log.d("BattleModel", "Requesting random enemy NPC...")

        enemyNpcDao.getRandomEnemyNPC { enemyNpc, error ->
            if (error != null) {
                onComplete(null, "Error getting random NPC: $error")
                Log.d("BattleModel", "Random NPC error: $error")
                return@getRandomEnemyNPC
            }

            if (enemyNpc == null) {
                onComplete(null, "No EnemyNPC available for battle")
                Log.d("BattleModel", "No EnemyNPC available for battle")
                return@getRandomEnemyNPC
            }

            actionMoveDAO.getActionMovesForUser(userId) { actionMovesFriend, actionMovesError ->
                if (actionMovesError != null) {
                    onComplete(null, "Error getting action moves for user: $actionMovesError")
                    Log.d("BattleModel", "ActionMoves error: $actionMovesError")
                    return@getActionMovesForUser
                }

                Log.d("BattleModel", "ActionMoves retrieved successfully for user: $userId")

                val newBattle = Battle(
                    id = userId,
                    userId = userId,
                    actionMoveListFriend = actionMovesFriend,
                    actionMoveListEnemy = listOf(enemyNpc.actionMove1, enemyNpc.actionMove2),
                    friendHp = 100,
                    friendHpMax = 100,
                    enemyHp = enemyNpc.hp,
                    enemyHpMax = enemyNpc.hp,
                    backgroundImage = enemyNpc.backgroundImage,
                    enemyImage = enemyNpc.image,
                    battleReward = enemyNpc.item
                )

                createNewBattle(newBattle) { success, creationError ->
                    if (success) {
                        onComplete(newBattle, null)
                        Log.d("BattleModel", "New battle created successfully for User ID: $userId")
                    } else {
                        onComplete(null, creationError ?: "Failed to create new battle")
                        Log.d("BattleModel", "Failed to create new battle for User ID: $userId")
                    }
                }
            }
        }
    }

    fun deleteBattle(battleId: String, onComplete: (Boolean, String?) -> Unit) {
        Log.d("BattleModel", "Deleting battle with ID: $battleId")

        battleDao.deleteBattle(battleId) { success, errorMessage ->
            if (success) {
                Log.d("BattleModel", "Battle successfully deleted: $battleId")
                onComplete(true, null)
            } else {
                Log.e("BattleModel", "Error deleting battle: $errorMessage")
                onComplete(false, errorMessage)
            }
        }
    }

    fun updateBattleData(id: String, battleData: Battle, onComplete: (Boolean, String?) -> Unit) {
        battleDao.updateBattleData(id, battleData, onComplete)
    }

    private fun createNewBattle(battle: Battle, onComplete: (Boolean, String?) -> Unit) {
        battleDao.createBattle(battle, onComplete)
    }

    private fun updateBattleHP(id: String, friendHp: Int, enemyHp: Int, onComplete: (Boolean, String?) -> Unit) {
        battleDao.updateBattleHP(id, friendHp, enemyHp, onComplete)
    }
}
