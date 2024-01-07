package com.idlegame.objects

data class Item(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val actionMove: ActionMove = ActionMove(),
    val image: String = "",
    var isPicked: Boolean? = false
)