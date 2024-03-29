package me.func.box

import me.func.box.cosmetic.BreakBedEffect
import me.func.box.cosmetic.KillMessage
import me.func.box.cosmetic.Starter
import me.func.box.cosmetic.Sword
import me.func.box.quest.BattlePassQuest
import me.func.protocol.ui.battlepass.BattlePassUserData
import java.util.*

data class Stat(
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
    var currentKillMessage: KillMessage,
    var killMessages: MutableList<KillMessage>,
    var currentBreakBedEffect: BreakBedEffect,
    var breakBedEffects: MutableList<BreakBedEffect>,

    var dailyClaimTimestamp: Long,
    var lastEnter: Long,
    var rewardStreak: Int,

    var progress: BattlePassUserData?,
    var claimedRewards: MutableList<Int>?,
    var data: List<BattlePassQuest>?,
    var lastGenerationTime: Long = System.currentTimeMillis(),
    )