package com.idlegame.objects

data class ActionMove(
    val id: String,
    val name: String,
    val damage: Int,
    val image: String? = null,
)