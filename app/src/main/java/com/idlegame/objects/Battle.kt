package com.idlegame.objects

data class Battle (
    val id: String = "",
    val userId: String = "",
    val actionMoveListFriend: List<ActionMove> = emptyList(),
    val actionMoveListEnemy: List<ActionMove> = emptyList(),
    var friendHp: Int = 100,
    var friendHpMax: Int = 100,
    var enemyHp: Int = 100,
    var enemyHpMax: Int = 100,
    val backgroundImage: String = "",
    val enemyImage: String = "",
    val battleReward: Item = Item()
)