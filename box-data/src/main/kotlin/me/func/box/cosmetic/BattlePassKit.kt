package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class BattlePassKit(
    private val rare: Rare,
    private val title: String,
    private val money: Int,
    private val skin: String
) : Donate {
    SMALL(Rare.COMMON, "Пара монет", 500, "coin"),
    MEDIUM(Rare.RARE, "Пачка монет", 2500, "coin2"),
    BIG(Rare.EPIC, "Коробка монет", 8000, "coin3"),
    EPIC(Rare.LEGENDARY, "Гора монет", 16000, "coin5");

    override fun getCode(): String = ""

    override fun getPrice(): Int = 0

    override fun getTitle(): String = title

    override fun getRare(): Rare = rare

    override fun getIcon(): ItemStack = item {
        type = Material.CLAY_BALL
        text("${getRare().getColored()} §f$title ($money)")
        nbt("rare", rare.ordinal)
        nbt("other", skin)
    }

    override fun give(user: User) { user.giveMoney(money) }

}