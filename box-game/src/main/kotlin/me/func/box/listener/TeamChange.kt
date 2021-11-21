package me.func.box.listener

import clepto.bukkit.B
import me.func.box.User
import me.func.box.app
import me.func.box.data.Status
import me.func.box.me.func.box.ModTransfer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import ru.cristalix.core.formatting.Formatting

object TeamChange : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (app.status == Status.STARTING && material == Material.WOOL) {
            app.teams.filter {
                !it.players.contains(player.uniqueId) && it.color.woolData.toByte() == player.itemInHand.getData().data
            }.forEach { team ->
                if (team.players.size >= app.slots / app.teams.size) {
                    player.sendMessage(Formatting.error("Ошибка! Команда заполнена."))
                    return@forEach
                }
                val prevTeam = app.teams.firstOrNull { it.players.contains(player.uniqueId) }
                prevTeam?.players?.remove(player.uniqueId)
                team.players.add(player.uniqueId)

                // Удаляем у всех игрока из команды и добавляем в другую
                val prevTeamIndex = app.teams.indexOf(prevTeam)
                Bukkit.getOnlinePlayers()
                    .filter {
                        it.inventory.heldItemSlot == prevTeamIndex || it.inventory.heldItemSlot == app.teams.indexOf(
                            team
                        )
                    }
                    .forEach { showTeamList(app.getUser(it)!!) }

                player.sendMessage(Formatting.fine("Вы выбрали команду: " + team.color.chatFormat + team.color.teamName))
            }
        }
    }

    @EventHandler
    fun PlayerItemHeldEvent.handle() {
        if (app.status != Status.STARTING)
            return
        val newItem = player.inventory.getItem(newSlot)
        if (newItem != player.inventory.getItem(previousSlot))
            B.postpone(1) { showTeamList(app.getUser(player)!!) }
    }

    @EventHandler
    fun InventoryClickEvent.handle() {
        if (app.status == Status.STARTING)
            isCancelled = true
    }

    private fun showTeamList(user: User) {
        if (app.slots > 16)
            return

        val teamIndex = user.player!!.inventory.heldItemSlot
        val item = user.player!!.inventory.getItem(teamIndex)

        val template = ModTransfer()
            .integer(teamIndex)

        if (item != null && item.getType() == Material.WOOL) {
            val players = app.teams[teamIndex].players
            players.take(4).map { app.getUser(it) }.forEach {
                template.string(it!!.player!!.name)
            }
            repeat(4 - players.size) {
                template.string(if (it < app.slots / app.teams.size - players.size) " §7..." else "")
            }
        }

        template.send("box:team", user)
    }
}