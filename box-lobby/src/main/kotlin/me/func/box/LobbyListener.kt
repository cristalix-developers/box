package me.func.box

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.scoreboard.ScoreBoard
import me.func.mod.util.after
import me.func.protocol.world.marker.Marker
import me.func.protocol.world.marker.MarkerSign
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService

class LobbyListener : Listener {

    private val navigationItem = item {
        type = Material.COMPASS
        text("&2Навигатор")
    }
    private val startGameItem = item {
        type = Material.COMPASS
        nbt("color", Color(255, 215, 0).asRGB())
        text("&bНачать играть")
    }
    private val donateItem = item {
        type = Material.CLAY_BALL
        nbt("skyblock", "donate")
        text("&eДонат")
    }
    private val profileItem = item {
        type = Material.CLAY_BALL
        nbt("skyblock", "info")
        text("&2Профиль")
    }
    private val hubItem = item {
        type = Material.CLAY_BALL
        text("&4Обратно в хаб")
        nbt("other", "arrow_back")
    }

    private val marker = Marker(-249.5, 112.6, 26.5, MarkerSign.WARNING)

    private val scoreboard = ScoreBoard.builder()
        .key("lobby")
        .header("Бедроковая коробка")
        .dynamic("Монеты") { app.getUser(it).stat.money }
        .dynamic("Игр сыграно") { app.getUser(it).stat.games }
        .dynamic("Побед") { app.getUser(it).stat.wins }
        .dynamic("Убийств") { app.getUser(it).stat.kills }
        .dynamic("Смертей") { app.getUser(it).stat.deaths }
        .build()

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.inventory.clear()
        player.inventory.setItem(0, navigationItem)
        player.inventory.setItem(1, startGameItem)
        player.inventory.setItem(2, donateItem)
        player.inventory.setItem(4, profileItem)
        player.inventory.setItem(8, hubItem)

        val money = app.getUser(player).stat.money
        if (money >= 5000) {
            Bukkit.getScheduler().runTaskLater(app, {
                Anime.marker(player, marker)
            }, 40)
        }

        if (app.getUser(player).stat.progress?.advanced == true)
            player.displayName = "${player.displayName} §6*"

        after {
            ScoreBoard.subscribe("lobby", player)
        }
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (action == Action.PHYSICAL) {
            isCancelled = true
            return
        }

        when (player.itemInHand) {
            navigationItem -> ModTransfer().send("func:navigator", player)
            startGameItem -> startGameMenu.open(player)
            donateItem -> player.performCommand("donate")
            profileItem -> statisticMenu(player)
            hubItem -> ITransferService.get().transfer(player.uniqueId, RealmId.of("HUB"))
        }
    }
}