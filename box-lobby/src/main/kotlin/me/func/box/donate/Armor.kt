package me.func.box.donate

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

enum class Armor(private val price: Int, private val rare: Rare, private val title: String, private val code: String) :
    Donate {

    NANO(39, Rare.COMMON, "Нано броня", "nano"),
    WITHER(39, Rare.COMMON, "Костюм визера", "wither"),
    HORROR(39, Rare.COMMON, "Костюм ужаса", "hungry_horror"),
    RENEGATE(39, Rare.COMMON, "Стальная броня", "renegade"),
    GOLEM(39, Rare.COMMON, "Костюм голема", "golem"),
    SPIDER(49, Rare.RARE, "Костюм паука", "spider0"),
    SHADOW(49, Rare.RARE, "Теневая броня", "shadow_walker"),
    WOLF(49, Rare.RARE, "Костюм волка", "wolf"),
    GHOST(49, Rare.RARE, "Костюм пешки", "ghost_kindler"),
    CACTUS(49, Rare.RARE, "Костюм кактуса", "cactus"),
    CRYSTAL(49, Rare.RARE, "Костюм кристалла", "crystal"),
    QUANTUM(79, Rare.LEGENDARY, "Квантовая броня", "quantum"),
    FROST(79, Rare.LEGENDARY, "Ледяная броня", "frost"),
    CHICKEN(79, Rare.LEGENDARY, "Ассасин", "chicken"),
    DRAK(299, Rare.LEGENDARY, "Бессмертный", "drak"),
    TITAN(299, Rare.LEGENDARY, "Одеяние титана", "titans"),
    ;

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getCode(): String {
        return code
    }

    override fun getRare(): Rare {
        return rare
    }

    fun getItem(current: Boolean, has: Boolean): ItemStack {
        return item {
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title)
            nbt("armors", code)
            if (current)
                enchant(Enchantment.LUCK, 1)
            type = Material.DIAMOND_HELMET
        }.build()
    }

    fun getItem(): ItemStack {
        return item {
            text(rare.color + rare.title + " §7" + title)
            nbt("armors", code)
            type = Material.DIAMOND_HELMET
        }.build()
    }
}