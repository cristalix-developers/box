package me.func.box

import org.bukkit.entity.Player

/**
 * @author Рейдж 29.06.2021
 * @project box
 */
enum class Starter(
    private val title: String,
    private val price: Int,
    private val rare: Rare,
    val items: Pair<org.bukkit.Material, Int>,
    val lore: String,
    val consumer: (Player) -> Any
) :
    Donate {
    NONE("Отсутствует", 0, Rare.COMMON, org.bukkit.Material.CLAY to 1, "", {}),
    MINER("Шахтёр", 15000, Rare.COMMON, org.bukkit.Material.STONE_PICKAXE to 1, "§bКаменная кирка", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.STONE_PICKAXE
            text("Шахтёрская кирка")
        }.build())
    }),
    WARRIOR("Воин", 15000, Rare.COMMON, org.bukkit.Material.STONE_SWORD to 1, "§bКаменный меч", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.STONE_SWORD
            text("Меч воина")
        }.build())
    }),
    DEFENDER(
        "Защитник", 15000, Rare.COMMON, org.bukkit.Material.IRON_CHESTPLATE to 1, "§bЖелезный нагрудник\n" +
                "§bКожаные ботинки", {
            it.inventory.addItem(dev.implario.bukkit.item.item {
                type = org.bukkit.Material.IRON_CHESTPLATE
                text("Нагрудник защитника")
                type = org.bukkit.Material.LEATHER_BOOTS
                text("Ботинки защитника")
            }.build())
        }
    ),
    BLOCKER("Блокировщик", 30000, Rare.RARE, org.bukkit.Material.ENDER_STONE to 32, "§b32 эндерняк", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.ENDER_STONE
            text("Защитный блок")
            amount(31)
        }.build())
    }),
    HURRIED("Торопливый", 30000, Rare.RARE, org.bukkit.Material.IRON_PICKAXE to 1, "§bЖелезная кирка", {
        val item = dev.implario.bukkit.item.item {
            type = org.bukkit.Material.IRON_PICKAXE
            text("Быстрая кирка")
        }.build()
        item.durability = (item.durability + 1 - 50).toShort()
        it.inventory.addItem(item)
    }),
    SCOUT("Лазутчик", 30000, Rare.RARE, org.bukkit.Material.WEB to 16, "§bПаутина (16 штук)", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.WEB
            text("Паутина лазутчика")
            amount(15)
        }.build())
    }),
    FUSE("Взрыватель", 50000, Rare.LEGENDARY, org.bukkit.Material.TNT to 10, "§bДинамит (10 штук)", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.TNT
            text("Взрывчатка")
            amount(10)
        }.build())
    }),
    TITAN(
        "Титан",
        50000,
        Rare.LEGENDARY,
        org.bukkit.Material.ENDER_STONE to 1,
        "§b1 эндерняк\n\n§a5% вещей остануться\n§aпосле смерти", {
            it.inventory.addItem(dev.implario.bukkit.item.item {
                type = org.bukkit.Material.ENDER_STONE
                text("Эндерняк")
            }.build())
        }
    ),
    HEAL("Целитель", 999999, Rare.LEGENDARY, org.bukkit.Material.GOLDEN_APPLE to 1, "§eЗолотое яблоко", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = org.bukkit.Material.GOLDEN_APPLE
            text("Золотое яблоко")
        }.build())
    });
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

    fun getItem(current: Boolean, has: Boolean): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            type = items.first
            amount = items.second
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title + "\n§7Вы получите: \n" + lore)
        }.build()
    }

    fun getItem(): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            text(rare.color + rare.title + " \n" + rare.color + title)
            type = items.first
            amount = items.second
        }.build()
    }
}