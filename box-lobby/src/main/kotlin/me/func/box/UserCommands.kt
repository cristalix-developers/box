package me.func.box

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection.Selection
import me.func.mod.util.command
import me.func.protocol.ActionLog
import me.func.protocol.GetLogPacket
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object UserCommands {
    init {
        fun register(name: String, apply: (Stat, Int) -> String) = command(name) { player, args ->
            if (player.isOp) player.sendMessage(
                apply(
                    app.userManager.getUser(Bukkit.getPlayer(args[0]).uniqueId).stat,
                    args[1].toInt()
                )
            )
        }

        register("money") { stat, value ->
            stat.money = value
            "Деньги выданы"
        }

        register("wins") { stat, value ->
            stat.wins = value
            "Победы изменены"
        }

        register("kills") { stat, value ->
            stat.kills = value
            "Убийства изменены"
        }

        register("battlepass") { stat, value ->
            stat.progress?.advanced = value == 1
            if (value == 1) "Батлпасс выдан" else "Батлпасс убран"
        }

        register("exp") { stat, value ->
            stat.progress?.exp = value
            "Опыт батлпасса выдан"
        }

        fun getDateTime(timestamp: Long) = Date(timestamp).toString()

        fun getMaterial(type: ActionLog): ItemStack {
            return when (type) {
                ActionLog.QUEST -> ItemStack(Material.PAPER)
                ActionLog.BATTLEPASS -> ItemStack(Material.DIAMOND)
                ActionLog.REWARD -> ItemStack(Material.GOLDEN_APPLE)
                ActionLog.SKIPLEVEL -> ItemStack(Material.APPLE)
            }
        }

        // Нужно добавить КЕШ
        fun openLogMenu(sender: Player, uuid: UUID, count: Int) {
            app.socketClient.writeAndAwaitResponse<GetLogPacket>(GetLogPacket(uuid, count)).thenAccept {
                val selection = Selection(
                    title = "Логи",
                    rows = 5,
                    columns = 1,
                )
                selection.hint = ""
                it.logs.reversed().forEach {
                    selection.add(
                        button {
                            hint = ""
                            item = getMaterial(it.action)
                            title = it.action.name
                            description = "${it.data}§7, время: ${getDateTime(it.timestamp)}"
                        })
                }
                selection.open(sender)
            }
        }

        command("getlastlogs") { player, args ->
            if (player.isOp) openLogMenu(player, Bukkit.getPlayer(args[0]).uniqueId, args[1].toInt())
        }

        command("logs") { player, _ -> openLogMenu(player, player.uniqueId, 50) }
    }
}