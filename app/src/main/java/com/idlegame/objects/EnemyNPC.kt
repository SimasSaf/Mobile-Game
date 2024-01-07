package com.idlegame.objects

data class EnemyNPC (
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val backgroundImage: String = "",
    val hp: Int = 0,
    val actionMove1: ActionMove = ActionMove(),
    val actionMove2: ActionMove = ActionMove(),
    val item: Item = Item()
)