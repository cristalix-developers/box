package me.func.box.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.AnvilInventory

class FixAnvilRename : Listener {

    @EventHandler
    fun InventoryClickEvent.handle() {
        if (inventory !is AnvilInventory)
            return
        if (slotType !== InventoryType.SlotType.RESULT)
            return
        if (currentItem.getType() == Material.GOLD_INGOT || currentItem.getType() == Material.EMERALD)
            isCancelled = true
    }
}