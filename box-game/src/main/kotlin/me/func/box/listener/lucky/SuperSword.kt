package me.func.box.listener.lucky

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

val upVector = Vector(0.0, 0.1, 0.0)

enum class SuperSword(val item: ItemStack, val onDamage: (attacker: User, victim: User) -> Any) {

    AIR_SWORD(item {
        type = org.bukkit.Material.IRON_SWORD
        text("§lВоздушный меч\n\n§7Подбрасывает противника вверх.")
        nbt("weapons", "iron_triden_3d")
    }.build(), { _, victim -> { victim.player!!.velocity = upVector.clone() } });

    fun give(user: User) {
        user.player!!.inventory.addItem(item)
        user.player!!.sendTitle("§bОружие!", item.meta.displayName, 10, 35, 20)
    }

}