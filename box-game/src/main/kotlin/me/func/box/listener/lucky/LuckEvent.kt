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
            block.breakNaturally(ItemStack(org.bukkit.Material.AIR))
    }),
    GIVE_BED({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.BED
        }.build())
    }),
    ;

    fun accept(user: User) {
        luckyConsumer(user)
    }
}