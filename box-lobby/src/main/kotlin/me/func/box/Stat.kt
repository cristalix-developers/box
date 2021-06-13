package me.func.box

import java.util.*

data class Stat (
    val id: UUID,
    var kills: Int,
    var deaths: Int,
    var wins: Int,
    var games: Int,
    var lastSeenName: String?
)