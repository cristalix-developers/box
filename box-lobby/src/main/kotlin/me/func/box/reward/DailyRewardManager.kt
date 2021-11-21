package me.func.box.reward

import me.func.box.me.func.box.ModTransfer
import me.func.box.User

object DailyRewardManager {

    fun open(user: User) {
        val transfer = ModTransfer().integer(user.stat.rewardStreak + 1)
        WeekRewards.values().forEach { transfer.item(it.icon).string("§7Награда: " + it.title) }
        transfer.send("murder:weekly-reward", user)
    }

}