package me.func.box

import java.util.*

data class Stat (
    val id: UUID,
    var skins: MutableList<String>?,
    var currentSkin: String,
    var kills: Int,
    var deaths: Int,
    var wins: Int,
    var games: Int,
    var emerald: Int,
    var stone: Int,
    var beds: Int,
    var lastSeenName: String? = null,
)