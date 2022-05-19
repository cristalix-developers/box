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

object GlobalListener : Listener {

    @EventHandler
    fun  FoodLevelChangeEvent.handle() { foodLevel = 20 }

    @EventHandler
    fun BlockBreakEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerDropItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityDamageEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun InventoryDragEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockPlaceEvent.handle() { isCancelled = true }

    @EventHandler
    fun CraftItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerInteractEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFadeEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockSpreadEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockGrowEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFromToEvent.handle() { isCancelled = true }

    @EventHandler
    fun HangingBreakByEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockBurnEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityExplodeEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerAdvancementCriterionGrantEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerSwapHandItemsEvent.handle() { isCancelled = true }

    @EventHandler
    fun InventoryMoveItemEvent.handle() { isCancelled = true }
}