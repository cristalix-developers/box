package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.awt.SystemColor.text

enum class Sword(
    private val price: Int,
    private val rare: Rare,
    private val title: String,
    private val code: Int,
    ): Donate {

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
    SNOW(299, Rare.LEGENDARY, "Меч ледяного титана", 13);

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

    override fun getIcon(): ItemStack = item {
        type = Material.DIAMOND_SWORD
        text("${getRare().getColored()}§f меч $title")
        nbt("weapons_other", code)
        nbt("rare", rare.ordinal)
    }

    fun getItem(): ItemStack = item {
        type = org.bukkit.Material.DIAMOND_SWORD
        text(rare.color + rare.title + " §7" + title)
        nbt("weapons_other", code)
    }

    override fun give(user: User) {
        if (user.stat.swords == null)
            user.stat.swords = arrayListOf(this)
        else
            user.stat.swords!!.add(this)
        user.stat.currentSword = this
    }
}