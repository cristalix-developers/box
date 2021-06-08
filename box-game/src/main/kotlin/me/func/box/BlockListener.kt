package me.func.box

import clepto.bukkit.B
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.item.Items

class BlockListener : Listener {

    @EventHandler
    fun BlockBreakEvent.handle() {
        if (player.itemInHand.hasItemMeta()) {
            val item = CraftItemStack.asNMSCopy(player.itemInHand)

            if (item.hasTag() && item.tag.hasKey("extra") && item.tag.getString("extra") == "pickaxe") {
                if (player.isSneaking)
                    return

                val yaw = player.location.yaw
                val pitch = player.location.pitch

                if (pitch < -45 || pitch > 45) {
                    breakBlock(block.location, 1, 0, 0)
                    breakBlock(block.location, 1, 0, 1)
                    breakBlock(block.location, 1, 0, -1)
                    breakBlock(block.location, 0, 0, 1)
                    breakBlock(block.location, 0, 0, -1)
                    breakBlock(block.location, -1, 0, -1)
                    breakBlock(block.location, -1, 0, 0)
                    breakBlock(block.location, -1, 0, 1)
                } else if ((yaw > -45 && yaw < 45) || (yaw > 135 && yaw < 225) || yaw < -135) {
                    breakBlock(block.location, -1, 1, 0)
                    breakBlock(block.location, 0, 1, 0)
                    breakBlock(block.location, 1, 1, 0)
                    breakBlock(block.location, -1, -1, 0)
                    breakBlock(block.location, 0, -1, 0)
                    breakBlock(block.location, 1, -1, 0)
                    breakBlock(block.location, -1, 0, 0)
                    breakBlock(block.location, 1, 0, 0)
                } else {
                    breakBlock(block.location, 0, 1, -1)
                    breakBlock(block.location, 0, 1, 0)
                    breakBlock(block.location, 0, 1, 1)
                    breakBlock(block.location, 0, -1, -1)
                    breakBlock(block.location, 0, -1, 0)
                    breakBlock(block.location, 0, -1, 1)
                    breakBlock(block.location, 0, 0, -1)
                    breakBlock(block.location, 0, 0, 1)
                }
            }
        }
    }

    private fun breakBlock(location: Location, x: Int, y: Int, z: Int) {
        val clone = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
        if (clone.block.type == Material.BEDROCK)
            return
        clone.block.breakNaturally()
    }
}