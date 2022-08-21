package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class MoneyBuy(
    val money: Int,
    val realPrice: Int,
    val percent: Int,
    val icon: String,
    private val itemStack: ItemStack) : Donate {

    SMALL(1000, 19, 0, "coin2", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "coin2")
    }),
    NORMAL(10000, 190, 20, "coin3", item {
        type = Material.CLAY_BALL
        nbt("other", "coin3")
    }),
    BIG(50000, 190 * 5, 30,"coin4", item {
        type = Material.CLAY_BALL
        nbt("other", "coin4")
    }),;

    override fun getPrice(): Int {
        return realPrice
    }

    override fun getTitle(): String {
        return "$money монет"
    }

    override fun getCode(): String {
        return icon
    }

    override fun getRare(): Rare = Rare.COMMON

    override fun getIcon(): ItemStack = itemStack

    override fun give(user: User) {
        user.stat.money += money
    }
}