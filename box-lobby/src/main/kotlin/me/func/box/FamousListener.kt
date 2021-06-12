package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import ru.cristalix.core.realm.IRealmService
import java.util.*


class FamousListener : Listener {

    private val realmService = IRealmService.get()

    @EventHandler
    fun PlayerJoinEvent.handle() {
        B.postpone(1) {
            player.allowFlight = true
            player.teleport(app.spawn)

            val stat = app.getUser(player)!!.stat!!
            val address = UUID.randomUUID().toString()
            val objective =
                Cristalix.scoreboardService().getPlayerObjective(player.uniqueId, address)
            objective.displayName = "Бедроковая коробка"
            objective.startGroup("Статистика")
                .record("Убийств") { "§c" + stat.kills }
                .record("Смертей") { "" + stat.deaths }
                .record("Побед") { "§b" + stat.wins }
                .record("Игр") { "§e" + stat.games }
            objective.startGroup("Сервер")
                .record("Онлайн") {
                    (realmService.getOnlineOnRealms("BOX4") +
                            realmService.getOnlineOnRealms("BOX8") +
                            realmService.getOnlineOnRealms("BOX50")).toString()
                }
            Cristalix.scoreboardService().setCurrentObjective(player.uniqueId, address)
        }
        player.gameMode = GameMode.ADVENTURE
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (app.spawn.distanceSquared(to) > 40 * 40)
            player.teleport(app.spawn)
    }

}