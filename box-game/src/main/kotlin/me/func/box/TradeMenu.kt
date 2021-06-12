package me.func.box

import dev.implario.bukkit.item.item
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider

class TradeMenu : Listener {

    private val menu = ControlledInventory.builder()
        .title("Крафты")
        .rows(5)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XAXDXPXOX",
                    "XAXDXPXOX",
                    "XAXDXPXOX",
                    "XAXDXPXOX",
                    "GSXXLXXJX"
                )
                contents.add('A', slot(player, ItemStack(Material.IRON_HELMET), Pair(Material.COBBLESTONE, 32)))
                contents.add('A', slot(player, ItemStack(Material.IRON_CHESTPLATE), Pair(Material.COBBLESTONE, 32)))
                contents.add('A', slot(player, ItemStack(Material.IRON_LEGGINGS), Pair(Material.COBBLESTONE, 32)))
                contents.add('A', slot(player, ItemStack(Material.IRON_BOOTS), Pair(Material.COBBLESTONE, 32)))

                contents.add('D', slot(player, ItemStack(Material.DIAMOND_HELMET), Pair(Material.GOLD_INGOT, 4)))
                contents.add('D', slot(player, ItemStack(Material.DIAMOND_CHESTPLATE), Pair(Material.GOLD_INGOT, 4)))
                contents.add('D', slot(player, ItemStack(Material.DIAMOND_LEGGINGS), Pair(Material.GOLD_INGOT, 4)))
                contents.add('D', slot(player, ItemStack(Material.DIAMOND_BOOTS), Pair(Material.GOLD_INGOT, 4)))

                contents.add('P', slot(player, ItemStack(Material.STONE_PICKAXE), Pair(Material.COBBLESTONE, 16)))
                contents.add('P', slot(player, item {
                    type = Material.IRON_PICKAXE
                    enchant(Enchantment.DIG_SPEED, 2)
                }.build(), Pair(Material.COBBLESTONE, 40)))
                contents.add('P', slot(player, ItemStack(Material.DIAMOND_PICKAXE), Pair(Material.COBBLESTONE, 80)))
                contents.add('P', slot(player, item {
                    type = Material.GOLD_PICKAXE
                    enchant(Enchantment.DIG_SPEED, 2)
                    nbt("Unbreakable", 1)
                    nbt("simulators", "donate_pickaxe")
                    nbt("extra", "pickaxe")
                }.build(), Pair(Material.EMERALD, 64), Pair(Material.GOLD_INGOT, 5)))

                contents.add('O', slot(player, ItemStack(Material.IRON_SWORD), Pair(Material.COBBLESTONE, 64)))
                contents.add('O', slot(player, ItemStack(Material.DIAMOND_SWORD), Pair(Material.COBBLESTONE, 128)))
                contents.add('O', slot(player, item {
                    type = Material.IRON_SWORD
                    enchant(Enchantment.DAMAGE_ALL, 3)
                    nbt("Unbreakable", 1)
                    nbt("weapons", "bronze_warhammer")
                    nbt("extra", "hammer")
                }.build(), Pair(Material.EMERALD, 128)))
                contents.add('O', slot(player, ItemStack(Material.CHEST), Pair(Material.EMERALD, 33)))
                contents.add('L', slot(player, ItemStack(Material.GOLD_INGOT), Pair(Material.COBBLESTONE, 48)))
                contents.add('J', slot(player, ItemStack(Material.COMPASS), Pair(Material.EMERALD, 148)))
                contents.add('S', slot(player, ItemStack(Material.BED), Pair(Material.EMERALD, 192)))
                contents.add('G', slot(player, ItemStack(Material.ENCHANTMENT_TABLE), Pair(Material.EMERALD, 256)))

                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private fun getName(material: Material): String {
        return when (material) {
            Material.COBBLESTONE -> "§7Булыжник"
            Material.EMERALD -> "§aИзумруд"
            Material.GOLD_INGOT -> "§eЗолото"
            else -> "§cОшибка"
        }
    }

    fun slot(player: Player, itemStack: ItemStack, vararg need: Pair<Material, Int>): ClickableItem {
        itemStack.lore =
            arrayListOf("", "§fНужно: ", *need.map { "  §f" + it.second + " " + getName(it.first) }.toTypedArray())
        return ClickableItem.of(itemStack) {
            need.forEach {
                if (!player.inventory.contains(it.first, it.second)) {
                    return@of
                }
            }
            need.forEach { pair ->
                val clone = ItemStack(pair.first)
                repeat(pair.second) { player.inventory.removeItem(clone) }
            }
            player.updateInventory()
            player.inventory.addItem(if (itemStack.type0 == Material.GOLD_INGOT) ItemStack(Material.GOLD_INGOT) else itemStack)
        }
    }

    @EventHandler
    fun InventoryOpenEvent.handle() {
        if (inventory.type == InventoryType.MERCHANT) {
            cancelled = true
            menu.open(player as CraftPlayer)
        }
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (clickedEntity.type == EntityType.VILLAGER) {
            cancelled = true
            menu.open(player as CraftPlayer)
        }
    }
}