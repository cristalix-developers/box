package me.func.box.map

import dev.implario.bukkit.item.item
import me.func.box.app
import me.func.box.cosmetic.Sword
import me.func.mod.selection.Button
import me.func.mod.selection.button
import me.func.mod.selection.selection
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack

class TradeMenu : Listener {
    val menu = selection {
        title = "Крафты"
        rows = 4
        columns = 4
        buttons(
            test(item { type = Material.IRON_HELMET
                text("Железный шлем")}, Pair(Material.COBBLESTONE, 32)),
            test(item { type = Material.DIAMOND_HELMET
                text("Алмазный шлем")}, Pair(Material.GOLD_INGOT, 4)),
            test(item { type = Material.STONE_PICKAXE
                text("Каменная кирка")}, Pair(Material.COBBLESTONE, 16)),
            test(item { type = Material.IRON_SWORD
                text("Железный меч")}, Pair(Material.COBBLESTONE, 64)),

            test(item { type = Material.IRON_CHESTPLATE
                text("Железный нагрудник")}, Pair(Material.COBBLESTONE, 32)),
            test(item { type = Material.DIAMOND_CHESTPLATE
                text("Алмазный нагрудник")}, Pair(Material.GOLD_INGOT, 4)),
            test(item {
                type = Material.IRON_PICKAXE
                text("Железная кирка")
                enchant(Enchantment.DIG_SPEED, 2)
            }, Pair(Material.COBBLESTONE, 40)),
            test(item { type = Material.DIAMOND_SWORD
                text("Алмазный меч")}, Pair(Material.COBBLESTONE, 128)),

            test(item { type = Material.IRON_LEGGINGS
                text("Железные поножи")}, Pair(Material.COBBLESTONE, 32)),
            test(item { type = Material.DIAMOND_LEGGINGS
                text("Алмазные поножи")}, Pair(Material.GOLD_INGOT, 4)),
            test(item {
                type = Material.DIAMOND_PICKAXE
                text("Алмазная кирка")
                enchant(Enchantment.DIG_SPEED, 2)
            }, Pair(Material.GOLD_INGOT, 2)),
            test(item {
                type = Material.IRON_SWORD
                text("Железный меч")
                enchant(Enchantment.DAMAGE_ALL, 3)
                nbt("Unbreakable", 1)
                nbt("weapons", "bronze_warhammer")
                nbt("extra", "hammer")
            }, Pair(Material.EMERALD, 128)),

            test(item { type = Material.IRON_BOOTS
                text("Железные ботинки")}, Pair(Material.COBBLESTONE, 32)),
            test(item { type = Material.DIAMOND_BOOTS
                text("Алмазные ботинки")}, Pair(Material.GOLD_INGOT, 4)),
            test(item {
                type = Material.GOLD_PICKAXE
                text("§eЗолотая кирка §l3x3")
                text("")
                text("§fЛомает сразу 9 блоков")
                enchant(Enchantment.DIG_SPEED, 2)
                nbt("Unbreakable", 1)
                nbt("simulators", "donate_pickaxe")
                nbt("extra", "pickaxe")
            }, Pair(Material.EMERALD, 64), Pair(Material.GOLD_INGOT, 5)),
            test(item { type = Material.GOLD_INGOT
                text("Золотой слиток")}, Pair(Material.COBBLESTONE, 48)),
            test(item { type = Material.CHEST
                text("Сундук")}, Pair(Material.EMERALD, 10)),
            test(item { type = Material.COMPASS
                text("Компас")}, Pair(Material.EMERALD, 128)),
            test(item { type = Material.BED
                text("Кровать")}, Pair(Material.EMERALD, 192)),
            test(item { type = Material.BOOKSHELF
                text("Книжная полка")}, Pair(Material.EMERALD, 12)),
            test(item { type = Material.ENCHANTMENT_TABLE
                text("Чародейский стол")}, Pair(Material.EMERALD, 128)),
            test(item { type = Material.TNT
                text("ТНТ")}, Pair(Material.EMERALD, 12))
        )
    }

    private fun getName(material: Material): String {
        return when (material) {
            Material.COBBLESTONE -> "§7Булыжник"
            Material.EMERALD -> "§aИзумруд"
            Material.GOLD_INGOT -> "§eЗолото"
            else -> "§cОшибка"
        }
    }

    private fun test(buyItem: ItemStack, vararg need: Pair<Material, Int>) : Button {
        return button {
            item = ItemStack(buyItem.getType())
            title = buyItem.itemMeta.displayName
            hover = "§fНужно: ${need.map { "§f${it.second} ${getName(it.first)}" }}"
            onClick { player, _, _ ->
                need.forEach {
                    if (!player.inventory.contains(it.first, it.second)) {
                        return@onClick
                    }
                }
                need.forEach { pair ->
                    val clone = ItemStack(pair.first)
                    repeat(pair.second) { player.inventory.removeItem(clone) }
                }

                when (buyItem.getType()) {
                    Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
                    Material.DIAMOND_BOOTS, Material.DIAMOND_SWORD -> player.inventory.addItem(setSkin(player, buyItem.getType()))
                    Material.GOLD_INGOT -> player.inventory.addItem(ItemStack(Material.GOLD_INGOT))
                    else -> player.inventory.addItem(buyItem)
                }
            }
        }
    }

    private fun setSkin(player: Player, material: Material): ItemStack {
        val stat = app.getUser(player)!!.stat
        val skin = stat.currentSkin

        return item {
            type = material
            if (skin.isEmpty()) return@item

            when (material) {
                Material.DIAMOND_HELMET -> nbt("armors", skin)
                Material.DIAMOND_CHESTPLATE -> nbt("armors", skin)
                Material.DIAMOND_LEGGINGS -> nbt("armors", skin)
                Material.DIAMOND_BOOTS -> nbt("armors", skin)
                Material.DIAMOND_SWORD -> {
                    val sword = stat.currentSword
                    if (sword != null && sword != Sword.NONE) nbt("weapons_other", sword.getCode())
                }
            }
        }
    }

    @EventHandler
    fun InventoryOpenEvent.handle() {
        if (inventory.type == InventoryType.MERCHANT) {
            cancelled = true
            menu.open(player as Player)
        }
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (clickedEntity.type == EntityType.VILLAGER) {
            cancelled = true
            menu.open(player as Player)
        }
    }
}