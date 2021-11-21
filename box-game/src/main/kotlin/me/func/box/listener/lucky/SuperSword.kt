package me.func.box.listener.lucky

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

val upVector = Vector(0.0, 0.5, 0.0)

enum class SuperSword(private val item: ItemStack, val onDamage: (attacker: User, victim: User) -> Any) {

    AIR_SWORD(item {
        type = org.bukkit.Material.IRON_SWORD
        text("§lВоздушный меч\n\n§7Подбрасывает противника вверх.")
        nbt("weapons", "iron_triden_3d")
        nbt("super", "AIR_SWORD")
    }.build(), { _, victim -> { victim.player!!.velocity = upVector.clone() } }),
    FIRE_SWORD(item {
        type = org.bukkit.Material.IRON_SWORD
        text("§lОгненный меч\n\n§7Поджигает противника.")
        nbt("weapons_other", "14")
        nbt("super", "FIRE_SWORD")
    }.build(), { _, victim -> { victim.player!!.fireTicks = 35 } }),
    POISON_SWORD(
        item {
            type = org.bukkit.Material.IRON_SWORD
            text("§lОтравляющий клинок\n\n§7Накладывает на противника\n§7эффект отравления.")
            nbt("weapons", "emerald_dagger")
            nbt("super", "POISON_SWORD")
        }.build(),
        { _, victim ->
            {
                victim.player!!.addPotionEffect(
                    org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.POISON,
                        35,
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
            nbt("super", "WITHER_SWORD")
        }.build(),
        { _, victim ->
            {
                victim.player!!.addPotionEffect(
                    org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.WITHER,
                        35,
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
            nbt("super", "HAMMER")
        }.build(),
        { _, victim ->
            {
                val underBlock = victim.player!!.location.block.getRelative(org.bukkit.block.BlockFace.DOWN)
                if (underBlock.type == org.bukkit.Material.STONE)
                    underBlock.setTypeAndDataFast(0, 0)
            }
        }),
    ;

    fun give(user: User) {
        user.player!!.inventory.addItem(item)
        user.player!!.sendTitle("§bОружие!", item.itemMeta.displayName, 10, 35, 20)
    }

}