package me.func.box.reward

import me.func.box.ModTransfer
import me.func.box.User

object DailyRewardManager {

    fun open(user: User, onlyShow: Boolean) {
        val transfer = ModTransfer().integer(user.stat.rewardStreak + 1).boolean(onlyShow)
        WeekRewards.values().forEach { transfer.item(it.icon).string("§7Награда: " + it.title) }
        transfer.send("murder:weekly-reward", user)
    }

}