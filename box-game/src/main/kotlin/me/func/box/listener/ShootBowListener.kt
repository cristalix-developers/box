package me.func.box.listener

import clepto.bukkit.B
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * @author Рейдж 02.08.2021
 * @project box
 */
class ShootBowListener : Listener {

    @EventHandler
    fun ProjectileLaunchEvent.handle() {
        if (entity is Arrow && ((entity as Arrow).shooter as CraftPlayer).itemInHand.containsEnchantment(Enchantment.LUCK)) {
            val tnt = entity.world.spawn(entity.location, TNTPrimed::class.java)

            tnt.ticksLived = 5
            tnt.fuseTicks = 20

            tnt.velocity = entity.velocity

            B.postpone(1) { entity.remove() }
        }
    }
}