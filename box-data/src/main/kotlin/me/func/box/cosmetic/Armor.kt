package me.func.box.cosmetic

import me.func.box.User

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
    NUCLEAR(299, Rare.LEGENDARY, "Костюм безопасности", "nuclear"),
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

    fun getItem(current: Boolean, has: Boolean): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title)
            nbt("armors", code)
            if (current)
                enchant(org.bukkit.enchantments.Enchantment.LUCK, 1)
            type = org.bukkit.Material.DIAMOND_HELMET
        }.build()
    }

    fun getItem(): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            text(rare.color + rare.title + " §7" + title)
            nbt("armors", code)
            type = org.bukkit.Material.DIAMOND_HELMET
        }.build()
    }

    override fun give(user: User) {
        if (user.stat.skins == null)
            user.stat.skins = arrayListOf(getCode())
        else
            user.stat.skins!!.add(getCode())
        user.stat.currentSkin = getCode()
    }
}