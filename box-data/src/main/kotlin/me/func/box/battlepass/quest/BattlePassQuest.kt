package me.func.box.battlepass.quest

import me.func.box.battlepass.ServerType

data class BattlePassQuest(
    val condition: String,
    val questType: QuestType,
    val server: ServerType,
    val goal: Int,
    var now: Int = 0,
    var exp: Int = 0,
) {
    fun getLore(open: Boolean = false) = "${if (goal <= now) "§7" else "§f"}${if (open) "" else "§m"}$condition${if (goal <= now) ". Награда собрана!" else ":§b $now из $goal. §6Награда $exp опыта"}${if (open) "" else ". §eТолько Премиум"}"
}
