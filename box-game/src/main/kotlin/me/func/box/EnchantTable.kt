package me.func.box

import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.material.Dye

class EnchantTable : Listener {

    private var infinityLapis: ItemStack

    init {
        val lapis = Dye()
        lapis.color = DyeColor.BLUE
        infinityLapis = lapis.toItemStack().also { infinityLapis = it }
        infinityLapis.setAmount(64)
    }

    @EventHandler
    fun InventoryClickEvent.handle() {
        if (clickedInventory is EnchantingInventory && slot == 1) {
            isCancelled = true
        }
    }

    @EventHandler
    fun InventoryOpenEvent.handle() {
        if (inventory is EnchantingInventory) {
            inventory.setItem(1, infinityLapis)
        }
    }

    @EventHandler
    fun InventoryCloseEvent.handle() {
        if (inventory is EnchantingInventory) {
            player.inventory.remove(infinityLapis.getType())
        }
    }

    @EventHandler
    fun EnchantItemEvent.handle() {
        if (inventory is EnchantingInventory) {
            inventory.setItem(1, infinityLapis)
        }
    }
}