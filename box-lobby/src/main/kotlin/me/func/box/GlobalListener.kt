package me.func.box

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class GlobalListener : Listener {

    @EventHandler
    fun disable(event: FoodLevelChangeEvent) {
        event.foodLevel = 20
    }

    @EventHandler
    fun disable(event: BlockBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: EntityDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: EntityDamageByEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: InventoryDragEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockPlaceEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: CraftItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerInteractEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockFadeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockSpreadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockGrowEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockFromToEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: HangingBreakByEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: EntityExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerArmorStandManipulateEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerAdvancementCriterionGrantEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: InventoryMoveItemEvent) {
        event.isCancelled = true
    }
}