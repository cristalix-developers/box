package me.func.box

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class FamousListener : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.teleport(app.spawn)
    }

}