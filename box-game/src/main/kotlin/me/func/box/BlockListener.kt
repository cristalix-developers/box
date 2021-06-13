package me.func.box

import clepto.bukkit.B
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import ru.cristalix.core.formatting.Formatting

class BlockListener : Listener {

    @EventHandler
    fun BlockBreakEvent.handle() {
        if (app.status == Status.STARTING || block.type == Material.WOOD || block.type == Material.WORKBENCH) {
            cancel = true
            return
        }
        if (block.type == Material.BED_BLOCK) {
            cancel = true
            app.teams.filter { it.location!!.distanceSquared(block.location) < 16 && !it.players.contains(player.uniqueId) }
                .forEach {
                    it.bed = false
                    cancel = false
                    dropItems = false
                    B.bc(Formatting.fine(player.name + " сломал кровать команды " + it.color.chatColor + it.color.teamName))
                    return
                }
            val bed = Bukkit.getOnlinePlayers().filter {
                app.getUser(it)!!.bed != null && app.getUser(it)!!.bed!!.distanceSquared(it.location) < 10
            }
            if (bed.isNotEmpty()) {
                app.getUser(bed[0])!!.bed = null
                cancel = false
                player.sendMessage(Formatting.fine("Вы сломали кровать врага!"))
                bed[0].sendMessage(Formatting.error("Ваша кровать сломана!"))
                return
            }
            return
        }

        block.drops.forEach { player.inventory.addItem(it) }
        block.drops.clear()
        dropItems = false

        if (player.itemInHand.hasItemMeta()) {
            val item = CraftItemStack.asNMSCopy(player.itemInHand)

            if (item.hasTag() && item.tag.hasKey("extra") && item.tag.getString("extra") == "pickaxe") {
                if (player.isSneaking)
                    return

                val yaw = player.location.yaw
                val pitch = player.location.pitch

                if (pitch < -45 || pitch > 45) {
                    breakBlock(player, block.location, 1, 0, 0)
                    breakBlock(player, block.location, 1, 0, 1)
                    breakBlock(player, block.location, 1, 0, -1)
                    breakBlock(player, block.location, 0, 0, 1)
                    breakBlock(player, block.location, 0, 0, -1)
                    breakBlock(player, block.location, -1, 0, -1)
                    breakBlock(player, block.location, -1, 0, 0)
                    breakBlock(player, block.location, -1, 0, 1)
                } else if ((yaw > -45 && yaw < 45) || (yaw > 135 && yaw < 225) || yaw < -135) {
                    breakBlock(player, block.location, -1, 1, 0)
                    breakBlock(player, block.location, 0, 1, 0)
                    breakBlock(player, block.location, 1, 1, 0)
                    breakBlock(player, block.location, -1, -1, 0)
                    breakBlock(player, block.location, 0, -1, 0)
                    breakBlock(player, block.location, 1, -1, 0)
                    breakBlock(player, block.location, -1, 0, 0)
                    breakBlock(player, block.location, 1, 0, 0)
                } else {
                    breakBlock(player, block.location, 0, 1, -1)
                    breakBlock(player, block.location, 0, 1, 0)
                    breakBlock(player, block.location, 0, 1, 1)
                    breakBlock(player, block.location, 0, -1, -1)
                    breakBlock(player, block.location, 0, -1, 0)
                    breakBlock(player, block.location, 0, -1, 1)
                    breakBlock(player, block.location, 0, 0, -1)
                    breakBlock(player, block.location, 0, 0, 1)
                }
            }
        }
    }

    private fun breakBlock(player: Player, location: Location, x: Int, y: Int, z: Int) {
        val clone = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
        if (clone.block.type == Material.BEDROCK || clone.block.type == Material.WOOD || clone.block.type == Material.BED_BLOCK)
            return
        clone.block.drops.forEach { player.inventory.addItem(it) }
        clone.block.type = Material.AIR
    }
}