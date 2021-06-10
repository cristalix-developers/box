package me.func.box

import clepto.bukkit.B
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.item.Items

class DefaultListener : Listener {

    private val effect = PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, true)

    @EventHandler
    fun PlayerJoinEvent.handle() {
        app.waitingBar.addViewer(player.uniqueId)

        B.postpone(1) { player.teleport(app.spawn) }
        player.addPotionEffect(effect, true)

        if (app.status == Status.STARTING) {
            app.teams.forEach {
                player.inventory.addItem(
                    Items.builder()
                        .displayName("Выбрать команду: " + it.color.chatFormat + it.color.teamName)
                        .type(Material.WOOL)
                        .color(it.color)
                        .build()
                )
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        if (player.gameMode == GameMode.SURVIVAL) {
            player.inventory.forEach {
                if (it != null)
                    player.world.dropItemNaturally(player.location, it)
            }
        }
        app.waitingBar.removeViewer(player.uniqueId)
        app.teams.filter { it.players.contains(player.uniqueId) }
            .forEach { it.players.remove(player.uniqueId) }
        tryGetWinner()
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        foodLevel = 20
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if (entityType == EntityType.VILLAGER)
            cancelled = true
        if (app.status == Status.STARTING)
            cancelled = true
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (app.status == Status.STARTING && material == Material.WOOL) {
            app.teams.filter {
                !it.players.contains(player.uniqueId) && it.color.woolData.toByte() == player.itemInHand.getData().data
            }.forEach { team ->
                if (team.players.size == app.slots / app.teams.size) {
                    player.sendMessage(Formatting.error("Ошибка! Команда заполена."))
                    return@forEach
                }
                app.teams.forEach { it.players.remove(player.uniqueId) }
                team.players.add(player.uniqueId)
                player.sendMessage(Formatting.fine("Вы выбрали команду: " + team.color.chatFormat + team.color.teamName))
            }
        }
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        cancelled = true
        val player = entity as CraftPlayer
        player.inventory.forEach {
            if (it != null)
                player.world.dropItemNaturally(player.location, it)
        }
        player.inventory.clear()
        app.teams.filter { it.players.contains(entity.uniqueId) }
            .forEach { team ->
                if (team.bed) {
                    B.postpone(1) { player.teleport(team.location) }
                    player.inventory.addItem(app.woodPickaxe)
                    player.health = 20.0
                } else {
                    team.players.remove(player.uniqueId)
                }
                return
            }
        tryGetWinner()
        player.gameMode = GameMode.SPECTATOR
        player.sendMessage(Formatting.error("Вы проиграли."))
    }

    private fun tryGetWinner() {
        val list = app.teams.filter { it.players.size > 0 }
        if (list.size == 1) {
            B.bc("")
            B.bc(Formatting.fine(list[0].color.teamName + " победили!"))
            B.bc("")
            app.status = Status.END
        }
    }
}

