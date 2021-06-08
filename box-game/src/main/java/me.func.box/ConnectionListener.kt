package me.func.box

import clepto.bukkit.B
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.item.Items

class ConnectionListener : Listener {

    private val effect = PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, true)

    @EventHandler
    fun PlayerJoinEvent.handle() {
        app.waitingBar.addViewer(player.uniqueId)
        player.teleport(app.spawn)
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

        player.isOp = true
        player.gameMode = GameMode.CREATIVE
        player.inventory.addItem(Items.builder().displayName("Хаха").type(Material.DIAMOND_PICKAXE).build())
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        app.waitingBar.removeViewer(player.uniqueId)
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
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
}