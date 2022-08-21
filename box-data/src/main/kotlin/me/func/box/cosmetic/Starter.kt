package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 29.06.2021
 * @project box
 */
enum class Starter(
    private val title: String,
    private val price: Int,
    private val rare: Rare,
    private val items: Pair<Material, Int>,
    val lore: String,
    val consumer: (Player) -> Any
) : Donate {
    NONE("Отсутствует", 0, Rare.COMMON, Material.CLAY to 1, "", {}),
    MINER("Шахтёр", 25000, Rare.COMMON, Material.STONE_PICKAXE to 1, "§bКаменная кирка", {
        it.inventory.addItem(item {
            type = Material.STONE_PICKAXE
            text("Шахтёрская кирка")
        })
    }),
    WARRIOR("Воин", 25000, Rare.COMMON, Material.STONE_SWORD to 1, "§bКаменный меч", {
        it.inventory.addItem(item {
            type = Material.STONE_SWORD
            text("Меч воина")
        })
    }),
    DEFENDER(
        "Защитник", 25000, Rare.COMMON, Material.IRON_CHESTPLATE to 1, "§bЖелезный нагрудник\n" +
                "§bКожаные ботинки", {
            it.inventory.addItem(item {
                type = Material.IRON_CHESTPLATE
                text("Нагрудник защитника")
            })
            it.inventory.addItem(item {
                type = Material.LEATHER_BOOTS
                text("Ботинки защитника")
            })
        }
    ),
    BLOCKER("Блокировщик", 25000, Rare.RARE, Material.ENDER_STONE to 32, "§b32 эндерняк", {
        it.inventory.addItem(item {
            type = Material.ENDER_STONE
            text("Защитный блок")
            amount(31)
        })
    }),
    HURRIED("Торопливый", 50000, Rare.RARE, Material.IRON_PICKAXE to 1, "§bЖелезная кирка", {
        val item = item {
            type = Material.IRON_PICKAXE
            text("Быстрая кирка")
        }
        item.durability = (item.durability + 1 - 50).toShort()
        it.inventory.addItem(item)
    }),
    SCOUT("Лазутчик", 50000, Rare.RARE, Material.WEB to 16, "§bПаутина (16 штук)", {
        it.inventory.addItem(item {
            type = Material.WEB
            text("Паутина лазутчика")
            amount(15)
        })
    }),
    FUSE("Взрыватель", 300000, Rare.LEGENDARY, Material.TNT to 10, "§bДинамит (10 штук)", {
        it.inventory.addItem(item {
            type = Material.TNT
            text("Взрывчатка")
            amount(10)
        })
    }),
    TITAN(
        "Титан",
        150000,
        Rare.LEGENDARY,
        Material.ENDER_STONE to 1,
        "§b1 эндерняк\n\n§a20% вещей остануться\n§aпосле смерти", {
            it.inventory.addItem(item {
                type = Material.ENDER_STONE
                text("Эндерняк")
            })
        }
    ),
    SNOW(
        "Морозный король",
        150000,
        Rare.LEGENDARY,
        Material.IRON_SWORD to 1,
        "§bскорость I на 20 минуты\n\n§a1 железный меч", {
            it.inventory.addItem(item {
                type = Material.IRON_SWORD
                text("Железный меч")
            })
            it.addPotionEffect(org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 20 * 60 * 20, 0))
        }
    ),
    HEAL("Целитель", 200000, Rare.LEGENDARY, Material.GOLDEN_APPLE to 1, "§eЗолотое яблоко", {
        it.inventory.addItem(item {
            type = Material.GOLDEN_APPLE
            text("Золотое яблоко")
        })
    }),
    SONYA("Соня", 250000, Rare.LEGENDARY, Material.BED to 1, "§bКровать", { user ->
        user.player!!.inventory.addItem(item {
            type = Material.BED
            text("Кровать")
        })
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

    fun getItem(): ItemStack {
        return item {
            text(rare.color + rare.title + " \n" + rare.color + title)
            type = items.first
            amount = items.second
        }
    }

    override fun getIcon(): ItemStack = item {
        type = items.first
        text("${getRare().getColored()}§f начальный набор $title")
        nbt("rare", rare.ordinal)
    }


    override fun give(user: User) {
        if (user.stat.starters == null)
            user.stat.starters = arrayListOf(this)
        else
            user.stat.starters!!.add(this)
        user.stat.currentStarter = this
    }
}