package me.func.box

import dev.implario.bukkit.item.item
import me.func.box.donate.DonateViewer
import me.func.mod.conversation.ModTransfer
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

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.inventory.clear()
        player.inventory.setItem(0, navigationItem)
        player.inventory.setItem(1, startGameItem)
        player.inventory.setItem(2, donateItem)
        player.inventory.setItem(4, profileItem)
        player.inventory.setItem(8, hubItem)
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (action == Action.PHYSICAL) isCancelled = true

        when (player.itemInHand) {
            navigationItem -> ModTransfer().send("func:navigator", player)
            startGameItem -> startGameMenu.open(player)
            donateItem -> DonateViewer().donateMenu.open(player)
            profileItem -> statisticMenu(player)
            hubItem -> ITransferService.get().transfer(player.uniqueId, RealmId.of("HUB"))
        }
    }
}