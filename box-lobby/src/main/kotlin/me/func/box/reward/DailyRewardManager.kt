package me.func.box.reward

import me.func.box.User
import me.func.mod.conversation.ModTransfer

object DailyRewardManager {

    fun open(user: User) {
        val transfer = ModTransfer().integer(user.stat.rewardStreak + 1)
        WeekRewards.values().forEach { transfer.item(it.icon).string("§7Награда: " + it.title) }
        transfer.send("murder:weekly-reward", user.player)
    }

}