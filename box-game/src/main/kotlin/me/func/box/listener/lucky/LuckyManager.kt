package me.func.box.listener.lucky

import me.func.box.app
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class LuckyManager : Listener {

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (!app.isLuckyGame)
            return
        val user = app.getUser(player)!!
        LuckEvent.values().random().accept(user)
        user.stat.luckyOpened++

        // todo: обработка открытия лаки блока
    }

}