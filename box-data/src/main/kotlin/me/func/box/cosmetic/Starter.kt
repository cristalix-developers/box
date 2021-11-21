package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Рейдж 29.06.2021
 * @project box
 */
enum class Starter(
    private val title: String,
    private val price: Int,
    private val rare: Rare,
    private val items: Pair<Material, Int>,
    private val lore: String,
    val consumer: (Player) -> Any
) : Donate {
    NONE("Отсутствует", 0, Rare.COMMON, Material.CLAY to 1, "", {}),
    MINER("Шахтёр", 15000, Rare.COMMON, Material.STONE_PICKAXE to 1, "§bКаменная кирка", {
        it.inventory.addItem(item {
            type = Material.STONE_PICKAXE
            text("Шахтёрская кирка")
        }.build())
    }),
    WARRIOR("Воин", 15000, Rare.COMMON, Material.STONE_SWORD to 1, "§bКаменный меч", {
        it.inventory.addItem(item {
            type = Material.STONE_SWORD
            text("Меч воина")
        }.build())
    }),
    DEFENDER(
        "Защитник", 15000, Rare.COMMON, Material.IRON_CHESTPLATE to 1, "§bЖелезный нагрудник\n" +
                "§bКожаные ботинки", {
            it.inventory.addItem(item {
                type = Material.IRON_CHESTPLATE
                text("Нагрудник защитника")
            }.build())
            it.inventory.addItem(item {
                type = Material.LEATHER_BOOTS
                text("Ботинки защитника")
            }.build())
        }
    ),
    BLOCKER("Блокировщик", 30000, Rare.RARE, Material.ENDER_STONE to 32, "§b32 эндерняк", {
        it.inventory.addItem(item {
            type = Material.ENDER_STONE
            text("Защитный блок")
            amount(31)
        }.build())
    }),
    HURRIED("Торопливый", 30000, Rare.RARE, Material.IRON_PICKAXE to 1, "§bЖелезная кирка", {
        val item = item {
            type = Material.IRON_PICKAXE
            text("Быстрая кирка")
        }.build()
        item.durability = (item.durability + 1 - 50).toShort()
        it.inventory.addItem(item)
    }),
    SCOUT("Лазутчик", 30000, Rare.RARE, Material.WEB to 16, "§bПаутина (16 штук)", {
        it.inventory.addItem(item {
            type = Material.WEB
            text("Паутина лазутчика")
            amount(15)
        }.build())
    }),
    FUSE("Взрыватель", 50000, Rare.LEGENDARY, Material.TNT to 10, "§bДинамит (10 штук)", {
        it.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.TNT
            text("Взрывчатка")
            amount(10)
        }.build())
    }),
    TITAN(
        "Титан",
        50000,
        Rare.LEGENDARY,
        Material.ENDER_STONE to 1,
        "§b1 эндерняк\n\n§a5% вещей остануться\n§aпосле смерти", {
            it.inventory.addItem(item {
                type = Material.ENDER_STONE
                text("Эндерняк")
            }.build())
        }
    ),
    SNOW(
        "Морозный король",
        50000,
        Rare.LEGENDARY,
        Material.WOOD_SWORD to 1,
        "§bскорость I на 2 минуты\n\n§a1 деревянный меч", {
            it.inventory.addItem(item {
                type = Material.WOOD_SWORD
                text("Деревянный меч")
            }.build())
            it.addPotionEffect(org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 20 * 60 * 2, 0))
        }
    ),
    HEAL("Целитель", 999999, Rare.LEGENDARY, Material.GOLDEN_APPLE to 1, "§eЗолотое яблоко", {
        it.inventory.addItem(item {
            type = Material.GOLDEN_APPLE
            text("Золотое яблоко")
        }.build())
    }),
    SONYA("Соня", 999999, Rare.LEGENDARY, Material.BED to 1, "§bКровать", { user ->
        user.player!!.inventory.addItem(item {
            type = Material.BED
            text("Кровать")
        }.build())
    });

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
        return item {
            type = items.first
            amount = items.second
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title + "\n§7Вы получите: \n" + lore)
        }.build()
    }

    fun getItem(): org.bukkit.inventory.ItemStack {
        return item {
            text(rare.color + rare.title + " \n" + rare.color + title)
            type = items.first
            amount = items.second
        }.build()
    }

    override fun give(user: User) {
        if (user.stat.starters == null)
            user.stat.starters = arrayListOf(this)
        else
            user.stat.starters!!.add(this)
        user.stat.currentStarter = this
        user.stat.money -= this.getPrice()
    }
}