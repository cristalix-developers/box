package me.func.box

import me.func.box.battlepass.BattlePassManager.rewards
import me.func.box.reward.WeekRewards
import me.func.mod.Anime
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.dailyReward
import me.func.mod.util.after
import me.func.protocol.ui.indicator.Indicators
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.realm.IRealmService
import java.util.*

val realmService = IRealmService.get()

object FamousListener : Listener {

    // Список OP
    private val ADMIN_LIST = setOf(
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd",  // func
        "6f3f4a2e-7f84-11e9-8374-1cb72caa35fd",  // faelan
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd",  // reidj
        "e7c13d3d-ac38-11e8-8374-1cb72caa35fd",  // delfikpro
        "0ddd561e-9205-11eb-acca-1cb72caa35fd",  // depressed
        "48919a24-20bd-11ea-a54a-1cb72caa35fd",  // MrMaximus_
        "26fe8226-a215-11e8-b884-1cb72caa35fd",   // DerbY
        "b7fa1de3-a464-11e8-8374-1cb72caa35fd" //R1DD1
    ).map { UUID.fromString(it) }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        after(10) {
            val op = ADMIN_LIST.contains(player.uniqueId)
            player.isOp = ADMIN_LIST.contains(player.uniqueId)
            player.gameMode = GameMode.ADVENTURE
            player.teleport(app.spawn)
            player.allowFlight = true


            val user = app.getUser(player)
            val stat = user.stat

            Anime.hideIndicator(player, Indicators.HEALTH, Indicators.HUNGER, Indicators.EXP, Indicators.ARMOR)
            user.moneyPanel.send(player)

            stat.lastSeenName = player.displayName

            player.displayName = (if (op) "㩖 " else "") + player.displayName

            val now = System.currentTimeMillis()
            // Обнулить комбо сбора наград если прошло больше суток или комбо >7
            if ((stat.rewardStreak > 0 && now - stat.lastEnter > 24 * 60 * 60 * 1000) || stat.rewardStreak > 6) {
                stat.rewardStreak = 0
            }

            if (now - stat.dailyClaimTimestamp > 14 * 60 * 60 * 1000) {

                stat.dailyClaimTimestamp = now

                dailyReward {

                    currentDay = stat.rewardStreak
                    storage = WeekRewards.values().map { data ->
                        button {
                            title = data.title
                            item = data.icon
                        }
                    }.toMutableList()
                }.open(player)

                val dailyReward = WeekRewards.values()[stat.rewardStreak]
                player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.title))
                dailyReward.give(user)
                stat.rewardStreak++
            }
            stat.lastEnter = now
        }
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (app.spawn.distanceSquared(to) > 70 * 70) player.teleport(app.spawn)
    }
}