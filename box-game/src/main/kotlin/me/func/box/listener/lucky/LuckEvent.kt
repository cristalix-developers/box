package me.func.box.listener.lucky

import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack;

enum class LuckEvent(val luckyConsumer: (User) -> Any) {
    GOLDEN_APPLE({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.GOLDEN_APPLE
        }.build())
    }),
    TNT_PRIMED({
        val tnt = it.player!!.world.spawn(it.player!!.location, org.bukkit.entity.TNTPrimed::class.java)
        tnt.fuseTicks = 200
        tnt
    }),
    GIVE_TNT({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.TNT
            amount(2)
        }.build())
    }),
    BLOCK_BREAK({
        val block = it.player!!.location.subtract(0.0, 10.0, 0.0).block
        if (block?.type != Material.BEDROCK)
            block.breakNaturally(ItemStack(Material.AIR))
    }),
    GIVE_BED({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.BED
        }.build())
    }),
    GIVE_EFFECTS({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                8 * 20,
                0
            )
        )
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE,
                8 * 20,
                0
            )
        )
    }),
    LEVITATION({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.LEVITATION,
                6 * 20,
                0
            )
        )
    }),
    SPEED({
        it.player!!.addPotionEffect(org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 5 * 20, 0))
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INVISIBILITY,
                8 * 20,
                0
            )
        )
    }),
    TNT_BOW({}),
    ARROW({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.ARROW
            amount(8)
        }.build())
    }),
    THREADS_STICKS({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.STICK
            amount(6)
        }.build())
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.WEB
            amount(6)
        }.build())
    }),
    ANVIL({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.ANVIL
        }.build())
    }),
    WATER({
        it.player!!.location.block.type = Material.WATER
        it.player!!
    }),
    LAVA({
        it.player!!.location.block.type = Material.LAVA
        it.player!!
    }),
    SNOWBALL({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.SNOW_BALL
            amount(16)
        }.build())
    })
    ;

    fun accept(user: User) {
        luckyConsumer(user)
    }
}