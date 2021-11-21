package me.func.box.cosmetic

import me.func.box.User

enum class Sword(private val price: Int, private val rare: Rare, private val title: String, private val code: Int) :
    Donate {

    NONE(0, Rare.COMMON, "Алмазный меч", 0),
    A(29, Rare.COMMON, "Эндер меч", 1),
    B(29, Rare.COMMON, "Раздвижной меч", 47),
    C(29, Rare.COMMON, "Дьявольский меч", 33),
    D(29, Rare.COMMON, "Бисквитный меч", 7),
    E(39, Rare.RARE, "Ледяная дубина", 29),
    F(39, Rare.RARE, "Нефриловый клинок", 39),
    G(39, Rare.RARE, "Клинок вампира", 43),
    H(79, Rare.LEGENDARY, "Коса смерти", 42),
    J(79, Rare.LEGENDARY, "Кровавая сабля", 41),
    K(79, Rare.LEGENDARY, "Топор разрушения", 34),
    M(299, Rare.LEGENDARY, "Коса титана", 15),
    L(299, Rare.LEGENDARY, "Сусальный топорик", 54),
    SNOW(299, Rare.LEGENDARY, "Меч ледяного титана", 13),
    ;

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getCode(): String {
        return code.toString()
    }

    override fun getRare(): Rare {
        return rare
    }

    fun getItem(current: Boolean, has: Boolean): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title)
            nbt("weapons_other", code)
            if (current)
                enchant(org.bukkit.enchantments.Enchantment.LUCK, 1)
            type = org.bukkit.Material.DIAMOND_SWORD
        }.build()
    }

    fun getItem(): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            text(rare.color + rare.title + " §7" + title)
            nbt("weapons_other", code)
            type = org.bukkit.Material.DIAMOND_SWORD
        }.build()
    }

    override fun give(user: User) {
        if (user.stat.swords == null)
            user.stat.swords = arrayListOf(this)
        else
            user.stat.swords!!.add(this)
        user.stat.currentSword = this
    }
}