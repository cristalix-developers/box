package me.func.box.info

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material

/**
 * @author Reidj 29.06.2021
 * @project box
 */
enum class Starter(val title: String, val cost: Int, val consumer: (User) -> Any) {
    NONE("Отсутствует", 0, {}),
    MINER("Шахтёр", 4500, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.STONE_PICKAXE
            text("Шахтёрская кирка")
        }.build())
    }),
    WARRIOR("Воин", 5600, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.STONE_SWORD
            text("Меч воина")
        }.build())
    }),
    HURRIED("Торопливый", 9900, { user ->
        val item = item {
            type = Material.IRON_PICKAXE
            text("Быстрая кирка")
        }.build()
        item.durability = (item.durability + 1 - 50).toShort()
        user.player!!.inventory.addItem(item)
    }),
    DEFENDER("Защитник", 5690, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.IRON_CHESTPLATE
            text("Нагрудник защитника")
            type = Material.LEATHER_BOOTS
            text("Ботинки защитника")
        }.build())
    }),
    SCOUT("Лазутчик", 11500, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.WEB
            text("Паутина лазутчика")
            amount(15)
        }.build())
    }),
    BLOCKER("Блокировщик", 9501, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.ENDER_STONE
            text("Защитный блок")
            amount(31)
        }.build())
    }),
    FUSE("Взрыватель", 50000, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.TNT
            text("Взрывчатка")
            amount(10)
        }.build())
    }),
    HEAL("Целитель", 999000, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.GOLDEN_APPLE
            text("Золотое яблоко")
        }.build())
    }),
    TITAN("Титан", 50000, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.ENDER_STONE
            text("Эндерняк")
        }.build())
    }),
    SONYA("Соня", 100000, { user ->
        user.player!!.inventory.addItem(item {
            type = Material.BED
            text("Кровать")
        }.build())
    })
}