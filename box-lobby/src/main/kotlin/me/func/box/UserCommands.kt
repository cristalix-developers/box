package me.func.box

import me.func.mod.selection.Selection
import me.func.mod.selection.button
import me.func.mod.util.command
import me.func.protocol.GetLogPacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

        fun openLogMenu(sender: Player, uuid: UUID, count: Int) {
            app.socketClient.writeAndAwaitResponse<GetLogPacket>(GetLogPacket(uuid, 50)).thenAccept {
                val selection = Selection(
                    title = "Логи",
                    rows = 3,
                    columns = 3,
                )
                it.logs.forEach {
                    selection.add(
                        button {
                            title = it.action.name
                            description = "${it.data}\n${it.timestamp}"
                        })
                }
                selection.open(sender)
            }
        }

        fun registerGetLogsCmd(name: String, apply: (Player, UUID, Int) -> Unit) = command(name) { player, args ->
            if (player.isOp) {
                apply(
                    player,
                    Bukkit.getPlayer(args[0]).uniqueId,
                    args[1].toInt()
                )
            }
        }

        registerGetLogsCmd("getlastlogs") { sender, uuid, value ->
            openLogMenu(sender, uuid, value)
        }

        fun logs(name: String, apply: (Player) -> Unit) = command(name) { player, args ->
            apply(
                player
            )
        }

        logs("logs") { player ->
            openLogMenu(player, player.uniqueId, 50)
        }
    }
}