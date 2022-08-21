package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import me.func.protocol.DropRare
import org.bukkit.inventory.ItemStack

enum class Armor(
    private val price: Int,
    private val rare: Rare,
    private val title: String,
    private val code: String,
    val itemStack: ItemStack
    ) : Donate {

    NANO(39, Rare.COMMON, "Нано броня", "nano", item {
        type = org.bukkit.Material.DIAMOND_HELMET
        nbt("armors", "nano")
    }),
    WITHER(39, Rare.COMMON, "Костюм визера", "wither", item {
        type = org.bukkit.Material.DIAMOND_HELMET
        nbt("armors", "wither")
    }),
    HORROR(39, Rare.COMMON, "Костюм ужаса", "hungry_horror", item {
        type = org.bukkit.Material.DIAMOND_HELMET
        nbt("armors", "wither")
    }),
    RENEGATE(39, Rare.COMMON, "Стальная броня", "renegade", item {
        type = org.bukkit.Material.DIAMOND_HELMET
        nbt("armors", "wither")
    }),
    GOLEM(39, Rare.COMMON, "Костюм голема", "golem", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    SPIDER(49, Rare.RARE, "Костюм паука", "spider0", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    SHADOW(49, Rare.RARE, "Теневая броня", "shadow_walker", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    WOLF(49, Rare.RARE, "Костюм волка", "wolf", item {
        type = org.bukkit.Material.DIAMOND_HELMET

    }),
    GHOST(49, Rare.RARE, "Костюм пешки", "ghost_kindler", item {
        type = org.bukkit.Material.DIAMOND_HELMET
        nbt("armors", "wither")
    }),
    CACTUS(49, Rare.RARE, "Костюм кактуса", "cactus", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    CRYSTAL(49, Rare.RARE, "Костюм кристалла", "crystal", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    QUANTUM(79, Rare.LEGENDARY, "Квантовая броня", "quantum", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    FROST(79, Rare.LEGENDARY, "Ледяная броня", "frost", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    CHICKEN(79, Rare.LEGENDARY, "Ассасин", "chicken", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    DRAK(299, Rare.LEGENDARY, "Бессмертный", "drak", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    TITAN(299, Rare.LEGENDARY, "Одеяние титана", "titans", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    NUCLEAR(299, Rare.LEGENDARY, "Костюм безопасности", "nuclear", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    }),
    SNOW(299, Rare.LEGENDARY, "Ледяной титан", "snow", item {
        type = org.bukkit.Material.DIAMOND_HELMET
    });

    override fun getPrice(): Int = price

    override fun getTitle(): String = title

    override fun getCode(): String = code

    override fun getRare(): Rare = rare


    override fun getIcon(): ItemStack {
        return item {
            type = org.bukkit.Material.DIAMOND_HELMET
            text(rare.color + rare.title + " §7" + title)
            nbt("armors", code)
            nbt("rare", rare.ordinal)
        }
    }

    override fun give(user: User) {
        if (user.stat.skins == null)
            user.stat.skins = arrayListOf(getCode())
        else
            user.stat.skins!!.add(getCode())
        user.stat.currentSkin = getCode()
    }
}