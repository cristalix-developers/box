package me.func.box.donate

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 29.06.2021
 * @project box
 */
enum class Starter(
    private val title: String,
    private val price: Int,
    private val rare: Rare,
    val items: Pair<Material, Int>,
    val lore: String
) :
    Donate {
    NONE("Отсутствует", 0, Rare.COMMON, Material.CLAY to 1, ""),
    MINER("Шахтёр", 15000, Rare.COMMON, Material.STONE_PICKAXE to 1, "§bКаменная кирка"),
    WARRIOR("Воин", 15000, Rare.COMMON, Material.STONE_SWORD to 1, "§bКаменный меч"),
    DEFENDER(
        "Защитник", 15000, Rare.COMMON, Material.IRON_CHESTPLATE to 1, "§bЖелезный нагрудник\n" +
                "§bКожаные ботинки"
    ),
    BLOCKER("Блокировщик", 30000, Rare.RARE, Material.ENDER_STONE to 32, "§b32 эндерняк"),
    HURRIED("Торопливый", 30000, Rare.RARE, Material.IRON_PICKAXE to 1, "§bЖелезная кирка"),
    SCOUT("Лазутчик", 30000, Rare.RARE, Material.WEB to 16, "§bПаутина (16 штук)"),
    FUSE("Взрыватель", 50000, Rare.LEGENDARY, Material.TNT to 10, "§bДинамит (10 штук)"),
    HEAL("Целитель", 999999, Rare.LEGENDARY, Material.GOLDEN_APPLE to 1, "§eЗолотое яблоко");
/*    SONYA("Соня", 100000, Rare.COMMON, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.BED
            text("Кровать")
        }.build())
    });*/

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getRare(): Rare {
        return rare
    }

    override fun getCode(): String {
        return name
    }

    fun getItem(current: Boolean, has: Boolean): ItemStack {
        return item {
            type = items.first
            amount = items.second
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title + "\n§7Вы получите: \n" + lore)
        }.build()
    }

    fun getItem(): ItemStack {
        return item {
            text(rare.color + rare.title + " \n" + rare.color + title)
            type = items.first
            amount = items.second
        }.build()
    }
}