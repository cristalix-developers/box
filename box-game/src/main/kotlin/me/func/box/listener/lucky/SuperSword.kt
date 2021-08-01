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
    }.build(), { _, victim -> { victim.player!!.velocity = upVector.clone() } }),
    FIRE_SWORD(item {
        type = org.bukkit.Material.IRON_SWORD
        text("§lОгненный меч\n\n§7Поджигает противника.")
        nbt("weapons_other", "14")
    }.build(), { _, victim -> { victim.player!!.fireTicks = 60 } }),
    POISON_SWORD(
        item {
            type = org.bukkit.Material.IRON_SWORD
            text("§lОтравляющий клинок\n\n§7Накладывает на противника\n§7эффект отравления.")
            nbt("weapons", "emerald_dagger")
        }.build(),
        { _, victim ->
            {
                victim.player!!.addPotionEffect(
                    org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.POISON,
                        5 * 20,
                        0
                    )
                )
            }
        }),
    WITHER_SWORD(
        item {
            type = org.bukkit.Material.IRON_SWORD
            text("§lИссушающий меч\n\n§7Накладывает на противника\n§7эффект иссушения.")
            nbt("weapons", "diamond_tiger")
        }.build(),
        { _, victim ->
            {
                victim.player!!.addPotionEffect(
                    org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.WITHER,
                        5 * 20,
                        0
                    )
                )
            }
        }),
    HAMMER(
        item {
            type = org.bukkit.Material.IRON_SWORD
            text("§lМолот\n\n§7Разрушает блок под\n§7противником.")
            nbt("weapons", "iron_mace")
        }.build(),
        { _, victim -> { victim.player!!.location.subtract(0.0, 1.0, 0.0).block.type = org.bukkit.Material.AIR } }),
    ;

    fun give(user: User) {
        user.player!!.inventory.addItem(item)
        user.player!!.sendTitle("§bОружие!", item.meta.displayName, 10, 35, 20)
    }

}