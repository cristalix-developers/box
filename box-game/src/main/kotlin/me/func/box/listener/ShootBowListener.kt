package me.func.box.listener

import org.bukkit.entity.Arrow
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

/**
 * @author Рейдж 02.08.2021
 * @project box
 */
class ShootBowListener : Listener {

    @EventHandler
    fun EntityShootBowEvent.handle() {
        if(projectile is Arrow) {
            val tnt = projectile.world.spawn(projectile.location, TNTPrimed::class.java)

            tnt.ticksLived = 5
            tnt.fuseTicks = 20

            tnt.velocity = projectile.velocity

            projectile.remove()
        }
    }
}