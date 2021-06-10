package me.func.box

import java.util.*

data class Stat (
    val uuid: UUID,
    var kills: Int,
    var deaths: Int,
    var wins: Int,
    var games: Int,
)