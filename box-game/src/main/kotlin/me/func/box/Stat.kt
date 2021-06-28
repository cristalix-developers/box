package me.func.box

import java.util.*

data class Stat (
    var id: UUID,
    var kills: Int,
    var deaths: Int,
    var wins: Int,
    var games: Int,
    var emerald: Int,
    var stone: Int,
    var beds: Int,
    val currentSkin: String
)