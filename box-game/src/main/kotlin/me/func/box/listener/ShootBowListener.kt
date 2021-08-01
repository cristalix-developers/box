package me.func.box.listener

import org.bukkit.entity.Arrow
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent

/**
 * @author Рейдж 02.08.2021
 * @project box
 */
class ShootBowListener {

    @EventHandler
    fun EntityShootBowEvent.handle() {
        val tnt = projectile.world.spawn(projectile.location, TNTPrimed::class.java)

        if(projectile is Arrow) {
            tnt.ticksLived = 5
            tnt.fuseTicks = 20

            tnt.velocity = projectile.velocity
            projectile = tnt
        }
    }
}