package com.idlegame.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idlegame.model.BattleModel
import com.idlegame.objects.Battle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SingleplayerViewModel() : ViewModel() {

    private val battleModel = BattleModel()
    private val _battleData = MutableLiveData<Battle>()
    val battleData: LiveData<Battle> = _battleData
    private val _errorMessage = MutableLiveData<String>()
    private val _battleOutcome = MutableLiveData<String?>()
    val battleOutcome: LiveData<String?> = _battleOutcome
    private val inventoryViewModel = InventoryViewModel()


    fun checkOrCreateBattle(userId: String) {
        battleModel.checkOrCreateBattle(userId) { battle, error ->
            if (battle != null) {
                _battleData.postValue(battle)
                Log.d("SingleplayerViewModel", "Battle data updated: $battle")
            } else {
                _errorMessage.postValue(error ?: "Error occurred while fetching/creating battle")
            }
        }
    }

    fun performAttack(attackIndex: Int) {
        val currentBattle = _battleData.value
        if (currentBattle == null) {
            _errorMessage.postValue("No current battle data available")
            return
        }

        // Player's attack without delay
        battleModel.performAttack(currentBattle, attackIndex, isPlayer = true) { updatedBattle, errorMessage ->
            if (errorMessage != null) {
                _errorMessage.postValue(errorMessage)
                return@performAttack
            }

            // Check for battle outcome after player's attack
            if (updatedBattle.enemyHp <= 0) {
                _battleData.postValue(updatedBattle)
                checkBattleOutcome(updatedBattle)
                return@performAttack
            }

            viewModelScope.launch { // Use viewModelScope for coroutine
                delay(1000) // Delay in milliseconds before enemy's counterattack

                // Enemy's counterattack
                val enemyAttackIndex = chooseEnemyAttackIndex(updatedBattle)
                battleModel.performAttack(updatedBattle, enemyAttackIndex, isPlayer = false) { battleAfterEnemyAttack, enemyAttackError ->
                    if (enemyAttackError != null) {
                        _errorMessage.postValue(enemyAttackError)
                        return@performAttack
                    }

                    // Update the LiveData with the new battle state
                    _battleData.postValue(battleAfterEnemyAttack)
                    checkBattleOutcome(battleAfterEnemyAttack)
                }
            }
        }
    }

    private fun chooseEnemyAttackIndex(battle: Battle): Int {
        // Logic to choose enemy's attack index, e.g., randomly
        return (battle.actionMoveListEnemy.indices).random()
    }


    private fun checkBattleOutcome(battle: Battle) {
        when {
            battle.friendHp <= 0 -> {
                _battleOutcome.postValue("defeat")
                deleteBattleAfterOutcome(battle.id)
            }
            battle.enemyHp <= 0 -> {
                _battleOutcome.postValue("victory")
                Log.d("SingleplayerViewModel", "Adding item to userId: ${battle.userId}")
                inventoryViewModel.addItemToInventory(battle.userId, battle.battleReward.id)
                deleteBattleAfterOutcome(battle.id)
            }
            else -> _battleOutcome.postValue(null)
        }
    }

    private fun deleteBattleAfterOutcome(battleId: String) {
        battleModel.deleteBattle(battleId) { success, errorMessage ->
            if (success) {
                Log.d("SingleplayerViewModel", "Battle successfully deleted: $battleId")
            } else {
                Log.e("SingleplayerViewModel", "Error deleting battle: $errorMessage")
            }
        }
    }


    fun updateBattleInDatabase(battle: Battle) {
        battleModel.updateBattleData(battle.id, battle) { success, errorMessage ->
            if (!success) {
                _errorMessage.postValue(errorMessage ?: "Error updating battle data")
            }
        }
    }
}
