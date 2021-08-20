package me.func.box

import me.func.box.Starter
import me.func.box.Sword
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
    var money: Int,
    var starters: MutableList<Starter>?,
    var currentStarter: Starter?,
    var swords: MutableList<Sword>?,
    var currentSword: Sword?,
    var luckyOpened: Int,
    var currentKillMessage: Messages,
    var killMessages: MutableList<Messages>,
)