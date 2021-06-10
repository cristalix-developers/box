package me.func.box

import clepto.bukkit.B
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class FamousListener : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {
        B.postpone(1) { player.teleport(app.spawn) }
        player.gameMode = GameMode.ADVENTURE
    }

}