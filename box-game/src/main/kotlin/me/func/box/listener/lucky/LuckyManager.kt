package me.func.box.listener.lucky

import me.func.box.app
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot

class LuckyManager : Listener {

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (clickedEntity.type != EntityType.ARMOR_STAND ||
            !app.isLuckyGame ||
            !clickedEntity.hasMetadata("lucky") ||
            hand == EquipmentSlot.OFF_HAND
        ) {
            return
        }

        clickedEntity.remove()

        val user = app.getUser(player)!!
        LuckEvent.values().random().accept(user)
        user.stat.luckyOpened++
    }

}