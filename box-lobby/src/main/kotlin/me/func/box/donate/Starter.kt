package me.func.box.donate

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 29.06.2021
 * @project box
 */
enum class Starter(private val title: String, private val price: Int, private val rare: Rare, val lore: ItemStack) :
    Donate {

    NONE("Отсутствует", 0, Rare.COMMON, item { }.build()),
    MINER("Шахтёр", 15000, Rare.COMMON, item {
        type = Material.STONE_PICKAXE
        text("Шахтёр §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §aОбычный\n§7Начальные предметы:\n§bКаменная кирка")
    }.build()),
    WARRIOR("Воин", 15000, Rare.COMMON, item {
        type = Material.STONE_SWORD
        text("Воин §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §aОбычный\n§7Начальные предметы:\n§bКаменный меч")
    }.build()),
    DEFENDER("Защитник", 15000, Rare.COMMON, item {
        type = Material.IRON_CHESTPLATE
        text("Защитник §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §aОбычный\n§7Начальные предметы:\n§bЖелезный нагрудник\n" +
                "§bКожаные ботинки")
    }.build()),
    BLOCKER("Блокировщик", 30000, Rare.RARE, item {
        type = Material.ENDER_STONE
        text("Блокировщик §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §9Редкий\n§7Начальные предметы:\n§b31 эндерняк")
        amount(31)
    }.build()),
    HURRIED("Торопливый", 30000, Rare.RARE, item {
        type = Material.IRON_PICKAXE
        nbt("RepairCount", 50)
        text("Торопливый §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §9Редкий\n§7Начальные предметы:\n§bЖелезная кирка (50 ударов)")
    }.build()),
    SCOUT("Лазутчик", 30000, Rare.RARE, item {
        type = Material.WEB
        amount(15)
        text("Лазутчик §bПОСМОТРЕТЬ\n\n§7Редкость: §9Редкий\n§7Начальные предметы:\n§bПаутина (15 штук)")
    }.build()),
    FUSE("Взрыватель", 50000, Rare.LEGENDARY, item {
        type = Material.TNT
        text("Взрыватель §bПОСМОТРЕТЬ\n" +
                "\n" +
                "§7Редкость: §6Легендарный\n§7Начальные предметы:\n§bДинамит (10 штук)")
        amount(10)
    }.build()), ;
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
}